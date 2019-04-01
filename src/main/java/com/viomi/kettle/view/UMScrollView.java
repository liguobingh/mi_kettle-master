package com.viomi.kettle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by young2 on 2016/6/24.
 */
public class UMScrollView extends ScrollView {
    private  boolean mTouchFlag=false;
    public UMScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public UMScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UMScrollView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.e("StickyLayout@@@@","UMScrollView cation="+ev.getAction());
        if(mTouchFlag) {
            super.onTouchEvent(ev);
            //postInvalidate();
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public void setTouchFlag(boolean flag){
        mTouchFlag=flag;
    }
}
