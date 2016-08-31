package com.hsq.webview.listener;

import android.net.Uri;
import android.webkit.ValueCallback;

/**
 * Created by heshiqi on 16/8/29.
 */
public abstract class WebViewListener {

    public void onProgressChanged(int progress) {}
    public void onReceivedTitle(String title) {}
    public void onReceivedTouchIconUrl(String url, boolean precomposed) {}


    public void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {}

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {}
}
