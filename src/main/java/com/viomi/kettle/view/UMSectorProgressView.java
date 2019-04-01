package com.viomi.kettle.view;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.utils.DoubleArith;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View; 
public class UMSectorProgressView extends View {
	
	/**
	 * 最大进度
	 */
	private int max=100;
	
	/**
	 * 当前进度
	 */
	private int progress;
	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize=70;

	private int mWidth, mHeight;
	private Paint mPaint, mPaint2,testPaint;
	private RectF mRectF;
	private float mR; // 半径
	private double mRCenterX, mRCenterY;
	private double mAngle = 40;
	private GestureDetector gestureDetector;  
	private onProgressListener mOnProgressListener;
	private Context context;
    private double lastAngle; //上一次转动的角度(如果超过180度，则角度会变小)
    private final float sectorAngle=280;
	
	public UMSectorProgressView(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);
		init(context);
	}

	public UMSectorProgressView(Context context, AttributeSet attrs) {

		super(context, attrs);
		init(context);
	}

	public UMSectorProgressView(Context context) {

		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context=context;
		 gestureDetector=new GestureDetector(context,new GestureDelectorSimlpeListener());  
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xff1bcec2);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(4);
		mPaint2 = new Paint();
		mPaint2.setColor(Color.WHITE); // 设置圆环的颜色
		mPaint2.setStyle(Paint.Style.STROKE); // 设置空心
		mPaint2.setStrokeWidth(2); // 设置圆环的宽度
		mPaint2.setAntiAlias(true); // 消除锯齿
		
		
	}

	
	/**
	 * 2.继承 SimpleOnGestureListener
	 * 重载 感兴趣的 手势
	 * @author yuan
	 *
	 */
	class GestureDelectorSimlpeListener extends SimpleOnGestureListener{
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.e("@@@@@@@@@@@@@","%%%%%%%%%%%%%%%%%%%");
			return true;
		}
	
	}
	
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		float pian=(float)20*UMGlobalParam.density;;
		if (mWidth == 0 || mHeight == 0) {
			mWidth = getWidth();
			mHeight = getHeight();
			mRectF = new RectF(0 + pian, 0 + pian, mWidth - pian, mHeight - pian);
			mR = (mHeight - (float)40*UMGlobalParam.density) / 2.0f;
		}
		//画外圆环
		canvas.drawArc(mRectF, -230, sectorAngle, false, mPaint);
		
		//画内刻度环
		Bitmap bitmap=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.background1);
		float radio=(float)bitmap.getHeight()/(float)bitmap.getWidth();
		Rect mDestRect = new Rect((int)pian*2, (int)pian*2,(int)(mWidth-pian*2), (int)((mWidth-pian*4)*radio+pian*2) ); 
		canvas.drawBitmap(bitmap, null, mDestRect, mPaint);
		
		/**
		 * 画进度
		 */
		progress= (int) (DoubleArith.sub(mAngle, (double) 40)*100/(double)sectorAngle);
		testPaint=new Paint();
		testPaint.setStrokeWidth(0); 
		testPaint.setColor(Color.WHITE);

		int size=(int)(textSize*UMGlobalParam.density);
		testPaint.setTextSize(size);
		testPaint.setTypeface(Typeface.DEFAULT); //设置字体
	//	Log.e("@@@@@@@@@@@@@",""+progress+",mAngle="+mAngle);
		int percent = (int)(((float)progress / (float)max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
		String percentStr=percent + "";
		if(percentStr.length()==1){
			percentStr="0"+percentStr;
		}
		float textWidth = testPaint.measureText(percentStr);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		canvas.drawText(percentStr, (float)mWidth/2 - textWidth / 2, (float)(mHeight/2+ size/2)-10*UMGlobalParam.density, testPaint); //画出进度百分比
			
		int size1=(int)(16*UMGlobalParam.density);
		testPaint.setTextSize(size1);
		canvas.drawText("℃", (float)mWidth/2 + textWidth / 2, (float)(mHeight/2- size/2+2*size1-10*UMGlobalParam.density), testPaint); //画出单位
		
		//画自选温度
		testPaint.setTextSize((float)20*UMGlobalParam.density);
		textWidth = testPaint.measureText("自选温度"); 
		canvas.drawText("自选温度", (float)mWidth/2 - textWidth / 2, (int)((mWidth-pian*4)*radio+pian*2)+(float)(20*UMGlobalParam.density), testPaint); //画出进度百分比
		
		//画滚动小圆
		Double oY = mR * Math.cos(mAngle * Math.PI / 180);
		Double ox = mR * Math.sin(mAngle * Math.PI / 180);
		mRCenterX = mHeight / 2.0f - ox;
		mRCenterY = mHeight / 2.0f + oY;
	//	canvas.drawCircle((float) mRCenterX, (float) mRCenterY, (float)15*UMGlobalParam.density, mPaint2);
		
		Bitmap bitmap1=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.direction);
		Matrix matrix=new Matrix();
		matrix.setRotate((float)(mAngle+180),0, 0);
		Bitmap bitmap2=Bitmap.createBitmap(bitmap1,0,0,bitmap1.getWidth(),bitmap1.getHeight(),matrix,true);
		float radio1=(float)bitmap1.getWidth()/(float)bitmap2.getWidth();
		int mr=(int)(15*UMGlobalParam.density/radio1);
		Rect rect = new Rect((int)mRCenterX-mr, (int)mRCenterY-mr,(int)mRCenterX+mr, (int)mRCenterY+mr ); 
		canvas.drawBitmap(bitmap2, null, rect, mPaint);
		
		

		//mOnProgressListener.onEnd();
		
		if(!bitmap.isRecycled()){
			bitmap.recycle();
		}
		if(!bitmap2.isRecycled()){
			bitmap2.recycle();
		}
	}
	
//	Bitmap
//	adjustPhotoRotation(Bitmap bm, final int orientationDegree)
//	{
//
//	        Matrix m = new Matrix();
//	        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//	        float targetX, targetY;
//	        if (orientationDegree == 90) {
//	        targetX = bm.getHeight();
//	        targetY = 0;
//	        } else {
//	        targetX = bm.getHeight();
//	        targetY = bm.getWidth();
//	  }
//
//	    final float[] values = new float[9];
//	    m.getValues(values);
//
//	    float x1 = values[Matrix.MTRANS_X];
//	    float y1 = values[Matrix.MTRANS_Y];
//
//	    m.postTranslate(targetX - x1, targetY - y1);
//
//	    Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
//	    Paint paint = new Paint();
//	    Canvas canvas = new Canvas(bm1);
//	    canvas.drawBitmap(bm, m, paint);
//
//	    return bm1;
//	  }

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();


		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
		//	Log.e("MotionEvent.ACTION_MOVE", "x = " + x + "y=" + y);
			double angle = measureAngle(x, y);
			if ((angle >= 0 && angle < 40) || (angle > 320 && angle <= 360)) {


			} else {
//				if(((x-mRCenterX)*(x-mRCenterX)+(y-mRCenterY)*(y-mRCenterY))>=(mR-10)*(mR-10)&&
//				((x-mRCenterX)*(x-mRCenterX)+(y-mRCenterY)*(y-mRCenterY))<=(mR+10)*(mR+10)){
//					mAngle = angle;
//				}
				mAngle = angle;
			}

			postInvalidate();
			break;
		case MotionEvent.ACTION_DOWN:
			//Log.e("MotionEvent.ACTION_DOWN", "x = " + x + "y=" + y);
			break;
		case MotionEvent.ACTION_UP:
			//Log.e("MotionEvent.ACTION_UP", "x = " + x + "y=" + y);
			//当进度到达最大值时  调用此函数
			if(mOnProgressListener != null){
					mOnProgressListener.onEnd();
			}
			break;
		default:
			break;
		}
		return true;
	}

	private double measureAngle(float x, float y) {

		if (mR == 0)
			return 0;
//		double angle = 0;
//		double params = (mR - y) / mR;
//		double acos = Math.acos(params);
//		angle = getAngleByAcos(acos);
//    	if(angle != 0){ 
//    		lastAngle = angle;
//    	}else if(angle == 0){//防止角度计算不到为0时
//    		angle = lastAngle;
//    	}
//		if (x < mR) { // 超过180度时
//			angle = 360 - angle;
//		}
//		angle = angle + 180;
//		if (angle > 360) {
//			angle -= 360;
//		}
//		return angle;
		double angle = (double) Math.toDegrees(Math.atan2(mR - y, mR - x));
        angle = ((int)(angle+2.5)/5)*5+90;
 
         if(angle<0){
        	 angle+=360;
         }
         return angle;
	}

//	private double getAngleByAcos(double acos) {
//		return (int) ((360 * acos) / (2 * Math.PI));
//	}
	
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
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
			mAngle=sectorAngle;
		}
		if(progress <= max){
			this.progress = progress;
			mAngle=(double)(sectorAngle*progress/(double)100)+40;
			postInvalidate();
		}
		Log.e("set progress",":"+progress+",mAngle:"+mAngle);
	}

	public void setOnProgressListener(onProgressListener mOnProgressListener) {
		this.mOnProgressListener = mOnProgressListener;
	}
	/**
	 * 回调接口
	 * 
	 */
	public  interface onProgressListener{
	/**
	 * 回调函数 当放开手时调用此方法
	 */
		public void onEnd(); 
	
	}





}
