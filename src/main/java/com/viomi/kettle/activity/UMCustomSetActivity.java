package com.viomi.kettle.activity;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.UMGlobalParam.HeatModel;
import com.viomi.kettle.dev.UMBluetoothManager;
import com.viomi.kettle.view.UMCustomToast;
import com.viomi.kettle.view.UMSwitchButton;
import com.viomi.kettle.view.UMThermometer;
import com.viomi.kettle.view.UMThermometer.onProgressListener;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class UMCustomSetActivity extends UMBaseActivity{
	private final static String TAG="UMCustomSetActivity";
	private RadioGroup radioGroup;
	private RadioButton radioButtonNotBoil,radioButtonBiol;
	private UMThermometer thermometer;
	private boolean modeChange=false;//点击模式改变
	private int tempCustom;//设置温度
	private @HeatModel int modelCustem;//设定模式
	private Toast mToast;
	private LinearLayout mBoilSetLayout;
	private UMSwitchButton mBoilModeSwitch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		layoutId=R.layout.um_custom_set_avtivity;
		super.onCreate(savedInstanceState);
		init();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UMBluetoothManager.getInstance().readBoilModeSet();
		tempCustom=UMBluetoothManager.getInstance().getTempSet();
		modelCustem=UMBluetoothManager.getInstance().getHeatModel();
		Log.d(TAG, "tempCustom="+tempCustom+",modelCustem="+modelCustem+"\n");
		refreshView();
	}
		
	private void init(){
		View title_bar = findViewById(R.id.title_bar);
		if(title_bar!=null){
			mHostActivity.enableWhiteTranslucentStatus();
		}

		radioGroup=(RadioGroup)findViewById(R.id.setting_layout);
		radioButtonNotBoil=(RadioButton)findViewById(R.id.notBoil);
		radioButtonBiol=(RadioButton)findViewById(R.id.boil);
		thermometer=(UMThermometer)findViewById(R.id.thermometer);

		mBoilSetLayout=(LinearLayout)findViewById(R.id.boil_set_layout);
		View moreView = findViewById(R.id.title_bar_more);
		moreView.setVisibility(View.INVISIBLE);
		
		FrameLayout titleBar=(FrameLayout)findViewById(R.id.title_bar);
		titleBar.setBackgroundColor(getResources().getColor(R.color.background));
		
		TextView title=(TextView)findViewById(R.id.title_bar_title);
		title.setText(getString(R.string.umtitle));
		TextView titleStatus=(TextView)findViewById(R.id.title_status);	
		titleStatus.setText(getString(R.string.um_set_custom_temp));
		
		ImageView backBn=(ImageView)findViewById(R.id.title_bar_return);
		backBn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mBoilModeSwitch=(UMSwitchButton)findViewById(R.id.mode_switch) ;
		if(UMBluetoothManager.getInstance().getBoilModeSet()==UMGlobalParam.BOIL_MODE_COMMON){
			mBoilModeSwitch.setChecked(false);
		}else if(UMBluetoothManager.getInstance().getBoilModeSet()==UMGlobalParam.BOIL_MODE_SPECIAL){
			mBoilModeSwitch.setChecked(true);
		}
		mBoilModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					UMBluetoothManager.getInstance().setBoilModeSet(UMGlobalParam.BOIL_MODE_SPECIAL);
				}else {
					UMBluetoothManager.getInstance().setBoilModeSet(UMGlobalParam.BOIL_MODE_COMMON);
				}
			}
		});

		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				modeChange=true;

			}
		});
		
		radioButtonBiol.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(modeChange){
					modeChange=false;
//					Log.d(TAG, "setKettleValue,mode="+UMGlobalParam.MODEL_KEEP_WARM_BOIL
//							+",temp"+tempCustom);
					modelCustem=UMGlobalParam.MODEL_KEEP_WARM_BOIL;
					setKettleValue(modelCustem,tempCustom);
					radioButtonBiol.setTextColor(getResources().getColor(R.color.button_set_color));
					radioButtonNotBoil.setTextColor(getResources().getColor(R.color.button_notset_color));
					mBoilSetLayout.setVisibility(View.VISIBLE);
				}

				
			}
		});
		
		radioButtonNotBoil.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(modeChange){
					modeChange=false;
//					Log.d(TAG, "setKettleValue,mode="+UMGlobalParam.MODEL_KEEP_WARM_NOT_BOIL
//							+",temp"+tempCustom);
					modelCustem=UMGlobalParam.MODEL_KEEP_WARM_NOT_BOIL;
					setKettleValue(modelCustem,tempCustom);
					radioButtonBiol.setTextColor(getResources().getColor(R.color.button_notset_color));
					radioButtonNotBoil.setTextColor(getResources().getColor(R.color.button_set_color));
					mBoilSetLayout.setVisibility(View.INVISIBLE);
					
				}
				
			}
		});

//		moreView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(null,UMModeSettingActivity.class.getName());
//			}
//		});

		
		thermometer.setOnProgressListener( new onProgressListener() {
			
			@Override
			public void onComplete() {
//				int flag=UMBluetoothManager.getInstance().getKeyChoose();
				int flag=0;
				Log.d(TAG, "onComplete,getKeyChoose="+flag+",UMGlobalParam.KEY_NULL="+UMGlobalParam.KEY_NULL);
				if(flag==UMGlobalParam.KEY_NULL){
					UMCustomToast.showToast(activity(),getString(R.string.toast_idle_temp_set), Toast.LENGTH_SHORT);
					Log.d(TAG, "setKettleValue,mode="+modelCustem+",temp"+thermometer.getProgress());
					tempCustom=thermometer.getProgress();
					setKettleValue(modelCustem,tempCustom);
				}else if(flag==UMGlobalParam.KEY_BOIL) {
					tempCustom=thermometer.getProgress();
					setKettleValue(modelCustem,tempCustom);
				}else if(flag==UMGlobalParam.KEY_KEEP_WARM&&tempCustom!=thermometer.getProgress()){
					new MLAlertDialog.Builder(activity()).setMessage(getString(R.string.toast_keep_warm_temp_set))
					.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							thermometer.setProgress(UMBluetoothManager.getInstance().getTempSet());
							
						}
					})
					.setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d(TAG, "setKettleValue,mode="+modelCustem+",temp"+thermometer.getProgress());
							tempCustom=thermometer.getProgress();
							setKettleValue(modelCustem,tempCustom);
							
						}
					}).create().show();
				}

			}
		});
		
		UMBluetoothManager.getInstance().setCustomView(thermometer,radioButtonNotBoil,radioButtonBiol,mBoilSetLayout,mBoilModeSwitch);

		if(!UMGlobalParam.getInstance().isSetFirst()){
		       final Dialog dialog1 = new Dialog(activity(), R.style.Dialog_Fullscreen);        
		        dialog1.setContentView(R.layout.um_custom_set_dialog);
		        final ImageView iv1 = (ImageView)dialog1.findViewById(R.id.temp_mode_set_tip);
		        final ImageView iv2 = (ImageView)dialog1.findViewById(R.id.scroll_tip);
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
						UMGlobalParam.getInstance().setSetFirst(true);
					}
				});
		        dialog1.show();
		}


 

	}
	
	
	private void refreshView(){
		if(modelCustem==UMGlobalParam.MODEL_KEEP_WARM_NOT_BOIL){
			radioButtonBiol.setChecked(false);
			radioButtonNotBoil.setChecked(true);
			radioButtonBiol.setTextColor(getResources().getColor(R.color.button_notset_color));
			radioButtonNotBoil.setTextColor(getResources().getColor(R.color.button_set_color));
			mBoilSetLayout.setVisibility(View.INVISIBLE);
		}else{
			radioButtonBiol.setChecked(true);
			radioButtonNotBoil.setChecked(false);
			radioButtonBiol.setTextColor(getResources().getColor(R.color.button_set_color));
			radioButtonNotBoil.setTextColor(getResources().getColor(R.color.button_notset_color));
			mBoilSetLayout.setVisibility(View.VISIBLE);
		}
		radioGroup.clearFocus();
		thermometer.setProgress(tempCustom);
		
//		UMBluetoothManager.getInstance().setOnSetupCallbackListenr(new UMBluetoothManager.onSetupCallbackListenr() {
//			
//			@Override
//			public void onCurrentSetupValue(int mode, int temp) {
//				Log.d(TAG, "onCurrentSetupValue result,mode="+mode+",temp"+temp);
//				tempCustom=temp;
//				modelCustem=mode;
//				
//			}
//		});
	}
	
	/****
	 * 设定水壶的温度和工作模式 参照{@link HeatModel} 
	 */
	public  void setKettleValue( int mode, int temp){
		Log.d(TAG, "setKettleValue,mode="+mode+",temp="+temp);
		UMBluetoothManager.getInstance().onSetup(mode, temp);
	}


}
