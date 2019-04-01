package com.viomi.kettle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/*
 * jason
 * 注意问题:
 * 圆的大小没有做屏幕适配,有时间在补上，其它基本没什么问题
 * 比起一个圆圆出现不如一次出现来的方便点，偷了点懒
 */
public class UMChartAnimView extends View {

	private float mXCenter, mYCenter;
	private int mHeight = 0, mWidth = 0;
	private Paint mPaint;
	private CPoint[] mPointList = new CPoint[7];
	private int[] mYList = new int[7];
	private int mMax,mMin,mStart;
	private float mPerHeight;
	private int offsetTop = 20,offsetBottom=20;
	private float mProgress = 0;
	private int mRadius=4;
	private int oneRadius = 19, secondRadius = 15;
	public static enum DrawStyle {
 	    Normal, Line, Curve, ErrorCurve
	}
 
	private DrawStyle mStyle = DrawStyle.Curve;
	
	public UMChartAnimView(Context context) {

		super(context);
		init();
	}

	public UMChartAnimView(Context context, AttributeSet attrs) {

		super(context, attrs);
		init();
	}

	public UMChartAnimView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		 
 		
	}

 	public boolean onTouchEvent(MotionEvent event) {
 		 
 		return false;
	}
 	
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		if (mHeight == 0) {
			mWidth = getWidth();
			mHeight = getHeight()-offsetBottom-offsetTop;
			mXCenter = mWidth / 2;
			mYCenter = mHeight / (float)2+offsetTop;
			mPaint = new Paint();
			mPaint.setColor(0xffffffff);
			mPaint.setStrokeWidth(2);
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.STROKE);
			initPoint();
		}
		mPaint.setStrokeWidth(2);
		mPaint.setStyle(Paint.Style.STROKE);
		if(mStyle == DrawStyle.Line) {
			canvas.drawLine(0, mYCenter, mWidth, mYCenter, mPaint); 
		} else if(mStyle == DrawStyle.Curve) {
			movePoint();
			drawscrollline(mPointList, canvas, mPaint);
		} else if(mStyle == DrawStyle.ErrorCurve){
			 
		}
	}

	private void initPoint() {

 		for(int i = 0;i<7;i++) {
			CPoint point = new CPoint();
			point.x = mWidth*i / (float)6;
 			point.y = mYCenter;
			point.offset = 0;
			point.progress = 0;
			point.radius = 15;
			point.oneComplete = false;
			point.complete = false;
			mPointList[i] = point;
		}
 		initYList();
 	}
	
	private void movePoint() {
	  
 		if(mProgress>=1.0)
			return;
		mProgress+=(float)0.040;
		for(int i = 0;i<7;i++) {
			CPoint point =  mPointList[i];
			point.progress = mProgress;
		} 
	}

	private void drawscrollline(CPoint[] ps, Canvas canvas, Paint paint) {

		for (int i = 0; i < ps.length - 1; i++) {
 			float wt = (ps[i].x + ps[i + 1].x)/(float)2;  
			Path path = new Path();
			path.moveTo(ps[i].x, ps[i].y+ps[i].offset*ps[i].progress);
			path.cubicTo(wt,
			ps[i].y+ps[i].offset*ps[i].progress,wt,
			ps[i+1].y+ps[i+1].offset*ps[i+1].progress, 
			ps[i+1].x, 
			ps[i+1].y+ps[i+1].offset*ps[i+1].progress);
			canvas.drawPath(path, paint);
		}
		if(mProgress<(float)1.0)
			postInvalidate();
		else {
			invalidateCircle2(canvas);
		}
	}
	
	private void invalidateCircle(Canvas canvas) {
 
 		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0xffffffff);
		
		int radius = (int) mPointList[0].radius;
		
		for(int i = 1; i<(mPointList.length-1);i++) {
			CPoint point =  mPointList[i];
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius, mPaint);
		}
		
		mPaint.setColor(0xff00abe3);
		
		for(int i = 1; i<(mPointList.length-1);i++) {
			CPoint point =  mPointList[i];
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius-4, mPaint);
		}
		
		mPaint.setColor(0xffffffff);
		
		if(mRadius<radius) {
			mRadius++;
			postInvalidate();
		}
	}

	private void invalidateCircle2(Canvas canvas) {
		 
 		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0xffffffff);
 		CPoint point =  mPointList[1];
		if(!point.complete) {
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius-4, mPaint);
	 		if(mRadius<oneRadius && !point.oneComplete) {
				mRadius++;
 			} else if(mRadius == oneRadius) {
				point.oneComplete  = true;
				mRadius--;
 			}  else if(mRadius > point.radius) {
				mRadius--;
 			} else {
 				point.complete =true;
 				mRadius = 4;
 			}
			postInvalidate();
			mPaint.setColor(0xffffffff);
	 		return;
		} else {
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius-4, mPaint);
		}
		point =  mPointList[2];
		mPaint.setColor(0xffffffff);
		if(!point.complete) {
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius-4, mPaint);
	 		if(mRadius<oneRadius && !point.oneComplete) {
				mRadius++;
 			} else if(mRadius == oneRadius) {
				point.oneComplete  = true;
				mRadius--;
 			}  else if(mRadius > point.radius) {
				mRadius--;
 			} else {
 				point.complete =true;
 				mRadius = 4;
 			}
			postInvalidate();
			mPaint.setColor(0xffffffff);
	 		return;
		} else {
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius-4, mPaint);
		}
		point =  mPointList[3];
		mPaint.setColor(0xffffffff);

		if(!point.complete) {
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius-4, mPaint);
	 		if(mRadius<oneRadius && !point.oneComplete) {
				mRadius++;
 			} else if(mRadius == oneRadius) {
				point.oneComplete  = true;
				mRadius--;
 			}  else if(mRadius > point.radius) {
				mRadius--;
 			} else {
 				point.complete =true;
 				mRadius = 4;
 			}
			postInvalidate();
			mPaint.setColor(0xffffffff);

	 		return;
		} else {
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius-4, mPaint);
		}
		point =  mPointList[4];
		mPaint.setColor(0xffffffff);

		if(!point.complete) {
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius-4, mPaint);
	 		if(mRadius<oneRadius && !point.oneComplete) {
				mRadius++;
 			} else if(mRadius == oneRadius) {
				point.oneComplete  = true;
				mRadius--;
 			}  else if(mRadius > point.radius) {
				mRadius--;
 			} else {
 				point.complete =true;
 				mRadius = 4;
 			}
			postInvalidate();
			mPaint.setColor(0xffffffff);
	 		return;
		} else {
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius-4, mPaint);
		}
		point =  mPointList[5];
		mPaint.setColor(0xffffffff);

		if(!point.complete) {
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, mRadius-4, mPaint);
	 		if(mRadius<oneRadius && !point.oneComplete) {
				mRadius++;
 			} else if(mRadius == oneRadius) {
				point.oneComplete  = true;
				mRadius--;
 			}  else if(mRadius > point.radius) {
				mRadius--;
 			} else {
 				point.complete =true;
 				mRadius = 4;
 			}
			postInvalidate();
			mPaint.setColor(0xffffffff);
	 		return;
		} else {
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius, mPaint);
	 		mPaint.setColor(0xff00abe3);
	 		canvas.drawCircle(point.x, point.y+point.offset, point.radius-4, mPaint);
		}
		mPaint.setColor(0xffffffff);
	}

	public boolean isCurve() {
		
		if(mStyle == DrawStyle.Curve)
			return true;
		else 
			return false;
	}
	
	public void setDrawStyleCurve(int[] list) {
		
		mStyle = DrawStyle.Curve;
		setYList(list);
		postInvalidate();
	}
	
	private void initYList() {
		
		mYList[0] = 0;
		mYList[1] = 0;
		mYList[2] = 0;
		mYList[3] = 0;
		mYList[4] = 0;
		mYList[5] = 0;
		mYList[6] = 0;
		mMax = 0;
		mMin = 0;
		mStart = mYList[0];
		setPerHeight();
	}
	
	public void setYList(int[] list) {
		
		mYList = list; 
		mMax = mYList[0];
		mMin = mYList[0];
		mStart = mYList[0];
		for(int i=1;i<mYList.length;i++) {
			if(mYList[i]>mMax) {
				mMax = mYList[i];
			}
			if(mYList[i]<mMin) {
				mMin = mYList[i];
			}
		}
		setPerHeight();
		for(int i = 0;i<7;i++) {
			CPoint point =  mPointList[i]; 
			point.offset = (mYList[0]-mYList[i])*mPerHeight;
		}
	}
	
	private void setPerHeight() {
		 
		int top = Math.abs(mMax - mStart);
		int bottom = Math.abs(mStart - mMin);
		if(top > bottom) {
			mPerHeight = (mYCenter-offsetTop)/(float)top;
		} else if(bottom>top) {
			mPerHeight = (mYCenter-offsetTop)/(float)bottom;
		} else {
			mPerHeight = 0;
		}
	}
	class CPoint {
		
		public float x;
		public float y;
		public float offset;
		public float progress;
		public float radius;
		public boolean oneComplete; 
		public boolean complete;
	}
}
