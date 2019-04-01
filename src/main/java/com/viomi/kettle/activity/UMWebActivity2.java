package com.viomi.kettle.activity;

import com.viomi.kettle.R;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class UMWebActivity2 extends UMBaseActivity {
 
	WebView webView = null;
	
	public void onCreate(Bundle savedInstanceState) {
		layoutId=R.layout.um_activity_webview;
//		setContentView(R.layout.um_activity_webview);
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		
		String url = getIntent().getExtras().getString("url");
 		String title = getIntent().getExtras().getString("title");
 		View titlebarView = findViewById(R.id.title_bar);
 		titlebarView.setBackgroundColor(0xffffffff);
		webView = (WebView) findViewById(R.id.webview);
		TextView textView = (TextView) findViewById(R.id.title); 
		textView.setText(title);
		View backView = findViewById(R.id.back);
		backView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				UMWebActivity2.this.finish();
			}
		});
		WebSettings wSet = webView.getSettings();
		wSet.setJavaScriptEnabled(true);  
	 	wSet.setLoadWithOverviewMode(true);
		wSet.setUseWideViewPort(true);
		webView.setBackgroundColor(Color.TRANSPARENT);
 		webView.loadUrl(url);
 		webView.setWebViewClient(new WebViewClient() {
 			public boolean shouldOverrideUrlLoading(WebView view, String url) {         
 				view.loadUrl(url);
                return true;
 			}
 		});
 	}
	
	public void onPause() {
	
		super.onPause();
		if(webView!=null)
			webView.reload();
	}
	  
	public void onResume() { 
		
		super.onResume();
		if(webView!=null)
			webView.onResume();
	}
}
