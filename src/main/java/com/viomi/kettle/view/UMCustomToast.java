package com.viomi.kettle.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;

/**
 * Created by young2 on 2016/3/17.
 */
public class UMCustomToast {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast=null;
        }
    };

    public static void showToast(Context mContext, String text, int duration) {

        mHandler.removeCallbacks(r);
        if (mToast != null){
            mToast.setText(text);
        }
        else{
            mToast = Toast.makeText(mContext, text, duration);
        }
        if(duration==Toast.LENGTH_SHORT){
            duration=2000;
        }else if(duration==Toast.LENGTH_LONG){
            duration=3000;
        }
        mHandler.postDelayed(r, duration);
        mToast.setGravity(Gravity.BOTTOM, 0, (int) (mContext.getResources().getDimension(R.dimen.um_bottom_setting_width)+30* UMGlobalParam.density));
        mToast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

}