package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.jimlemmers.scenicrouteamsterdam.Models.POI;
import com.jimlemmers.scenicrouteamsterdam.R;

import org.parceler.Parcels;

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
        POI poi = Parcels.unwrap(intent.getParcelableExtra("poi"));

        if (poi.uri != null) {
            mWebView.loadUrl(poi.uri);
        } else {
            mWebView.loadData(poi.generateHTML(), "text/html", null);
        }
        setContentView(mWebView);
    }
}
