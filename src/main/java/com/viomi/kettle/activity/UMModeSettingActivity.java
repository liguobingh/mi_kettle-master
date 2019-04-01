package com.viomi.kettle.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;

/**
 * Created by young2 on 2016/4/13.
 */
public class UMModeSettingActivity extends UMBaseActivity{



    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId= R.layout.um_mode_setting_activity;
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
       // Typeface typeface= UMGlobalParam.getInstance().getTextTypeface();
        TextView title=(TextView)findViewById(R.id.title);
        title.setText(getString(R.string.um_title_mode_title));
    //    title.setTypeface(typeface);

        TextView name=(TextView)findViewById(R.id.name);
     //   name.setTypeface(typeface);

        TextView detail=(TextView)findViewById(R.id.detail);
     //   detail.setTypeface(typeface);

        ImageView backBn=(ImageView)findViewById(R.id.back);
        backBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
}
