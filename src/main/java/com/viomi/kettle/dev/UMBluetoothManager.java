package com.viomi.kettle.dev;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quintic.libota.bleGlobalVariables;
import com.quintic.libota.otaManager;
import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.UMGlobalParam.Errors;
import com.viomi.kettle.UMGlobalParam.HeatModel;
import com.viomi.kettle.UMGlobalParam.Status;
import com.viomi.kettle.activity.UMGattAttributes;
import com.viomi.kettle.interfaces.UMOtaInterface;
import com.viomi.kettle.interfaces.UMStatusInterface;
import com.viomi.kettle.utils.FileUtil;
import com.viomi.kettle.utils.UMDataProcessUtil;
import com.viomi.kettle.utils.log;
import com.viomi.kettle.view.UMCircleProgress;
import com.viomi.kettle.view.UMModelSetView;
import com.viomi.kettle.view.UMSwitchButton;
import com.viomi.kettle.view.UMThermometer;
import com.viomi.kettle.view.UmTempSeekbarView;
import com.viomi.kettle.view.UmTimeSeekbarView;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.Response.BleNotifyResponse;
import com.xiaomi.smarthome.bluetooth.Response.BleReadResponse;
import com.xiaomi.smarthome.bluetooth.Response.BleWriteResponse;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.bluetooth.XmBluetoothRecord;
import com.xiaomi.smarthome.device.api.BtFirmwareUpdateInfo;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.DeviceStat;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;


public class UMBluetoothManager {
	private final static String TAG="UMBluetoothManager";
	private Context context;
	private String mac;
	private String model;
	private String did;
	private UMCircleProgress mCircleProgress;
	private RadioButton mRadioButtonNotBoil,mRadioButtonBiol;
	private UMThermometer mThermometer;
	private LinearLayout mBoilSetLayout;
	private UMSwitchButton mBoilModeSwitch;
	private View mPointView;
	private SeekBar mTimeSeekBar;
	private TextView mTimeTextView,mErrorText;

	private UmTempSeekbarView mUmTempSeekbarView;
	private UmTimeSeekbarView mUmTimeSeekbarView;
	private UMModelSetView mUMModelSetView;
	private TextView mStatusView;
	private UMStatusInterface mStatusListener ;
	
	private @HeatModel int mHeatModel;//当前模式
	private int mTemp=50;//当前设置温度
	private BleNotifyResponse notifyResponse=null;
	private BleWriteResponse writeResponse=null;
	private Response.BleConnectResponse connectResponse=null;
	private float mSetTime;//保温时间

	private int mConsumeTime=0;//保温消耗时间
	private  int mBoilModeSet=0;//倒水放回，直接保温

	private UMUpgraderMsg mUpgraderMsg=new UMUpgraderMsg();
    private UMOtaInterface mOtaInterface;
    private otaManager updateManager=new otaManager();
	private boolean mIsOwer=false;//是否主人
	
	public static UMBluetoothManager INSTANCE;
	public static UMBluetoothManager getInstance(){
		if(INSTANCE==null){
			synchronized (UMBluetoothManager.class) {
				if(INSTANCE==null){
					INSTANCE=new UMBluetoothManager();
				}
			}
		}
		return INSTANCE;
	}
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {

			try {
				switch (msg.what) {
				case UMGlobalParam.MSG_WHAT_SETUP_FAIL:
					if(mThermometer!=null){
						mThermometer.setProgress(mTemp);
					}

					if(mRadioButtonBiol!=null&&mRadioButtonNotBoil!=null){
						if(mHeatModel==UMGlobalParam.MODEL_KEEP_WARM_NOT_BOIL){
							mRadioButtonBiol.setChecked(false);
							mRadioButtonNotBoil.setChecked(true);
							mRadioButtonBiol.setTextColor(context.getResources().getColor(R.color.button_notset_color));
							mRadioButtonNotBoil.setTextColor(context.getResources().getColor(R.color.button_set_color));
							mBoilSetLayout.setVisibility(View.INVISIBLE);
						}else{
							mRadioButtonBiol.setChecked(true);
							mRadioButtonNotBoil.setChecked(false);
							mRadioButtonBiol.setTextColor(context.getResources().getColor(R.color.button_set_color));
							mRadioButtonNotBoil.setTextColor(context.getResources().getColor(R.color.button_notset_color));
							mBoilSetLayout.setVisibility(View.VISIBLE);
						}
					}

					Log.e(TAG, "seton fail!mHeatModel="+mHeatModel+",mTemp="+mTemp);
					Toast.makeText(context, context.getString(R.string.um_setup_fail), Toast.LENGTH_LONG).show();
					break;

				case UMGlobalParam.MSG_WHAT_TIME_WRITE:
					int result=msg.arg1;
					Log.i(TAG,"MSG_WHAT_TIME_WRITE,result="+result);
					if(result!=0){
						setKeepWarmTimeView(mSetTime);
						Log.e(TAG, "write keep warm time fail!reset to "+mSetTime);
					}
					break;

				case UMGlobalParam.MSG_WHAT_TIME_READ:
					int code=msg.arg1;
					byte[] values=(byte[])msg.obj;
					Log.e(TAG,"MSG_WHAT_TIME_READ,code="+code+",value="+values[0]);
					if(code==0&&values!=null&&values.length>0){
						mSetTime=(float) ((int)(values[0])*0.5);
						if(mSetTime>UMGlobalParam.MAX_KEEP_WARM_TIME){
							mSetTime=UMGlobalParam.MAX_KEEP_WARM_TIME;
						}
						Log.d(TAG, "read keep warm time="+mSetTime);
						setKeepWarmTimeView(mSetTime);
					}
					break;

					case UMGlobalParam.MSG_BOIL_MODE_SET_WRITE:
						int result1=msg.arg1;
						Log.i(TAG,"MSG_BOIL_MODE_SET_WRITE,result="+result1);
						if(result1!=0){
							Log.e(TAG, "write boil mode set fail! ");
						}
						break;

					case UMGlobalParam.MSG_BOIL_MODE_SET_READ:
						int code1=msg.arg1;
						byte[] values1=(byte[])msg.obj;
						Log.d(TAG,"MSG_WHAT_TIME_READ,code="+code1+",value="+values1[0]);
						if(code1==0&&values1!=null&&values1.length>0){
							int  flag=values1[0];
							Log.d(TAG, "read boil mode set="+flag);
							if(flag==UMGlobalParam.BOIL_MODE_COMMON){
								mBoilModeSwitch.setChecked(false);
								mBoilModeSet=flag;
							}else if(flag==UMGlobalParam.BOIL_MODE_SPECIAL){
								mBoilModeSwitch.setChecked(true);
								mBoilModeSet=flag;
							}
						}
						break;

				case UMGlobalParam.MSG_REFRESH_UPDATE_INFO_SUC:
					log.d(TAG,"MSG_REFRESH_UPDATE_INFO_SUC,mPointView="+mPointView);
					if (mPointView != null&&mIsOwer) {
						mPointView.setVisibility(View.VISIBLE);
					}
					break;
				case UMGlobalParam.MSG_REFRESH_UPDATE_INFO_ERR:
					if (mPointView != null&&mIsOwer) {
						mPointView.setVisibility(View.GONE);
					}
					break;
				default:
					break;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		};
	};

	private  BroadcastReceiver mReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				Log.e(TAG,"BroadcastReceiver,intent null!");
				return;
			}
	
			String receMac = intent.getStringExtra(XmBluetoothManager.KEY_DEVICE_ADDRESS);
			log.d(TAG,"BroadcastReceiver,mac="+receMac);
			if (TextUtils.isEmpty(receMac)|| !receMac.equalsIgnoreCase(mac)) {
				Log.e(TAG,"BroadcastReceiver,receMac not macth!");
				return;
			}
			
			String action = intent.getAction();
			log.d(TAG,"BroadcastReceiver,action="+action);
			if (XmBluetoothManager.ACTION_CHARACTER_CHANGED.equalsIgnoreCase(action)) {
				UUID service = (UUID) intent.getSerializableExtra(XmBluetoothManager.KEY_SERVICE_UUID);
				UUID character = (UUID) intent.getSerializableExtra(XmBluetoothManager.KEY_CHARACTER_UUID);
				byte[] value = intent.getByteArrayExtra(XmBluetoothManager.KEY_CHARACTER_VALUE);
				log.d(TAG,"BroadcastReceiver,service="+service+",character="+character);
				if (service != null && character != null) {
					processNotify(service, character, value);
				}
			} else if (XmBluetoothManager.ACTION_CONNECT_STATUS_CHANGED
					.equalsIgnoreCase(action)) {
				int status = intent.getIntExtra(XmBluetoothManager.KEY_CONNECT_STATUS,XmBluetoothManager.STATUS_UNKNOWN);
				processConnectStatusChanged(status);
			}
		}
		
	};
	
	/**
	 * 处理网络状态变化
	 * 
	 * @param status
	 */
	private void processConnectStatusChanged(int status) {
		log.d(TAG,"processConnectStatusChanged,status="+status);

		if(status==XmBluetoothManager.STATUS_DISCONNECTED){
			Log.d(TAG,"reconnect");
			if(mStatusListener!=null){
				mStatusListener.isOnlineChange(false);
			}
			if(mOtaInterface!=null){
				mOtaInterface.otaFail();
			}
			connect();
		}else {
			if(mStatusListener!=null){
				mStatusListener.isOnlineChange(true);
			}

		}
	}
	
	/**
	 * 处理收到的notify通知
	 */
	private void processNotify(UUID service, UUID character, byte[] value) {
		if(service==null||character==null||value==null){
			return;
		}
		log.d(TAG,"processNotify,service="+service.toString()+",character="+character.toString());
		log.d(TAG,"processNotify,value="+UMDataProcessUtil.bytesToHexString(value));
		if(service.equals(UMGattAttributes.SERVICE_UUID)&&character.equals(UMGattAttributes.STATUS_UUID)){
			refreshView(value);
			if(UMGlobalParam.isSaveStatusData){
				saveDataToFile(value);
			}
		}else if(service.equals(bleGlobalVariables.UUID_QUINTIC_OTA_SERVICE)&&character.equals(bleGlobalVariables.UUID_OTA_NOTIFY_CHARACTERISTIC)){
        	updateManager.otaGetResult(value);
		}else if(service.equals(UMGattAttributes.SERVICE_UUID)&&character.equals(UMGattAttributes.PUREWATER_UUID)){

			int index= UMBleParser.getRecordIndex(value);
			if(index>=0){
				log.d(TAG,"each record data:"+UMDataProcessUtil.bytesToHexString(value));
				if(index== UMBleParser.getRecordIndex(UMBleParser.RECORD_STOP_BYTES)){
					//getRecordFinished();
				}else {
					uploadRecord(value);
				}
			}
		}

	//	refreshView(value);

	}
	

	/***
	 * 蓝牙管理初始化，启动连接管理和打开状态字notify
	 * @param mDeviceStat
	 */
	public void init(Context context , DeviceStat mDeviceStat ,boolean isOwer, UMStatusInterface mStatusInterface){
		this.context=context;
		this.model=mDeviceStat.model;
		this.mac=mDeviceStat.mac;
		this.did=mDeviceStat.did;
		this.mIsOwer=isOwer;
		this.mStatusListener=mStatusInterface;
		registerBleNotifyReceiver();
		//connect();
		openStatusNotify();
		if(UMGlobalParam.isSaveStatusData){
			initFile();
		}
		Log.e(TAG,"model="+this.model);
	}

	/***
	 * 蓝牙管理初始化，启动连接管理和打开状态字notify
	 * @param mDeviceStat
	 */
	public void init(Context context , DeviceStat mDeviceStat){
		this.context=context;
		this.model=mDeviceStat.model;
		this.mac=mDeviceStat.mac;
		this.did=mDeviceStat.did;
		registerBleNotifyReceiver();
		//connect();
		openStatusNotify();
		if(UMGlobalParam.isSaveStatusData){
			initFile();
		}
	}
	
	private void registerBleNotifyReceiver() {
		IntentFilter filter = new IntentFilter(XmBluetoothManager.ACTION_CHARACTER_CHANGED);
		filter.addAction(XmBluetoothManager.ACTION_CONNECT_STATUS_CHANGED);
		context.registerReceiver(mReceiver,filter);
	}
	
	private void unregisterBleNotifyReceiver() {
		if((mReceiver!=null)&&(context!=null)){
			context.unregisterReceiver(mReceiver);
		}
	}
	
	/******
	 *连接蓝牙，并打开notify
	 */
	private void connect(){
		Log.d(TAG,"ble connect");
		if(connectResponse==null) {
			connectResponse = new Response.BleConnectResponse() {
				@Override
				public void onResponse(int i, Bundle bundle) {
					if (i == Code.REQUEST_SUCCESS) {
						Log.d(TAG,"BleConnectResponse,success !");
						openStatusNotify();
					}else {
						if(mStatusListener!=null){
							mStatusListener.isOnlineChange(false);
						}
			          	Log.e(TAG,"BleConnectResponse,fail !");
					}
				}
			};
		}
		XmBluetoothManager.getInstance().secureConnect(mac, connectResponse);

	}

	private void openStatusNotify(){
		//打开notify
		if(notifyResponse==null){
			notifyResponse =new BleNotifyResponse() {

				@Override
				public void onResponse(int code, Void data) {
					if(code ==Code.REQUEST_SUCCESS){
						Log.i(TAG,"BleNotifyResponse,success!");
					}else {
						Log.e(TAG,"BleNotifyResponse fail,onResponse:code="+code+",data="+data);
					}
				}
			};
		}
		XmBluetoothManager.getInstance().notify(mac,UMGattAttributes.SERVICE_UUID,UMGattAttributes.STATUS_UUID,notifyResponse);
	}
	
	/*****
	 * 断开
	 */
	public void disconnect(){
		Log.d(TAG,"disconnect");
		unregisterBleNotifyReceiver();
		XmBluetoothManager.getInstance().unnotify(mac,UMGattAttributes.SERVICE_UUID,UMGattAttributes.STATUS_UUID);
		XmBluetoothManager.getInstance().unnotify(mac,UMGattAttributes.SERVICE_UUID,UMGattAttributes.PUREWATER_UUID);
    	XmBluetoothManager.getInstance().disconnect(mac,30*1000);

	}
	
	/***
	 * 传入ui参数
	 * @param circleProgress 圆形进度条
	 * @param seekbar  保温时长
	 * @param errorText  错误
	 */
	public void setStatusView(View circleProgress,View seekbar,View errorText,View timeText,View pointView){
		mCircleProgress=(UMCircleProgress)circleProgress;
		mTimeTextView=(TextView)timeText;
		mTimeSeekBar=(SeekBar)seekbar;
		mErrorText=(TextView)errorText;
		mPointView=pointView;
	}
	
	public void setCustomView(View thermometer,View radioButtonNotBoil,View radioButtonBiol,LinearLayout boilSetLayout ,View boilModeSwitch){
		mThermometer=(UMThermometer) thermometer;
		mRadioButtonBiol=(RadioButton) radioButtonBiol;
		mRadioButtonNotBoil=(RadioButton) radioButtonNotBoil;
		mBoilSetLayout=boilSetLayout;
		mBoilModeSwitch=(UMSwitchButton) boilModeSwitch;
	}

	/***
	 * 传入ui参数
	 */
	public void setMainView(View umTimeSeekbarView,View umTempSeekbarView,View uMModelSetView,View pointView){
		mUmTimeSeekbarView= (UmTimeSeekbarView) umTimeSeekbarView;
		mUmTempSeekbarView= (UmTempSeekbarView) umTempSeekbarView;
		mUMModelSetView= (UMModelSetView) uMModelSetView;
		mPointView=pointView;
	}
	
	/*****
	 * 获取当前设置的加热模式
	 * @return
	 */
	public @HeatModel int getHeatModel(){
		return mHeatModel;
	}
	
	/*****
	 * 获取当前设置的温度
	 * @return
	 */
	public  int getTempSet(){
		return mTemp;
	}

	/****
	 * 刷新状态界面
	 * @param data 接收蓝牙通知数据
	 */
	private void refreshView(byte[] data){
		if(mStatusListener!=null){
			mStatusListener.onStatusDataReceive(data);
		}
//		if(data==null){
//			Log.e(TAG,"MSG_WHAT_STATUS_VARY,data null!");
//			return;
//		}
//		if(data.length<7){
//			Log.e(TAG,"MSG_WHAT_STATUS_VARY,data length is "+data.length+",not correct!");
//			return;
//		}
//
//		//状态显示
//		@Status int status=data[0];
////		if(mCircleProgress!=null){
////			mCircleProgress.setStatus(status);
////		}
//
//		//按键选择
//		int mKeyChoose=data[1]&0xff;
////		if(mCircleProgress!=null){
////			mCircleProgress.setKeyChoose(mKeyChoose);
////		}
//
//		//是否完成
//		boolean isCompleted;
//		if(data[2]==0){
//			isCompleted=false;
//		}else if(data[2]==1){
//			isCompleted=true;
//		}
//
//		//异常
//		@Errors int mError=data[3];
//	//	setErrorText(mError);
//
//		//自定义温度
//		mTemp=data[4]&0xff;
//		if(mCircleProgress!=null){
//			mCircleProgress.setTemp(mTemp);
//		}
//
//		//实际温度显示
//		int temp=data[5]&0xff;
//		if(temp<0){
//			temp=0;
//		}else if (temp>100) {
//			temp=100;
//		}
//		if(mCircleProgress!=null){
//			mCircleProgress.setProgress(temp);
//		}
//
//		//自定义模式
//		mHeatModel=data[6];
//		if(mCircleProgress!=null){
//			mCircleProgress.setMode(mHeatModel);
//		}
//
//		//保温消耗时间
//		if(data.length>=9){
//			mConsumeTime=data[7]&0xff+(data[8]&0xff)*256;
//			if(mTimeSeekBar!=null){
//				float time= (float) (mConsumeTime/60.0);
//				if(time>UMGlobalParam.MAX_KEEP_WARM_TIME){
//					time=UMGlobalParam.MAX_KEEP_WARM_TIME;
//				}
//				mTimeSeekBar.setSecondaryProgress((int)(time*10));
//			}
//		}

		
	}

	
	/*****
	 * 设置参数
	 * @param setupType 设置类型  模式和温度设置
	 * @param value  设置数值
	 */
	public  void onSetup( int setupType, int value){
		log.d(TAG,"onSetup,setupType="+setupType+",value="+value);
		if(writeResponse==null){
			writeResponse=new BleWriteResponse() {
				
				@Override
				public void onResponse(int code, Void data) {
					Log.d(TAG,"BleWriteResponse,code="+code+",data="+data+"\n");
					if(code!=0){
						if(mHandler!=null){
							Message msg=mHandler.obtainMessage();
							msg.what=UMGlobalParam.MSG_WHAT_SETUP_FAIL;
							mHandler.sendMessage(msg);
						}

					}
	
				}
			};
		}
		byte[] setBytes=new byte[2];
		setBytes[0]=(byte) (setupType&0xff);
		setBytes[1]=(byte)(value&0xff);
		XmBluetoothManager.getInstance().write(mac, UMGattAttributes.SERVICE_UUID, UMGattAttributes.SETUP_UUID, setBytes, writeResponse);
		
	}
	
	/***
	 * 设置保温时长
	 * @param time 保温时长
	 */
	public void setKeepWarmTime(float time){
		byte[] values=new byte[1];
		values[0]=(byte) (time/0.5);
		log.d(TAG,"setKeepWarmTime,time="+time+",value="+values[0]);
		XmBluetoothManager.getInstance().write(mac, UMGattAttributes.SERVICE_UUID, UMGattAttributes.TIME_SET_UUID, 
				values,new BleWriteResponse() {
					
					@Override
					public void onResponse(int code, Void data) {
						if(mHandler!=null){
							Message msg=mHandler.obtainMessage();
							msg.what=UMGlobalParam.MSG_WHAT_TIME_WRITE;
							msg.arg1=code;
							mHandler.sendMessage(msg);
						}
					}
				});
	}
	
	
	/***
	 * 读取保温时间
	 */
	public void readKeepWarmTime(){
		XmBluetoothManager.getInstance().read(mac, UMGattAttributes.SERVICE_UUID, UMGattAttributes.TIME_SET_UUID, new BleReadResponse() {
			
			@Override
			public void onResponse(int code, byte[] data) {
				if(mHandler!=null){
					Message msg=mHandler.obtainMessage();
					msg.what=UMGlobalParam.MSG_WHAT_TIME_READ;
					msg.arg1=code;
					msg.obj=data;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	/***
	 * 设置倒水放回，直接保温参数
	 * @param flag 0,倒水放回后直接煮沸，1，水
	 */
	public void setBoilModeSet(int flag){
		byte[] values=new byte[1];
		values[0]=(byte) flag;
		log.d(TAG,"setBoilModeSet,value="+values[0]);
		XmBluetoothManager.getInstance().write(mac, UMGattAttributes.SERVICE_UUID, UMGattAttributes.BOIL_MODE_SET_UUID,
				values,new BleWriteResponse() {

					@Override
					public void onResponse(int code, Void data) {
						if(mHandler!=null){
							Message msg=mHandler.obtainMessage();
							msg.what=UMGlobalParam.MSG_BOIL_MODE_SET_WRITE;
							msg.arg1=code;
							mHandler.sendMessage(msg);
						}


					}
				});
	}


	/***
	 * 读取倒水放回，直接保温参数
	 */
	public void readBoilModeSet(){
		XmBluetoothManager.getInstance().read(mac, UMGattAttributes.SERVICE_UUID, UMGattAttributes.BOIL_MODE_SET_UUID, new BleReadResponse() {

			@Override
			public void onResponse(int code, byte[] data) {
				if(mHandler!=null){
					Message msg=mHandler.obtainMessage();
					msg.what=UMGlobalParam.MSG_BOIL_MODE_SET_READ;
					msg.arg1=code;
					msg.obj=data;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	/***
	 * 获取倒水放回，直接保温参数，readBoidulModeSet之后才生效
	 */
	public int getBoilModeSet(){
		return mBoilModeSet;
	}

	public void setKeepWarmTimeView(float time){
		
		if(mTimeSeekBar!=null){
			mTimeSeekBar.setProgress((int)(time*10));
		}
		if(mTimeTextView!=null){
			if(time>0){
				Locale locale=context.getResources().getConfiguration().locale;
				if(locale!=null&&"ru".equals(locale.getLanguage())){//俄语小数点是","
					mTimeTextView.setText(context.getString(R.string.um_desc_keep_warm_time)+(" "
							+time).replace(".",",")+" "+context.getString(R.string.um_text_hour));
				}else {
					mTimeTextView.setText(context.getString(R.string.um_desc_keep_warm_time)+" "
							+time+" "+context.getString(R.string.um_text_hour));
				}
			}else {
				mTimeTextView.setText(context.getString(R.string.um_desc_keep_warm_time)+":"+context.getString(R.string.um_text_notime));	
			}
		}
	}
	
	/***
	 * 读取固件版本
	 */
	public void readMcuVersion(){

		XmBluetoothManager.getInstance().read(mac, UMGattAttributes.INFO_SERVICE_UUID, UMGattAttributes.MCU_VERSION_UUID, new BleReadResponse() {
			
			@Override
			public void onResponse(int code, byte[] data) {
				
				try {
			        if (code == Code.REQUEST_SUCCESS) {
			        	if(data!=null&&data.length>0){
			        		String version=new String(data);
							mUpgraderMsg.currentMcuVersion=version;
			        		Log.i(TAG, "readMcuVersion success! version:"+version);
							upgradeCompare(version,mUpgraderMsg.latestMcuVersion);
			        	}
			        	return;
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.e(TAG, "readMcuVersion fail!");
			}
		});
	}
	


	/***
	 * 打开用水记录notify
	 */
	public void openEachRecordNotify(){
		XmBluetoothManager.getInstance().notify(mac, UMGattAttributes.SERVICE_UUID, UMGattAttributes.PUREWATER_UUID, new BleNotifyResponse() {
			@Override
			public void onResponse(int i, Void aVoid) {
				if(i!=Code.REQUEST_SUCCESS){
					Log.e(TAG,"openOtaUpdateNotify,onResponse:code="+i+",data="+aVoid);
				}
			}
		});
	}

	/***
	 * 上报煮水记录
	 */
	private void uploadRecord(byte[] value){

		UMKettleEachRecord kettleEachRecord=UMBleParser.parseEachRecord(mac,value);
		if(kettleEachRecord==null){
			Log.e(TAG,"parseEachRecord,fail!");
		}
		List<XmBluetoothRecord> records = new ArrayList<XmBluetoothRecord>();

		XmBluetoothRecord record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="mac";
		record.value=kettleEachRecord.mac;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="time";
		record.value=kettleEachRecord.time;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="index";
		record.value=""+kettleEachRecord.index;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="initialTemp";
		record.value=""+kettleEachRecord.initialTemp;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="boilTemp";
		record.value=""+kettleEachRecord.boilTemp;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="setTemp";
		record.value=""+kettleEachRecord.setTemp;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="workMode";
		record.value=""+kettleEachRecord.workMode;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="errorCode";
		record.value=""+kettleEachRecord.errorCode;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="cuteOffTime";
		record.value=""+kettleEachRecord.cuteOffTime;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="heatTime";
		record.value=""+kettleEachRecord.heatTime;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="holdTime";
		record.value=""+kettleEachRecord.holdTime;
		record.trigger = null;
		records.add(record);

		record = new XmBluetoothRecord();
		record.type = XmBluetoothRecord.TYPE_EVENT;
		record.key="Reserved";
		record.value=""+kettleEachRecord.Reserved;
		record.trigger = null;
		records.add(record);

		log.d(TAG,"record,kettleEachRecord="+kettleEachRecord.toString());

		log.d(TAG,"reportBluetoothRecords,did="+did+",model"+model);
		XmPluginHostApi.instance().reportBluetoothRecords(did, model, records, new Callback<List<Boolean>>() {

			@Override
			public void onSuccess(List<Boolean> booleans) {
				Log.i(TAG,"reportBluetoothRecords,success !");
				confirmBoilRecord();
			}

			@Override
			public void onFailure(int code, String error) {
				Log.e(TAG,"reportBluetoothRecords,fail:code="+code+",error="+error);
			}
		});
	}

	/***
	 * 确认读取并上传了煮水记录
	 */
	private void confirmBoilRecord(){
		byte[] bytes=new byte[]{0x01};
		XmBluetoothManager.getInstance().write(mac,  UMGattAttributes.SERVICE_UUID, UMGattAttributes.PUREWATER_UUID,  bytes, new Response.BleWriteResponse() {
			@Override
			public void onResponse(int i, Void aVoid) {
				if(i!=0){
					Log.e(TAG,"write back for each record fail!");
				}
			}
		});
	}


	public void setmUpgraderMsg(UMUpgraderMsg msg){
		mUpgraderMsg=msg;
	}
	
	public UMUpgraderMsg getUMUpgraderMsg(){
		return mUpgraderMsg;
	}

	/***
	 * 打开升级notify
	 */
	public void openOtaUpdateNotify(){
		XmBluetoothManager.getInstance().notify(mac, bleGlobalVariables.UUID_QUINTIC_OTA_SERVICE, bleGlobalVariables.UUID_OTA_NOTIFY_CHARACTERISTIC, new BleNotifyResponse() {
			
			@Override
			public void onResponse(int code, Void data) {
				if(code!=Code.REQUEST_SUCCESS){
					Log.e(TAG,"openOtaUpdateNotify,onResponse:code="+code+",data="+data);	
				}
			}
		});
	}
	
	/***
	 * 关闭固件notify升级
	 */
	public void closeOtaUpdateNotify(){
		XmBluetoothManager.getInstance().unnotify(mac, bleGlobalVariables.UUID_QUINTIC_OTA_SERVICE, bleGlobalVariables.UUID_OTA_NOTIFY_CHARACTERISTIC);
	}
	
	/***
	 * 写ota升级character
	 * @param data
	 */
	public void writeOtaUpdateCharacter(byte[] data){
		XmBluetoothManager.getInstance().write(mac,  bleGlobalVariables.UUID_QUINTIC_OTA_SERVICE, bleGlobalVariables.UUID_OTA_WRITE_CHARACTERISTIC, data, new BleWriteResponse() {
			
			@Override
			public void onResponse(int code, Void data) {
				if(code==0){
					updateManager.notifyWriteDataCompleted();
				}else {
					Log.e(TAG,"writeOtaUpdateCharacter,onResponse:code="+code);	
					if(mOtaInterface!=null){
						mOtaInterface.otaFail();
					}
				}
				
			}
		});
	}
	
	/***
	 * 升级失败接口
	 * @param otaInterface
	 */
	public void onOtaFail(UMOtaInterface otaInterface){
		mOtaInterface=otaInterface;
	}
	

	public otaManager getOtaManager(){
		return updateManager;
	}

	/***
	 * 初始化测试数据
	 */
	private void initFile(){
		String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/kettletest/data.txt";
		File file=new File(path);
		if(file.exists()&&file.isFile()){
			file.delete();
		}
	}

	/***
	 * 保存notify数据，测试用
	 * @param value notify接收
     */
	private void saveDataToFile(byte[] value){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/kettletest/data.txt";
		try {

			File file=new File(path);
			if(file.exists()&&file.isFile()){
				if(file.length()>1024*1024){
					file.delete();
				}
			}
			FileUtil.creatFile(path);
			String data=format.format(date)+","+UMDataProcessUtil.bytesToHexString(value)+"\n";
			FileUtil.appendMethodB(path,data);
			Log.e(TAG,"save file ,path="+path+",data="+data);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG,"save file fail !msg="+e.getMessage());
		}

	}

	/***
	 * 读取固件升级信息
	 */
	public void runUpdateInfo() {
		Log.i(TAG,"runUpdateInfo,!model="+model);
		XmPluginHostApi.instance().getBluetoothFirmwareUpdateInfo(model, new Callback<BtFirmwareUpdateInfo>() {

			@Override
			public void onSuccess(BtFirmwareUpdateInfo result) {
				if(result==null){
					Log.e(TAG, "getBluetoothFirmwareUpdateInfo onSuccess return null");
					return;
				}
				Log.i(TAG,"version"+result.version+",changeLog="+result.changeLog);
				mUpgraderMsg.latestMcuVersion=result.version;
				mUpgraderMsg.upgradeDescription=result.changeLog;
				mUpgraderMsg.url=result.url;
				upgradeCompare(mUpgraderMsg.currentMcuVersion,result.version);

			}

			@Override
			public void onFailure(int error, String errorInfo) {
				if(mHandler!=null){
					mHandler.sendEmptyMessageDelayed(UMGlobalParam.MSG_REFRESH_UPDATE_INFO_ERR,100);
				}
			}
		});
	}

	private void upgradeCompare(String currentVersion,String latestMcuVersion){
		Log.i(TAG,"currentVersion="+currentVersion+",latestMcuVersion="+latestMcuVersion);
		if(currentVersion==null||latestMcuVersion==null){
			return;
		}
		if(latestMcuVersion.equals("")||currentVersion.equals("")){
			return;
		}
		if(latestMcuVersion.compareToIgnoreCase(currentVersion)>0){
			if(mHandler!=null){
				log.d(TAG,"send upgrade flag");
				mHandler.sendEmptyMessageDelayed(UMGlobalParam.MSG_REFRESH_UPDATE_INFO_SUC,100);
			}
		}else {
			if(mHandler!=null){
				mHandler.sendEmptyMessageDelayed(UMGlobalParam.MSG_REFRESH_UPDATE_INFO_ERR,100);
			}
		}

	}

	public String getCurrentVersion(){
		return mUpgraderMsg.currentMcuVersion;
	}

	public void close(){
		mHandler=null;
		mStatusListener=null;
		notifyResponse=null;
		writeResponse=null;
		connectResponse=null;
		INSTANCE=null;
	}



	
	/*****
	 * 设置后回调监听器
	 * @param lister
	 */
//	public void setOnSetupCallbackListenr(onSetupCallbackListenr lister){
//		this.listenr=lister;
//	}
//	
//	public interface onSetupCallbackListenr{
//		public void onCurrentSetupValue(int mode,int temp);
//	}
	
}
