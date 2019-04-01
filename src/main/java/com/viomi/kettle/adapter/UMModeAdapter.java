package com.viomi.kettle.adapter;

import android.R.integer;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.viomi.kettle.R;


/**
 * Created by Administrator on 14-6-14.
 */


public class UMModeAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private List<Map<String, Object>> listItems;    //选项信息集合
    private LayoutInflater listContainer;           //视图容器
    private boolean[] hasChecked;             //记录单项选中状态
    private  int  selectItem=-1; //选中的项
    
    public final class ListItemView{                //自定义控件集合
        public TextView modeTextView;
        public TextView tempTextView;

    }
    TimerTask timerTask;
    Timer timer;

    public UMModeAdapter(Context context, List<Map<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        hasChecked = new boolean[getCount()];
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return listItems.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 记录勾选了哪个选项
     * @param checkedID 选中的选项序号
     */
    private void checkedChange(int checkedID) {
        hasChecked[checkedID] = !hasChecked[checkedID];
    }

    /**
     * 判断选项是否选择
     * @param checkedID 物品序号
     * @return 返回是否选中状态
     */
    public boolean hasChecked(int checkedID) {
        return hasChecked[checkedID];
    }


    /**
     * ListView Item设置
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.e("method", "getView");
        final int selectID = position;
        //自定义视图
        ListItemView  listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.um_mode_list_item, null);
            //获取控件对象
            listItemView.modeTextView = (TextView)convertView.findViewById(R.id.mode);
            listItemView.tempTextView = (TextView)convertView.findViewById(R.id.temp);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.modeTextView.setText((String) listItems.get(position).get("mode"));
        listItemView.tempTextView.setText((String) listItems.get(position).get("temp"));
      
        if(position==selectItem){
        	listItemView.modeTextView.setTextColor(context.getResources().getColor(R.color.text_color));
        	listItemView.tempTextView.setTextColor(context.getResources().getColor(R.color.text_color));
        }else{
        	listItemView.modeTextView.setTextColor(context.getResources().getColor(R.color.gray));
        	listItemView.tempTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }

        return convertView;
    }

    public  void updateListData(List<Map<String, Object>> listItems)
    {
        this.listItems = listItems;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
    
    public void setSelectItem( int selectItem){
    	 this.selectItem = selectItem; 
    }

}
