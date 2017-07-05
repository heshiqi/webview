package com.hsq.webview.dome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hsq.webview.AHWebView;
import com.hsq.webview.jsinteractor.DefaultInterpreter;
import com.hsq.webview.listener.WebViewListener;

import java.util.List;


public class MainActivity extends AppCompatActivity implements BowserImageFunc.BowserImageListener {

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
//                .url("https://www.zhihu.com/question/30445201")
                .url("file:///android_asset/www/index3.html")
                .builder(webView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
        webView.attachActivity(this);
        webView.setInterpreter(new DefaultInterpreter());

        webView.addJavascriptFunction(new BowserImageFunc(webView,this), "bowserimage");
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

    @Override
    public void bowserImage(int index, String currentUrl, List<String> images) {
        Toast.makeText(this,"打开大图",Toast.LENGTH_LONG).show();
    }

    private class MyWebViewListener extends WebViewListener {
        @Override
        public void onReceivedTitle(String title) {
            titleTv.setText(title);
        }
    }
}
