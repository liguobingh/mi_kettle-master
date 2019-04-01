package com.viomi.kettle.activity;


import com.viomi.kettle.R;
import com.xiaomi.smarthome.device.api.XmPluginBaseActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class UMBaseActivity  extends XmPluginBaseActivity
{
	
	public int layoutId = R.layout.um_activity_mainscreen;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutId);
		View title_bar = findViewById(R.id.title_bar);
		if(title_bar!=null){
			mHostActivity.setTitleBarPadding(title_bar);
		}

	}
	
}
