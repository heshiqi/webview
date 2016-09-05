package com.hsq.webview.client;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hsq.webview.AHWebView;
import com.hsq.webview.listener.WebViewListener;


/**
 * Created by heshiqi on 16/8/30.
 */
public class MyWebViewClient extends WebViewClient {

    protected WebViewListener listener;

    protected WebViewClient webViewClient;

    private AHWebView webView;


    public MyWebViewClient(WebViewClient webViewClient, AHWebView webView, WebViewListener listener) {
        this.webViewClient = webViewClient;
        this.webView = webView;
        this.listener = listener;
    }

    protected long mLastError;

    protected void setLastError() {
        mLastError = System.currentTimeMillis();
    }

    protected boolean hasError() {
        return (mLastError + 500) >= System.currentTimeMillis();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        setLastError();

        if (webViewClient != null) {
            webViewClient.onReceivedError(view, errorCode, description, failingUrl);
        }
//        view.loadUrl("about:blank");
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

        if (listener != null) {
            listener.onPageStarted(url, hasError());
        }
        if (webViewClient != null) {
            webViewClient.onPageStarted(view, url, favicon);
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {
//        if ("about:blank".equals(url)) {
//           setLastError();
//        }
        if (listener != null) {
            listener.onPageFinished(url, hasError());
        }
        if (webViewClient != null) {
            webViewClient.onPageFinished(view, url);
        }

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            if (webViewClient != null) {
                // if the user-specified handler asks to override the request
                if (webViewClient.shouldOverrideUrlLoading(view, url)) {
                    // cancel the original request
                    return true;
                }
            }
            if (url.startsWith("http")) {
                view.loadUrl(url);
            }else{

            }
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (webViewClient != null) {
            webViewClient.onLoadResource(view, url);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (webViewClient != null) {
                return webViewClient.shouldInterceptRequest(view, url);
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        } else {
            return null;
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            return shouldInterceptRequest(view, request.getUrl().toString());

        } else {
            return null;
        }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (webViewClient != null) {
            webViewClient.onFormResubmission(view, dontResend, resend);
        } else {
            super.onFormResubmission(view, dontResend, resend);
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (webViewClient != null) {
            webViewClient.doUpdateVisitedHistory(view, url, isReload);
        } else {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (webViewClient != null) {
                webViewClient.onReceivedClientCertRequest(view, request);
            } else {
                super.onReceivedClientCertRequest(view, request);
            }
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (webViewClient != null) {
            webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (webViewClient != null) {
            return webViewClient.shouldOverrideKeyEvent(view, event);
        } else {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (webViewClient != null) {
            webViewClient.onUnhandledKeyEvent(view, event);
        } else {
            super.onUnhandledKeyEvent(view, event);
        }
    }

//    @SuppressLint("NewApi")
//    @SuppressWarnings("all")
//    public void onUnhandledInputEvent(WebView view, InputEvent event) {
//        if (Build.VERSION.SDK_INT >= 21) {
//            if (webViewClient != null) {
//                webViewClient.onUnhandledInputEvent(view, event);
//            }
//            else {
//                super.onUnhandledInputEvent(view, event);
//            }
//        }
//    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (webViewClient != null) {
            webViewClient.onScaleChanged(view, oldScale, newScale);
        } else {
            super.onScaleChanged(view, oldScale, newScale);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (Build.VERSION.SDK_INT >= 12) {
            if (webViewClient != null) {
                webViewClient.onReceivedLoginRequest(view, realm, account, args);
            } else {
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (webViewClient != null) {
            webViewClient.onPageCommitVisible(view, url);
        }
    }
}