package com.jimlemmers.scenicrouteamsterdam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class InformationActivity extends AppCompatActivity {
    private WebView mWebView;
    private String TAG = "InformationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String HTML = intent.getStringExtra("HTML");


        if (url != null) {
            Log.d(TAG, url);
            mWebView.loadUrl(url);
        } else {
            Log.d(TAG, HTML);
            mWebView.loadData(HTML, "text/html", null);
        }
        setContentView(mWebView);
    }
}
