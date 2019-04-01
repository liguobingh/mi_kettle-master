
package com.viomi.kettle.view;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getMode;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

/***
 * A special layout when measured in AT_MOST will take up a given percentage of
 * the available space.
 */
public class UMWeightedLinearLayout extends LinearLayout {
    private float mMajorWeight;
    private float mMinorWeight;

    public UMWeightedLinearLayout(Context context) {
        super(context);
    }

    public UMWeightedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // mMajorWeight = 0.9f;
        // mMinorWeight = 0.85f;
        mMajorWeight = 1.0f;
        mMinorWeight = 1.0f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final boolean isPortrait = screenWidth < metrics.heightPixels;

        final int widthMode = getMode(widthMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        boolean measure = false;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, EXACTLY);

        final float widthWeight = isPortrait ? mMinorWeight : mMajorWeight;
        if (widthMode == AT_MOST && widthWeight > 0.0f) {
            if (width < (screenWidth * widthWeight)) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (screenWidth * widthWeight),
                        EXACTLY);
                measure = true;
            }
        }

        // TODO: Support height?
        if (measure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
