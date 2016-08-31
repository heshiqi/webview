package com.hsq.webview.client;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hsq.webview.AHErrorLayout;
import com.hsq.webview.AHWebView;
import com.hsq.webview.listener.WebViewListener;

/**
 * Created by heshiqi on 16/8/30.
 */
public class MyWebViewClient extends WebViewClient {

    protected WebViewListener listener;

    protected WebViewClient webViewClient;

    private AHWebView webView;

    protected boolean isLoadSuccess;//标记是否加载成功

    public MyWebViewClient(WebViewClient webViewClient,AHWebView webView, WebViewListener listener) {
        this.webViewClient = webViewClient;
        this.webView=webView;
        this.listener = listener;
        this.isLoadSuccess = true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
        super.onReceivedError(view, request, error);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        isLoadSuccess = false;
        if (webViewClient != null) {
            webViewClient.onReceivedError(view, errorCode, description, failingUrl);
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (webViewClient != null) {
            webViewClient.onReceivedHttpError(view, request, errorResponse);
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (webViewClient != null) {
            webViewClient.onReceivedSslError(view, handler, error);
        }
        super.onReceivedSslError(view, handler, error);
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        isLoadSuccess = true;
        if (!webView.isShowProgressBar()) {
            webView.getErrorLayout().setErrorType(AHErrorLayout.TYPE_LOADING);
        }
        if (listener != null) {
            listener.onPageStarted(url);
        }
        if (webViewClient != null) {
            webViewClient.onPageStarted(view, url, favicon);
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        if (!webView.isShowProgressBar()) {
            if (isLoadSuccess) {
                webView.getErrorLayout().setErrorType(AHErrorLayout.TYPE_HIDE);
            } else {
                webView.getErrorLayout().setErrorType(AHErrorLayout.TYPE_NETWORK_ERROR);
            }
        }
        if (listener != null) {
            listener.onPageFinished(url);
        }
        if (webViewClient != null) {
            webViewClient.onPageFinished(view, url);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean resutl = true;
        if (webViewClient != null) {
            resutl = webViewClient.shouldOverrideUrlLoading(view, url);
        }
        webView.setUrl(url);
        view.loadUrl(url);
        return resutl;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (listener != null) {
            listener.onLoadResource(url);
        }
        if (webViewClient != null) {
            webViewClient.onLoadResource(view, url);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (listener != null) {
            listener.onPageCommitVisible(url);
        }
        if (webViewClient != null) {
            webViewClient.onPageCommitVisible(view, url);
        }
    }
}