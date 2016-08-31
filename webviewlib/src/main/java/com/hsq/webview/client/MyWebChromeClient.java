package com.hsq.webview.client;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.hsq.webview.AHWebView;
import com.hsq.webview.listener.WebViewListener;

/**
 * Created by heshiqi on 16/8/30.
 */
public class MyWebChromeClient extends WebChromeClient {

    private WebViewListener listener;
    private WebChromeClient webChromeClient;
    private AHWebView webView;

    public MyWebChromeClient(WebChromeClient webChromeClient, AHWebView webView, WebViewListener listener) {
        this.webChromeClient = webChromeClient;
        this.webView = webView;
        this.listener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {

        ProgressBar progressBar = webView.getProgressBar();
        if (progressBar != null&&webView.isShowProgressBar()) {
            if (progress == AHWebView.MAX_PROGRESS) {
                progress = 0;
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
            progressBar.setProgress(progress);
        }
        if (listener != null) {
            listener.onProgressChanged(progress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (listener != null) {
            listener.onReceivedTitle(title);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (listener != null) {
            listener.onReceivedTouchIconUrl(url, precomposed);
        }
    }
}
