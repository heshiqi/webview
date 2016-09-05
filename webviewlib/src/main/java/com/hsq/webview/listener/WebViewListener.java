package com.hsq.webview.listener;

import android.net.Uri;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by heshiqi on 16/8/29.
 *
 * webview 的加载过程回调事件
 */
public abstract class WebViewListener {

    public void onProgressChanged(int progress) {}
    public void onReceivedTitle(String title) {}
    public void onReceivedTouchIconUrl(String url, boolean precomposed) {}
    public void onPageStarted(String url,boolean hasError){}
    public void onPageFinished(String url,boolean hasError){}
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result){}

    public void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {}

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {}
}
