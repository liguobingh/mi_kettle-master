/**   
 * Copyright © 2015 All rights reserved.
 * 
 * @Title: BubbleLayout.java 
 * @Prject: BubbleLayout
 * @Package: com.example.bubblelayout 
 * @Description: TODO
 * @author: raot raotao.bj@cabletech.com.cn/719055805@qq.com 
 * @date: 2015年3月2日 下午2:52:08 
 * @version: V1.0   
 */
package com.viomi.kettle.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.viomi.kettle.R;
import com.viomi.kettle.utils.ColorUtil;

/**
 * @ClassName: BubbleLayout
 * @Description:
 */
public class BubbleLayout extends View {

	private List<Bubble> mBubbles = new ArrayList<Bubble>();
	private static final int mBubbleCount=100;
	private Random random = new Random();
	private int width, height;
	private boolean starting = false;
	private boolean isPause = false;
	private int[] mBackColors=new int[5];
	//private  Thread mBubbleThread;
	private Paint paint = new Paint();
	private Paint mBubblePaint = new Paint();
	float[] mHSVColor = new float[3];
	private Timer mTimer;
	private TimerTask mTimerTask;
	private Timer mColorTimer;
	private TimerTask mColorTimerTask;
	ArgbEvaluator mArgbEvaluator=new ArgbEvaluator();

	private Shader shader;
	//初始化y轴移动速度
	private final int mInitSpeedY=1;
	//初始化x轴移动速度
	private final int mInitSpeedX=8;
	//初始化气泡大小
	private final int mInitBubbleSize=27;
	//气泡透明度变化速率
	private final float mMovingScaleSec= (float) 0.99;
	//气泡大小变化速率
	private final float mMovingSizeSec = (float) 0.9999;

	private float density=getResources().getDisplayMetrics().density;
	private final int mMaxHeight=-900;
	private final int mBottomAddHeight=100;
	private int mFinallMovingRad=3;
	private boolean mOffline=false;
	private int mDestineTemp=40;//目标温度
	private int mCurrentTemp=0;//当前温度
	private int mCurrentBubbleCount=0;//当前的泡泡数量

	private float mColorPercent=0;//颜色变化率百分比
	private ColorStep mCurrentStep,mOldStep,mNewStep;//新旧颜色
	private  ColorStep mColorStep1,mColorStep2,mColorStep3,mColorStep4;//四个阶段的颜色

	private boolean isBubblesRun=false;//是否有气泡

//	ValueAnimator mBubbleColorAnimation,mBackgroudcolorAnimation;

	public BubbleLayout(Context context) {
		super(context);
		init();
	}

	public BubbleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BubbleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void  init(){

		mColorStep1=new ColorStep();
		mColorStep1.step=1;
		mColorStep1.backgroudColor=getResources().getColor(R.color.bubble_backgroud_1);
		mColorStep1.bubbleColor=getResources().getColor(R.color.bubble_color_1);

		mColorStep2=new ColorStep();
		mColorStep2.step=2;
		mColorStep2.backgroudColor=getResources().getColor(R.color.bubble_backgroud_2);
		mColorStep2.bubbleColor=getResources().getColor(R.color.bubble_color_2);

		mColorStep3=new ColorStep();
		mColorStep3.step=3;
		mColorStep3.backgroudColor=getResources().getColor(R.color.bubble_backgroud_3);
		mColorStep3.bubbleColor=getResources().getColor(R.color.bubble_color_3);

		mColorStep4=new ColorStep();
		mColorStep4.step=4;
		mColorStep4.backgroudColor=getResources().getColor(R.color.bubble_backgroud_4);
		mColorStep4.bubbleColor=getResources().getColor(R.color.bubble_color_4);

		mCurrentStep=new ColorStep();
		mCurrentStep.step=1;
		mCurrentStep.backgroudColor=getResources().getColor(R.color.bubble_backgroud_1);
		mCurrentStep.bubbleColor=getResources().getColor(R.color.bubble_color_1);

		mOldStep=new ColorStep();
		mOldStep.step=1;
		mOldStep.backgroudColor=getResources().getColor(R.color.bubble_backgroud_1);
		mOldStep.bubbleColor=getResources().getColor(R.color.bubble_color_1);

		mNewStep=new ColorStep();
		mNewStep.step=1;
		mNewStep.backgroudColor=getResources().getColor(R.color.bubble_backgroud_1);
		mNewStep.bubbleColor=getResources().getColor(R.color.bubble_color_1);

		setLayoutBackgroundColor(mCurrentStep.backgroudColor);
		setTemp(30);
		for(int i=0;i<mBubbleCount;i++){
			Bubble bubble = new Bubble();
			int radius=0;
			if(i<30){
				 radius = random.nextInt((int) (density*(3)));
			}else {
				 radius = random.nextInt((int) (density*((i-30)/2+3)));
			}

			bubble.radius=radius;
			bubble.scale=1;
			bubble.speedY= (int) (mInitSpeedY+0.2*mDestineTemp);
			bubble.speedX= (int) (random.nextFloat()-0.5f);
			bubble.x= random.nextFloat()*width;
			bubble.y=random.nextFloat()*100*density+height;
			bubble.alpha= 0;
			bubble.color=mCurrentStep.bubbleColor;
			mBubbles.add(bubble);
		}


		//startTimer();
	}


	public void setTemp(int temp){

		mDestineTemp=temp;
		if(mOffline){
			restoreOfflineColor(temp);
		}else {
			onColorStepProcess(temp);
		}
		mOffline=false;

		if(mDestineTemp!=mCurrentTemp){
			startTimer();
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		isPause = false;
		width = getWidth();
		height = getHeight();

		 shader= new RadialGradient(width/2,height,(float) (height*1.5),mBackColors,
				null,Shader.TileMode.REPEAT);
		
//		Shader shader = new LinearGradient(0, 0, 0, height, new int[] {
//				getResources().getColor(R.color.blue_bright),
//				getResources().getColor(R.color.blue_light),
//				getResources().getColor(R.color.blue_dark) },
//				null, Shader.TileMode.REPEAT);
		paint.setShader(shader);
		canvas.drawRect(0, 0, width, height, paint);
		shader=null;
		refreshView(canvas);
		invalidate();
	}
	@Override
	public void invalidate() {
		super.invalidate();
		isPause = true;
	}

	private void refreshView(Canvas canvas){

		for (int i=0; i<mBubbles.size();i++)
		{
			Bubble bubble=mBubbles.get(i);
			bubble.scale= (float) (bubble.scale*mMovingSizeSec);
			bubble.radius=bubble.radius*bubble.scale;
			if(bubble.radius<1.5)
			{
				bubble.radius= (float) (Math.random()+mFinallMovingRad);
			}
			bubble.speedY+=0.01*bubble.speedY;

			bubble.x+= (random.nextFloat()-0.5)*density*2;
			bubble.alpha= (int) (bubble.alpha*mMovingScaleSec);
			if(bubble.y>=(height+bubble.radius)){//温度修改时，修改未出现的气泡的透明度
				if(i>mCurrentBubbleCount){
					bubble.alpha=0;
					//高于mCurrentBubbleCount的气泡先不出现，控制先后顺序，不会堆一起出来
				}else {
					bubble.alpha= (int) ((int) (random.nextFloat()*255)*0.7);
					bubble.y-=bubble.speedY;
				}
			}else {
				bubble.y-=bubble.speedY;
			}

			if(bubble.y<mMaxHeight)
			{
				resetOneCircle(bubble,i);
			}
			if(bubble.x>width || bubble.x<0)
			{
				resetOneCircle(bubble,i);
			}
			mBubblePaint.setColor(bubble.color);
			mBubblePaint.setAlpha(bubble.alpha);
			if((!mOffline)&&isBubblesRun){
				canvas.drawCircle(bubble.x, bubble.y, bubble.radius, mBubblePaint);
			}

		}

	}



	private void resetOneCircle(Bubble bubble,int index)
	{
		int radius=0;
		if(index<30){
			radius = random.nextInt((int) (density*(3)));
		}else {
			radius = random.nextInt((int) (density*((index-30)/2+3)));
		}
		bubble.radius=radius;
		bubble.scale=1;
		bubble.speedY= (int) (mInitSpeedY+0.2*mDestineTemp);
		bubble.speedX= (int) (random.nextFloat()-0.5f)*radius;
		bubble.x= random.nextFloat()*width;
		bubble.y=random.nextFloat()*100*density+height;
		bubble.alpha= 0;
		bubble.color=mCurrentStep.bubbleColor;
		if(index>=mCurrentBubbleCount){
			bubble.alpha= 0;
		}else {
			bubble.alpha= (int) ((int) (random.nextFloat()*255)*0.7);
		}


	}

//	private void onColorAnimation(final ColorStep oldStep, final ColorStep newStep){
//		stopAnimation();
//
////		Log.e("###################","onColorAnimation=");
//		 mBackgroudcolorAnimation = ValueAnimator.ofObject( new ArgbEvaluator(), oldStep.backgroudColor,newStep.backgroudColor);
//		mBackgroudcolorAnimation.setDuration(5 * 1000);
//		mBackgroudcolorAnimation.setInterpolator(new ExpoEaseInOutInterpolater());
//		mBackgroudcolorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				mCurrentStep.backgroudColor=(Integer) animation.getAnimatedValue();
//				setLayoutBackgroundColor(mCurrentStep.backgroudColor);
//			}
//		});
//		mBackgroudcolorAnimation.addListener(new Animator.AnimatorListener() {
//			@Override
//			public void onAnimationStart(Animator animation) {
//				mOldStep.step=newStep.step;
//				mCurrentStep.step=newStep.step;
//			}
//
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				mOldStep.backgroudColor=newStep.backgroudColor;
//				mCurrentStep.backgroudColor=newStep.backgroudColor;
//				setLayoutBackgroundColor(mCurrentStep.backgroudColor);
//			}
//
//			@Override
//			public void onAnimationCancel(Animator animation) {
//				mOldStep.backgroudColor=mCurrentStep.backgroudColor;
//			}
//			public void onAnimationRepeat(Animator animation) {
//
//			}
//		});
//
//		mBubbleColorAnimation = ValueAnimator.ofObject( new ArgbEvaluator(), oldStep.bubbleColor, newStep.bubbleColor);
//		mBubbleColorAnimation.setDuration(5 * 1000);
//		mBubbleColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				mCurrentStep.bubbleColor=(Integer) animation.getAnimatedValue();
//			}
//		});
//		mBubbleColorAnimation.addListener(new Animator.AnimatorListener() {
//			@Override
//			public void onAnimationStart(Animator animation) {
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				mOldStep.bubbleColor=newStep.bubbleColor;
//				mCurrentStep.bubbleColor=newStep.bubbleColor;
//			}
//
//			@Override
//			public void onAnimationCancel(Animator animation) {
//				mOldStep.bubbleColor=mCurrentStep.bubbleColor;
//			}
//
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//
//			}
//		});
//
//		mBubbleColorAnimation.start();
//		mBackgroudcolorAnimation.start();
//	}

	private  void onColorStepProcess(int temp){
		int step;
		if(temp<=45){
			step=1;
		}else if(temp<=70){
			step=2;
		}else if(temp<=85){
			step=3;
		}else {
			step=4;
		}
		if(mNewStep.step!=step){
			Log.d("BubbleLayout","startColorChange,mNewStep.step="+mNewStep.step+",step="+step);
			mOldStep.step=mCurrentStep.step;
			mOldStep.bubbleColor=mCurrentStep.bubbleColor;
			mOldStep.backgroudColor=mCurrentStep.backgroudColor;
			mNewStep.step=step;
			switch (step){
				case 1:
					mNewStep.step=mColorStep1.step;
					mNewStep.backgroudColor=mColorStep1.backgroudColor;
					mNewStep.bubbleColor=mColorStep1.bubbleColor;
					break;
				case 2:
					mNewStep.step=mColorStep2.step;
					mNewStep.backgroudColor=mColorStep2.backgroudColor;
					mNewStep.bubbleColor=mColorStep2.bubbleColor;
					break;
				case 3:
					mNewStep.step=mColorStep3.step;
					mNewStep.backgroudColor=mColorStep3.backgroudColor;
					mNewStep.bubbleColor=mColorStep3.bubbleColor;
					break;
				case 4:
					mNewStep.step=mColorStep4.step;
					mNewStep.backgroudColor=mColorStep4.backgroudColor;
					mNewStep.bubbleColor=mColorStep4.bubbleColor;
					break;
			}
			startColorChange(mOldStep,mNewStep);
			//onColorAnimation(mOldStep,mNewStep);
		}
	}


	
//	private void setLayoutBackgroundColor1(int value){
//		int minValue1=35;
//        int minValue=30;
//        int maxValue=100;
//        int minColor=200;
//        int maxColor=360;
//        if(value<minValue){
//            value=minValue1;
//        }else if (value>maxValue){
//            value=maxValue;
//        }
//        float per=((float)(maxColor-minColor))/(maxValue-minValue);
//        int color1 = (int) ((value-minValue)*per+minColor)-20;
//		int color2 = (int) ((value-minValue)*per+minColor)-10;
//		int color3 = (int) ((value-minValue)*per+minColor);
//
//        mBackColors[0]=formatColor(color1,(float)0.4,1);
//        mBackColors[1]=formatColor(color2,(float)0.8,1);
//        mBackColors[2]=formatColor(color3,(float)1,1);
//
//		postInvalidate();
//	}

	private void setLayoutBackgroundColor(int color){

	//	int color=getBackgroudColor(value);

		Color.colorToHSV(color,mHSVColor);

		mBackColors[0]=formatColor((int) mHSVColor[0],(float)0.45,1);
		mBackColors[1]=formatColor((int) mHSVColor[0],(float)0.7,1);
		mBackColors[2]=formatColor((int) mHSVColor[0],(float)0.90,1);
		mBackColors[3]=formatColor((int) mHSVColor[0],(float)0.95,1);
		mBackColors[4]=formatColor((int) mHSVColor[0],(float)1,1);

	//	postInvalidate();
	}



	public void setOffline(){
		stopColorTimer();
	//	stopAnimation();
		mOffline=true;
		if(mBackColors!=null){
			mBackColors[0]=getResources().getColor(R.color.offline_color);
			mBackColors[1]=getResources().getColor(R.color.offline_color);
			mBackColors[2]=getResources().getColor(R.color.offline_color);
			mBackColors[3]=getResources().getColor(R.color.offline_color);
			mBackColors[4]=getResources().getColor(R.color.offline_color);
		}

	//	postInvalidate();
	}

	/***
	 * 离线后恢复颜色
	 * @param temp
     */
	private void restoreOfflineColor(int temp){
		if(temp<=45){
			mCurrentStep.step=mColorStep1.step;
			mCurrentStep.bubbleColor=mColorStep1.bubbleColor;;
			mCurrentStep.backgroudColor=mColorStep1.backgroudColor;
		}else if(temp<=70){
			mCurrentStep.step=mColorStep2.step;
			mCurrentStep.bubbleColor=mColorStep2.bubbleColor;;
			mCurrentStep.backgroudColor=mColorStep2.backgroudColor;
		}else if(temp<=85){
			mCurrentStep.step=mColorStep3.step;
			mCurrentStep.bubbleColor=mColorStep3.bubbleColor;;
			mCurrentStep.backgroudColor=mColorStep3.backgroudColor;
		}else {
			mCurrentStep.step=mColorStep4.step;
			mCurrentStep.bubbleColor=mColorStep4.bubbleColor;;
			mCurrentStep.backgroudColor=mColorStep4.backgroudColor;
		}
		mNewStep.step=mCurrentStep.step;
		mNewStep.backgroudColor=mCurrentStep.backgroudColor;
		mNewStep.bubbleColor=mCurrentStep.bubbleColor;
		mOldStep.step=mCurrentStep.step;
		mOldStep.backgroudColor=mCurrentStep.backgroudColor;
		mOldStep.bubbleColor=mCurrentStep.bubbleColor;
		setLayoutBackgroundColor(mCurrentStep.backgroudColor);
	}

//	private void onColorAnimation(){
//		AlphaAnimation am=new AlphaAnimation(0,1);
//		am.setDuration(1000);
//		Canvas canvas=new Canvas();
//		canvas.sta
//	}

//	private int getBubbleColor(int temp){
//		int color;
//		if(temp<45){
//			color=getResources().getColor(R.color.bubble_color_1);
//		}else if(temp<70){
//			float fraction=(temp-(45-1))/(float)(70-(45-1));
//			color= (int) mArgbEvaluator.evaluate(fraction,getResources().getColor(R.color.bubble_color_1),
//					getResources().getColor(R.color.bubble_color_2));
//		}else if(temp<85){
//			float fraction=(temp-(70-1))/(float)(85-(70-1));
//			color= (int) mArgbEvaluator.evaluate(fraction,getResources().getColor(R.color.bubble_color_2),
//					getResources().getColor(R.color.bubble_color_3));
//		}else {
//		//	color=getResources().getColor(R.color.bubble_color_5);
//			float fraction=(temp-(85-1))/(float)(100-(85-1));
//			color= (int) mArgbEvaluator.evaluate(fraction,getResources().getColor(R.color.bubble_color_3),
//					getResources().getColor(R.color.bubble_color_5));
//		}
//
//		return color;
//	}
//
//	private int getBackgroudColor(int temp){
//		int color;
//		if(temp<45){
//			color=getResources().getColor(R.color.bubble_backgroud_1);
//		}else if(temp<70) {
//			float fraction=(temp-(45-1))/(float)(70-(45-1));
//			color= (int) mArgbEvaluator.evaluate(fraction,getResources().getColor(R.color.bubble_backgroud_1),
//					getResources().getColor(R.color.bubble_backgroud_2));
//		}else if(temp<85){
//			float fraction=(temp-(70-1))/(float)(85-(70-1));
//			color= (int) mArgbEvaluator.evaluate(fraction,getResources().getColor(R.color.bubble_backgroud_2),
//					getResources().getColor(R.color.bubble_backgroud_3));
//		}else {
//			float fraction=(temp-(85-1))/(float)(100-(85-1));
//			color= (int) mArgbEvaluator.evaluate(fraction,getResources().getColor(R.color.bubble_backgroud_3),
//					getResources().getColor(R.color.bubble_backgroud_5));
//
//		}
//
//		return color;
//	}

	
    /***
     * 修改背景颜色
     * @param value hsv里的色彩值。
     * hsv有三个成员，hsv[0]的范围是[0,360),表示色彩，hsv[1]范围[0,1]表示饱和度，hsv[2]范围[0,1]表示色度值
     */
    public int formatColor(int value,float saturate,float chroma){
        mHSVColor[0]=value;
        mHSVColor[1]=saturate;
        mHSVColor[2]=chroma;
        int color= Color.HSVToColor(mHSVColor);
        return color;
    }


	public void startTimer(){
		stopTimer();
		mTimer=new Timer();
		mTimerTask=new TimerTask() {
			@Override
			public void run() {
				if(mCurrentTemp<mDestineTemp){
					mCurrentBubbleCount++;
					mCurrentTemp++;
				}else {
					mCurrentTemp=mDestineTemp;
					mCurrentBubbleCount=mDestineTemp;
					stopTimer();
				}
			}
		};
		mTimer.schedule(mTimerTask,50,100);

	}

	public void stopTimer(){
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		if(mTimerTask!=null){
			mTimerTask.cancel();
			mTimerTask=null;
		}
	}


	private void startColorChange(final ColorStep oldStep, final ColorStep newStep){
		stopColorTimer();

		mColorTimer=new Timer();
		mColorTimerTask=new TimerTask() {
			@Override
			public void run() {
				mColorPercent+=0.01;
				//mOldStep.step=newStep.step;
				mCurrentStep.step=newStep.step;
				mCurrentStep.backgroudColor= (int) mArgbEvaluator.evaluate(mColorPercent,oldStep.backgroudColor, newStep.backgroudColor);
				mCurrentStep.bubbleColor= (int) mArgbEvaluator.evaluate(mColorPercent,oldStep.bubbleColor, newStep.bubbleColor);
				if(mColorPercent>=1){
					//mOldStep.step=newStep.step;
//					mOldStep.backgroudColor=newStep.backgroudColor;
//					mOldStep.bubbleColor=newStep.bubbleColor;
					//mCurrentStep.step=newStep.step;
					mCurrentStep.backgroudColor=newStep.backgroudColor;
					mCurrentStep.bubbleColor=newStep.bubbleColor;
					stopColorTimer();
				}
				setLayoutBackgroundColor(mCurrentStep.backgroudColor);
			}
		};
		mColorTimer.schedule(mColorTimerTask,50,50);
		mColorPercent=0;

	}


	public void stopColorTimer(){
		if(mColorTimer!=null){
			mColorTimer.cancel();
			mColorTimer=null;
		}
		if(mColorTimerTask!=null){
			mColorTimerTask.cancel();
			mColorTimerTask=null;
		}
	}
//	private void stopAnimation(){
//		if(mBackgroudcolorAnimation!=null){
//			mBackgroudcolorAnimation.cancel();
//			mBackgroudcolorAnimation=null;
//		}
//		if(mBubbleColorAnimation!=null){
//			mBubbleColorAnimation.cancel();
//			mBubbleColorAnimation=null;
//		}
//	}

	public  void  close(){
		shader=null;
		paint=null;
		random=null;
		mBubblePaint=null;
	//	mBackColors=null;
		mBubbles=null;
		mHSVColor=null;
		mArgbEvaluator=null;
		mColorStep1=null;
		mColorStep2=null;
		mColorStep3=null;
		mColorStep4=null;
		mCurrentStep=null;
		mOldStep=null;
		mNewStep=null;
		//stopAnimation();
		stopTimer();
		stopColorTimer();
	}

	private class Bubble {
		/** 气泡半径 */
		public float radius;

		/** 气泡半径 */
		public float scale;

		/** 上升速度 */
		public float speedY;

		/** 平移速度 */
		public int speedX;

		/** 气泡x坐标 */
		public float x;

		/** 气泡y坐标 */
		public float y;

		/** 气泡透明度 */
		public int alpha;

		/** 气泡颜色 */
		public int color;


	}

	//颜色变化等级
	private class ColorStep{
		int step;
		int bubbleColor;
		int backgroudColor;
	}

	public void setBubblesRun(boolean flag){
		isBubblesRun=flag;
	}
}
