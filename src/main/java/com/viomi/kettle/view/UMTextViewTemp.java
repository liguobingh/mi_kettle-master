package com.viomi.kettle.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.viomi.kettle.R;


public class UMTextViewTemp extends View {
 
  			  int mFrom = 0, mTo = 0;
 	private boolean mUnKnow= false;
 	private int     mFromColor =0xff7a98b3;
	private Paint   mTextPaint, mCompletePaint, mTDSPaint;
	private int     mWidth, mHeight;
	private Rect    mTextRect;
	private int 	mD = 0;
	private int 	mU = 0;
//	private int 	mH = 0;
	 
 	public UMTextViewTemp(Context context, AttributeSet attrs, int defStyleAttr) {
 		
		super(context, attrs, defStyleAttr);
		init(context);
 	}

	public UMTextViewTemp(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		init(context);
 	}

	public UMTextViewTemp(Context context) {
		
		super(context);
		init(context);
 	}

 	protected void onDraw(Canvas canvas) {
 		
 		super.onDraw(canvas);
 		if(mWidth == 0) {
 			mWidth = getWidth();
 			mHeight = getHeight();
 			mTextPaint.setTextSize(mHeight*106/100);
 			mCompletePaint.setTextSize(mHeight*106/100);
 			mTDSPaint.setTextSize(mHeight*24/100);
 			mTextPaint.getTextBounds("00", 0,2, mTextRect);
  		}
 		if(mUnKnow) {
 			drawTextUnknow(canvas); 
  		} else {
  			drawText(canvas);
  		} 
 	}

 	private void init(Context context) {

		mFromColor=getResources().getColor(R.color.white);
  		mTextPaint = new Paint();
		mTextPaint.setColor(mFromColor);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextRect = new Rect(); 
		mCompletePaint = new Paint();
		mCompletePaint.setColor(mFromColor);
		mCompletePaint.setAlpha(102);
		mCompletePaint.setAntiAlias(true);
		mCompletePaint.setStyle(Paint.Style.FILL);
 		mTDSPaint = new Paint();
		mTDSPaint.setColor(mFromColor);
		mTDSPaint.setAntiAlias(true);
		mTDSPaint.setStyle(Paint.Style.FILL);
  	}
 	
 	public void setTypeface(Typeface typeface) {
 		
		mTextPaint.setTypeface(typeface);
		mCompletePaint.setTypeface(typeface);
		mTDSPaint.setTypeface(typeface);
		invalidate();
 	}
	
 	public void setData(int from, int to) {
 		
  		mUnKnow  = false;
 		   mFrom = from;
 		     mTo = to;
 		ValueAnimator valueAnimation = ValueAnimator.ofFloat(mFrom, mTo);
 		valueAnimation.setDuration(3*1000);
 		valueAnimation.setInterpolator(new ExpoEaseInOutInterpolater());
 		valueAnimation.addUpdateListener(new AnimatorUpdateListener() { 
 			public void onAnimationUpdate(ValueAnimator animator) {  
 				Float a = ((Float) animator.getAnimatedValue()-mTo)/(mFrom-mTo);
 				Integer num = (int) (((a>.5?Math.ceil(a*10):Math.floor(a*10))/10 * (mFrom-mTo))+mTo);
 				UMTextViewTemp.this.mathNum(num);
 				UMTextViewTemp.this.invalidate();
 	   		}
 	  	});
 		valueAnimation.start(); 
   	}
 	
 	public void setTextTargetChange(int to ) {
 		 
 		mUnKnow  = false;
 		mFrom = mTo;
 		mTo = to;
 		ValueAnimator valueAnimation = ValueAnimator.ofFloat(mFrom, mTo);
 		valueAnimation.setDuration(3*1000);
 		valueAnimation.setInterpolator(new ExpoEaseInOutInterpolater());
 		valueAnimation.addUpdateListener(new AnimatorUpdateListener() { 
 			public void onAnimationUpdate(ValueAnimator animator) {  
 				Float a = ((Float) animator.getAnimatedValue()-mTo)/(mFrom-mTo);
 				Integer num = (int) (((a>.5?Math.ceil(a*10):Math.floor(a*10))/10 * (mFrom-mTo))+mTo);
 				UMTextViewTemp.this.mathNum(num);
 				UMTextViewTemp.this.invalidate();
 	   		}
 	  	});
 		valueAnimation.start(); 
  	}
 	 
 	public void setTextNum(int num) { 
 		
 		mUnKnow  = false;
 		mathNum(num);
		invalidate();
  	}
 	
 	public int getTo() {
 		
 		return mTo;
 	}
 	
 	public void setTextUnKnow() {
 		
 		mUnKnow  = true;
 		invalidate();
 	}
 	  
 	private void drawText(Canvas canvas) {
 		
 		float tWidth = mTextPaint.measureText("00", 0, 2);
// 		if(mH == 0) {
// 			canvas.drawText(""+mH, mWidth/(float)2-tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mCompletePaint);
// 		} else {
// 			canvas.drawText(""+mH, mWidth/(float)2-tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mTextPaint);
// 		}
 		if( mD==0) {
 			canvas.drawText(""+mD, mWidth/(float)2-tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mCompletePaint);
 		} else {
 			canvas.drawText(""+mD, mWidth/(float)2-tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mTextPaint);
 		}
 		if(mD==0 && mU == 0 ) {
 			canvas.drawText(""+mU, mWidth/(float)2-tWidth/(float)2+tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mCompletePaint);
 		} else {	
 			canvas.drawText(""+mU, mWidth/(float)2-tWidth/(float)2+tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mTextPaint);
 		}
 		canvas.drawText("℃", mWidth/(float)2-tWidth/(float)2+tWidth*2/(float)2+15, mHeight/(float)2-mTextRect.height()/(float)2+ mHeight*24/100, mTDSPaint);
 	}

 	private void drawTextUnknow(Canvas canvas) {
 		
 		float tWidth = mTextPaint.measureText("??", 0, 2);
		canvas.drawText("?", mWidth/(float)2-tWidth/(float)2, mHeight/(float)2+mTextRect.height()/(float)2, mCompletePaint);
		canvas.drawText("?", mWidth/(float)2-tWidth/(float)2+tWidth/(float)3, mHeight/(float)2+mTextRect.height()/(float)2, mCompletePaint);
		canvas.drawText("?", mWidth/(float)2-tWidth/(float)2+tWidth*2/(float)3, mHeight/(float)2+mTextRect.height()/(float)2, mCompletePaint);
 		canvas.drawText("℃", mWidth/(float)2-tWidth/(float)2+tWidth*3/(float)3+20, mHeight/(float)2+mTextRect.height()/(float)2, mTDSPaint);
 	}
 	 
 	public void mathNum(int num) {
   		
 		mU = num%10;
 		mD = (num/10)%10;
 		//mH = (num/100%10);
  	}
 
 	public void  setTextColor(int color) { 
 		 
 		mTextPaint.setColor(color);
 		mTDSPaint.setColor(color);
		mCompletePaint.setColor(color);
		mCompletePaint.setAlpha(102); 
		invalidate();
  	}
  	
	public interface onProcessListener { 
		
 		public void onComplete(boolean target);
	}
}
