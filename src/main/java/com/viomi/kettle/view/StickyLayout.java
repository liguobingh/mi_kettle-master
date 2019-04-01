package com.viomi.kettle.view;

import java.util.NoSuchElementException;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;

public class StickyLayout extends LinearLayout {

	private static final String TAG = "StickyLayout";
	private static final boolean DEBUG = false;
	private int mMiniHeight = 320;
	private OnGiveUpTouchEventListener mGiveUpTouchEventListener;
	public interface OnGiveUpTouchEventListener {
		public boolean giveUpTouchEvent(MotionEvent event,int direct);
		public void onScrollCallBack(float per);
	}
	private UMScrollView mScrollView;
 	private View mHeader;
	private View mContent;
	private View mNumberView;

	private int mOriginalHeaderHeight;
	private int mHeaderHeight;
	private int mStatus = STATUS_EXPANDED;
	public static final int STATUS_EXPANDED = 1;
	public static final int STATUS_COLLAPSED = 2;
	private int mTouchSlop;
 	private int mLastX = 0;
	private int mLastY = 0;
 	private int mLastXIntercept = 0;
	private int mLastYIntercept = 0; 
	private boolean mInitDataSucceed = false;
	private boolean mIsSticky = true;
	private boolean mDisallowInterceptTouchEventOnHeader = true;
	private float[] mHSVColor = new float[3];

	public StickyLayout(Context context) {
		super(context);
	}

	public StickyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	
	public StickyLayout(Context context, AttributeSet attrs, int defStyle) {
	
		super(context, attrs, defStyle);
	}
	
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus && (mHeader == null || mContent == null)) {
			initData();
		}
	}

	private void initData() {
		int headerId = R.id.sticky_header;
		int contentId = R.id.sticky_content;
		int scrollviewId = R.id.scrollview;
		if(scrollviewId!=0){
			mScrollView = (UMScrollView) findViewById(scrollviewId);
		}
		if (headerId != 0 && contentId != 0) {
			mHeader = findViewById(headerId);
			mContent = findViewById(contentId);
			mNumberView = mHeader.findViewById(R.id.layout_number);
			mOriginalHeaderHeight = mHeader.getMeasuredHeight();
			Log.e(TAG, "mOriginalHeaderHeight=" + mOriginalHeaderHeight);
			mHeaderHeight = mOriginalHeaderHeight;
			mMiniHeight = mNumberView.getMeasuredHeight()- (int)UMGlobalParam.density*10;
			mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();// getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
			Log.e(TAG, "mTouchSlop=" + mTouchSlop);
			if (mHeaderHeight > 0) {
				mInitDataSucceed = true;
			}
			if (DEBUG) {
				Log.d(TAG, "mTouchSlop = " + mTouchSlop + "mHeaderHeight = " + mHeaderHeight);
			}
		} else {
			throw new NoSuchElementException(
					"Did your view with id \"sticky_header\" or \"sticky_content\" exists?");
		}
		
	}

	public void setOnGiveUpTouchEventListener(OnGiveUpTouchEventListener l) {

		mGiveUpTouchEventListener = l;
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		Log.e(TAG, "onInterceptTouchEvent");
		int intercepted = 0;
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			Log.e(TAG, "onInterceptTouchEvent action down");
			mLastXIntercept = x;
			mLastYIntercept = y;
			mLastX = x;
			mLastY = y;
			intercepted = 0;
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int deltaX = x - mLastXIntercept;
			int deltaY = y - mLastYIntercept;
			Log.e(TAG, "onInterceptTouchEvent action move");
			if (mDisallowInterceptTouchEventOnHeader && y <= getHeaderHeight()) {
				intercepted = 0;
			} else if (Math.abs(deltaY) <= Math.abs(deltaX)) {
				intercepted = 0;
			} else if (mStatus == STATUS_EXPANDED && deltaY <= -mTouchSlop) {
				Log.e(TAG, "onInterceptTouchEvent STATUS_EXPANDED");
				intercepted = 1;
			} else if (mGiveUpTouchEventListener != null) {
					if (mGiveUpTouchEventListener.giveUpTouchEvent(event,mStatus) && deltaY >= mTouchSlop) {
						intercepted = 1;
 					}
			}
			break;
		}

		case MotionEvent.ACTION_UP: {

			intercepted = 0;
			mLastXIntercept = mLastYIntercept = 0;
			break;
		}
		default:
			break;
		}
		if (DEBUG) {
			Log.d(TAG, "intercepted=" + intercepted);
		}
		return intercepted != 0;
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (!mIsSticky) {
			Log.e(TAG, "mIsSticky = false");
			return true;
		} 
		int x = (int) event.getX();
		int y = (int) event.getY();
		Log.e(TAG," event.getY()="+y);
		int deltaX = x - mLastX;
		int deltaY = y - mLastY;

		if(deltaY < 0 && mStatus == STATUS_COLLAPSED && event.getAction()!=MotionEvent.ACTION_UP && event.getAction()!=MotionEvent.ACTION_DOWN) {
			Log.e(TAG,"mStatus == STATUS_COLLAPSED  return false");
			if(mScrollView!=null){
				mScrollView.setTouchFlag(true);
				mScrollView.onTouchEvent(event);
			}
			return true;
		}
//		Log.e(TAG,"@@@@@@@@@@@@@@@@@@@@@@@@@ =="+event.getAction());

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {

			break;
		}
		case MotionEvent.ACTION_MOVE: {

			if (DEBUG) {
				Log.d(TAG, "onTouch mHeaderHeight=" + mHeaderHeight + "  deltaY=" + deltaY + "  mlastY=" + mLastY);
			}
			mHeaderHeight += deltaY;
			Log.e(TAG, "mHeaderHeight = " + mHeaderHeight);
			setHeaderHeight(mHeaderHeight);
			if(mScrollView!=null){
				mScrollView.setTouchFlag(false);
 			}
			break;
		}
		case MotionEvent.ACTION_UP: {
	 
			int destHeight = mMiniHeight;
			if (mHeaderHeight <= (mMiniHeight+(mOriginalHeaderHeight-mMiniHeight) * 0.5)) {
				destHeight = mMiniHeight;
				mStatus = STATUS_COLLAPSED;
			} else {
				destHeight = mOriginalHeaderHeight;
				mStatus = STATUS_EXPANDED;
			} 
			this.smoothSetHeaderHeight(mHeaderHeight, destHeight, 300);
			break;
		}
		default:
			break;
		}
		mLastX = x;
		mLastY = y;
		return true;
	}

	public void smoothSetHeaderHeight(final int from, final int to, long duration) {

		smoothSetHeaderHeight(from, to, duration, false);
	}

	private Handler mHandler = new Handler() { 	
		public void handleMessage(android.os.Message msg) { 
			switch (msg.what) {
			case 1:
//				mHandler
				break; 
			default:
				break;
			}
			
		};
	};
	
	public void smoothSetHeaderHeight(final int from, final int to, long duration, final boolean modifyOriginalHeaderHeight) {
		
		final int frameCount = (int) (duration / 1000f * 30) + 1;
		final float partation = (to - from) / (float) frameCount;
		new Thread("Thread#smoothSetHeaderHeight") {
			public void run() {
				for (int i = 0; i < frameCount; i++) {
					final int height;
					if (i == frameCount - 1) {
						height = to;
					} else {
						height = (int) (from + partation * i);
					}
					post(new Runnable() {
						public void run() {
							setHeaderHeight(height);
						}
					});
					try {
						sleep(10);
					} catch (InterruptedException e) { 

					}
				}
				if (modifyOriginalHeaderHeight) {
					setOriginalHeaderHeight(to);
				}
			};
		}.start();
	}

	public void setOriginalHeaderHeight(int originalHeaderHeight) {
		mOriginalHeaderHeight = originalHeaderHeight;
	}

	public void setHeaderHeight(int height, boolean modifyOriginalHeaderHeight) {
		if (modifyOriginalHeaderHeight) {
			setOriginalHeaderHeight(height);
		}
		setHeaderHeight(height);
	}

	public void setHeaderHeight(int height) {
		
		if (!mInitDataSucceed) {
			initData();
		}
		if (DEBUG) {
			Log.d(TAG, "setHeaderHeight height=" + height);
		}
		if (height <= mMiniHeight) {
			height = mMiniHeight;
		} else if (height > mOriginalHeaderHeight) {
			height = mOriginalHeaderHeight;
		}
		if (height == mMiniHeight) {
			mStatus = STATUS_COLLAPSED;
		} else {
			mStatus = STATUS_EXPANDED;
		}
		if(mNumberView!=null) {
 			float per = (height-mMiniHeight)/(float)(mOriginalHeaderHeight-mMiniHeight);
			mNumberView.setScaleX(per*0.3f+0.7f);
			mNumberView.setScaleY(per*0.3f+0.7f);
			if(mGiveUpTouchEventListener!=null) {
				mGiveUpTouchEventListener.onScrollCallBack(per);
			}
		} 
		if (mHeader != null && mHeader.getLayoutParams() != null) {
			mHeader.getLayoutParams().height = height;
			mHeader.requestLayout();
			mHeaderHeight = height;
		} else {
			if (DEBUG) {
				Log.e(TAG, "null LayoutParams when setHeaderHeight");
			}
		}
	}

	public int getHeaderHeight() {
		return mHeaderHeight;
	}

	public void setSticky(boolean isSticky) {
		mIsSticky = isSticky;
	}

	public void requestDisallowInterceptTouchEventOnHeader(boolean disallowIntercept) {
		mDisallowInterceptTouchEventOnHeader = disallowIntercept;
	}

	public void setHeaderColorByInt(int value){
		int minValue=30;
		int maxValue=100;
		int minColor=180;
		int maxColor=360;
		if(value<minValue){
			value=minValue;
		}else if (value>maxValue){
			value=maxValue;
		}
		float per=((float)(maxColor-minColor))/(maxValue-minValue);
		int color = (int) ((value-minValue)*per+minColor);
		setLayoutBackgroundColor(color);
	}

	/***
	 * 修改背景颜色 
	 * @param value hsv里的色彩值。
	 * hsv有三个成员，hsv[0]的范围是[0,360),表示色彩，hsv[1]范围[0,1]表示饱和度，hsv[2]范围[0,1]表示值
	 */
	public void setLayoutBackgroundColor(int value){
		mHSVColor[0]=value;
		mHSVColor[1]=1;
		mHSVColor[2]=1;	
		int color=Color.HSVToColor(mHSVColor);
		mHeader.setBackgroundColor(color);
	}

	public void close(){
		mGiveUpTouchEventListener=null;
	}
}