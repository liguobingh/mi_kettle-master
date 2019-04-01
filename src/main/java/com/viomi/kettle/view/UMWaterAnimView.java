package com.viomi.kettle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class UMWaterAnimView extends View {
	 
	enum SizesTyle{
		
		
	};
	private int mHeight, mWidth;
	private Paint mPaint;
	private CPoint[] mPointFirstList = new CPoint[7];
	
	public UMWaterAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
		
		super(context, attrs, defStyleAttr);
		init();
	}

	public UMWaterAnimView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		init();
	}

	public UMWaterAnimView(Context context) {
		
		super(context);
		init();
	}
	
	
	private void init() {
		
		mPaint = new Paint();
		mPaint.setColor(0xff7a98b3);
 		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
	}
	 	
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		if (mHeight == 0) {
			mHeight = getHeight();
			mWidth = getWidth();
			initPoint();
		}
		drawscrollline(mPointFirstList, canvas, mPaint);
	}

	private void initPoint() {
		
 		for(int i = 0;i<7;i++) {
			CPoint point = new CPoint();
			point.x = mWidth*i/3;
 			point.y = mHeight*((i+1)%2==0?5/(float)19:6/(float)19);
			point.offsetX = 0;
			point.offsetY = 0;
			mPointFirstList[i] = point;
		}
	}

	private void drawscrollline(CPoint[] ps, Canvas canvas, Paint paint) {

		Path path = new Path();
		for (int i = 0; i < ps.length - 1; i++) {
 			float wt = (ps[i].x + ps[i + 1].x)/(float)2;  
			path.moveTo(ps[i].x, ps[i].y);
			path.cubicTo(wt,
			ps[i].y,wt,
			ps[i+1].y, 
			ps[i+1].x, 
			ps[i+1].y);
		}
		path.lineTo(0, mHeight);
		path.lineTo(ps[6].x, mHeight);
		canvas.drawPath(path, paint);

	}

	class CPoint {
		public float x;
		public float y;
		public Paint paint;
		public int offsetX;
		public int offsetY;
	}
}
