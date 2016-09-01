package com.hsq.webview.dome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hsq.webview.AHWebView;
import com.hsq.webview.listener.WebViewListener;


public class MainActivity extends AppCompatActivity {

    private View backBtn;
    private TextView titleTv;

    private AHWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backBtn=findViewById(R.id.back);
        titleTv=(TextView)findViewById(R.id.title);
        webView = (AHWebView) findViewById(R.id.webView);

        new AHWebView.Builder().showProgressBar(true)
                .progressBarHeight(2)
                .setWebViewListener(new MyWebViewListener())
                .url("http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492289&idx=1&sn=1993befdd0f5b2f9a5f84dfe4024b568&scene=0#wechat_redirect")
                .builder(webView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);

    }

    @Override
    public void onBackPressed() {
        if (!webView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }

    private class MyWebViewListener extends WebViewListener {
        @Override
        public void onReceivedTitle(String title) {
            titleTv.setText(title);
        }
    }
}
