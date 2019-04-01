package com.viomi.kettle.view;

import java.util.HashMap;
import java.util.Map.Entry;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class UMThermometer extends View implements OnGestureListener{
	
	private GestureDetector detector;

	private Paint mPaint1,mPaint2,textPaint;
	private float density;
	private int cylinderWidth=20;//内圆柱宽度
	private int cylindderHeight;//内圆柱高度
	private int borderWidth=1;//外框线条宽度,不能小于2
	private int gapWidth=2;//间隙宽度
	
    private int mCenterX;//X轴中心线坐标
    private int height;//整个width的宽高
    private int triangleWidth=16;//游标三角形边长
	private int backColor;//画笔颜色，圆柱底色
	private RectF mRectF;
	
	private float progress=35;//当前进度
	private int max=100;//最大刻度
	private int min=30;//最小刻度
	private float[] scaleYs;//每个刻度Y坐标
	private float scaleTextSize ;//刻度数字大小
	private float modelTextSize ;//模式字体大小
	private float tempTextSize =40;//当前温度字体大小
	private HashMap<String, Integer> modelMap;
 //	Typeface typeface;
	
	private float currentY;//当前刻度Y轴坐标
	private float modelRightX;//模式名称最右边X坐标
	private onProgressListener mOnProgressListener;//监听放手动作

	public UMThermometer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public UMThermometer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UMThermometer(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
	//	typeface= UMGlobalParam.getInstance().getTextTypeface();
		detector=new GestureDetector(this);
		
		backColor=getResources().getColor(R.color.text_color);
		DisplayMetrics metrics=new DisplayMetrics();
		metrics=context.getResources().getDisplayMetrics();
		density=metrics.density;
		
		cylinderWidth*=density;
		borderWidth*=density;
		gapWidth*=density;
		triangleWidth*=density;
		
		mRectF=new RectF();
		mPaint1=new Paint();
		mPaint2=new Paint();
		textPaint=new Paint();
		
		scaleTextSize=getResources().getDimensionPixelSize(R.dimen.min_text_size);
		modelTextSize=getResources().getDimensionPixelSize(R.dimen.mid_text_size);
		tempTextSize*=density;
		
		modelMap=new HashMap<String, Integer>();
		modelMap.put("益生菌冲剂", 40);
	//	modelMap.put("蜜水", 40);
		modelMap.put("奶粉", 50);
		modelMap.put("婴儿米粉", 70);
		modelMap.put("白茶", 80);
		modelMap.put("咖啡", 90);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		getWidth();
		height=getHeight();
		mCenterX=getWidth()/2;
		cylindderHeight=height-borderWidth*2-gapWidth*2;

		mPaint1.setDither(true);
		mPaint1.setAntiAlias(true);
		mPaint1.setStyle(Paint.Style.STROKE);
		mPaint1.setStrokeJoin(Paint.Join.ROUND);
		mPaint1.setStrokeCap(Paint.Cap.ROUND);
		mPaint1.setStrokeWidth(borderWidth);
		mPaint1.setColor(backColor);
		
		mPaint2.setDither(true);
		mPaint2.setAntiAlias(true);
		mPaint2.setStyle(Paint.Style.FILL);
		mPaint2.setStrokeJoin(Paint.Join.ROUND);
		mPaint2.setStrokeCap(Paint.Cap.ROUND);
		mPaint2.setStrokeWidth(cylinderWidth);
		mPaint2.setColor(backColor);
		
	    textPaint.setColor(backColor);
	  //  textPaint.setTypeface(typeface);
	    textPaint.setAntiAlias(true); // 消除锯齿
		//外框绘制
		int mRBorder=(cylinderWidth+gapWidth*2)/2;//圆柱底部圆形半径

		mRectF.set(mCenterX-mRBorder-borderWidth/2.0f,borderWidth/2.0f,mCenterX+mRBorder+borderWidth/2.0f,(mRBorder+borderWidth/2.0f)*2);
		canvas.drawArc(mRectF, -180, 180, false, mPaint1);//画上半圆
		mRectF.set(mCenterX-mRBorder-borderWidth/2.0f,height-(mRBorder+borderWidth/2.0f)*2,mCenterX+mRBorder+borderWidth/2.0f,height-borderWidth/2.0f);
		canvas.drawArc(mRectF, 0, 180, false, mPaint1);//画下半圆
		canvas.drawLine(mCenterX-mRBorder-borderWidth/2.0f, mRBorder+borderWidth, mCenterX-mRBorder-borderWidth/2.0f, height-(mRBorder+borderWidth), mPaint1);//画左边直线
		canvas.drawLine(mCenterX+mRBorder+borderWidth/2.0f, mRBorder+borderWidth, mCenterX+mRBorder+borderWidth/2.0f, height-(mRBorder+borderWidth), mPaint1);//画右边直线
	
		//圆柱绘制
		int mR=cylinderWidth/2;//圆柱底部圆形半径
		mRectF.set(mCenterX-mR,borderWidth+gapWidth,mCenterX+mR,mR*2+borderWidth+gapWidth);
		canvas.drawArc(mRectF, -180, 180, false, mPaint2);//画上半圆
		
		float aboveHeight=cylindderHeight*((float)(max-progress)/(float)(max-min))-mR;//画上半部分圆柱
		canvas.drawRect(mCenterX-mR, mR+borderWidth+gapWidth, mCenterX+mR, mR+borderWidth+gapWidth+aboveHeight, mPaint2);
		
		mPaint2.setColor(Color.WHITE);
		mRectF.set(mCenterX-mR,height-(mR*2+borderWidth+gapWidth),mCenterX+mR,height-borderWidth-gapWidth);
		canvas.drawArc(mRectF, 0, 180, false, mPaint2);//画下半圆
		
		float belowHeight=cylindderHeight*((float)(progress-min)/(float)(max-min))-mR;//画下半部分圆柱
		canvas.drawRect(mCenterX-mR, height-gapWidth-borderWidth-mR-belowHeight,mCenterX+mR,height-gapWidth-borderWidth-mR, mPaint2);
		

		//绘制刻度
		scaleYs=new float[max-min];
		int bottom=height-gapWidth-borderWidth;
		float eachLen=cylindderHeight/(float)((max-min)*1.0f);
		for(int i=0;i<max-min;i++){
			if(i==0){
				scaleYs[0]=bottom;
				continue;
			}
			scaleYs[i]=bottom-eachLen*i;
			if(i%10==0){
				canvas.drawLine(mCenterX-mRBorder-8*density-borderWidth, bottom-eachLen*i, mCenterX-mRBorder-borderWidth, bottom-eachLen*i, mPaint1);
			}else if(i%5==0){
				canvas.drawLine(mCenterX-mRBorder-4*density-borderWidth, bottom-eachLen*i, mCenterX-mRBorder-borderWidth, bottom-eachLen*i, mPaint1);
			}
		}
		
	//	textPaint.setStrokeWidth(0);
		float rightX=mCenterX-mRBorder-10*density-borderWidth-4*density;
		modelRightX=rightX;
		drawTextBaseRight(canvas,textPaint,"℃",scaleTextSize,rightX,scaleYs[4]);
		textPaint.setColor(getResources().getColor(R.color.text_color1));
		for(int i=10;i<max-min;i+=10){
			drawTextBaseRight(canvas,textPaint,""+(i+min),scaleTextSize,rightX,scaleYs[i-1]);	
		}
	
		//绘制模式
		textPaint.setColor(backColor);
		rightX-=textPaint.measureText("00")+10*density;
		textPaint.setTextSize(modelTextSize);
		for(Entry<String, Integer> entry:modelMap.entrySet()){
			int value=entry.getValue();
			String key=entry.getKey();

			if(value==getDealedPeogress((int)progress))
			{
				textPaint.setColor(Color.WHITE);
			}else{
				textPaint.setColor(backColor);
			}
			drawTextBaseRight(canvas,textPaint,key,modelTextSize,rightX,scaleYs[value-min-1]);
		}
		
		float cyTop=gapWidth+borderWidth;//进度条顶部
		float cyBottom=height-gapWidth-borderWidth;//进度条底部
		currentY=cyBottom-(progress-min)*(cyBottom-cyTop)/(max-min);
		
		//绘制游标三角行
		mPaint2.setColor(getResources().getColor(R.color.nonius_color));
		float currentX=mCenterX+cylinderWidth/2+gapWidth+borderWidth;
		canvas.drawPath(getTrianglePath(currentX,currentY), mPaint2);
		
		//绘制当前温度值
		mPaint2.setColor(Color.WHITE);
		currentX+=triangleWidth*Math.sin(60 * Math.PI / 180)+5*density;
		drawText(canvas, mPaint2, ""+getDealedPeogress((int)progress), tempTextSize, currentX, currentY);
		int unitSize=getResources().getDimensionPixelSize((R.dimen.mid_text_size));//单位字体大小
		currentX+=tempTextSize+5 *density;
		float uintY=currentY-tempTextSize/2+unitSize;
		drawText(canvas, mPaint2, "℃", unitSize, currentX, uintY);
		

	}
	
	//写字,Y坐标居中，text：字体内容；size:字体大小；startX,最左边X坐标；midY:最底下Y坐标
	private void drawText(Canvas canvas,Paint paint,String text ,float size,float startX,float midY){
		paint.setTextSize(size);
		FontMetrics fontMetrics= paint.getFontMetrics();
		float bottomY=midY+size/2;
		float topY=midY-size/2;
		float textBaseY = topY + (bottomY - topY) /2 - (fontMetrics.bottom - fontMetrics.top) /2 - fontMetrics.top;
		canvas.drawText(text, startX,textBaseY, paint);
	}
	
	//以最右边为基准写字，text：字体内容；size:字体大小；stopX,最右边X坐标；stopY:最底下Y坐标
	private void drawTextBaseRight(Canvas canvas,Paint paint,String text ,float size,float stopX,float stopY){
		paint.setTextSize(size);
		float textSize=paint.measureText(text);
		float startX=stopX-textSize;
		canvas.drawText(text, startX, stopY, paint);
		
	}
	
	
	//获取等边三角形Path,point:左边顶点
	private Path getTrianglePath(float pointX,float pointY){
		double angle=60;
		Path path=new Path();
		path.moveTo(pointX, pointY);
		float triHeight= (float)(triangleWidth*Math.sin(angle * Math.PI / 180));
		path.lineTo(pointX+triHeight, pointY+triHeight/2.0f);
		path.lineTo(pointX+triHeight, pointY-triHeight/2.0f);
		return path;
	}
	
	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized int getProgress() {
		return getDealedPeogress((int)progress);
	}
	
	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
	 * 刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if(progress < 0){
			throw new IllegalArgumentException("progress not less than 0");
		}
		if(progress > max){
			progress = max;
		}
		if(progress <= max){
			this.progress = progress;
			postInvalidate();
		}
		Log.e("set progress",":"+progress);
	}
	
	
	//完成设置监听器
	public void setOnProgressListener(onProgressListener mOnProgressListener){
		this.mOnProgressListener=mOnProgressListener;
	}
	
	public interface onProgressListener{
		//回调函数 当放开手时调用此方法
		public void onComplete();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getAction()==MotionEvent.ACTION_UP){
		//	Log.e("@@@@@@@@@@@@@@","ACTION_UP");
			if(mOnProgressListener!=null){
				mOnProgressListener.onComplete();
			}
		}
		detector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
	//	Log.e("@@@@@@@@@@@@@@","onDown");
		return false;
	}


	@Override
	public void onLongPress(MotionEvent arg0) {
	//	Log.e("@@@@@@@@@@@@@@","onLongPress");
		
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {//滑动设置温度
		float mProgress;
		float endX=arg1.getX();
		float endY=arg1.getY();
		float cyTop=gapWidth+borderWidth;//进度条顶部
		float cyBottom=height-gapWidth-borderWidth;//进度条底部
		//if(endX>mCenterX-cylinderWidth/2&&endX<mCenterX+cylinderWidth/2){
		{
			mProgress=((cyBottom-endY)/(cyBottom-cyTop)*(max-min))+min;
			if(mProgress<40){
				mProgress=40;	
			}else if(mProgress>90){
				mProgress=90;
			}
			progress=mProgress;
		}
			postInvalidate();
			return true;
			
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	//	Log.e("@@@@@@@@@@@@@@","onShowPress");
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {//点击模式名称，设定温度
	//	Log.e("@@@@@@@@@@@@@@","onSingleTapUp");
		float left,right,top,bottom;//模式名称所在矩形外框的边
		float x=arg0.getX();
		float y=arg0.getY();
		mPaint2.setTextSize(modelTextSize);
		float margin=12*density;
		right=modelRightX+margin;
		for(Entry<String, Integer> entry:modelMap.entrySet()){
			int value=entry.getValue();
			String key=entry.getKey();
		//	float length=mPaint2.measureText("测试用的宽度吖");
			left=0;
			top=scaleYs[value-min-1]-modelTextSize/2-margin;
			bottom=scaleYs[value-min-1]+modelTextSize/2+margin;
			if(x>=left&&x<=right&&y>=top&&y<=bottom){
				progress=value;//点击在模式名称位置
				postInvalidate();
				if(mOnProgressListener!=null){
					mOnProgressListener.onComplete();
				}
			}

		}
		
		return true;
	}
	
	
	
	/***
	 * 去处理过的进度值，精度为5
	 * @param progress 原始进度
	 * @return 处理后的进度
	 */
	private int getDealedPeogress(int progress){
		return progress/5*5;
	}



}
