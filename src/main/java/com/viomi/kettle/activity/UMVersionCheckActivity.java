package com.viomi.kettle.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import com.quintic.libota.BluetoothLeInterface;
import com.quintic.libota.otaManager;
import com.quintic.libota.bleGlobalVariables.otaResult;
import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.dev.UMBluetoothManager;
import com.viomi.kettle.interfaces.UMOtaInterface;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;


import android.R.integer;
import android.bluetooth.BluetoothGatt;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UMVersionCheckActivity extends UMBaseActivity{
    private final static String TAG = UMVersionCheckActivity.class.getSimpleName();
    private String mDefaultFirmwarePath=null;
    private ArrayList<HashMap<String, Object>> mFilelist=null;  
    private  TextView mCurrenTextView,mLatestTextView,mProgressDesc;
    private ImageView mLogoImageView;
    private Button mConfirmButton;
    public static final int  UPDATE_DATA= 1;
    public static final int  ERROR_CODE= 2;
    private boolean mStopUpdate=false;
    private ProgressBar mProgressBar;
    String latestVersion;
    private int mButtonClick=0;//安装按键点击操作，0：关闭，1升级
    private Thread updateThread=null;
    
    private otaManager updateManager=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		layoutId=R.layout.um_version_check_activity;
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.um_version_check_activity);
		init();
	}
	
	public void init(){
		updateManager=UMBluetoothManager.getInstance().getOtaManager();
		
		
		mHandler= new MyHandler(this);
		//Typeface typeface= UMGlobalParam.getInstance().getTextTypeface();
		
        mDefaultFirmwarePath=Environment.getExternalStorageDirectory().getAbsolutePath();
        int result=getFirmwareFileList("bin");
        Log.e(TAG,"mDefaultFirmwarePath="+   mDefaultFirmwarePath);
        Log.e(TAG,"getFirmwareFileList="+ result);
		TextView title=(TextView)findViewById(R.id.title);
		title.setText("米家电水壶");
        mCurrenTextView=(TextView)findViewById(R.id.current_version);
      //  mCurrenTextView.setTypeface(typeface);
        mLatestTextView=(TextView)findViewById(R.id.latest_version);
      // mLatestTextView.setTypeface(typeface);
        mConfirmButton=(Button)findViewById(R.id.confirm_button);
        mLogoImageView=(ImageView)findViewById(R.id.logo);
        mProgressBar=(ProgressBar)findViewById(R.id.progress);
        mProgressDesc=(TextView)findViewById(R.id.progress_desc);
        
        String currentVersion=UMBluetoothManager.getInstance().getUMUpgraderMsg().currentMcuVersion;
        mCurrenTextView.setText("当前版本:"+currentVersion);
        latestVersion=getLatestVersion();
        mDefaultFirmwarePath+="/"+latestVersion+".bin";
        Log.e(TAG,"getLatestVersion="+ latestVersion);
        
        if(latestVersion==null||latestVersion.equalsIgnoreCase(currentVersion)){
        	mLatestTextView.setText("固件已经是最新版本");
        	mLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.update_img_latest));
        	mConfirmButton.setText("确定");
        	mButtonClick=0;

        }else {
        	mLatestTextView.setText("最新版本:"+latestVersion);
        	mLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.update_img_update));
        	mConfirmButton.setText("立即更新");
        	mButtonClick=1;
		}
        
    	mConfirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Log.e(TAG, "mButtonClick="+mButtonClick);
				if(mButtonClick==0){
					finish();
				}else {
					updateProgress();
				}
				
			}
		});
        
		ImageView backBn=(ImageView)findViewById(R.id.back);
		backBn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
        
		
		UMBluetoothManager.getInstance().onOtaFail(new UMOtaInterface() {
			
			@Override
			public void otaFail() {
				if(!mStopUpdate){
	    			updateManager.otaStop();
	    			mStopUpdate=true;	
	    			SendUpdateMsg(ERROR_CODE,"ERROR_CODE","Gatt write fail or disconnet");
				}

				
			}
		});
		
	}
	
	
	private int getFirmwareFileList(String Extension) 
	{
        mFilelist=new ArrayList<HashMap<String, Object>>();
        
		String Path=mDefaultFirmwarePath;
		File current=new File(Path);
		if(!current.exists())
		{		
			Log.e(TAG, Path+":No such file or directory");
			return -1;
		}
		if(!current.canRead())
		{
			Log.e(TAG, ":No permission to open "+Path);
			return -2;				
		}
	    File[] files =current.listFiles();
	    Log.i(TAG, "List files under "+Path+":");
	    for (File f:files)
	    {
	        if (f.isFile())
	        {
	    		if(!current.canRead())
	    		{
	    			Log.w(TAG, ":No permission to read file "+Path+",skipped!");
	    			continue;				
	    		}
	            if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) 
	            {
	            	Log.i(TAG,"add file: "+ f.getName()+" size: "+f.length());
	            	HashMap<String, Object> item = new HashMap<String, Object>();
	            	item.put("filename",f.getName());
	            	item.put("filesize",String.valueOf(f.length())+" bytes");
	            	mFilelist.add(item);
	            }
	        }
	    }
	    if(mFilelist.isEmpty())
	    {
		Toast.makeText(getApplicationContext(), Path+" is empty", Toast.LENGTH_LONG).show();
	    	return -3;
	    }
	    return 0;
	}		
	
	public String getLatestVersion(){
		if(mFilelist==null&&mFilelist.isEmpty()){
			return null;
		}
		String versionString="";
		
		for(HashMap<String, Object> item :mFilelist){
			String fileName=(String) item.get("filename");
			String filesize=(String) item.get("filesize");
			if(!"0 bytes".equals(filesize)){
				String name=fileName.replace(".bin", "");
				if(versionString.equals("")||(versionString.compareToIgnoreCase(name)<0)){
					versionString=name;
				}
			}
		}
		return versionString;
		
	}
	
	
    private static class MyHandler extends Handler{
    	private WeakReference<UMVersionCheckActivity> activityReference;
    	public MyHandler(UMVersionCheckActivity activity){
    		activityReference=new WeakReference<UMVersionCheckActivity>(activity);
    	}
    	@Override
    	public void handleMessage(Message msg) {   
    	final UMVersionCheckActivity theActivity=activityReference.get();
		if (!Thread.currentThread().isInterrupted()) {
				switch(msg.what)
				{
					case UPDATE_DATA:
						int[] data=msg.getData().getIntArray("UPDATE_DATA");
		        		int percent=data[0];	
//		        		byteRate=data[1];
//		        		elapsedTime=data[2];
		        		Log.d(TAG,"percent:"+percent);        		
		        		if(percent>=100)
			            {	       
		        			theActivity.mLatestTextView.setText("固件更新完成");
		        			theActivity.mCurrenTextView.setText("当前版本:"+theActivity.latestVersion);
		        			theActivity.mProgressBar.setVisibility(View.GONE);
		        			theActivity.mProgressDesc.setVisibility(View.GONE);
		        			theActivity.mLogoImageView.setImageDrawable(theActivity.getResources().getDrawable(R.drawable.update_img_success));
		        			theActivity.mConfirmButton.setText("确定");
		        			theActivity.mButtonClick=0;
		        			theActivity.mStopUpdate=true;
		        			Log.e(TAG, "ota update finish!");
			            }else {
		        			theActivity.mProgressDesc.setVisibility(View.VISIBLE);
		        			theActivity.mProgressDesc.setText(percent+"%");
						}
						break;
					case ERROR_CODE:
			//			String errStr="Update Fail: "+msg.getData().getString("ERROR_CODE");
		//				progressDialog.setProgress(percent);
		//            	progressDialog.setMessage(generateDisplayMsg(errStr,elapsedTime,byteRate));  
						Log.e(TAG, "ota update start fail!");
						theActivity.mStopUpdate=true;
						theActivity.mProgressBar.setVisibility(View.GONE);
						theActivity.mLogoImageView.setImageDrawable(theActivity.getResources().getDrawable(R.drawable.update_img_fail));
						theActivity.mConfirmButton.setText("重试");
	        			theActivity.mProgressDesc.setVisibility(View.GONE);
						theActivity.mButtonClick=1;
						break;
				}	                	                	            
		    }
		}
    }
//	private  Handler mHandler = new Handler()
//	{
//		
//	    int percent=0;int byteRate=0;int elapsedTime=0;   
//        public void handleMessage(Message msg) {   
//        	     	
//        	if (!Thread.currentThread().isInterrupted()) {
//        		switch(msg.what)
//        		{
//        			case UPDATE_DATA:
//        				int[] data=msg.getData().getIntArray("UPDATE_DATA");
//		        		percent=data[0];	
//		        		byteRate=data[1];
//		        		elapsedTime=data[2];
//		        		//Log.d(TAG,"per:"+percent+" bps:"+byteRate+" time:"+elapsedTime);        		
//		        		if(percent>=100)
//			            {	       
//		                	mLatestTextView.setText("固件更新完成");
//		                    mCurrenTextView.setText("当前版本:"+latestVersion);
//	        				mProgressBar.setVisibility(View.GONE);
//	        	        	mLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.update_img_success));
//	        	        	mConfirmButton.setText("确定");
//	        	        	mButtonClick=0;
//			            }
//        				break;
//        			case ERROR_CODE:
//        				String errStr="Update Fail: "+msg.getData().getString("ERROR_CODE");
////        				progressDialog.setProgress(percent);
////		            	progressDialog.setMessage(generateDisplayMsg(errStr,elapsedTime,byteRate));  
//        				mProgressBar.setVisibility(View.GONE);
//        	        	mLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.update_img_fail));
//        	        	mConfirmButton.setText("重试");
//        	        	mButtonClick=1;
//        				break;
//        		}	                	                	            
//            }
//        }		
//	};
	
	
   public void updateProgress()
    {
	//	BluetoothGatt bluetoothGatt=new BluetoothGatt();
	   
       updateInstance ins=new updateInstance();
       ins.bleInterfaceInit();
       Log.i(TAG, "ota update start!");
		if(updateManager.otaStart(mDefaultFirmwarePath,ins)==otaResult.OTA_RESULT_SUCCESS)
		{      
			Log.i(TAG, "ota update start sucess!");
	    	mStopUpdate=false;
	    	mProgressBar.setVisibility(View.VISIBLE);
			mProgressDesc.setVisibility(View.VISIBLE);
			mProgressDesc.setText("0%");
	         updateThread=new Thread(update);
	        updateThread.start();	
		}else
		{
			Log.e(TAG, "ota update start fail!");
			Toast.makeText(activity(), "固件升级启动失败！", Toast.LENGTH_LONG).show();
		}

    }    
   
   
	
    Runnable update=new Runnable()
	{
		public void run()
		{			    		    	
	    	int[] extra=new int[8];
	    	while(!mStopUpdate)
	    	{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        	if(!Thread.currentThread().isInterrupted()) 
	        	{	
	        		otaResult ret=updateManager.otaGetProcess(extra);
	        		if(ret==otaResult.OTA_RESULT_SUCCESS)
	        			SendUpdateMsg(UPDATE_DATA,"UPDATE_DATA",extra);
	        		else
	        		{
	        			updateManager.otaStop();
	        			mStopUpdate=true;	
	        			SendUpdateMsg(ERROR_CODE,"ERROR_CODE",otaError2String(ret));
	        		}
		        }
        	}
	    }
	};
	
	public void onDestroy() {
		super.onDestroy();
		mStopUpdate=true;
		if(updateThread!=null){
			updateThread.interrupt();
			updateThread=null;
		}
	};
	
    private void SendUpdateMsg(int type,String key,String str)
    {
		Message msg=new Message();
		msg.what=type;
		msg.getData().putString(key, str);
		if(mHandler!=null)
			mHandler.sendMessage(msg);			
    }
    
    private void SendUpdateMsg(int type,String key,int[] value)
    {
		Message msg=new Message();
		msg.what=type;
		msg.getData().putIntArray(key, value);
		if(mHandler!=null)
			mHandler.sendMessage(msg);			
    }	
    
    private class updateInstance extends BluetoothLeInterface
    {
    	@Override
    	public boolean bleInterfaceInit()
    	{
    	    //return super.bleInterfaceInit(bluetoothGatt); 
    		if(XmBluetoothManager.getInstance()!=null){
    			return true;
    		}
			return false;
    		
    	}
    	
	    @Override
	    public boolean writeCharacteristic(byte[] data) {
	    	UMBluetoothManager.getInstance().writeOtaUpdateCharacter(data);
	    	return true;
	    }
	    
	    @Override
	    public boolean setCharacteristicNotification(boolean enabled) {
	    	if(enabled){
	    		UMBluetoothManager.getInstance().openOtaUpdateNotify();
	    	}else {
				UMBluetoothManager.getInstance().closeOtaUpdateNotify();
			}
	    	return true;
	    }
    	
    	
    }	
    
	private static String otaError2String(otaResult ret)
	{
		switch(ret)
		{
			case OTA_RESULT_SUCCESS:
				return "SUCCESS";	    
			case OTA_RESULT_PKT_CHECKSUM_ERROR:
				return "Transmission is failed,firmware checksum error";
			case OTA_RESULT_PKT_LEN_ERROR:
				return "Transmission is failed,packet length error";	    
			case OTA_RESULT_DEVICE_NOT_SUPPORT_OTA:
				return "The OTA function is disabled by the server";	    
			case OTA_RESULT_FW_SIZE_ERROR:
				return "Transmission is failed,firmware file size error";
			case OTA_RESULT_FW_VERIFY_ERROR: 	
				return "Transmission is failed,verify failed";				
			case OTA_RESULT_OPEN_FIRMWAREFILE_ERROR:
				return "Open firmware file failed";
			case OTA_RESULT_META_RESPONSE_TIMEOUT:
				return "Wait meta packet response timeout";
			case OTA_RESULT_DATA_RESPONSE_TIMEOUT:
				return "Wait data packet response timeout";
			case OTA_RESULT_SEND_META_ERROR:
				return "Send meta data error";
			case OTA_RESULT_RECEIVED_INVALID_PACKET:
			    	return "Transmission is failed,received invalid packet";
			case OTA_RESULT_INVALID_ARGUMENT:     	
			default:
				return "Unknown error";
		}
	}    
}
