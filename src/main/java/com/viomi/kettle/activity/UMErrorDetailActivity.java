package com.viomi.kettle.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.viomi.kettle.R;
import com.viomi.kettle.UMGlobalParam;
import com.viomi.kettle.UMGlobalParam.Errors;

public class UMErrorDetailActivity extends UMBaseActivity implements OnClickListener {
  
	private TextView mTitleView;
  	private TextView mDescrideView, mNameView,mHelpView;
  	private ImageView mImageView;

	
	public void onCreate(Bundle savedInstanceState) {
		
		layoutId = R.layout.um_activity_error_detail;
 		super.onCreate(savedInstanceState);
  		init();
  		initLocal();
	}
	
	private void init() {
		 
		mTitleView = (TextView) findViewById(R.id.title);	
		mTitleView.setText(mTitleView.getContext().getResources().getString(R.string.umtitle));
		mNameView = (TextView) findViewById(R.id.name);
		mDescrideView = (TextView)findViewById(R.id.describe);
	 	View back = findViewById(R.id.back);
		back.setOnClickListener(this); 
		mHelpView= (TextView)findViewById(R.id.help);
		mHelpView.setOnClickListener(this);
		mImageView = (ImageView) findViewById(R.id.image);

	}

	private void initLocal() {
 
		@Errors int  position = getIntent().getExtras().getInt("position");
 		switch (position) {
		case UMGlobalParam.ERROR_HEAT:
 		//	mImageView.setImageResource(R.drawable.um_image2);
 			mNameView.setText(mNameView.getContext().getResources().getString(R.string.um_error_heat));
			mDescrideView.setText(mNameView.getContext().getResources().getString(R.string.um_string_errord1)); 
			break;
		case UMGlobalParam.ERROR_SENSOR:
 			mNameView.setText(mNameView.getContext().getResources().getString(R.string.um_error_sensor));
 			mDescrideView.setText(mNameView.getContext().getResources().getString(R.string.um_string_errord2));
 			break;
		case UMGlobalParam.ERROR_PARCH:
 			mNameView.setText(mNameView.getContext().getResources().getString(R.string.um_error_parch));
  			mDescrideView.setText(mNameView.getContext().getResources().getString(R.string.um_string_errord3));
			break;
  		default:
			break;
		}
  	}
 	
	public void onClick(View v) {
	 
		if(v.getId() == R.id.back) {
		 	UMErrorDetailActivity.this.finish();
		} else if(v.getId() == R.id.help) {	
			try { 
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4001005678"));
	            UMErrorDetailActivity.this.startActivity(intent);	 
			} catch(Exception e) {
			 	
			}
 		}
	}
}
