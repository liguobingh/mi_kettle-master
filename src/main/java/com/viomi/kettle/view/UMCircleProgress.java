package com.viomi.kettle.view;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.UMGlobalParam.KeyChoose;
import com.viomi.kettle.UMGlobalParam.Status;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.provider.Settings.Global;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ScrollView;

public class UMCircleProgress extends ScrollView{
	private static final String TAG="UMCircleProgress";
	private Paint mPaintCircle,mPaintText1,mPaintText2,mPaintText3;;
	private float mR;//环半径
	private float annulusWidth=14;//环的厚度
	private float mXCenter;//圆心X坐标
	private float mYCenter;//圆心Y坐标
	private float padding=10;//圆形到边间隔
	private float statusTextSize ;//状态字体大小
	private float detailTextSize=14;//详情字体大小
	private float tempTextSize =80;//当前温度字体大小
	private int backColor;//画笔颜色，圆环底色
	private float density;
	private float mAngle = 40;//转过角度
	private float progress=30;//当前进度
	private int max=100;//最大温度
	private RectF mRectF;
 //	Typeface typeface;
 	
 	private final static int BOIL_TEMP=98;//沸腾温度

	private Context context;
	private Timer timer;
	private TimerTask timerTask;//进度变动计时器
	private static int frameNum=24;//滑动帧数
	private static long timeInterval = 1000/frameNum;//每帧间隔，单位ms
	private float progressUnit;//每帧转到的幅度
	private float progressEnd;//目标进度
	
	private @Status int status;//状态
	private boolean online;//在线标志
	private boolean isCompleted;//是否已完成
	private int mSetTemp;//自定义温度
	private int mSetMode;//自定义保温模式
	private @KeyChoose int mKeyChoose=UMGlobalParam.KEY_NULL;//按键按下
	
	public UMCircleProgress(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	
	public UMCircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UMCircleProgress(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		this.context=context;
	//	typeface=UMGlobalParam.getInstance().getTextTypeface();
		backColor=getResources().getColor(R.color.circle_color);
		DisplayMetrics metrics=new DisplayMetrics();
		metrics=context.getResources().getDisplayMetrics();
		density=metrics.density;
		
		padding*=density;
		annulusWidth*=density;
		tempTextSize*=density;
		statusTextSize=getResources().getDimensionPixelSize(R.dimen.title_text_size);
		detailTextSize*=density;
		
		mRectF=new RectF();
		
		mPaintCircle=new Paint();
		mPaintText1=new Paint();
		mPaintText2=new Paint();
		mPaintText3=new Paint();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int viewWidth=MeasureSpec.getSize(widthMeasureSpec);
		int viewHeight=MeasureSpec.getSize(heightMeasureSpec);
		mR=(viewHeight-annulusWidth-padding*2)/2;
		mXCenter=viewWidth/2;
		mYCenter=viewHeight/2;
		

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mPaintCircle.setStyle(Style.STROKE);
		mPaintCircle.setStrokeWidth(annulusWidth);
		mPaintCircle.setColor(backColor);
		mPaintCircle.setAntiAlias(true); // 消除锯齿

		mPaintText1.setTextSize(tempTextSize);
		mPaintText1.setTypeface(Typeface.MONOSPACE);
		mPaintText1.setColor(Color.WHITE);
		mPaintText1.setAntiAlias(true); // 消除锯齿
	//	mPaintText1.setTypeface(typeface);
		
		mPaintText2.setTextSize(statusTextSize);
		mPaintText2.setColor(Color.WHITE);
		mPaintText2.setAntiAlias(true); // 消除锯齿
	//	mPaintText2.setTypeface(typeface);
		
		mPaintText3.setTextSize(detailTextSize);
		mPaintText3.setColor(getResources().getColor(R.color.text_color1));
		mPaintText3.setAntiAlias(true); // 消除锯齿
		mPaintText3.setTextAlign(Align.CENTER);
	//	mPaintText3.setTypeface(typeface);
		
		int mProgress=Math.round(progress);
		
		//画外环
		canvas.drawCircle(mXCenter, mYCenter, mR, mPaintCircle);
		
		//画扇形进度
		if(online){
			if(mProgress<BOIL_TEMP){
				mPaintCircle.setColor(Color.WHITE);
			}else {
			//	mPaintCircle.setColor(getResources().getColor(R.color.darkorange));
				mPaintCircle.setColor(Color.WHITE);
				mPaintText1.setColor(Color.WHITE);
			}
		}else {
			//mPaintCircle.setColor(getResources().getColor(R.color.darkgray));
			mPaintText1.setColor(Color.WHITE);
		}
		mRectF.set(mXCenter-mR, mYCenter-mR, mXCenter+mR, mYCenter+mR);
		mAngle=progress*360.0f/100.0f;
		canvas.drawArc(mRectF, -270, mAngle, false, mPaintCircle);
		
		//画进度数值
		String textShow="";
		float tempSize;
		if(online){
			if(mProgress<BOIL_TEMP||status!=UMGlobalParam.STATUS_HEATING){
				textShow=mProgress+"";
				mPaintText1.setColor(Color.WHITE);
				tempSize=tempTextSize;
				mPaintText1.setTextSize(tempTextSize);
			}else {
				textShow=context.getString(R.string.um_desc_boiling);
				mPaintText1.setColor(Color.WHITE);
				tempSize=50*density;;
			}
		}else {
			textShow=context.getString(R.string.um_desc_offline);
			//mPaintText1.setColor(getResources().getColor(R.color.darkgray));
			mPaintText1.setColor(Color.WHITE);
			tempSize=40*density;;
		}
		mPaintText1.setTextSize(tempSize);
		mPaintText1.setTextAlign(Align.CENTER);
		drawText(canvas, mPaintText1, textShow, mXCenter, mYCenter-statusTextSize/2);
		FontMetrics fontMetrics1= mPaintText1.getFontMetrics();
	//	FontMetrics fontMetrics2= mPaintText2.getFontMetrics();
		float textWidth=mPaintText1.measureText(textShow);
		float textHeight1=fontMetrics1.bottom-fontMetrics1.top;
		//float textHeight2=fontMetrics2.bottom-fontMetrics2.top;
		if(online&&(mProgress<BOIL_TEMP||status!=UMGlobalParam.STATUS_HEATING)){
			mPaintText2.setTextAlign(Align.LEFT);
			mPaintText2.setTextSize(statusTextSize);
			drawText(canvas, mPaintText2, "℃", mXCenter+textWidth/2, mYCenter-tempSize/2+statusTextSize-statusTextSize/2);
		}
	
		//绘制状态
		String statusStr=getStatusString();
		mPaintText2.setTextAlign(Align.CENTER);
		mPaintText2.setTextSize(statusTextSize);

		drawText(canvas, mPaintText2, statusStr, mXCenter, mYCenter+textHeight1/2-statusTextSize/2+8*density);
	
		
		float textButtomY = mYCenter+textHeight1/2-statusTextSize/2+30*density;
		String textStr=getModeString();
		drawText(canvas, mPaintText3, textStr, mXCenter, textButtomY);

		
	}
	
	//写字,Y坐标居中，text：字体内容；size:字体大小；startX,最左边X坐标；midY:最底下Y坐标
	private void drawText(Canvas canvas,Paint paint,String text ,float startX,float midY){
	//	paint.setTextSize(size);
		float size=paint.getTextSize();
		FontMetrics fontMetrics= paint.getFontMetrics();
		float bottomY=midY+size/2;
		float topY=midY-size/2;
		float textBaseY = topY + (bottomY - topY) /2 - (fontMetrics.bottom - fontMetrics.top) /2 - fontMetrics.top;
		canvas.drawText(text, startX,textBaseY, paint);
	
	}
	
	//设置自定义温度
	public void setTemp(int temp){
		if(this.mSetTemp!=temp){
			this.mSetTemp=temp;
			postInvalidate();
			Log.e("setTemp",":"+temp);
		}
	}
	
	public int  getTemp(){
		return mSetTemp;
	}
	
	//设置自定义保温模式
	public void setKeyChoose(int keyCode){
		if(this.mKeyChoose!=keyCode){
			this.mKeyChoose=keyCode;
			postInvalidate();
			Log.e("mKeyChoose",":"+keyCode);
		}
	}
	
	public int  getKeyChoose(){
		return mKeyChoose;
	}
	
	
	//设置按键按下
	public void setMode(int mode){
		if(this.mSetMode!=mode){
			this.mSetMode=mode;
			postInvalidate();
			Log.e("setMode",":"+mode);
		}
	}
	
	public int  getMode(){
		return mSetMode;
	}
	
	
	//获取在线状态
	public boolean isOnline(){
		return online;
	}
	
	//设置在线状态
	public void setOnline(boolean online){
		if(this.online!=online){
			this.online=online;
			postInvalidate();
			Log.e("setOnline",":"+online);
		}
	}
	
	//获取状态
	public @Status int getStatus(){
		return status;
	}
	
	//设置状态
	public void setStatus(@Status int mStatus){
		Log.d(TAG,"setStatus="+mStatus+"\n");
		
		if(this.status!=mStatus){
			this.status=mStatus;
			postInvalidate();
			Log.e("set status",":"+status);
		}
		
	}

	//获取是否已完成
	public boolean isCompleted(){
		return isCompleted;
	}
	
	//设置是否已完成
	public void setIsCompleted( boolean isCompleted){
		Log.d(TAG,"setIsCompleted="+isCompleted+"\n");
		
		if(this.isCompleted!=isCompleted){
			this.isCompleted=isCompleted;
			postInvalidate();
			Log.e("setIsCompleted",":"+isCompleted);
		}
		
	}
	
	//状态显示
	private String getStatusString(){
		String statusStr="";

		if(isOnline()){
			if(status==UMGlobalParam.STATUS_IDLE){
				statusStr=context.getString(R.string.um_status_close);
			}else if(status==UMGlobalParam.STATUS_HEATING){
				statusStr=context.getString(R.string.um_status_heating);
			}
			else if(status==UMGlobalParam.STATUS_KEEP_WARM_NOT__BOIL){
				statusStr=context.getString(R.string.um_status_keep_warm);
			}else if(status==UMGlobalParam.STATUS_KEEP_WARM_BOIL){
				if(isCompleted){
					statusStr=context.getString(R.string.um_status_keep_warm);
				}else {
					statusStr=context.getString(R.string.um_status_keep_warm_temp_down);
				}
			}else{
				statusStr=context.getString(R.string.um_status_abnormal);
			}
		}

		
		return statusStr;
	}
	
	//模式设置显示
	private String getModeString(){
		String displayStr="";
		if(isOnline()){
//			if(isCompleted){
//				displayStr+=context.getString(R.string.um_had_complete);
//			}else {
				if(status!=UMGlobalParam.STATUS_IDLE) {
					if(mKeyChoose!=UMGlobalParam.KEY_BOIL){
						if(mSetMode==UMGlobalParam.MODEL_KEEP_WARM_NOT_BOIL){
							displayStr=context.getString(R.string.um_desc_keep_warm_not_boil);
							displayStr+=mSetTemp+context.getString(R.string.um_keep_warm_not_boil_string2);
						}else if(mSetMode==UMGlobalParam.MODEL_KEEP_WARM_BOIL){
							displayStr=context.getString(R.string.um_desc_keep_warm_boil);
							displayStr+=mSetTemp+"℃";
						}else {
							displayStr=context.getString(R.string.um_boiling_string);
						}
					}else {
						displayStr=context.getString(R.string.um_boiling_string);
					}
				}
			//}
		}

		return displayStr;
	}
	
	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized float getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
	 * 刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		Log.d(TAG,"progress="+progress+"\n");
		if(progress < 0){
			throw new IllegalArgumentException("progress not less than 0");
		}
		float proTemp=this.progress;
		if(this.progress != progress){
			if(progress > max){
				progress = max;
			}
			//this.progress = progress;
			progressInvalidate(proTemp,progress);
			//Log.d(TAG,"set progress:"+progress+",mAngle:"+mAngle);
		}
	}
	private void progressInvalidate(float oldProgress,float newProgress){
		stopTimer();
		timer=new Timer();
		timerTask=new TimerTask() {
			
			@Override
			public void run() {
				progress+=progressUnit;
				if(progressUnit<0){
					if(progress<=progressEnd){
						progress=progressEnd;
						stopTimer();
					}
				}else {
					if(progress>=progressEnd){
						progress=progressEnd;
						stopTimer();
					}
				}

				postInvalidate();
			}
		};
		progressEnd=newProgress;
		progressUnit=(newProgress-oldProgress)/frameNum;
		timer.schedule(timerTask, timeInterval,timeInterval);
	}


	
	public void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		if(timerTask!=null){
			timerTask.cancel();
			timerTask=null;
		}
	}
}
