  
package com.viomi.kettle.view;

import android.view.animation.Interpolator;
 
public class ExpoEaseInOutInterpolater implements Interpolator {
	
    public float getInterpolation(float t) {
    	
        if (t == 0) {
            return 0;
        }
        if (t == 1) {
            return 1;
        }
        t *= 2;
        if (t < 1) {
            return 0.5f * (float) Math.pow(2, 10 * (t - 1));
        }        
        --t;
        return 0.5f * (float) (-Math.pow(2, -10 * t) + 2);
    }
}