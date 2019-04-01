package com.viomi.kettle;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import com.viomi.kettle.activity.UMMainScreenActivity;
import com.viomi.kettle.activity.UMStatusActivity;
import com.xiaomi.plugin.core.XmPluginPackage;
import com.xiaomi.smarthome.bluetooth.XmBluetoothDevice;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.device.api.BaseWidgetView;
import com.xiaomi.smarthome.device.api.DeviceStat;
import com.xiaomi.smarthome.device.api.IXmPluginMessageReceiver;
import com.xiaomi.smarthome.device.api.MessageCallback;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

public class UMKettleMessageReceiver  implements IXmPluginMessageReceiver  {
	@Override
	public boolean handleMessage(Context context,
			XmPluginPackage xmPluginPackage, int type, Intent intent,
			DeviceStat deviceStat) {
		Log.e("UMKettleMessageReceiver","start enter plug!deviceStat.did="+deviceStat.did);
        switch (type) {
        case LAUNCHER: {// 启动入口
            XmPluginHostApi.instance().startActivity(context, xmPluginPackage, intent,
                    deviceStat.did, UMMainScreenActivity.class);
            return true;
        	}
        
		case IXmPluginMessageReceiver.MSG_BLUETOOTH_PAIRING:
			ArrayList<XmBluetoothDevice> devices = intent.getParcelableArrayListExtra(XmBluetoothManager.KEY_DEVICES);
			break;
			
        default:
            break;
	    }
	    return false;
	}

	@Override
	public boolean handleMessage(Context context,
			XmPluginPackage xmPluginPackage, int type, Intent intent,
			DeviceStat deviceStat, MessageCallback callback) {
		return false;
	}

	@Override
	public BaseWidgetView createWidgetView(Context context,
			LayoutInflater layoutInflater, XmPluginPackage xmPluginPackage,
			int type, Intent intent, DeviceStat deviceStat) {
		// TODO Auto-generated method stub
		return null;
	}
}
