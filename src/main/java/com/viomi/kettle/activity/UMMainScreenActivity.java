package com.viomi.kettle.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.dev.UMBlueToothUpgrader;
import com.viomi.kettle.dev.UMBluetoothManager;
import com.viomi.kettle.dev.UMSceneManager;
import com.viomi.kettle.interfaces.UMStatusInterface;
import com.viomi.kettle.utils.FileUtil;
import com.viomi.kettle.utils.PhoneUtil;
import com.viomi.kettle.utils.log;
import com.viomi.kettle.view.BubbleLayout;
import com.viomi.kettle.view.StickyLayout;
import com.viomi.kettle.view.UMAutoTextView;
import com.viomi.kettle.view.UMModelSetView;
import com.viomi.kettle.view.UMScrollView;
import com.viomi.kettle.view.UMSwitchButton;
import com.viomi.kettle.view.UMTextViewTemp;
import com.viomi.kettle.view.UmTempSeekbarView;
import com.viomi.kettle.view.UmTimeSeekbarView;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.BaseDevice;
import com.xiaomi.smarthome.device.api.IXmPluginHostActivity;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by young2 on 2016/6/1.
 */
public class UMMainScreenActivity extends UMBaseActivity implements StickyLayout.OnGiveUpTouchEventListener {
    private final static String TAG = "UMMainScreenActivity";
    private final static int MENU_REQUEST_CODE = 1;
    private static final int REQUEST_MENUS_SECOND = 22;
    private StickyLayout mStickyLayoutView;
    private View mStickyContentView;
    private View mNumberHeaderView;
    private UmTempSeekbarView mUmTempSeekbarView;
    private UmTimeSeekbarView mUmTimeSeekbarView;
    private View mErrorStatusList;
    private UMModelSetView mUMModelSetView;
    private UMSwitchButton mLiftUpSwitchButton;
    private UMScrollView mScrollView;
    //  Typeface typeface;
    private TextView mStatusView;
    private UMTextViewTemp mTempView;
    private ImageView mSpectialStatusView;
    private View mTitleBar;
    private BubbleLayout mBubbleAnimation;
    private MLAlertDialog mlAlertDialog;
    private AlertDialog mLiftUpWarnDialog;
    private Timer mTimer;
    private TimerTask mTimeTask;
    private boolean mDataRefresh = true;//数据是否刷新，水壶500ms左右，上报一次数据，设置后1000ms才开始读数据

    private boolean mIsOnline = true;
    private int mKeyChoose = UMGlobalParam.KEY_NULL;//按键选择
    private int mTempCustom;//设置温度
    private @UMGlobalParam.HeatModel
    int mHeatModel;//设定模式
    private @UMGlobalParam.Errors
    int mError;//异常
    private boolean mIsLiftUpHold = false;
    private boolean mIgnoreChange;

    private SharedPreferences mSharedPreferences;
    private File mLicenseFile;
    private File mPrivacyFile;
    public final static byte MSG_WHAT_INIT_LICENSE = 100;//初始化隐私协议文件

    public final static byte VERSION_LICENSE_ADD = 68;//添加隱私協議api
    private boolean mIsOwer = false;//是否主人

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.um_activity_mainscreen;
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate，getApiLevel=" + XmPluginHostApi.instance().getApiLevel());
        mTitleBar = findViewById(R.id.title_bar);
        if (mTitleBar != null) {
            mHostActivity.enableWhiteTranslucentStatus();
            mHostActivity.setTitleBarPadding(null);
        }

        mSharedPreferences = activity().getSharedPreferences(String.valueOf(mDeviceStat.did), Context.MODE_PRIVATE);
        Log.d(TAG, "model=" + mDeviceStat.model);
        // 显示隐私授权条例
        if (mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V2) || mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V3)
                || mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V5)) {
            String uid = mSharedPreferences.getString("uid", "");
            long time = mSharedPreferences.getLong("time", 0);
            if (uid.equals("") && time == 0) {
                boolean flag = XmPluginHostApi.instance().getApiLevel() >= VERSION_LICENSE_ADD;
                Log.d("@@@@@", "版本号：" + XmPluginHostApi.instance().getApiLevel());
                Log.d(TAG, "yinsi=" + flag);
                if (flag) {
                    lisenseInit();
                } else {
                    Log.d(TAG, "getApiLevel=" + XmPluginHostApi.instance().getApiLevel());
                    if (mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V5)) {
                        mHostActivity.showUserLicenseDialog(activity().getResources().getString(R.string.um_license_title),
                                getResources().getString(R.string.um_license_name),
                                Html.fromHtml(getResources().getString(R.string.um_license)),
                                getResources().getString(R.string.um_conceal_name),
                                Html.fromHtml(getResources().getString(R.string.um_ko_conceal)),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mSharedPreferences.edit().putString("uid", XmPluginHostApi.instance().getAccountId()).apply();
                                        mSharedPreferences.edit().putLong("time", System.currentTimeMillis()).apply();
                                    }
                                });
                    } else {
                        mHostActivity.showUserLicenseDialog(activity().getResources().getString(R.string.um_license_title),
                                getResources().getString(R.string.um_license_name),
                                Html.fromHtml(getResources().getString(R.string.um_license)),
                                getResources().getString(R.string.um_conceal_name),
                                Html.fromHtml(getResources().getString(R.string.um_conceal)),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mSharedPreferences.edit().putString("uid", XmPluginHostApi.instance().getAccountId()).apply();
                                        mSharedPreferences.edit().putLong("time", System.currentTimeMillis()).apply();
                                    }
                                });
                    }
                }
            }
        }
        init();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT_INIT_LICENSE) {
                mHostActivity.showUserLicenseUriDialog(activity().getResources().getString(R.string.um_license_title),
                        getResources().getString(R.string.um_license_name),
                        mLicenseFile.getAbsolutePath(),
                        getResources().getString(R.string.um_conceal_name),
                        mPrivacyFile.getAbsolutePath(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSharedPreferences.edit().putString("uid", XmPluginHostApi.instance().getAccountId()).apply();
                                mSharedPreferences.edit().putLong("time", System.currentTimeMillis()).apply();
                            }
                        });
            }
        }
    };

    private void lisenseInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyLicenseFile();
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_WHAT_INIT_LICENSE);
                }
            }
        }).start();
    }

    private void init() {
        Log.e(TAG, "mStickyLayoutView0=" + mStickyLayoutView);
        UMGlobalParam.getInstance().init();
        UMSceneManager.getInstance().init(this);
        //    typeface= UMGlobalParam.getInstance().getTextTypeface();
        mStickyLayoutView = (StickyLayout) findViewById(R.id.sticky_layout);
        mStickyLayoutView.setOnGiveUpTouchEventListener(this);
        mStickyContentView = mStickyLayoutView.findViewById(R.id.sticky_content);
        mStickyLayoutView.setSticky(true);
        mNumberHeaderView = findViewById(R.id.layout_number);
        mTempView = (UMTextViewTemp) mNumberHeaderView.findViewById(R.id.temp);
        //     mTempView.setTypeface(typeface);
        mStatusView = (TextView) mNumberHeaderView.findViewById(R.id.status_descride);
        mBubbleAnimation = (BubbleLayout) findViewById(R.id.bubble_animation);
        mSpectialStatusView = (ImageView) findViewById(R.id.status_view);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBubbleAnimation.getLayoutParams();
        mBubbleAnimation.setLayoutParams(layoutParams);

        mScrollView = (UMScrollView) findViewById(R.id.scrollview);
        View moreView = findViewById(R.id.title_bar_more);
        View shareView = findViewById(R.id.title_bar_share);
        View pointView = findViewById(R.id.title_bar_redpoint);
        pointView.setVisibility(View.INVISIBLE);

        TextView title = (TextView) findViewById(R.id.title_bar_title);
        title.setText(getString(R.string.umtitle));
//        tempView.setTextTargetChange(60);
//        tempView.setData(36,80);

        mUmTempSeekbarView = (UmTempSeekbarView) findViewById(R.id.layout_temp_set);
        mUmTempSeekbarView.setTempFirst(30);
        mUmTimeSeekbarView = (UmTimeSeekbarView) findViewById(R.id.layout_time_set);
        mUmTimeSeekbarView.setTimeFirst(12);
        mUMModelSetView = (UMModelSetView) findViewById(R.id.model_set);
        mUMModelSetView.setModelFirst(true, true);

        mLiftUpSwitchButton = (UMSwitchButton) findViewById(R.id.kettle_up_switch);
        // mErrorStatusList = findViewById(R.id.error_status_list);
        //mBubbleAnimation.setTemp(30);

        ImageView backBn = (ImageView) findViewById(R.id.title_bar_return);
        backBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        moreView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                ArrayList<IXmPluginHostActivity.MenuItemBase> menuItemBases=new ArrayList<IXmPluginHostActivity.MenuItemBase>();
//                IXmPluginHostActivity.IntentMenuItem intentMenuItem=new IXmPluginHostActivity.IntentMenuItem();
//                intentMenuItem.name=getString(R.string.um_string_mi_directions);
//                intentMenuItem.intent=mHostActivity.getActivityIntent(null, UMWebActivity.class.getName());
//                menuItemBases.add(intentMenuItem);
//                IXmPluginHostActivity.BleMenuItem upgraderItem=IXmPluginHostActivity.BleMenuItem.newUpgraderItem(new UMBlueToothUpgrader());
//                menuItemBases.add(upgraderItem);
//                mHostActivity.openMoreMenu(menuItemBases,true,MENU_REQUEST_CODE);

                ArrayList<IXmPluginHostActivity.MenuItemBase> menus = new
                        ArrayList<>();
                ////插件自定义菜单，可以在public void onActivityResult(int requestCode, int resultCode, Intent data) 中接收用户点击的菜单项，String result = data.getStringExtra("menu");
                IXmPluginHostActivity.StringMenuItem stringMenuItem = new IXmPluginHostActivity.StringMenuItem();
                stringMenuItem.name = getString(R.string.um_common_setting);
                menus.add(stringMenuItem);

                Intent intent = new Intent();
                intent.putExtra("scence_enable", false);
                intent.putExtra("common_setting_enable", false);
                mHostActivity.openMoreMenu2(menus, true, MENU_REQUEST_CODE, intent);

            }
        });
        shareView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mHostActivity.openShareActivity();
            }
        });
        BaseDevice baseDevice = new BaseDevice(mDeviceStat);
        if (baseDevice.isOwner()) {
            shareView.setVisibility(View.VISIBLE);
            moreView.setVisibility(View.VISIBLE);
            mIsOwer = true;
        } else if (baseDevice.isFamily() && !baseDevice.isOwner()) {
            shareView.setVisibility(View.GONE);
            moreView.setVisibility(View.GONE);
        } else if (!baseDevice.isFamily() && !baseDevice.isOwner()) {
            shareView.setVisibility(View.GONE);
            moreView.setVisibility(View.GONE);
        }

        if (XmPluginHostApi.instance().getApiLevel() >= 32) {
            int connState = XmBluetoothManager.getInstance().getConnectStatus(mDeviceStat.mac);
            if (connState != XmBluetoothManager.STATE_CONNECTED) {
                setOfflineView();
            }
        }

        mUmTimeSeekbarView.setOnTimeChangeListener(new UmTimeSeekbarView.OnTimeChangeListener() {
            @Override
            public void onTimeChange(float time) {
                UMBluetoothManager.getInstance().setKeepWarmTime(time);
                startTimer();
            }
        });

        mUmTempSeekbarView.setOnTempChangeListener(new UmTempSeekbarView.OnTempChangeListener() {
            @Override
            public void onTempChange(final int temp) {

                int flag = mKeyChoose;
                Log.d(TAG, "onTempChange,getKeyChoose=" + flag + ",UMGlobalParam.KEY_NULL=" + UMGlobalParam.KEY_NULL);
                if (flag == UMGlobalParam.KEY_NULL) {
                    // UMCustomToast.showToast(activity(),getString(R.string.toast_idle_temp_set), Toast.LENGTH_SHORT);
                    UMBluetoothManager.getInstance().onSetup(mHeatModel, temp);
                    startTimer();
                } else if (flag == UMGlobalParam.KEY_BOIL) {
                    UMBluetoothManager.getInstance().onSetup(mHeatModel, temp);
                    startTimer();
                } else if (flag == UMGlobalParam.KEY_KEEP_WARM && mTempCustom != temp) {
                    mlAlertDialog = new MLAlertDialog.Builder(activity()).setMessage(getString(R.string.toast_keep_warm_temp_set))
                            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUmTempSeekbarView.setTemp(mTempCustom);

                                }
                            })
                            .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UMBluetoothManager.getInstance().onSetup(mHeatModel, temp);
                                    startTimer();

                                }
                            }).create();
                    mlAlertDialog.show();
                }

            }

            @Override
            public void onMoreLayoutClick() {
                startActivity(null, UMSceneActivity.class.getName());
            }
        });

        mUMModelSetView.setOnModelChangeListener(new UMModelSetView.OnModelChangeListener() {
            @Override
            public void onBoilModelChange(boolean isBoil) {
                if (isBoil) {
                    mHeatModel = UMGlobalParam.MODEL_KEEP_WARM_BOIL;
                } else {
                    mHeatModel = UMGlobalParam.MODEL_KEEP_WARM_NOT_BOIL;
                }
                UMBluetoothManager.getInstance().onSetup(mHeatModel, mTempCustom);
                startTimer();
            }

            @Override
            public void onRepeatBoilPrevent(boolean flag) {
                if (flag) {
                    UMBluetoothManager.getInstance().setBoilModeSet(1);
                } else {
                    UMBluetoothManager.getInstance().setBoilModeSet(0);
                }
                startTimer();

            }
        });

        mStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mError == UMGlobalParam.ERROR_NOT) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("position", mError);
                startActivity(intent, UMErrorDetailActivity.class.getName());
            }
        });

        UMBluetoothManager.getInstance().setMainView(mUmTimeSeekbarView, mUmTempSeekbarView, mUMModelSetView, pointView);
        UMBluetoothManager.getInstance().init(activity(), mDeviceStat, mIsOwer, new UMStatusInterface() {
            @Override
            public void onStatusDataReceive(byte[] data) {

                refreshView(data);
            }

            @Override
            public void isOnlineChange(boolean isOnline) {
                Log.d(TAG, "isOnline=" + isOnline);
                mIsOnline = isOnline;
                if (!mIsOnline) {
                    setOfflineView();
                }
            }
        });
        mLiftUpSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (!mIgnoreChange) {

                    if (isChecked) {
                        if (UMBluetoothManager.getInstance().getCurrentVersion().length() != 0
                                && UMBluetoothManager.getInstance().getCurrentVersion().compareToIgnoreCase("6.2.0.8") <= 0) {
                            Toast.makeText(activity(), getString(R.string.text_kettle_lift_up_upgrade_tips), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mLiftUpWarnDialog == null) {
                            LayoutInflater layoutInflater = LayoutInflater.from(activity());
                            View view = layoutInflater.inflate(R.layout.um_dialog_lift_up, null);

                            TextView buttonCancel = (TextView) view.findViewById(R.id.button_cancel);
                            TextView buttonOk = (TextView) view.findViewById(R.id.button_confirm);
                            mLiftUpWarnDialog = new AlertDialog.Builder(activity()).setView(view).create();
                            Window dialogWindow = mLiftUpWarnDialog.getWindow();
                            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                            lp.width = PhoneUtil.dipToPx(activity(), 600);
                            dialogWindow.setAttributes(lp);

                            buttonCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mLiftUpWarnDialog.dismiss();
                                }
                            });
                            buttonOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mLiftUpWarnDialog.dismiss();
                                    int value = 0;
                                    if (isChecked) {
                                        value = 1;
                                    } else {
                                        value = 0;
                                    }
                                    UMBluetoothManager.getInstance().onSetup(0x03, value);
                                    startTimer();
                                    mIgnoreChange = true;
                                    mLiftUpSwitchButton.setChecked(true);
                                    mIgnoreChange = false;
                                }
                            });
                        }
                        mLiftUpWarnDialog.show();
//                        mlAlertDialog=   new MLAlertDialog.Builder(activity()).setMessage(getString(R.string.text_kettle_lift_up_tips))
//                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        int value=0;
//                                        if(isChecked){
//                                            value=1;
//                                        }else {
//                                            value=0;
//                                        }
//                                        UMBluetoothManager.getInstance().onSetup(0x03,value);
//                                        startTimer();
//                                        mIgnoreChange=true;
//                                        mLiftUpSwitchButton.setChecked(true);
//                                        mIgnoreChange=false;
//                                    }
//                                }).create();
//                        mlAlertDialog.show();
                    } else {
                        int value = 0;
                        if (isChecked) {
                            value = 1;
                        } else {
                            value = 0;
                        }
                        UMBluetoothManager.getInstance().onSetup(0x03, value);
                        startTimer();
                    }

                }
            }
        });
        UMBluetoothManager.getInstance().readMcuVersion();
        UMBluetoothManager.getInstance().openEachRecordNotify();
    }

    /***
     * 获取html内容
     * @param assetHtml
     * @return
     */
    private String getHtmlContent(String assetHtml) {
        InputStream is = null;
        try {
            is = getAssets().open(assetHtml);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /***
     * 复制隐私协议文件到sdcard里
     */
    private void copyLicenseFile() {

        String licenseFileName = "license.html";
        String privacyFileName = "private.html";
        if (mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V5)) {
            privacyFileName = "private_ko.html";
        }
        String rootPath = activity().getFilesDir().getAbsolutePath();
        mLicenseFile = new File(rootPath, mDeviceStat.model + "_license.html");
        mPrivacyFile = new File(rootPath, mDeviceStat.model + "_privacy.html");
        try {
            log.d(TAG, "copyLicenseFile mLicenseFile:" + mLicenseFile.getAbsolutePath());
            log.d(TAG, "copyLicenseFile mPrivacyFile:" + mPrivacyFile.getAbsolutePath());
            FileUtil.copyFromAssets(getAssets(), licenseFileName, mLicenseFile.getAbsolutePath(), true);
            FileUtil.copyFromAssets(getAssets(), privacyFileName, mPrivacyFile.getAbsolutePath(), true);
        } catch (IOException e) {
            Log.e(TAG, "copyLicenseFile error！msg=:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /***
     * 隐私文件是否存在
     * @return
     */
    private boolean isLicenseFileExist() {
        String rootPath = activity().getFilesDir().getAbsolutePath();
        File licenseFile = new File(rootPath, mDeviceStat.model + "_license.html");
        File privacyFile = new File(rootPath, mDeviceStat.model + "_privacy.html");
        boolean licenseFileFlag = FileUtil.isFileExist(licenseFile.getAbsolutePath());
        boolean privacyFileFlag = FileUtil.isFileExist(privacyFile.getAbsolutePath());
        if (licenseFileFlag & privacyFileFlag) {
            mLicenseFile = licenseFile;
            mPrivacyFile = privacyFile;
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "code=" + requestCode);

        switch (requestCode) {
            case MENU_REQUEST_CODE:
                if (data != null) {
                    String name = data.getStringExtra("menu");
                    log.d(TAG, "menu=" + name);
                    if (!TextUtils.isEmpty(name)) {
                        if (name.equals(getString(R.string.um_common_setting))) {
                            openSubMenu1();
                        }
                    }
                }
                break;
            case REQUEST_MENUS_SECOND:
                // 撤销用户协议
                if (data != null && data.getExtras() != null) {
                    String result_data = data.getStringExtra("result_data");
                    if ("removedLicense".equals(result_data)) {
                        mSharedPreferences.edit().putString("uid", "").apply();
                        mSharedPreferences.edit().putLong("time", 0).apply();
                        UMGlobalParam.getInstance().clear();
                        activity().finish();
                    }
                }
                break;
        }
    }

    //传入升级管理
    private void openSubMenu1() {
        ArrayList<IXmPluginHostActivity.MenuItemBase> items = new ArrayList<IXmPluginHostActivity.MenuItemBase>();
        IXmPluginHostActivity.IntentMenuItem intentMenuItem = new IXmPluginHostActivity.IntentMenuItem();
        intentMenuItem.name = getString(R.string.um_device_info);
        intentMenuItem.intent = mHostActivity.getActivityIntent(null, UMDeviceInfoActivity.class.getName());
        intentMenuItem.intent.putExtra("mac", mDeviceStat.mac);
        items.add(intentMenuItem);
        IXmPluginHostActivity.BleMenuItem bleMenu = IXmPluginHostActivity.BleMenuItem.newUpgraderItem(new UMBlueToothUpgrader());
        items.add(bleMenu);
        Intent commonSettingIntent = new Intent();
        boolean flag = XmPluginHostApi.instance().getApiLevel() >= VERSION_LICENSE_ADD;
        Log.d(TAG, "yinsi1=" + flag);
        if (flag) {
            if (mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V2) || mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V3)
                    || mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V5)) {
                if (mLicenseFile == null || mPrivacyFile == null) {//没加载过隐私协议文件
                    if (!isLicenseFileExist()) {//检测是否已经有隐私协议文件，有的话加载,没的话复制一份然后加载，先不考虑io操作阻塞
                        copyLicenseFile();
                    }
                }
                log.d(TAG, "openMoreMenu mLicenseFile:" + mLicenseFile.getAbsolutePath());
                log.d(TAG, "openMoreMenu mPrivacyFile:" + mPrivacyFile.getAbsolutePath());
                commonSettingIntent.putExtra("enableRemoveLicense", true);
                commonSettingIntent.putExtra("licenseContentUri", mLicenseFile.getAbsolutePath());
                commonSettingIntent.putExtra("privacyContentUri", mPrivacyFile.getAbsolutePath());
//           commonSettingIntent.putExtra("licenseContentHtml",getHtmlContent("private_ko.html"));
//           commonSettingIntent.putExtra("privacyContentHtml",getHtmlContent("private_ko.html"));
            }
        } else {
            if (mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V2) || mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V3)) {
                commonSettingIntent.putExtra("enableRemoveLicense", true);
                commonSettingIntent.putExtra("licenseContent", Html.fromHtml(activity().getResources().getString(R.string.um_license)));
                commonSettingIntent.putExtra("privacyContent", Html.fromHtml(activity().getResources().getString(R.string.um_conceal)));
            } else if (mDeviceStat.model.equals(UMGlobalParam.MODEL_KETTLE_V5)) {
                commonSettingIntent.putExtra("enableRemoveLicense", true);
                commonSettingIntent.putExtra("licenseContent", Html.fromHtml(activity().getResources().getString(R.string.um_license)));
                commonSettingIntent.putExtra("privacyContent", Html.fromHtml(activity().getResources().getString(R.string.um_ko_conceal)));
            }
        }

        hostActivity().openMoreMenu(items, true, REQUEST_MENUS_SECOND, commonSettingIntent);
    }

    private void setOfflineView() {
        mSpectialStatusView.setImageDrawable(getResources().getDrawable(R.drawable.status_disconnect));
        mSpectialStatusView.setVisibility(View.VISIBLE);
        mTempView.setVisibility(View.INVISIBLE);
        mStatusView.setText(getString(R.string.um_desc_offline));
        if (mBubbleAnimation != null) {
            mBubbleAnimation.setOffline();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        UMBluetoothManager.getInstance().runUpdateInfo();
        mUmTempSeekbarView.setTempFirst(mTempCustom);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UMBluetoothManager.getInstance().disconnect();
        UMBluetoothManager.getInstance().close();
        stopTimer();
        if (mBubbleAnimation != null) {
            mBubbleAnimation.close();
        }

        UMSceneManager.getInstance().close();
        mStickyLayoutView.close();
        mUmTempSeekbarView.close();
        mUmTimeSeekbarView.close();
        mUMModelSetView.close();

        if (mLiftUpWarnDialog != null) {
            mLiftUpWarnDialog.dismiss();
        }
        if (mlAlertDialog != null) {
            mlAlertDialog.dismiss();
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_WHAT_INIT_LICENSE);
            mHandler = null;
        }
    }

    /****
     * 刷新状态界面
     * @param data 接收蓝牙通知数据
     */
    private void refreshView(byte[] data) {

        if (data == null) {
            Log.e(TAG, "MSG_WHAT_STATUS_VARY,data null!");
            return;
        }
        if (data.length < 7) {
            Log.e(TAG, "MSG_WHAT_STATUS_VARY,data length is " + data.length + ",not correct!");
            return;
        }

        //状态显示
        @UMGlobalParam.Status int status = data[0];

        //按键选择
        mKeyChoose = data[1] & 0xff;

        //是否完成
        boolean isCompleted = false;
        if (data[2] == 0) {
            isCompleted = false;
        } else if (data[2] == 1) {
            isCompleted = true;
        }

        //异常
        mError = data[3];
        //      mError=UMGlobalParam.ERROR_PARCH;
//        status=UMGlobalParam.STATUS_ABNORMAL;

        //	setErrorText(mError);

        //自定义温度
        int mTemp = data[4] & 0xff;
        mTempCustom = mTemp;

        //实际温度显示
        int currentTemp = data[5] & 0xff;
        if (currentTemp < 0) {
            currentTemp = 0;
        } else if (currentTemp > 100) {
            currentTemp = 100;
        }
        mTempView.setTextNum(currentTemp);

        //自定义模式
        mHeatModel = data[6];
        boolean isBiol;
        if (mHeatModel == UMGlobalParam.MODEL_KEEP_WARM_BOIL) {
            isBiol = true;
        } else {
            isBiol = false;
        }

        //保温消耗时间、煮沸模式特殊模式、保温时间
        float mSetTime = UMGlobalParam.MAX_KEEP_WARM_TIME;
        boolean boilModeSelect = true;
        int mConsumeTime = 0;

        if (data.length >= 11) {
            mConsumeTime = data[7] & 0xff + (data[8] & 0xff) * 256;
            log.d(TAG, "mConsumeTime=" + mConsumeTime);

            if (data[9] == 0) {
                boilModeSelect = false;
            } else if (data[9] == 1) {
                boilModeSelect = true;
            }

            mSetTime = (float) ((int) (data[10] & 0xff) * 0.5);
            if (mSetTime > UMGlobalParam.MAX_KEEP_WARM_TIME) {
                mSetTime = UMGlobalParam.MAX_KEEP_WARM_TIME;
            } else if (mSetTime < UMGlobalParam.MIN_KEEP_WARM_TIME) {
                mSetTime = UMGlobalParam.MIN_KEEP_WARM_TIME;
            }

            //提壶记忆功能开关
            if (data.length >= 12) {
                if (data[11] == 0) {
                    mIsLiftUpHold = false;
                } else if (data[11] == 1) {
                    mIsLiftUpHold = true;
                }
            }
        }
        mStatusView.setText(getStatusString(mIsOnline, status, isCompleted, currentTemp, mTempCustom, mConsumeTime));
        mBubbleAnimation.setTemp(currentTemp);
        onSpecialStatusShow(mIsOnline, status, mError, currentTemp);
        if (!mDataRefresh) {
            return;
        }
        mUmTimeSeekbarView.setTime(mSetTime);
        //弹出工作中修改确认窗口时，不刷新设置温度
        if (mlAlertDialog == null || (!mlAlertDialog.isShowing())) {
            mUmTempSeekbarView.setTemp(mTemp);
        }

        mUMModelSetView.setModel(isBiol);
        mUMModelSetView.setBoilModeSelect(boilModeSelect);

        if ((!mIsLiftUpHold) && mLiftUpSwitchButton.isChecked()) {
            mIgnoreChange = true;
            mLiftUpSwitchButton.setChecked(false);
            mIgnoreChange = false;
        } else if (mIsLiftUpHold && (!mLiftUpSwitchButton.isChecked())) {
            mIgnoreChange = true;
            mLiftUpSwitchButton.setChecked(true);
            mIgnoreChange = false;
        }

    }

    /***
     * 特殊状态显示 离线/沸腾/异常
     * @param mIsOnline
     * @param status
     * @param error
     * @param temp
     */
    private void onSpecialStatusShow(boolean mIsOnline, int status, int error, int temp) {
        log.d(TAG, "onSpecialStatusShow,mIsOnline=" + mIsOnline + ",status=" + status + ",error=" + error + ",temp=" + temp);
        mTempView.setVisibility(View.VISIBLE);
        if (!mIsOnline) {
            setOfflineView();
            return;
        } else {
            if (status == UMGlobalParam.STATUS_ABNORMAL) {
                mSpectialStatusView.setImageDrawable(getResources().getDrawable(R.drawable.status_error));
                mSpectialStatusView.setVisibility(View.VISIBLE);
                mTempView.setVisibility(View.INVISIBLE);
                mStatusView.setText(getErrorText(error));
            } else {
                if ((temp >= UMGlobalParam.BOIL_TEMP && status == UMGlobalParam.STATUS_HEATING) || (temp >= 100)) {
                    mSpectialStatusView.setImageDrawable(getResources().getDrawable(R.drawable.status_boil));
                    mSpectialStatusView.setVisibility(View.VISIBLE);
                    mTempView.setVisibility(View.INVISIBLE);
                    mStatusView.setText(getString(R.string.um_desc_boiling));
                } else {
                    mSpectialStatusView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /***
     * 设置异常显示
     * @param errorCode
     */
    public String getErrorText(@UMGlobalParam.Errors int errorCode) {

        String displayStr = "";
        switch (errorCode) {
            case UMGlobalParam.ERROR_NOT:
                displayStr = getString(R.string.um_error_not);
                break;
            case UMGlobalParam.ERROR_HEAT:
                displayStr = getString(R.string.um_error_heat);
                break;
            case UMGlobalParam.ERROR_SENSOR:
                displayStr = getString(R.string.um_error_sensor);
                break;
            case UMGlobalParam.ERROR_PARCH:
                displayStr = getString(R.string.um_error_parch);
                break;
            default:
                displayStr = getString(R.string.um_error_unknow);
                break;
        }
        return displayStr;

    }


    //状态显示
    private String getStatusString(boolean isOnline, int status, boolean isCompleted, int currentTemp, int setTemp, int mConsumeTime) {
        String statusStr = "";
        log.d(TAG, "isOnline=" + isOnline + ",status=" + status + ",isCompleted=" + isCompleted);
        if (isOnline) {
            if (status == UMGlobalParam.STATUS_IDLE) {
                statusStr = getString(R.string.um_status_close);
                mBubbleAnimation.setBubblesRun(false);
            } else if (status == UMGlobalParam.STATUS_HEATING) {
                mBubbleAnimation.setBubblesRun(true);
                statusStr = getString(R.string.um_status_heating);
            } else if (status == UMGlobalParam.STATUS_KEEP_WARM_NOT__BOIL) {
                mBubbleAnimation.setBubblesRun(false);
                if (mConsumeTime <= 0) {
                    statusStr = getString(R.string.um_status_keep_warm);
                } else {
                    if (mConsumeTime < 60) {
                        statusStr = getString(R.string.um_status_keep_warm1) + " " + mConsumeTime + " " + getString(R.string.um_status_keep_warm_m);
                    } else {
                        int hour = mConsumeTime / 60;
                        int minute = mConsumeTime % 60;
                        if (minute == 0) {
                            statusStr = getString(R.string.um_status_keep_warm1) + " " + hour + " " + getString(R.string.um_status_keep_warm_h);
                        } else {
                            statusStr = getString(R.string.um_status_keep_warm1) + " " + hour + " " + getString(R.string.um_status_keep_warm_h)
                                    + " " + minute + " " + getString(R.string.um_status_keep_warm_m);
                        }
                    }

                }

            } else if (status == UMGlobalParam.STATUS_KEEP_WARM_BOIL) {
                mBubbleAnimation.setBubblesRun(false);
                if ((!isCompleted) && currentTemp > setTemp) {

                    statusStr = getString(R.string.um_status_keep_warm_temp_down);
                } else {
                    if (mConsumeTime <= 0) {
                        statusStr = getString(R.string.um_status_keep_warm);
                    } else {
                        if (mConsumeTime < 60) {
                            statusStr = getString(R.string.um_status_keep_warm1) + " " + mConsumeTime + " " + getString(R.string.um_status_keep_warm_m);
                        } else {
                            int hour = mConsumeTime / 60;
                            int minute = mConsumeTime % 60;
                            if (minute == 0) {
                                statusStr = getString(R.string.um_status_keep_warm1) + " " + hour + " " + getString(R.string.um_status_keep_warm_h);
                            } else {
                                statusStr = getString(R.string.um_status_keep_warm1) + " " + hour + " " + getString(R.string.um_status_keep_warm_h)
                                        + " " + minute + " " + getString(R.string.um_status_keep_warm_m);
                            }
                        }

                    }
                }
            } else {
                mBubbleAnimation.setBubblesRun(false);
                statusStr = getString(R.string.um_status_abnormal);
            }
        }
        return statusStr;
    }


    @Override
    public boolean giveUpTouchEvent(MotionEvent event, int direct) {

        if (mScrollView.getScrollY() == 0) {
            log.d("StickyLayout", "giveUpTouchEvent");
            return true;
        }
        return false;
    }

    @Override
    public void onScrollCallBack(float per) {

    }


    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
    }

    public void startTimer() {
        stopTimer();
        mDataRefresh = false;
        mTimer = new Timer();
        mTimeTask = new TimerTask() {
            @Override
            public void run() {
                mDataRefresh = true;
            }
        };
        mTimer.schedule(mTimeTask, 1400);

    }

    private void setTitleColorByInt(int value) {
        int minValue = 30;
        int maxValue = 100;
        int minColor = 180;
        int maxColor = 360;
        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }
        float per = ((float) (maxColor - minColor)) / (maxValue - minValue);
        int color = (int) ((value - minValue) * per + minColor);
        setLayoutBackgroundColor(color);
    }

    /***
     * 修改背景颜色
     * @param value hsv里的色彩值。
     * hsv有三个成员，hsv[0]的范围是[0,360),表示色彩，hsv[1]范围[0,1]表示饱和度，hsv[2]范围[0,1]表示值
     */
    public void setLayoutBackgroundColor(int value) {
        float[] mHSVColor = new float[3];
        mHSVColor[0] = value;
        mHSVColor[1] = 1;
        mHSVColor[2] = 1;
        int color = Color.HSVToColor(mHSVColor);
        mTitleBar.setBackgroundColor(color);
    }
}
