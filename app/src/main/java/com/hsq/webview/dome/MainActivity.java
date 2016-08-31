package com.hsq.webview.dome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hsq.webview.AHWebView;


public class MainActivity extends AppCompatActivity {

    private AHWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);    webView=(AHWebView)findViewById(R.id.webView);
        webView.initWebViewWithBuilder(new AHWebView.Builder().showProgressBar(true).progressBarHeight(3).url("http://www.baidu.com"));
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }
}
