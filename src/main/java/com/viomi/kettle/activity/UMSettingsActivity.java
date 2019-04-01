package com.viomi.kettle.activity;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UMSettingsActivity extends UMBaseActivity {
	private  String mCurrentVersion="0.0.1";
	private  String mLatestVersion="0.0.1";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		layoutId=R.layout.um_activity_settings;
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.um_activity_settings);
		init();
	}
	
	public void init(){
//		Intent intent=getIntent();
//		if(intent!=null){
//			mCurrentVersion=intent.getStringExtra("version");
//			
//		}
//		TextView newVersionText =(TextView)findViewById(R.id.new_version);
//		if(!mCurrentVersion.equals(mLatestVersion)){
//			newVersionText.setText(mLatestVersion);
//			newVersionText.setVisibility(View.VISIBLE);
//		}else {
//			newVersionText.setVisibility(View.INVISIBLE);
//		}
		
//		View title_bar = findViewById(R.id.title_bar);
//		if(title_bar!=null){
//			mHostActivity.setTitleBarPadding(title_bar);
//		}

		//Typeface typeface= UMGlobalParam.getInstance().getTextTypeface();
		TextView title=(TextView)findViewById(R.id.title);
		title.setText("更多");
	//	title.setTypeface(typeface);
		
		RelativeLayout versionLayout0=(RelativeLayout)findViewById(R.id.item00);
		versionLayout0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent=new Intent(UMSettingsActivity.this,UMVersionCheckActivity.class);
				//startActivity(intent);
				startActivity(null,UMWebActivity.class.getName());
			}
		});
		
		RelativeLayout versionLayout1=(RelativeLayout)findViewById(R.id.item01);
		versionLayout1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent=new Intent(UMSettingsActivity.this,UMVersionCheckActivity.class);
				//startActivity(intent);
				startActivity(null,UMVersionCheckActivity.class.getName());
			}
		});
		
		
		ImageView backBn=(ImageView)findViewById(R.id.back);
		backBn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	
}
