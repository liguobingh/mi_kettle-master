package com.viomi.kettle.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;


public class UMAutoTextView extends TextView implements
		ViewSwitcher.ViewFactory {

	private float mHeight;
	private Context mContext;
//	private AnimationSet mInUp;
//	private AnimationSet mOutUp;

	public UMAutoTextView(Context context) {

		this(context, null);
	}

	public UMAutoTextView(Context context, AttributeSet attrs) {

		super(context, attrs);
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.auto3d);
	//	mHeight = a.getDimension(R.styleable.auto3d_auto3dTextSize, 12);
		mHeight=context.getResources().getDisplayMetrics().density*6;
		a.recycle();
		mContext = context;
		init();
	}

	private void init() {

		float down = getResources().getDimension(R.dimen.um_down_size);
		//setFactory(this);
//		mInUp = createAnim(-1 * down, 0, false);
//		mOutUp = createAnim(0, down, true);
	}

//	private AnimationSet createAnim(float start, float end, boolean out) {
//
//		AnimationSet anim = new AnimationSet(true);
//		final TranslateAnimation rotation = new TranslateAnimation(0, 0, start,
//				end);
//		rotation.setDuration(3 * 100);
//		rotation.setFillAfter(true);
//		rotation.setAnimationListener(new AnimationListener() {
//			public void onAnimationStart(Animation arg0) {
//
//			}
//
//			public void onAnimationRepeat(Animation arg0) {
//
//			}
//
//			public void onAnimationEnd(Animation arg0) {
//				((TextView) (UMAutoTextView.this).getChildAt(0))
//						.setTextColor(getResources().getColor(R.color.white));
//				((TextView) (UMAutoTextView.this).getChildAt(1))
//						.setTextColor(getResources().getColor(R.color.white));
//			}
//		});
//		anim.addAnimation(rotation);
//
//		Animation anim2 = null;
//		try {
//			if (out) {
//				anim2 = AnimationUtils.loadAnimation(mContext,
//						android.R.anim.fade_out);
//			} else {
//				anim2 = AnimationUtils.loadAnimation(mContext,
//						android.R.anim.fade_in);
//			}
//		} catch (Exception e) {
//
//		}
//		anim.addAnimation(anim2);
//		return anim;
//	}

	private int mNum = 0;

	public View makeView() {

		TextView t = new TextView(mContext);
		t.setGravity(Gravity.CENTER);
		t.setTextSize(mHeight);
		t.setMaxLines(2);
		if (mNum == 0) {
			t.setTextColor(getResources().getColor(R.color.white));
		} else {
			t.setTextColor(getResources().getColor(R.color.white));
		}
		mNum++;
		return t;
	}

//	public void next() {
//
//		try {
//			if (UMAutoTextView.this.getChildCount() < 2) {
//				return;
//			}
//			if (UMAutoTextView.this.getChildAt(0) == null)
//				return;
//			if (UMAutoTextView.this.getChildAt(1) == null)
//				return;
//			((TextView) (UMAutoTextView.this).getChildAt(0)).clearAnimation();
//			((TextView) (UMAutoTextView.this).getChildAt(1)).clearAnimation();
//			if (getInAnimation() != mInUp) {
//				setInAnimation(mInUp);
//			}
//			if (getOutAnimation() != mOutUp) {
//				setOutAnimation(mOutUp);
//			}
//		} catch (Exception e) {
//
//		}
//	}
}
