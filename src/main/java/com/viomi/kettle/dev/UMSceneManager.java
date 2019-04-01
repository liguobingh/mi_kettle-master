package com.viomi.kettle.dev;

import android.content.Context;
import android.util.Log;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.data.KettleScene;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by young2 on 2016/6/3.
 */
public class  UMSceneManager{
    private final static String TAG="UMSceneManager";
    private static UMSceneManager INSTANCE;
    private Context mContext;
    private ArrayList<KettleScene> mSceneList=new ArrayList<>();
    ArrayList<KettleScene>  mChooseList=new ArrayList<>();
    private int mChooseCount=3;//选择常用的数目
    private int mSumCount=5;//总的场景的数目

    public static UMSceneManager getInstance(){
        synchronized (UMSceneManager.class){
            if(INSTANCE==null){
                synchronized (UMSceneManager.class){
                    if(INSTANCE==null){
                        INSTANCE=new UMSceneManager();
                    }
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context){

        mContext= context;
        KettleScene kettleScene1=new KettleScene();
        kettleScene1.Value=90;
       kettleScene1.Desc=mContext.getString(R.string.text_scene_coffee);
       kettleScene1.name=mContext.getString(R.string.text_coffee);
        kettleScene1.ChooseDrawable=R.drawable.um_scene_coffee_main;
        kettleScene1.PressDrawable=R.drawable.um_scene_coffee_sub;
        kettleScene1.SelectedDrawable=R.drawable.um_scene_coffee_select;

        KettleScene kettleScene2=new KettleScene();
        kettleScene2.Value=80;
        kettleScene2.Desc=mContext.getResources().getString(R.string.text_scene_tea);
        kettleScene2.name=mContext.getResources().getString(R.string.text_tea);
        kettleScene2.ChooseDrawable=R.drawable.um_scene_tea_main;
        kettleScene2.PressDrawable=R.drawable.um_scene_tea_sub;
        kettleScene2.SelectedDrawable=R.drawable.um_scene_tea_select;

        KettleScene kettleScene3=new KettleScene();
        kettleScene3.Value=70;
        kettleScene3.Desc=mContext.getResources().getString(R.string.text_scene_cereal);
        kettleScene3.name=mContext.getResources().getString(R.string.text_cereal);
        kettleScene3.ChooseDrawable=R.drawable.um_scene_cereal_main;
        kettleScene3.PressDrawable=R.drawable.um_scene_cereal_sub;
        kettleScene3.SelectedDrawable=R.drawable.um_scene_cereal_select;

        KettleScene kettleScene4=new KettleScene();
        kettleScene4.Value=50;
        kettleScene4.Desc=mContext.getResources().getString(R.string.text_scene_milk);
       kettleScene4.name=mContext.getResources().getString(R.string.text_milk);
        kettleScene4.ChooseDrawable=R.drawable.um_scene_milk_main;
        kettleScene4.PressDrawable=R.drawable.um_scene_milk_sub;
        kettleScene4.SelectedDrawable=R.drawable.um_scene_milk_select;

        KettleScene kettleScene5=new KettleScene();
        kettleScene5.Value=40;
        kettleScene5.Desc=mContext.getString(R.string.text_scene_protiotics);
        kettleScene5.name=mContext.getString(R.string.text_protiotics);
        kettleScene5.ChooseDrawable=R.drawable.um_scene_protiotics_main;
        kettleScene5.PressDrawable=R.drawable.um_scene_protiotics_sub;
        kettleScene5.SelectedDrawable=R.drawable.um_scene_protiotics_select;

        mSceneList.add(kettleScene1);
        mSceneList.add(kettleScene2);
        mSceneList.add(kettleScene3);
        mSceneList.add(kettleScene4);
        mSceneList.add(kettleScene5);
    }


    public void setChooseScene(int[] set){
        if(set==null||set.length<mChooseCount){
            Log.e(TAG,"set input error!");
            return;
        }
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("index0",set[0]);
            jsonObject.put("index1",set[1]);
            jsonObject.put("index2",set[2]);
            UMGlobalParam.getInstance().setSceneJson(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"setChooseScene error!msg:"+e.getMessage());
        }

    }

    public ArrayList<KettleScene> getChooseScene(){
        String jsonStr=UMGlobalParam.getInstance().getSceneJson();
        int[] set=new int[mChooseCount];
        set[0]=0;
        set[1]=1;
        set[2]=3;
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            set[0]=jsonObject.getInt("index0");
            set[1]=jsonObject.getInt("index1");
            set[2]=jsonObject.getInt("index2");
        } catch (JSONException e) {
          //  e.printStackTrace();
            Log.e(TAG,"getChooseScene error!msg:"+e.getMessage());
        }
        mChooseList.clear();
        mChooseList.add(mSceneList.get(set[0]));
        mChooseList.add(mSceneList.get(set[1]));
        mChooseList.add(mSceneList.get(set[2]));
        Log.e(TAG,"mChooseList:"+mChooseList.get(0).Value+","+mChooseList.get(1).Value+","+mChooseList.get(2).Value);
        return mChooseList;
    }

    public int[] getSceneIndexSet(){
        String jsonStr=UMGlobalParam.getInstance().getSceneJson();
        int[] set=new int[mChooseCount];
        set[0]=0;
        set[1]=1;
        set[2]=3;
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            set[0]=jsonObject.getInt("index0");
            set[1]=jsonObject.getInt("index1");
            set[2]=jsonObject.getInt("index2");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"getChooseScene error!msg:"+e.getMessage());
        }
        return set;
    }

    public ArrayList<KettleScene> getSceneList(){

        return mSceneList;
    }

    public void  close(){
        mSceneList=null;
        mChooseList=null;
        INSTANCE=null;
    }

}
