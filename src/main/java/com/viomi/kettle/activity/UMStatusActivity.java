package com.viomi.kettle.activity;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.dev.UMBlueToothUpgrader;
import com.viomi.kettle.dev.UMBluetoothManager;
import com.viomi.kettle.view.UMCircleProgress;
import com.xiaomi.smarthome.device.api.BaseDevice;
import com.xiaomi.smarthome.device.api.IXmPluginHostActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

public class UMStatusActivity extends UMBaseActivity{
	private final static String TAG="UMStatusActivity";
	private final static byte MENU_REQUEST_CODE = -1;
	private UMCircleProgress circleProgress;
	private TextView  titleStatus,mKeepWarmTimeText,errorText;
	private SeekBar mSeekBar;
//	Typeface typeface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		layoutId=R.layout.um_status_activity;
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		View title_bar = findViewById(R.id.title_bar);
		if(title_bar!=null){
			mHostActivity.enableWhiteTranslucentStatus();
		}
		init();
		
	}
	
	private void init(){

		UMGlobalParam.getInstance().init();
	//	typeface= UMGlobalParam.getInstance().getTextTypeface();
		circleProgress=(UMCircleProgress)findViewById(R.id.progress);
		titleStatus=(TextView)findViewById(R.id.title_status);
		titleStatus.setText(getString(R.string.um_working_status));
	//	titleStatus.setTypeface(typeface);
		mSeekBar=(SeekBar)findViewById(R.id.time_seekbar);
		mKeepWarmTimeText=(TextView)findViewById(R.id.keep_warm_desc);
	//	mKeepWarmTimeText.setTypeface(typeface);
		errorText=(TextView)findViewById(R.id.error_text);
	//	errorText.setTypeface(typeface);
		View moreView = findViewById(R.id.title_bar_more);
		View shareView = findViewById(R.id.title_bar_share);
		View pointView = findViewById(R.id.title_bar_redpoint);
		pointView.setVisibility(View.INVISIBLE);
		Log.d(TAG,"mDeviceStat.mac="+mDeviceStat.mac+",mDeviceStat.isOnline="+mDeviceStat.isOnline);
		UMBluetoothManager.getInstance().setStatusView(circleProgress, mSeekBar,errorText,mKeepWarmTimeText,pointView);
		UMBluetoothManager.getInstance().init(activity(), mDeviceStat);

		FrameLayout titleBar=(FrameLayout)findViewById(R.id.title_bar);
		titleBar.setBackgroundColor(getResources().getColor(R.color.background));
		
		errorText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
		//		intent.putExtra("position", UMBluetoothManager.getInstance().getError());
				startActivity(intent,UMErrorDetailActivity.class.getName());
			}
		});
		
		TextView title=(TextView)findViewById(R.id.title_bar_title);
		title.setText(getString(R.string.umtitle));
		
		ImageView backBn=(ImageView)findViewById(R.id.title_bar_return);
		backBn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		ImageView customBn=(ImageView)findViewById(R.id.custom_temp_set);
		customBn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			//	Intent intent=new Intent(UMStatusActivity.this,UMCustomSetActivity.class);
			//	startActivity(intent);
				startActivity(null,UMCustomSetActivity.class.getName());
			}
		});
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBar!=null){
					float mProgress=(seekBar.getProgress()/5*5)/10.0f;
					UMBluetoothManager.getInstance().setKeepWarmTime(mProgress);
				}

			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float mProgress=(progress/5*5)/10.0f;
				if(mProgress>0){
					mKeepWarmTimeText.setText(getString(R.string.um_desc_keep_warm_time)
							+mProgress+getString(R.string.um_text_hour));	
				}else {
					mKeepWarmTimeText.setText(getString(R.string.um_desc_keep_warm_time)+":"+getString(R.string.um_text_notime));	
				}
			}
		});
		
		mKeepWarmTimeText.setText(getString(R.string.um_desc_keep_warm_time)+":"+getString(R.string.um_text_notime));

		moreView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				ArrayList<IXmPluginHostActivity.MenuItemBase> menuItemBases=new ArrayList<IXmPluginHostActivity.MenuItemBase>();
				IXmPluginHostActivity.IntentMenuItem intentMenuItem=new IXmPluginHostActivity.IntentMenuItem();
				intentMenuItem.name=getString(R.string.um_string_mi_directions);
				intentMenuItem.intent=mHostActivity.getActivityIntent(null, UMWebActivity.class.getName());
				menuItemBases.add(intentMenuItem);
				IXmPluginHostActivity.BleMenuItem upgraderItem=IXmPluginHostActivity.BleMenuItem.newUpgraderItem(new UMBlueToothUpgrader());
			//	upgraderItem.setHasNewerVersion(true); // 设置是否要有红点提示
				menuItemBases.add(upgraderItem);
				mHostActivity.openMoreMenu(menuItemBases,true,MENU_REQUEST_CODE);
				
			//startActivity(null,UMSettingsActivity.class.getName());
			}
		});
		shareView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mHostActivity.openShareActivity();
			}
		});
		
		BaseDevice baseDevice = new BaseDevice(mDeviceStat);
		if(baseDevice.isOwner()) {
			shareView.setVisibility(View.VISIBLE);
			moreView.setVisibility(View.VISIBLE);
		} else if(baseDevice.isFamily() && !baseDevice.isOwner()){
			
			shareView.setVisibility(View.GONE);
			moreView.setVisibility(View.GONE);
		} else if(!baseDevice.isFamily() && !baseDevice.isOwner()) {
			shareView.setVisibility(View.GONE);
			moreView.setVisibility(View.GONE);
		}
		UMBluetoothManager.getInstance().readKeepWarmTime();
		UMBluetoothManager.getInstance().readMcuVersion();

		if(!UMGlobalParam.getInstance().isStatusFirst()){
		       final Dialog dialog1 = new Dialog(activity(), R.style.Dialog_Fullscreen);        
		        dialog1.setContentView(R.layout.um_status_tip_dialog);
		        final ImageView iv1 = (ImageView)dialog1.findViewById(R.id.temp_time_set_tip);
		        final ImageView iv2 = (ImageView)dialog1.findViewById(R.id.setting_enter_tip);
		        iv1.setVisibility(View.VISIBLE);
		        iv2.setVisibility(View.INVISIBLE);
		        iv1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
				        iv2.setVisibility(View.VISIBLE);
				        iv1.setVisibility(View.INVISIBLE);
						
					}
				});
		        iv2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog1.dismiss();
						UMGlobalParam.getInstance().setStatusFirst(true);
					}
				});
		        dialog1.show();
		}
		UMBluetoothManager.getInstance().openEachRecordNotify();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume");
		UMBluetoothManager.getInstance().runUpdateInfo();
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(circleProgress!=null){
			circleProgress.stopTimer();
		}
		UMBluetoothManager.getInstance().disconnect();
		UMBluetoothManager.getInstance().close();
	}
}
