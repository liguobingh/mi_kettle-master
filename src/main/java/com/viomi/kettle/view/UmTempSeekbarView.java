package com.viomi.kettle.view;



import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.viomi.kettle.R;
import com.viomi.kettle.data.KettleScene;
import com.viomi.kettle.dev.UMSceneManager;

import java.util.ArrayList;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class UmTempSeekbarView extends LinearLayout{
	private TextView mDescTextView;
	private SeekBar mSeekBar;
	private RadioGroup mRadioGroup;
	private RadioButton mRadioButton0,mRadioButton1,mRadioButton2;
	private RadioButton mRadioButtonCustom;
	private LinearLayout mMoreLayout;
	private int mSetTemp, mMinTemp,mMaxTemp;
	private ArrayList<KettleScene> mSceneList;
	private OnTempChangeListener mTempChangeListener;
	private boolean mSceneChange=false;//点击模式改变
	private  int mProgressTemp=40;//温度条温度，防治跳动
	
	public UmTempSeekbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public UmTempSeekbarView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		View.inflate(context, R.layout.um_keep_warm_temp_select, this);
		mSeekBar=(SeekBar)findViewById(R.id.seekbar);
		mDescTextView=(TextView)findViewById(R.id.desc);
		mRadioGroup=(RadioGroup)findViewById(R.id.scene_radiogroup);
		mRadioButton0=(RadioButton) findViewById(R.id.custom_scene0);
		mRadioButton1=(RadioButton) findViewById(R.id.custom_scene1);
		mRadioButton2=(RadioButton) findViewById(R.id.custom_scene2);
		mRadioButtonCustom=(RadioButton) findViewById(R.id.scene_more);
		mMoreLayout= (LinearLayout) findViewById(R.id.more_layout);

		TypedArray array=context.obtainStyledAttributes(R.styleable.temp_seekbar);
		mSetTemp=array.getInt(R.styleable.temp_seekbar_temp, 40);
		mMinTemp=array.getInt(R.styleable.temp_seekbar_minTemp, 40);
		mMaxTemp=array.getInt(R.styleable.temp_seekbar_maxTemp, 90);
		array.recycle();
		mSeekBar.setMax(mMaxTemp-mMinTemp);
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBar!=null){
					mProgressTemp=seekBar.getProgress()+mMinTemp;
					mSetTemp=formatTemp(mProgressTemp);
					if(mTempChangeListener!=null){
						mTempChangeListener.onTempChange(mSetTemp);
					}
					mDescTextView.setText(mSetTemp+" "+"℃");
					mDescTextView.append(" "+getSceneChoose(mSetTemp));
					SetSceneChooseButton(mSetTemp);
				}
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				int mProgress=formatTemp(progress+mMinTemp);
				mDescTextView.setText(mProgress+" "+"℃");
				mDescTextView.append(" "+getSceneChoose(mProgress));
				SetSceneChooseButton(mProgress);
				
			}
		});
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				mSceneChange=true;	
			}
		});
		
		mRadioButton0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mSceneChange){
					mSceneChange=false;
					mSetTemp=mSceneList.get(0).Value;
					if(mTempChangeListener!=null){
						mTempChangeListener.onTempChange(mSetTemp);
					}
					setBarProgress(mSetTemp-mMinTemp,mProgressTemp-mMinTemp);
					mDescTextView.setText(mSetTemp+" "+"℃");
					mDescTextView.append(" "+getSceneChoose(mSetTemp));
				}
				
			}
		});
		mRadioButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mSceneChange){
					mSceneChange=false;
					mSetTemp=mSceneList.get(1).Value;
					if(mTempChangeListener!=null){
						mTempChangeListener.onTempChange(mSetTemp);
					}
					setBarProgress(mSetTemp-mMinTemp,mProgressTemp-mMinTemp);
					mDescTextView.setText(mSetTemp+" "+"℃");
					mDescTextView.append(" "+getSceneChoose(mSetTemp));
				}
				
			}
		});
		mRadioButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(mSceneChange){
					mSceneChange=false;
					mSetTemp=mSceneList.get(2).Value;
					if(mTempChangeListener!=null){
						mTempChangeListener.onTempChange(mSetTemp);
					}

					setBarProgress(mSetTemp-mMinTemp,mProgressTemp-mMinTemp);
					mDescTextView.setText(mSetTemp+" "+"℃");
					mDescTextView.append(" "+getSceneChoose(mSetTemp));
				}
				
			}
		});
		mMoreLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mTempChangeListener!=null){
					mTempChangeListener.onMoreLayoutClick();
				}
			}
		});

		mRadioButtonCustom.post(new Runnable() {
			@Override
			public void run() {
			//	Log.e("2222222","行数："+mRadioButtonCustom.getLineCount());
				if(mRadioButtonCustom.getLineCount()>=2){
					mRadioButtonCustom.setTextSize(COMPLEX_UNIT_SP,10);
				}
			}
		});
		//setTempFirst(mSetTemp);
	}
	
	/***
	 * 具体场景显示
	 */
	private String getSceneChoose(int temp){
		String sceneStr="";
		if(temp==mSceneList.get(0).Value){
			sceneStr=mSceneList.get(0).Desc;
			
		}else if(temp==mSceneList.get(1).Value){
			sceneStr=mSceneList.get(1).Desc;
		}else if(temp==mSceneList.get(2).Value){
			sceneStr=mSceneList.get(2).Desc;
		}
		return sceneStr;
	}
	
	private void SetSceneChooseButton(int temp){

		mRadioButton0.setChecked(true);
		mRadioButton1.setChecked(true);
		mRadioButton2.setChecked(true);
		if(temp==mSceneList.get(0).Value){
			mRadioButton0.setChecked(true);
			mRadioButton1.setChecked(false);
			mRadioButton2.setChecked(false);
			
		}else if(temp==mSceneList.get(1).Value){
			mRadioButton0.setChecked(false);
			mRadioButton1.setChecked(true);
			mRadioButton2.setChecked(false);
		}else if(temp==mSceneList.get(2).Value){

			mRadioButton0.setChecked(false);
			mRadioButton1.setChecked(false);
			mRadioButton2.setChecked(true);
		}else {
			mRadioButton0.setChecked(false);
			mRadioButton1.setChecked(false);
			mRadioButton2.setChecked(false);
		}
	}


	public void setTempFirst(int temp){

		if(temp<mMinTemp){
			temp=mMinTemp;
		}else if(temp>mMaxTemp){
			temp=mMaxTemp;
		}

			mSetTemp=formatTemp(temp);
			setBarProgress(mSetTemp-mMinTemp,mProgressTemp-mMinTemp);
			setSceneView(mSetTemp);

	}

	/***
	 * 保温温度设置
	 * @param temp
	 */
	public void setTemp(int temp){

		if(temp<mMinTemp){
			temp=mMinTemp;
		}else if(temp>mMaxTemp){
			temp=mMaxTemp;
		}

		if(mSetTemp!=formatTemp(temp)){
			mSetTemp=formatTemp(temp);
			setBarProgress(mSetTemp-mMinTemp,mProgressTemp-mMinTemp);
			setSceneView(mSetTemp);
		}

	}
	
	
	/***
	 * 滑动修改温度监听
	 * @param listener
	 */
	public void setOnTempChangeListener(OnTempChangeListener listener){
		mTempChangeListener=listener;
	}

	public interface OnTempChangeListener {
		void onTempChange(int temp);
		void onMoreLayoutClick();
	}

	public void  setSceneView(int temp){
		mSceneList=getScenesChoose();
		mRadioButton0.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(0).ChooseDrawable),null,null);
		mRadioButton0.setText(mSceneList.get(0).Value+" ℃");
		mRadioButton1.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(1).ChooseDrawable),null,null);
		mRadioButton1.setText(mSceneList.get(1).Value+" ℃");
		mRadioButton2.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(2).ChooseDrawable),null,null);
		mRadioButton2.setText(mSceneList.get(2).Value+" ℃");
		SetSceneChooseButton(temp);
		mDescTextView.setText(temp+" "+"℃");
    	mDescTextView.append(" "+getSceneChoose(temp));
	}

	/***
	 * 获取常用的场景
	 * @return
     */
	private ArrayList<KettleScene> getScenesChoose(){
		return UMSceneManager.getInstance().getChooseScene();
	}

	
	private int formatTemp(int mRealTemp){
		return mRealTemp/5*5;
	}

	private void setBarProgress(int setTemp,int pressTemp){
		if(setTemp!=formatTemp(pressTemp)){
			mSeekBar.setProgress(setTemp);
		}
	}

	public void  close(){
		mTempChangeListener=null;
	}

	
}
