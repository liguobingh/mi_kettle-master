package com.viomi.kettle.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.viomi.kettle.R;

public class UMModelSetView extends LinearLayout{
	private RadioGroup mRadioGroup;
	private RadioButton mRadioButtonBoil,mRadioButtonNotBoil;
	private UMSwitchButton mSwitchButton;
	private RelativeLayout mLayout;
	private boolean mBoilModel;
	private boolean mRepeatBoilPrevent;
	private boolean mModelChange=false;//点击模式改变
	private OnModelChangeListener mModelChangeListener;
	
	public UMModelSetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UMModelSetView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		View.inflate(context, R.layout.um_boil_mode_layout, this);
		mRadioButtonBoil=(RadioButton) findViewById(R.id.boil);
		mRadioButtonNotBoil=(RadioButton) findViewById(R.id.notBoil);	
		mRadioGroup=(RadioGroup) findViewById(R.id.radiogroup);
		mLayout=(RelativeLayout) findViewById(R.id.boil_model_set);
		mSwitchButton=(UMSwitchButton) findViewById(R.id.mode_switch);
		TypedArray array=context.obtainStyledAttributes(R.styleable.boil_model_set);
		mBoilModel=array.getBoolean(R.styleable.boil_model_set_isBoil, true);
		mRepeatBoilPrevent=array.getBoolean(R.styleable.boil_model_set_repeatBoilPrevent, true);
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				mModelChange=true;	
			}
		});
		
		mRadioButtonBoil.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mModelChange){
					mModelChange=false;
					if(mModelChangeListener!=null){
						mModelChangeListener.onBoilModelChange(true);
					}
					mLayout.setVisibility(VISIBLE);

				}
				
			}
		});
		
		mRadioButtonNotBoil.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mModelChange){
					mModelChange=false;
					if(mModelChangeListener!=null){
						mModelChangeListener.onBoilModelChange(false);
					}

					mLayout.setVisibility(GONE);
					
				}
				
			}
		});
		
		mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(mModelChangeListener!=null){
						mModelChangeListener.onRepeatBoilPrevent(isChecked);
					}

			}
		});
	//	setModelFirst(mBoilModel, mRepeatBoilPrevent);
	}

	public void setModelFirst(boolean isBoil,boolean repeatBoilPrevent){
		mBoilModel=isBoil;
		mRepeatBoilPrevent=repeatBoilPrevent;
		mRadioButtonBoil.setChecked(isBoil);
		mRadioButtonNotBoil.setChecked(!isBoil);
		mSwitchButton.setChecked(repeatBoilPrevent);
		if(isBoil){
			mLayout.setVisibility(VISIBLE);
		}else {
			mLayout.setVisibility(GONE);
		}
	}

	public void setModel(boolean isBoil){

		if(mBoilModel!=isBoil){
			mBoilModel=isBoil;
			mRadioButtonBoil.setChecked(isBoil);
			mRadioButtonNotBoil.setChecked(!isBoil);
			if(isBoil){
				mLayout.setVisibility(VISIBLE);
			}else {
				mLayout.setVisibility(GONE);
			}
		}
	}

	public void setBoilModeSelect(boolean repeatBoilPrevent){

		if(mRepeatBoilPrevent!=repeatBoilPrevent){
			mRepeatBoilPrevent=repeatBoilPrevent;
			mSwitchButton.setChecked(repeatBoilPrevent);
		}
	}


	public void setOnModelChangeListener(OnModelChangeListener listener){
		mModelChangeListener=listener;
	}

	public interface OnModelChangeListener {
		void onBoilModelChange(boolean isBoil);
		void onRepeatBoilPrevent(boolean flag);
	
	}

	public void close(){
		mModelChangeListener=null;
	}
}
