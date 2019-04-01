package com.viomi.kettle.activity;

import com.viomi.kettle.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class UMWebActivity extends UMBaseActivity {
	public final static String POPULARIZATIONURL = "https://viomi-faq.mi-ae.com.cn/faqs/kettle.html";
	
	private WebView webview = null;

	public void onCreate(Bundle savedInstanceState) {
		layoutId=R.layout.um_activity_webview;
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.um_activity_webview);
		init();
	}
	 
	public void onResume() {
	 	super.onResume();
	 	if(webview != null)
	 		webview.onResume();
	}
	
 	public void onPause() {
	 	super.onPause();
		if(webview != null)
	 		webview.reload();
	}
	
 	public void onDestroy() {
	 	super.onDestroy(); 
	}

	private void init() {
		
//		String url = getIntent().getExtras().getString("url");
// 		String title = getIntent().getExtras().getString("title");
		String title=getString(R.string.um_string_mi_directions);
		String url=POPULARIZATIONURL;
 		View titlebarView = findViewById(R.id.title_bar);
 		titlebarView.setBackgroundColor(0xffffffff);
 		webview = (WebView) findViewById(R.id.webview);
		TextView textView = (TextView) findViewById(R.id.title); 
		textView.setText(title);
		View backView = findViewById(R.id.back);
		backView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				UMWebActivity.this.finish();
			}
		});
		WebSettings wSet = webview.getSettings();
		wSet.setJavaScriptEnabled(true);  
	 	wSet.setLoadWithOverviewMode(true);
		wSet.setUseWideViewPort(true);
		webview.setBackgroundColor(Color.TRANSPARENT);
		webview.loadUrl(url);
		webview.setWebViewClient(new WebViewClient() {
 			public boolean shouldOverrideUrlLoading(WebView view, String url) {         
 				Intent intent = new Intent();
 				intent.putExtra("url", url);
 				intent.putExtra("title",  getString(R.string.um_string_mi_directions));
   				startActivity(intent, UMWebActivity2.class.getName());
  				return true;
 			} 
 		});
 	}
}
