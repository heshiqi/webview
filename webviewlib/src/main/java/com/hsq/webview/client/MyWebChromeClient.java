package com.hsq.webview.client;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

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

    // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, null);

    }

    // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooser(uploadMsg, acceptType, null);
    }

    // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if(listener!=null){
            listener.openFileInput(uploadMsg, null, false);
        }
    }

    @SuppressWarnings("all")
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        if(webChromeClient!=null){
            webChromeClient.onShowFileChooser(webView,filePathCallback,fileChooserParams);
        }
//        if (Build.VERSION.SDK_INT >= 21) {
            if(listener!=null){
                final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;
                listener.openFileInput(null, filePathCallback, allowMultiple);
            }

            return true;
//        }

    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        if (listener != null) {
            listener.onProgressChanged(progress);
        }
        if (webChromeClient != null) {
            webChromeClient.onProgressChanged(view, progress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (listener != null) {
            listener.onReceivedTitle(title);
        }
        if (webChromeClient != null) {
            webChromeClient.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (webChromeClient != null) {
            webChromeClient.onReceivedIcon(view, icon);
        }

    }


    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (listener != null) {
            listener.onReceivedTouchIconUrl(url, precomposed);
        }
        if (webChromeClient != null) {
            webChromeClient.onReceivedTouchIconUrl(view, url,precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (webChromeClient != null) {
            webChromeClient.onShowCustomView(view, callback);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (Build.VERSION.SDK_INT >= 14) {
            if (webChromeClient != null) {
                webChromeClient.onShowCustomView(view, requestedOrientation, callback);
            }
        }
    }

    @Override
    public void onHideCustomView() {
        if (webChromeClient != null) {
            webChromeClient.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (webChromeClient != null) {
            return webChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }else{
            return super.onCreateWindow(view,isDialog,isUserGesture,resultMsg);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (webChromeClient != null) {
            webChromeClient.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (webChromeClient != null) {
            webChromeClient.onCloseWindow(window);
        }
    }

    /**
     * window.alert
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (webChromeClient != null) {
            return webChromeClient.onJsAlert(view, url, message, result);
        }
        else {
            return super.onJsAlert(view, url, message, result);
        }
    }


    /**
     * window.confirm
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (webChromeClient != null) {
            return webChromeClient.onJsConfirm(view, url, message, result);
        }
        else {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    /**
     *window.prompt
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

        if (listener != null) {
            listener.onJsPrompt(view, url,message,defaultValue,result);
        }
        if (webChromeClient != null) {
            return webChromeClient.onJsPrompt(view, url, message, defaultValue, result);
        }
//        result.confirm("123");
        return true;

    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {

        if (webChromeClient != null) {
            return webChromeClient.onJsBeforeUnload(view, url, message, result);
        }
        else {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (webView.isWebViewGeolocationEnabled()) {
            callback.invoke(origin, true, false);
        }
        else {
            if (webChromeClient != null) {
                webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            else {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (webChromeClient != null) {
            webChromeClient.onGeolocationPermissionsHidePrompt();
        }
        else {
            super.onGeolocationPermissionsHidePrompt();
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPermissionRequest(PermissionRequest request) {
        request.grant(request.getResources());
        if (Build.VERSION.SDK_INT >= 21) {
            if (webChromeClient != null) {
                webChromeClient.onPermissionRequest(request);
            }
            else {
                super.onPermissionRequest(request);
            }
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (webChromeClient != null) {
                webChromeClient.onPermissionRequestCanceled(request);
            }
            else {
                super.onPermissionRequestCanceled(request);
            }
        }
    }

    @Override
    public boolean onJsTimeout() {
        if (webChromeClient != null) {
            return webChromeClient.onJsTimeout();
        }
        else {
            return super.onJsTimeout();
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (webChromeClient != null) {
            webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
        }
        else {
            super.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

        if (webChromeClient != null) {
            return webChromeClient.onConsoleMessage(consoleMessage);
        }
        else {
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (webChromeClient != null) {
            return webChromeClient.getDefaultVideoPoster();
        }
        else {
            return super.getDefaultVideoPoster();
        }
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (webChromeClient != null) {
            return webChromeClient.getVideoLoadingProgressView();
        }
        else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (webChromeClient != null) {
            webChromeClient.getVisitedHistory(callback);
        }
        else {
            super.getVisitedHistory(callback);
        }
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (webChromeClient != null) {
            webChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
        else {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (webChromeClient != null) {
            webChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
        else {
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

}
