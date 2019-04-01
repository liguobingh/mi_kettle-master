package com.viomi.kettle;

import com.viomi.kettle.activity.UMBaseActivity;
import com.viomi.kettle.dev.UMBluetoothManager;
import com.viomi.kettle.view.UMThermometer;
import com.xiaomi.smarthome.bluetooth.XmBluetoothDevice;
import com.xiaomi.smarthome.device.api.XmPluginBaseActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends UMBaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("enter plug","success!!!!!!!!!!");
		setContentView(R.layout.test);
	//	UMThermometer thermometer=(UMThermometer)findViewById(R.id.thermometer);
	}
}
