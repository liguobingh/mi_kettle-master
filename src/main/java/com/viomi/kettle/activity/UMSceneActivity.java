package com.viomi.kettle.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.viomi.kettle.R;
import com.viomi.kettle.data.KettleScene;
import com.viomi.kettle.dev.UMSceneManager;
import com.viomi.kettle.view.UMChartAnimView;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by young2 on 2016/6/6.
 */
public class UMSceneActivity extends UMBaseActivity implements View.OnClickListener{
    private UMChartAnimView mUMChartAnimView;
    private RadioButton mCustomSceneBn0,mCustomSceneBn1,mCustomSceneBn2;
    private RadioButton mSceneButton0,mSceneButton1,mSceneButton2,mSceneButton3,mSceneButton4;
    private ArrayList<KettleScene> mSceneList;
    private ArrayList<KettleScene>  mChooseList=new ArrayList<>();
    private int[] mIndexSet;
    private int mIndex=0;
    private MLAlertDialog mlAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId= R.layout.um_scene_activity;
        super.onCreate(savedInstanceState);
        View title_bar = findViewById(R.id.title_bar);
        if(title_bar!=null){
            mHostActivity.enableWhiteTranslucentStatus();
        }
        init();

    }

    private void init(){

        View moreView = findViewById(R.id.title_bar_more);
        View shareView = findViewById(R.id.title_bar_share);
        View pointView = findViewById(R.id.title_bar_redpoint);
        pointView.setVisibility(View.INVISIBLE);
        moreView.setVisibility(View.INVISIBLE);
        shareView.setVisibility(View.INVISIBLE);
        TextView title=(TextView)findViewById(R.id.title_bar_title);
        title.setText(getString(R.string.umtitle));
        FrameLayout titleBar=(FrameLayout)findViewById(R.id.title_bar);
        titleBar.setBackgroundColor(getResources().getColor(R.color.background));
        ImageView backBn=(ImageView)findViewById(R.id.title_bar_return);
        backBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();;
            }
        });
        initScene();


    }

    private  void  initScene(){
        mCustomSceneBn0= (RadioButton) findViewById(R.id.custom_scene0);
        mCustomSceneBn1= (RadioButton) findViewById(R.id.custom_scene1);
        mCustomSceneBn2= (RadioButton) findViewById(R.id.custom_scene2);
        mSceneButton0= (RadioButton) findViewById(R.id.scene0);
        mSceneButton1= (RadioButton) findViewById(R.id.scene1);
        mSceneButton2= (RadioButton) findViewById(R.id.scene2);
        mSceneButton3= (RadioButton) findViewById(R.id.scene3);
        mSceneButton4= (RadioButton) findViewById(R.id.scene4);
        mSceneButton0.setOnClickListener(this);
        mSceneButton1.setOnClickListener(this);
        mSceneButton2.setOnClickListener(this);
        mSceneButton3.setOnClickListener(this);
        mSceneButton4.setOnClickListener(this);
        mSceneList= UMSceneManager.getInstance().getSceneList();
        mChooseList=UMSceneManager.getInstance().getChooseScene();

        mSceneButton0.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(0).PressDrawable),null,null);
        mSceneButton0.setText(mSceneList.get(0).Value+" ℃"+"\n"+mSceneList.get(0).name);
        mSceneButton1.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(1).PressDrawable),null,null);
        mSceneButton1.setText(mSceneList.get(1).Value+" ℃"+"\n"+mSceneList.get(1).name);
        mSceneButton2.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(2).PressDrawable),null,null);
        mSceneButton2.setText(mSceneList.get(2).Value+" ℃"+"\n"+mSceneList.get(2).name);
        mSceneButton3.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(3).PressDrawable),null,null);
        mSceneButton3.setText(mSceneList.get(3).Value+" ℃"+"\n"+mSceneList.get(3).name);
        mSceneButton4.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(4).PressDrawable),null,null);
        mSceneButton4.setText(mSceneList.get(4).Value+" ℃"+"\n"+mSceneList.get(4).name);
        setSceneChoose(mChooseList);

    }

    private void setSceneChoose(ArrayList<KettleScene> chooseList){
        if(chooseList==null||chooseList.size()<3){
            return;
        }
        mCustomSceneBn0.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(chooseList.get(0).PressDrawable),null,null);
        mCustomSceneBn0.setText(chooseList.get(0).Value+" ℃"+"\n"+chooseList.get(0).name);
        mCustomSceneBn1.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(chooseList.get(1).PressDrawable),null,null);
        mCustomSceneBn1.setText(chooseList.get(1).Value+" ℃"+"\n"+chooseList.get(1).name);
        mCustomSceneBn2.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(chooseList.get(2).PressDrawable),null,null);
        mCustomSceneBn2.setText(chooseList.get(2).Value+" ℃"+"\n"+chooseList.get(2).name);

        int i=0;
        if(chooseList.contains(mSceneList.get(i))){
            mSceneButton0.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).SelectedDrawable),null,null);
            mSceneButton0.setTextColor(getResources().getColor(R.color.button_scene_press_color));
        }else {
            mSceneButton0.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).PressDrawable),null,null);
            mSceneButton0.setTextColor(getResources().getColor(R.color.button_scene_normal_color));
        }
        i++;
        if(chooseList.contains(mSceneList.get(i))){
            mSceneButton1.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).SelectedDrawable),null,null);
            mSceneButton1.setTextColor(getResources().getColor(R.color.button_scene_press_color));
        }else {
            mSceneButton1.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).PressDrawable),null,null);
            mSceneButton1.setTextColor(getResources().getColor(R.color.button_scene_normal_color));
        }
        i++;
        if(chooseList.contains(mSceneList.get(i))){
            mSceneButton2.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).SelectedDrawable),null,null);
            mSceneButton2.setTextColor(getResources().getColor(R.color.button_scene_press_color));
        }else {
            mSceneButton2.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).PressDrawable),null,null);
            mSceneButton2.setTextColor(getResources().getColor(R.color.button_scene_normal_color));
        }
        i++;
        if(chooseList.contains(mSceneList.get(i))){
            mSceneButton3.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).SelectedDrawable),null,null);
            mSceneButton3.setTextColor(getResources().getColor(R.color.button_scene_press_color));
        }else {
            mSceneButton3.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).PressDrawable),null,null);
            mSceneButton3.setTextColor(getResources().getColor(R.color.button_scene_normal_color));
        }
        i++;
        if(chooseList.contains(mSceneList.get(i))){
            mSceneButton4.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).SelectedDrawable),null,null);
            mSceneButton4.setTextColor(getResources().getColor(R.color.button_scene_press_color));
        }else {
            mSceneButton4.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(mSceneList.get(i).PressDrawable),null,null);
            mSceneButton4.setTextColor(getResources().getColor(R.color.button_scene_normal_color));
        }
    }


    @Override
    public void onClick(View v) {
        mIndexSet=UMSceneManager.getInstance().getSceneIndexSet();
        switch (v.getId()){
            case R.id.scene0:
                mIndex=0;
                break;
            case R.id.scene1:
                mIndex=1;
                break;
            case R.id.scene2:
                mIndex=2;
                break;
            case R.id.scene3:
                mIndex=3;
                break;
            case R.id.scene4:
                mIndex=4;
                break;
            default:
                return;
        }
        for(int i=0;i<mIndexSet.length;i++){
            if(mIndex==mIndexSet[i]){
                Toast.makeText(activity(),R.string.um_scene_selected,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        LayoutInflater inflater = LayoutInflater.from(activity());
        View view=inflater.inflate(R.layout.um_scene_layout,null);
        mlAlertDialog =new MLAlertDialog.Builder(activity()).setTitle(R.string.title_choose_replace_scene)
                .setView(view)
                .setNegativeButton(R.string.button_cancel,null)
                .show();
        RadioButton chooseButton0= (RadioButton) view.findViewById(R.id.replace_scene0);
        RadioButton chooseButton1= (RadioButton) view.findViewById(R.id.replace_scene1);
        RadioButton chooseButton2= (RadioButton) view.findViewById(R.id.replace_scene2);

        ArrayList<KettleScene> chooseList= UMSceneManager.getInstance().getChooseScene();
        chooseButton0.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(chooseList.get(0).PressDrawable),null,null);
        chooseButton0.setText(chooseList.get(0).Value+" ℃"+"\n"+chooseList.get(0).name);
        chooseButton1.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(chooseList.get(1).PressDrawable),null,null);
        chooseButton1.setText(chooseList.get(1).Value+" ℃"+"\n"+chooseList.get(1).name);
        chooseButton2.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(chooseList.get(2).PressDrawable),null,null);
        chooseButton2.setText(chooseList.get(2).Value+" ℃"+"\n"+chooseList.get(2).name);

        chooseButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndexSet[0]=mIndex;
                UMSceneManager.getInstance().setChooseScene(mIndexSet);
                setSceneChoose( UMSceneManager.getInstance().getChooseScene());

                if(mlAlertDialog!=null){
                    mlAlertDialog.dismiss();
                }

            }
        });
        chooseButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndexSet[1]=mIndex;
                UMSceneManager.getInstance().setChooseScene(mIndexSet);
                setSceneChoose( UMSceneManager.getInstance().getChooseScene());

                if(mlAlertDialog!=null){
                    mlAlertDialog.dismiss();
                }
            }
        });
        chooseButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndexSet[2]=mIndex;
                UMSceneManager.getInstance().setChooseScene(mIndexSet);
                setSceneChoose( UMSceneManager.getInstance().getChooseScene());
                if(mlAlertDialog!=null){
                    mlAlertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mlAlertDialog!=null){
            mlAlertDialog.dismiss();
        }
    }
}
