package com.viomi.kettle.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.viomi.kettle.R;
import com.viomi.kettle.utils.log;

import java.util.Locale;

public class UmTimeSeekbarView extends LinearLayout{
	private TextView mDescTextView;
	private SeekBar mSeekBar;
	private float mSetTime, mMinTime,mMaxTime;
	private OnTimeChangeListener mTimeChangeListener;
	private Context mContext;
	
	public UmTimeSeekbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		init(context);

	}

	public UmTimeSeekbarView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		View.inflate(context, R.layout.um_keep_warm_time_set, this);
		mSeekBar=(SeekBar)findViewById(R.id.seekbar);
		mDescTextView=(TextView)findViewById(R.id.desc);
		
		TypedArray array=context.obtainStyledAttributes(R.styleable.time_seekbar);
		mSetTime=array.getFloat(R.styleable.time_seekbar_time, 12);
		mMaxTime=array.getFloat(R.styleable.time_seekbar_maxTime, 12);
		mMinTime=array.getFloat(R.styleable.time_seekbar_minTime, 1);
		array.recycle();
		int leng=((int)(mMaxTime-mMinTime)*10);//总进度设为小时数*10，方便小数点统计
		mSeekBar.setMax(leng);
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBar!=null){
					mSetTime=(seekBar.getProgress()/5*5)/10.0f+mMinTime;
					if(mTimeChangeListener!=null){
						mTimeChangeListener.onTimeChange(mSetTime);
						mDescTextView.setText(formatString(getResources().getString(R.string.desc_keep_warm)
								+ formatFloatStr(mSetTime) + " " + getResources().getString(R.string.util_keep_warm)));
					}
				}
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float mProgress=(progress/5*5)/10.0f+mMinTime;
				mDescTextView.setText(formatString(getResources().getString(R.string.desc_keep_warm)
						+formatFloatStr(mProgress)+ " " + getResources().getString(R.string.util_keep_warm)));
				
			}
		});
		//setTimeFirst(mSetTime);
	}


	public void setTimeFirst(float time){

		if(time<mMinTime){
			time=mMinTime;
		}else if(time>mMaxTime){
			time=mMaxTime;
		}

		mSetTime=time;
		int barProgress=(int) ((time-mMinTime)*10);
		barProgress=barProgress/5*5;
		mSeekBar.setProgress(barProgress);
		mDescTextView.setText(formatString(getResources().getString(R.string.desc_keep_warm)+formatFloatStr(time)
				+ " " + getResources().getString(R.string.util_keep_warm)));


	}


	
	/***
	 * 保温时长设置
	 * @param time
	 */
	public void setTime(float time){

		if(time<mMinTime){
			time=mMinTime;
		}else if(time>mMaxTime){
			time=mMaxTime;
		}
		if(mSetTime!=time){
			mSetTime=time;
			int barProgress=(int) ((time-mMinTime)*10);
			barProgress=barProgress/5*5;
			mSeekBar.setProgress(barProgress);
			mDescTextView.setText(formatString(getResources().getString(R.string.desc_keep_warm)+formatFloatStr(time)
					+ " " + getResources().getString(R.string.util_keep_warm)));
		}

	}

	/***
	 * 浮点数转string,俄罗斯版本特殊处理
	 * @param time
	 * @return
	 */
	private String formatFloatStr( float time){
		Locale locale=mContext.getResources().getConfiguration().locale;
		log.d("####", "language="+locale.getLanguage());
		if(locale==null){
			return (" " +time);
		}
		if("ru".equals(locale.getLanguage())){//俄语小数点是","
			return (" " +time).replace(".",",");
		}else {
			return (" " +time);
		}
	}
	
	
	/***
	 * 滑动修改温度监听
	 * @param listener
	 */
	public void setOnTimeChangeListener(OnTimeChangeListener listener){
		mTimeChangeListener=listener;
	}

	public interface OnTimeChangeListener {
		void onTimeChange(float time);
	}
	
	
	private  String formatString(String str){
		return  str.replace(".0","");
	}

	
	
	public void  close(){
		mTimeChangeListener=null;
	}
	
}
