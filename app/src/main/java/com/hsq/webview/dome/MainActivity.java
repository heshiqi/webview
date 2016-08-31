package com.hsq.webview.dome;

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
        webView.initWebViewWithBuilder(new AHWebView.Builder().showProgressBar(true).progressBarHeight(3).setWebViewListener(new MyWebViewListener()).url("http://3g.163.com/touch/news/subchannel/all?nav=2&version=v_standard"));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
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

    private class MyWebViewListener extends WebViewListener {
        @Override
        public void onReceivedTitle(String title) {
            titleTv.setText(title);
        }
    }
}
