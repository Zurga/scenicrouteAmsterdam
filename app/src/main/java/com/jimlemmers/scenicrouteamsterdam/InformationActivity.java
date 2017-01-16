package com.jimlemmers.scenicrouteamsterdam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class InformationActivity extends AppCompatActivity {
    private WebView mWebView = new WebView(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String HTML = intent.getStringExtra("HTML");

        if (url != null) {
            mWebView.loadUrl(url);
        } else {
            mWebView.loadData(HTML, "data/html", "UTF-8");
        }
    }
}
