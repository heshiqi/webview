package com.hsq.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hsq.webview.client.MyWebChromeClient;
import com.hsq.webview.client.MyWebViewClient;
import com.hsq.webview.listener.WebViewListener;
import com.hsq.webview.util.Utils;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heshiqi on 16/8/29.
 */
public class AHWebView extends FrameLayout {

    protected static final int REQUEST_CODE_FILE_PICKER = 123456;//默认选择文件请求码
    protected static final int MAX_PROGRESS = 100;//加载进度条最大值
    protected float density = 1;

    protected LinearLayout contentLayout;//webview的父布局
    protected ProgressBar progressBar;//加载进度条
    protected WebView webView;
    protected AHErrorLayout errorLayout;//加载状态布局


    protected int PROGRESSBAR_DEFAULT_COLOR = Color.parseColor("#6699ff");//加载进度条的默认颜色

    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;//webview 掉起设备选择文件的请求码

    protected CustomWebListener customWebListener;//监听webview的加载状态回调
    protected WeakReference<Activity> mActivity;
    protected WeakReference<Fragment> mFragment;
    protected ValueCallback<Uri> mFileUploadCallbackFirst;//Android 5.0之前的文件上传回调
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;//Android 5.0之后的文件上传回调
    protected final Map<String, String> mHttpHeaders = new HashMap<String, String>();//加载url时 添加的请求头
    protected String mUploadableFileTypes = "*/*";//上传文件的 类型
    protected String mimeType;//加载内容的 类型
    protected String encoding;//加载内容的 字符编码
    protected String data;//加载的数据源
    protected String url;//加载的ur

    public AHWebView(Context context) {
        this(context, null);
    }

    public AHWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AHWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AHWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    /**
     * 设置activity的引用
     *
     * @param activity
     */
    public void attachActivity(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }

    /**
     * 设置fragment的引用
     *
     * @param fragment
     */
    public void attachFragment(Fragment fragment) {
        mFragment = new WeakReference<Fragment>(fragment);
    }

    /**
     * 初始化布局
     *
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {

        customWebListener = new CustomWebListener();
        density = context.getResources().getDisplayMetrics().density;

        {
            /**
             * 添加webview的父布局
             */
            contentLayout = new LinearLayout(context);
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            addView(contentLayout, getFrameLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        {
            /**
             * 添加webview 布局
             */
            webView = new WebView(context);
            contentLayout.addView(webView, getLinearLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }

        {
            //添加加载状态布局
            errorLayout = new AHErrorLayout(context);
            errorLayout.setOnLayoutClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showErrorLayout) {

                    }
                    reload();
                }
            });
            addView(errorLayout, getFrameLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        {
            //添加加载进度条布局
            progressBar = new ProgressBar(context);
            progressBar.setMax(MAX_PROGRESS);
            addView(progressBar, getFrameLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        }
    }


    /**
     * 初始化webview配置
     *
     * @param builder
     */
    private AHWebView initWebViewWithBuilder(Builder builder) {

        initializeConfigParams(builder);

        initializeProgressBar();

        initializeWebViewConfig();

        if (data != null) {
            webView.loadData(data, mimeType, encoding);
        } else if (url != null) {
            webView.loadUrl(url);
        }
        return this;

    }

    /**
     * 初始化WebView的配置参数
     *
     * @param builder
     */
    private void initializeConfigParams(Builder builder) {
        webViewListener = builder.listener;
        webViewClient = builder.webViewClient;
        webChromeClient = builder.webChromeClient;

        showProgressBar = builder.showProgressBar != null ? builder.showProgressBar : true;
        showErrorLayout = builder.showErrorLayout != null ? builder.showErrorLayout : false;
        progressBarColor = builder.progressBarColor != null ? builder.progressBarColor : PROGRESSBAR_DEFAULT_COLOR;
        progressBarHeight = builder.progressBarHeight != null ? getSize(builder.progressBarHeight) : getSize(1);

        webViewSupportZoom = builder.webViewSupportZoom;
        webViewMediaPlaybackRequiresUserGesture = builder.webViewMediaPlaybackRequiresUserGesture;
        webViewBuiltInZoomControls = builder.webViewBuiltInZoomControls != null ? builder.webViewBuiltInZoomControls : false;
        webViewDisplayZoomControls = builder.webViewDisplayZoomControls != null ? builder.webViewDisplayZoomControls : false;
        webViewAllowFileAccess = builder.webViewAllowFileAccess != null ? builder.webViewAllowFileAccess : true;
        webViewAllowContentAccess = builder.webViewAllowContentAccess;
        webViewLoadWithOverviewMode = builder.webViewLoadWithOverviewMode != null ? builder.webViewLoadWithOverviewMode : true;
        webViewSaveFormData = builder.webViewSaveFormData;
        webViewTextZoom = builder.webViewTextZoom;
        webViewUseWideViewPort = builder.webViewUseWideViewPort;
        webViewSupportMultipleWindows = builder.webViewSupportMultipleWindows;
        webViewLayoutAlgorithm = builder.webViewLayoutAlgorithm;
        webViewStandardFontFamily = builder.webViewStandardFontFamily;
        webViewFixedFontFamily = builder.webViewFixedFontFamily;
        webViewSansSerifFontFamily = builder.webViewSansSerifFontFamily;
        webViewSerifFontFamily = builder.webViewSerifFontFamily;
        webViewCursiveFontFamily = builder.webViewCursiveFontFamily;
        webViewFantasyFontFamily = builder.webViewFantasyFontFamily;
        webViewMinimumFontSize = builder.webViewMinimumFontSize;
        webViewMinimumLogicalFontSize = builder.webViewMinimumLogicalFontSize;
        webViewDefaultFontSize = builder.webViewDefaultFontSize;
        webViewDefaultFixedFontSize = builder.webViewDefaultFixedFontSize;
        webViewLoadsImagesAutomatically = builder.webViewLoadsImagesAutomatically;
        webViewBlockNetworkImage = builder.webViewBlockNetworkImage;
        webViewBlockNetworkLoads = builder.webViewBlockNetworkLoads;
        webViewJavaScriptEnabled = builder.webViewJavaScriptEnabled != null ? builder.webViewJavaScriptEnabled : true;
        webViewAllowUniversalAccessFromFileURLs = builder.webViewAllowUniversalAccessFromFileURLs;
        webViewAllowFileAccessFromFileURLs = builder.webViewAllowFileAccessFromFileURLs;
        webViewGeolocationDatabasePath = builder.webViewGeolocationDatabasePath;
        webViewAppCacheEnabled = builder.webViewAppCacheEnabled != null ? builder.webViewAppCacheEnabled : true;
        webViewAppCachePath = builder.webViewAppCachePath;
        webViewDatabaseEnabled = builder.webViewDatabaseEnabled;
        webViewDomStorageEnabled = builder.webViewDomStorageEnabled != null ? builder.webViewDomStorageEnabled : true;
        webViewGeolocationEnabled = builder.webViewGeolocationEnabled;
        webViewJavaScriptCanOpenWindowsAutomatically = builder.webViewJavaScriptCanOpenWindowsAutomatically;
        webViewDefaultTextEncodingName = builder.webViewDefaultTextEncodingName;
        webViewUserAgentString = builder.webViewUserAgentString;
        webViewNeedInitialFocus = builder.webViewNeedInitialFocus;
        webViewCacheMode = builder.webViewCacheMode;
        webViewMixedContentMode = builder.webViewMixedContentMode;
        webViewOffscreenPreRaster = builder.webViewOffscreenPreRaster;
        thirdPartyCookiesEnabled = builder.thirdPartyCookiesEnabled != null ? builder.thirdPartyCookiesEnabled : true;
        cookiesEnabled = builder.cookiesEnabled;
        injectJavaScript = builder.injectJavaScript;

        mimeType = builder.mimeType;
        encoding = builder.encoding;
        data = builder.data;
        url = builder.url;
    }

    /**
     * 设置WebView的配置参数
     */
    private void initializeWebViewConfig() {
        webView.setWebChromeClient(new MyWebChromeClient(webChromeClient, this, customWebListener));
        webView.setWebViewClient(new MyWebViewClient(webViewClient, this, customWebListener));
        webView.setDownloadListener(downloadListener);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setSaveEnabled(true);

        WebSettings settings = webView.getSettings();

        if (Build.VERSION.SDK_INT < 18) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        if (webViewSupportZoom != null) {
            settings.setSupportZoom(webViewSupportZoom);
        }
        if (webViewMediaPlaybackRequiresUserGesture != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(webViewMediaPlaybackRequiresUserGesture);
        }
        if (webViewBuiltInZoomControls != null) {
            settings.setBuiltInZoomControls(webViewBuiltInZoomControls);
        }
        if (webViewDisplayZoomControls != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(webViewDisplayZoomControls);
        }
        if (webViewAllowFileAccess != null) {
            settings.setAllowFileAccess(webViewAllowFileAccess);
        }
        if (webViewAllowContentAccess != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settings.setAllowContentAccess(webViewAllowContentAccess);
        }
        if (webViewLoadWithOverviewMode != null) {
            settings.setLoadWithOverviewMode(webViewLoadWithOverviewMode);
        }
        if (webViewSaveFormData != null) {
            settings.setSaveFormData(webViewSaveFormData);
        }
        if (webViewTextZoom != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            settings.setTextZoom(webViewTextZoom);
        }
        if (webViewUseWideViewPort != null) {
            settings.setUseWideViewPort(webViewUseWideViewPort);
        }
        if (webViewSupportMultipleWindows != null) {
            settings.setSupportMultipleWindows(webViewSupportMultipleWindows);
        }
        if (webViewLayoutAlgorithm != null) {
            settings.setLayoutAlgorithm(webViewLayoutAlgorithm);
        }
        if (webViewStandardFontFamily != null) {
            settings.setStandardFontFamily(webViewStandardFontFamily);
        }
        if (webViewFixedFontFamily != null) {
            settings.setFixedFontFamily(webViewFixedFontFamily);
        }
        if (webViewSansSerifFontFamily != null) {
            settings.setSansSerifFontFamily(webViewSansSerifFontFamily);
        }
        if (webViewSerifFontFamily != null) {
            settings.setSerifFontFamily(webViewSerifFontFamily);
        }
        if (webViewCursiveFontFamily != null) {
            settings.setCursiveFontFamily(webViewCursiveFontFamily);
        }
        if (webViewFantasyFontFamily != null) {
            settings.setFantasyFontFamily(webViewFantasyFontFamily);
        }
        if (webViewMinimumFontSize != null) {
            settings.setMinimumFontSize(webViewMinimumFontSize);
        }
        if (webViewMinimumLogicalFontSize != null) {
            settings.setMinimumLogicalFontSize(webViewMinimumLogicalFontSize);
        }
        if (webViewDefaultFontSize != null) {
            settings.setDefaultFontSize(webViewDefaultFontSize);
        }
        if (webViewDefaultFixedFontSize != null) {
            settings.setDefaultFixedFontSize(webViewDefaultFixedFontSize);
        }
        if (webViewLoadsImagesAutomatically != null) {
            settings.setLoadsImagesAutomatically(webViewLoadsImagesAutomatically);
        }
        if (webViewBlockNetworkImage != null) {
            settings.setBlockNetworkImage(webViewBlockNetworkImage);
        }
        if (webViewBlockNetworkLoads != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            settings.setBlockNetworkLoads(webViewBlockNetworkLoads);
        }
        if (webViewJavaScriptEnabled != null) {
            settings.setJavaScriptEnabled(webViewJavaScriptEnabled);
        }
        if (webViewAllowUniversalAccessFromFileURLs != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(webViewAllowUniversalAccessFromFileURLs);
        }
        if (webViewAllowFileAccessFromFileURLs != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(webViewAllowFileAccessFromFileURLs);
        }
        if (webViewGeolocationDatabasePath != null) {
            settings.setGeolocationDatabasePath(webViewGeolocationDatabasePath);
        }
        if (webViewAppCacheEnabled != null) {
            settings.setAppCacheEnabled(webViewAppCacheEnabled);
        }
        if (webViewAppCachePath != null) {
            settings.setAppCachePath(webViewAppCachePath);
        }
        if (webViewDatabaseEnabled != null) {
            settings.setDatabaseEnabled(webViewDatabaseEnabled);
        }
        if (webViewDomStorageEnabled != null) {
            settings.setDomStorageEnabled(webViewDomStorageEnabled);
        }
        if (webViewGeolocationEnabled != null) {
            settings.setGeolocationEnabled(webViewGeolocationEnabled);
        }
        if (webViewJavaScriptCanOpenWindowsAutomatically != null) {
            settings.setJavaScriptCanOpenWindowsAutomatically(webViewJavaScriptCanOpenWindowsAutomatically);
        }
        if (webViewDefaultTextEncodingName != null) {
            settings.setDefaultTextEncodingName(webViewDefaultTextEncodingName);
        }
        if (webViewUserAgentString != null) {
            settings.setUserAgentString(webViewUserAgentString);
        }
        if (webViewNeedInitialFocus != null) {
            settings.setNeedInitialFocus(webViewNeedInitialFocus);
        }
        if (webViewCacheMode != null) {
            settings.setCacheMode(webViewCacheMode);
        }
        if (webViewMixedContentMode != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(webViewMixedContentMode);
        }
        if (webViewOffscreenPreRaster != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settings.setOffscreenPreRaster(webViewOffscreenPreRaster);
        }

        if (thirdPartyCookiesEnabled != null) {
            setThirdPartyCookiesEnabled(true);
        }

        if (cookiesEnabled != null) {
            setCookiesEnabled(cookiesEnabled);
        }

//            // Other webview options
//            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//            webView.setScrollbarFadingEnabled(false);
//            //Additional Webview Properties
//            webView.setSoundEffectsEnabled(true);
//            webView.setHorizontalFadingEdgeEnabled(false);
//            webView.setKeepScreenOn(true);
//            webView.setScrollbarFadingEnabled(true);
//            webView.setVerticalFadingEdgeEnabled(false);
    }

    /**
     * 初始化进度条数据
     */
    private void initializeProgressBar() {
        progressBar.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
        if (showProgressBar) {
            progressBar.setReachedBarColor(progressBarColor);
            progressBar.setReachedBarHeight(progressBarHeight);
            progressBar.setUnreachedBarColor(Color.TRANSPARENT);
//            progressBar.getProgressDrawable().setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN);
            progressBar.getLayoutParams().height=progressBarHeight;
            progressBar.setVisibility(VISIBLE);
        } else {
            progressBar.setVisibility(INVISIBLE);
        }

        if (showErrorLayout) {
            errorLayout.setErrorType(AHErrorLayout.TYPE_LOADING);
        } else {
            errorLayout.setErrorType(AHErrorLayout.TYPE_HIDE);
        }
    }

    public void loadData(String data, String mimeType, String encoding) {
        this.data = data;
        this.mimeType = mimeType;
        this.encoding = encoding;
        if (this.data != null) {
            webView.loadData(data, mimeType, encoding);
        }
    }

    public void loadUrl(String url) {
        if (url == null) {
            return;
        }
        this.url = url;
        if (mHttpHeaders.size() > 0) {
            webView.loadUrl(url, mHttpHeaders);
        } else {
            webView.loadUrl(url);
        }
    }

    public void loadUrl(final String url, Map<String, String> additionalHttpHeaders) {
        if (additionalHttpHeaders == null) {
            additionalHttpHeaders = mHttpHeaders;
        } else if (mHttpHeaders.size() > 0) {
            additionalHttpHeaders.putAll(mHttpHeaders);
        }

        webView.loadUrl(url, additionalHttpHeaders);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onResume() {
        if (Build.VERSION.SDK_INT >= 11) {
            webView.onResume();
        }
        webView.resumeTimers();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPause() {
        webView.pauseTimers();
        if (Build.VERSION.SDK_INT >= 11) {
            webView.onPause();
        }
    }


    /**
     * 设置上传文件的类型
     * @param mimeType
     */
    public void setUploadableFileTypes(final String mimeType) {
        mUploadableFileTypes = mimeType;
    }

    /**
     * 选取文件返回处理
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    /**
     * 回退键处理
     * @return
     */
    public boolean onBackPressed() {
        if (canGoBack()) {
            goBack();
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings("static-method")
    public void setCookiesEnabled(final boolean enabled) {
        CookieManager.getInstance().setAcceptCookie(enabled);
    }

    @SuppressLint("NewApi")
    public void setThirdPartyCookiesEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, enabled);
        }
    }

    /**
     * 每次请求附加的HTTP 头
     * <p/>
     * This does only affect the main requests, not the requests to included resources (e.g. images)
     * <p/>
     * If you later want to delete an HTTP header that was previously added this way, call `removeHttpHeader()`
     * <p/>
     * The `WebView` implementation may in some cases overwrite headers that you set or unset
     *
     * @param name  the name of the HTTP header to add
     * @param value the value of the HTTP header to send
     */
    public void addHttpHeader(final String name, final String value) {
        mHttpHeaders.put(name, value);
    }

    /**
     * Removes one of the HTTP headers that have previously been added via `addHttpHeader()`
     * <p/>
     * If you want to unset a pre-defined header, set it to an empty string with `addHttpHeader()` instead
     * <p/>
     * The `WebView` implementation may in some cases overwrite headers that you set or unset
     *
     * @param name the name of the HTTP header to remove
     */
    public void removeHttpHeader(final String name) {
        mHttpHeaders.remove(name);
    }

    /**
     * 刷新当前界面
     */
    public void reload() {
        webView.reload();
    }

    public WebSettings getSettings() {
        return webView.getSettings();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }

    public boolean canGoForward() {
        return webView.canGoForward();
    }

    public void goForward() {
        webView.goForward();
    }

    public WebView getWebView() {
        return webView;
    }


    public boolean isWebViewGeolocationEnabled() {
        return webViewGeolocationEnabled == null ? false : true;
    }

    private LayoutParams getFrameLayoutParams(int width, int height) {
        return new LayoutParams(getSize(width), getSize(height));
    }

    private LinearLayout.LayoutParams getLinearLayoutParams(int width, int height) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height));
    }

    private LinearLayout.LayoutParams getLinearLayoutParams(int width, int height, int weight) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
    }

    private int getSize(float size) {
        return (int) (size < 0 ? size : dp(size));
    }

    private int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }


    public void showFindDialog(String text, boolean showIme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            webView.showFindDialog("", true);

    }

    /**
     * webview 的下载事件监听
     */
    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            if (webViewListener != null) {
                webViewListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
            }
        }
    };

    /**
     * 横屏竖屏转换
     *
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    public void onDestroy() {
        if (webView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onPause();
        }
        destroyWebView();
        mActivity=null;
        mFragment=null;
    }

    private void destroyWebView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (contentLayout != null && webView != null) {
                    // 移除webview
                    try {
                        contentLayout.removeView(webView);
                    } catch (Exception ignored) {
                    }

                    // 然后在移除webview中所有的view
                    try {
                        webView.removeAllViews();
                    } catch (Exception ignored) {
                    }
                    webView.destroy();
                }
            }
        }, ViewConfiguration.getZoomControlsTimeout() + 1000L);
    }


    protected WebViewClient webViewClient;
    protected WebChromeClient webChromeClient;
    protected WebViewListener webViewListener;
    protected Boolean showProgressBar;
    protected Boolean showErrorLayout;
    protected Integer progressBarColor;
    protected int progressBarHeight;
    protected Boolean webViewSupportZoom;
    protected Boolean webViewBuiltInZoomControls;
    protected Boolean webViewDisplayZoomControls;
    protected Boolean webViewMediaPlaybackRequiresUserGesture;
    protected Boolean webViewAllowFileAccess = Boolean.TRUE;
    protected Boolean webViewAllowContentAccess;
    protected Boolean webViewLoadWithOverviewMode = Boolean.TRUE;
    protected Boolean webViewSaveFormData;
    protected Integer webViewTextZoom;
    protected Boolean webViewUseWideViewPort;
    protected Boolean webViewSupportMultipleWindows;
    protected WebSettings.LayoutAlgorithm webViewLayoutAlgorithm;
    protected String webViewStandardFontFamily;
    protected String webViewFixedFontFamily;
    protected String webViewSansSerifFontFamily;
    protected String webViewSerifFontFamily;
    protected String webViewCursiveFontFamily;
    protected String webViewFantasyFontFamily;
    protected Integer webViewMinimumFontSize;
    protected Integer webViewMinimumLogicalFontSize;
    protected Integer webViewDefaultFontSize;
    protected Integer webViewDefaultFixedFontSize;
    protected Boolean webViewLoadsImagesAutomatically;
    protected Boolean webViewBlockNetworkImage;
    protected Boolean webViewBlockNetworkLoads;
    protected Boolean webViewJavaScriptEnabled = Boolean.TRUE;
    protected Boolean webViewAllowUniversalAccessFromFileURLs;
    protected Boolean webViewAllowFileAccessFromFileURLs;
    protected String webViewGeolocationDatabasePath;
    protected Boolean webViewAppCacheEnabled = Boolean.TRUE;
    protected String webViewAppCachePath;
    protected Boolean webViewDatabaseEnabled;
    protected Boolean webViewDomStorageEnabled = Boolean.TRUE;
    protected Boolean webViewGeolocationEnabled;
    protected Boolean webViewJavaScriptCanOpenWindowsAutomatically;
    protected String webViewDefaultTextEncodingName;
    protected String webViewUserAgentString;
    protected Boolean webViewNeedInitialFocus;
    protected Integer webViewCacheMode;
    protected Integer webViewMixedContentMode;
    protected Boolean webViewOffscreenPreRaster;
    protected Boolean thirdPartyCookiesEnabled;
    protected Boolean cookiesEnabled;
    ;
    protected String injectJavaScript;


    /**
     * 加载url
     *
     * @param url
     * @param preventCaching 是否缓存内容
     */
    public void loadUrl(String url, final boolean preventCaching) {
        if (preventCaching) {
            url = Utils.makeUrlUnique(url);
        }

        loadUrl(url);
    }

    /**
     * 加载url
     *
     * @param url
     * @param preventCaching        是否缓存内容
     * @param additionalHttpHeaders 所添加的headers
     */
    public void loadUrl(String url, final boolean preventCaching, final Map<String, String> additionalHttpHeaders) {
        if (preventCaching) {
            url = Utils.makeUrlUnique(url);
        }

        loadUrl(url, additionalHttpHeaders);
    }


    public class CustomWebListener extends WebViewListener {
        @Override
        public void onProgressChanged(int progress) {
            if (progressBar != null && showProgressBar) {
                if (progress == AHWebView.MAX_PROGRESS) {
                    progress = 0;
                }
                Log.d("hh",progress+"");
                progressBar.setProgress(progress);
            }
            if (webViewListener != null) {
                webViewListener.onProgressChanged(progress);
            }
        }

        @Override
        public void onReceivedTitle(String title) {
            if (webViewListener != null) {
                webViewListener.onReceivedTitle(title);
            }
        }

        @Override
        public void onReceivedTouchIconUrl(String url, boolean precomposed) {
            if (webViewListener != null) {
                webViewListener.onReceivedTouchIconUrl(url, precomposed);
            }
        }

        @Override
        public void onPageStarted(String url, boolean hasError) {

            if (!hasError) {
                if (showErrorLayout) {
                    errorLayout.setErrorType(AHErrorLayout.TYPE_LOADING);
                }
            }
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(String url, boolean hasError) {
            if (hasError) {
                errorLayout.setErrorType(AHErrorLayout.TYPE_NETWORK_ERROR);

            }else{
                errorLayout.setErrorType(AHErrorLayout.TYPE_HIDE);
            }
//            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void openFileInput(ValueCallback<Uri> fileUploadCallbackFirst, ValueCallback<Uri[]> fileUploadCallbackSecond, boolean allowMultiple) {
            if (webViewListener != null) {
                webViewListener.openFileInput(fileUploadCallbackFirst, fileUploadCallbackSecond, allowMultiple);
            }
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            if (webViewListener != null) {
                webViewListener.onDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength);
            }
        }
    }

    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        i.setType(mUploadableFileTypes);

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mFragment.get().startActivityForResult(Intent.createChooser(i, Utils.getFileUploadPromptLabel()), mRequestCodeFilePicker);
        } else if (mActivity != null && mActivity.get() != null) {
            mActivity.get().startActivityForResult(Intent.createChooser(i, Utils.getFileUploadPromptLabel()), mRequestCodeFilePicker);
        }
    }


    public static class Builder implements Serializable {

        protected transient WebViewListener listener;
        protected transient WebViewClient webViewClient;
        protected transient WebChromeClient webChromeClient;

        /**
         * 是否显示加载进度条
         */
        protected Boolean showProgressBar;

        /**
         * 是否显示加载进度条
         */
        protected Boolean showErrorLayout;

        /**
         * 加载进度条的颜色
         */
        protected Integer progressBarColor;
        /**
         * 加载进度条的高度
         */
        protected Float progressBarHeight;

        /**
         * WebView是否支持使用屏幕上的缩放控件和手势进行缩放，默认值true。设置setBuiltInZoomControls(boolean)可以使用特殊的缩放机制。该项设置不会影响zoomIn() and zoomOut()的缩放操作。
         */
        protected Boolean webViewSupportZoom;

        /**
         * WebView是否需要用户的手势进行媒体播放，默认值为true。
         */
        protected Boolean webViewMediaPlaybackRequiresUserGesture;

        /**
         * 是否使用内置的缩放机制。内置的缩放机制包括屏幕上的缩放控件（浮于WebView内容之上）和缩放手势的运用。通过setDisplayZoomControls(boolean)可以控制是否显示这些控件，默认值为false。
         */
        protected Boolean webViewBuiltInZoomControls;

        /**
         * 使用内置的缩放机制时是否展示缩放控件，默认值true。参见setBuiltInZoomControls(boolean).
         */
        protected Boolean webViewDisplayZoomControls;

        /**
         * 是否允许访问文件，默认允许。注意，这里只是允许或禁止对文件系统的访问，Assets 和 resources 文件使用file:///android_asset和file:///android_res仍是可访问的。
         */
        protected Boolean webViewAllowFileAccess;

        /**
         * 是否允许在WebView中访问内容URL（Content Url），默认允许。内容Url访问允许WebView从安装在系统中的内容提供者载入内容。
         */
        protected Boolean webViewAllowContentAccess;

        /**
         * 是否允许WebView度超出以概览的方式载入页面，默认false。即缩小内容以适应屏幕宽度。该项设置在内容宽度超出WebView控件的宽度时生效，例如当{@link WebSettings#getUseWideViewPort()}返回true时。
         */
        protected Boolean webViewLoadWithOverviewMode;

        /**
         * WebView是否保存表单数据，默认值true。
         */
        protected Boolean webViewSaveFormData;

        /**
         * 设置页面上的文本缩放百分比，默认100。
         */
        protected Integer webViewTextZoom;

        /**
         * WebView是否支持HTML的“viewport”标签或者使用wide viewport。设置值为true时，布局的宽度总是与WebView控件上的设备无关像素（device-dependent pixels）宽度一致。当值为true且页面包含viewport标记，将使用标签指定的宽度。如果页面不包含标签或者标签没有提供宽度，那就使用wide viewport。
         */
        protected Boolean webViewUseWideViewPort;

        /**
         * 设置WebView是否支持多窗口。如果设置为true，主程序要实现{@link WebChromeClient#onCreateWindow(WebView, boolean, boolean, Message)}，默认false
         */
        protected Boolean webViewSupportMultipleWindows;

        /**
         * 设置布局，会引起WebView的重新布局（relayout）,默认值NARROW_COLUMNS
         */
        protected WebSettings.LayoutAlgorithm webViewLayoutAlgorithm;

        /**
         * 设置标准字体集的名字，默认值“sans-serif”
         */
        protected String webViewStandardFontFamily;

        /**
         * 设置固定的字体集的名字，默认为”monospace”。
         */
        protected String webViewFixedFontFamily;

        /**
         * 设置无衬线字体集（sans-serif font family）的名字。默认值”sans-serif”.
         */
        protected String webViewSansSerifFontFamily;

        /**
         * 设置衬线字体集（serif font family）的名字，默认“sans-serif”。
         */
        protected String webViewSerifFontFamily;

        /**
         * 设置WebView字体库字体，默认“cursive”
         */
        protected String webViewCursiveFontFamily;

        /**
         * 设置fantasy字体集（font family）的名字默认为“fantasy”
         */
        protected String webViewFantasyFontFamily;

        /**
         * 设置最小的字号，默认为8
         */
        protected Integer webViewMinimumFontSize;

        /**
         * 设置最小的本地字号，默认为8。
         */
        protected Integer webViewMinimumLogicalFontSize;

        /**
         * 设置默认的字体大小，默认16，可取值1到72
         */
        protected Integer webViewDefaultFontSize;

        /**
         * 设置默认固定的字体大小，默认为16，可取值1到72
         */
        protected Integer webViewDefaultFixedFontSize;

        /**
         * WebView是否下载图片资源，默认为true。注意，该方法控制所有图片的下载，包括使用URI嵌入的图片（使用{@link WebSettings#setBlockNetworkImage(boolean)} 只控制使用网络URI的图片的下载）。如果该设置项的值由false变为true，WebView展示的内容所引用的所有的图片资源将自动下载。
         */
        protected Boolean webViewLoadsImagesAutomatically;

        /**
         * 是否禁止从网络（通过http和https URI schemes访问的资源）下载图片资源，默认值为false。注意，除非{@link WebSettings#getLoadsImagesAutomatically()}返回true,否则该方法无效。还请注意，即使此项设置为false，使用{@link WebSettings#setBlockNetworkLoads(boolean)}禁止所有网络加载也会阻止网络图片的加载。当此项设置的值从true变为false，WebView当前显示的内容所引用的网络图片资源会自动获取。
         */
        protected Boolean webViewBlockNetworkImage;

        /**
         * 是否禁止从网络下载数据，如果app有INTERNET权限，默认值为false，否则默认为true。使用{@link WebSettings#setBlockNetworkImage(boolean)} 只会禁止图片资源的加载。注意此值由true变为false，当前WebView展示的内容所引用的网络资源不会自动加载，直到调用了重载。如果APP没有INTERNET权限，设置此值为false会抛出SecurityException。
         */
        protected Boolean webViewBlockNetworkLoads;

        /**
         * 设置WebView是否允许执行JavaScript脚本，默认false，不允许。
         */
        protected Boolean webViewJavaScriptEnabled;

        /**
         * 是否允许运行在一个file schema URL环境下的JavaScript访问来自其他任何来源的内容，包括其他file schema URLs. 参见setAllowFileAccessFromFileURLs(boolean)，为了确保安全，应该设置为不允许，注意这项设置只影响对file schema 资源的JavaScript访问，其他形式的访问，例如来自图片HTML单元的访问不受影响。为了防止相同的域策略（same domain policy）对ICE_CREAM_SANDWICH以及更老机型的侵害，应该显式地设置此值为false。ICE_CREAM_SANDWICH_MR1 以及更老的版本此默认值为true，JELLY_BEAN以及更新版本此默认值为false
         */
        protected Boolean webViewAllowUniversalAccessFromFileURLs = Boolean.FALSE;

        /**
         * 是否允许运行在一个URL环境（the context of a file scheme URL）中的JavaScript访问来自其他URL环境的内容，为了保证安全，应该不允许。也请注意，这项设置只影响对file schema 资源的JavaScript访问，其他形式的访问，例如来自图片HTML单元的访问不受影响。为了防止相同的域策略（same domain policy）对ICE_CREAM_SANDWICH以及更老机型的侵害，应该显式地设置此值为false。
         */
        protected Boolean webViewAllowFileAccessFromFileURLs = Boolean.FALSE;

        /**
         * 定位数据库的保存路径，为了确保定位权限和缓存位置的持久化，该方法应该传入一个应用可写的路径。
         */
        protected String webViewGeolocationDatabasePath;

        /**
         * 应用缓存API是否可用，默认值false, 结合{@link WebSettings#setAppCachePath(String)}使用。
         */
        protected Boolean webViewAppCacheEnabled;

        /**
         * 设置应用缓存文件的路径。为了让应用缓存API可用，此方法必须传入一个应用可写的路径。该方法只会执行一次，重复调用会被忽略。
         */
        protected String webViewAppCachePath;

        /**
         * 数据库存储API是否可用，默认值false。如何正确设置数据存储API参见{@link WebSettings#setDatabasePath(String)}。该设置对同一进程中的所有WebView实例均有效。注意，只能在当前进程的任意WebView加载页面之前修改此项，因为此节点之后WebView的实现类可能会忽略该项设置的改变。
         */
        protected Boolean webViewDatabaseEnabled;

        /**
         * DOM存储API是否可用，默认false。
         */
        protected Boolean webViewDomStorageEnabled;

        /**
         * 定位是否可用，默认为true。请注意，为了确保定位API在WebView的页面中可用，必须遵守如下约定:
         * (1) app必须有定位的权限，参见ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION；
         * (2) app必须提供onGeolocationPermissionsShowPrompt(String, GeolocationPermissions.Callback)回调方法的实现，在页面通过JavaScript定位API请求定位时接收通知。
         * 作为可选项，可以在数据库中存储历史位置和Web初始权限，参见setGeolocationDatabasePath(String).
         */
        protected Boolean webViewGeolocationEnabled;

        /**
         * 让JavaScript自动打开窗口，默认false。适用于JavaScript方法window.open()。
         */
        protected Boolean webViewJavaScriptCanOpenWindowsAutomatically;

        /**
         * 设置默认的字符编码集，默认”UTF-8”.
         */
        protected String webViewDefaultTextEncodingName;

        /**
         * 设置WebView的用户代理字符串。如果字符串为null或者empty，将使用系统默认值。注意从KITKAT版本开始，加载网页时改变用户代理会让WebView再次初始化加载。
         */
        protected String webViewUserAgentString;

        /**
         * 调用requestFocus(int, android.graphics.Rect)时是否需要设置节点获取焦点，默认值为true。
         */
        protected Boolean webViewNeedInitialFocus;

        /**
         * 重写使用缓存的方式，默认值LOAD_DEFAULT。缓存的使用方式基于导航类型，正常的页面加载，检测缓存，需要时缓存内容复现。导航返回时，内容不会复现，只有内容会从缓存盘中恢复。该方法允许客户端通过指定LOAD_DEFAULT, LOAD_CACHE_ELSE_NETWORK, LOAD_NO_CACHE or LOAD_CACHE_ONLY的其中一项来重写其行为。
         */
        protected Integer webViewCacheMode;

        /**
         * 当一个安全的来源（origin）试图从一个不安全的来源加载资源时配置WebView的行为。默认情况下，KITKAT及更低版本默认值为MIXED_CONTENT_ALWAYS_ALLOW，LOLLIPOP版本默认值MIXED_CONTENT_NEVER_ALLOW，WebView首选的最安全的操作模式为MIXED_CONTENT_NEVER_ALLOW ，不鼓励使用MIXED_CONTENT_ALWAYS_ALLOW。
         */
        protected Integer webViewMixedContentMode;

        /**
         * 当WebView切换到后台但仍然与窗口关联时是否raster tiles，打开它可以避免在WebView从后台切换到前台时重新绘制，默认值false。在这种模式下后台的WebView占用更多的内存。请按如下准则显示内存的使用：
         * 1、WebView的尺寸不能比设备的屏幕尺寸更大；
         * 2、限制在少数WebView上使用该模式；
         * 3、在可见的WebView和即将显现的WebView上使用；
         */
        protected Boolean webViewOffscreenPreRaster;

        protected Boolean thirdPartyCookiesEnabled;
        protected Boolean cookiesEnabled;

        protected String injectJavaScript;

        protected String mimeType;
        protected String encoding;
        protected String data;
        protected String url;

        public Builder setWebViewClient(WebViewClient webViewClient) {
            this.webViewClient = webViewClient;
            return this;
        }

        public Builder setWebChromeClient(WebChromeClient webChromeClient) {
            this.webChromeClient = webChromeClient;
            return this;
        }

        public Builder setWebViewListener(WebViewListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder showProgressBar(boolean showProgressBar) {
            this.showProgressBar = showProgressBar;
            return this;
        }

        public Builder showErrorLayout(boolean showErrorLayout) {
            this.showErrorLayout = showErrorLayout;
            return this;
        }

        public Builder progressBarColor(int color) {
            this.progressBarColor = color;
            return this;
        }

        public Builder progressBarHeight(float height) {
            this.progressBarHeight = height;
            return this;
        }

        public Builder progressBarHeight(int height) {
            this.progressBarHeight = (float) height;
            return this;
        }

        public Builder webViewSupportZoom(boolean webViewSupportZoom) {
            this.webViewSupportZoom = webViewSupportZoom;
            return this;
        }

        public Builder webViewMediaPlaybackRequiresUserGesture(boolean webViewMediaPlaybackRequiresUserGesture) {
            this.webViewMediaPlaybackRequiresUserGesture = webViewMediaPlaybackRequiresUserGesture;
            return this;
        }

        public Builder webViewBuiltInZoomControls(boolean webViewBuiltInZoomControls) {
            this.webViewBuiltInZoomControls = webViewBuiltInZoomControls;
            return this;
        }

        public Builder webViewDisplayZoomControls(boolean webViewDisplayZoomControls) {
            this.webViewDisplayZoomControls = webViewDisplayZoomControls;
            return this;
        }

        public Builder webViewAllowFileAccess(boolean webViewAllowFileAccess) {
            this.webViewAllowFileAccess = webViewAllowFileAccess;
            return this;
        }

        public Builder webViewAllowContentAccess(boolean webViewAllowContentAccess) {
            this.webViewAllowContentAccess = webViewAllowContentAccess;
            return this;
        }

        public Builder webViewLoadWithOverviewMode(boolean webViewLoadWithOverviewMode) {
            this.webViewLoadWithOverviewMode = webViewLoadWithOverviewMode;
            return this;
        }

        public Builder webViewSaveFormData(boolean webViewSaveFormData) {
            this.webViewSaveFormData = webViewSaveFormData;
            return this;
        }

        public Builder webViewTextZoom(int webViewTextZoom) {
            this.webViewTextZoom = webViewTextZoom;
            return this;
        }

        public Builder webViewUseWideViewPort(boolean webViewUseWideViewPort) {
            this.webViewUseWideViewPort = webViewUseWideViewPort;
            return this;
        }

        public Builder webViewSupportMultipleWindows(boolean webViewSupportMultipleWindows) {
            this.webViewSupportMultipleWindows = webViewSupportMultipleWindows;
            return this;
        }

        public Builder webViewLayoutAlgorithm(WebSettings.LayoutAlgorithm webViewLayoutAlgorithm) {
            this.webViewLayoutAlgorithm = webViewLayoutAlgorithm;
            return this;
        }

        public Builder webViewStandardFontFamily(String webViewStandardFontFamily) {
            this.webViewStandardFontFamily = webViewStandardFontFamily;
            return this;
        }

        public Builder webViewFixedFontFamily(String webViewFixedFontFamily) {
            this.webViewFixedFontFamily = webViewFixedFontFamily;
            return this;
        }

        public Builder webViewSansSerifFontFamily(String webViewSansSerifFontFamily) {
            this.webViewSansSerifFontFamily = webViewSansSerifFontFamily;
            return this;
        }

        public Builder webViewSerifFontFamily(String webViewSerifFontFamily) {
            this.webViewSerifFontFamily = webViewSerifFontFamily;
            return this;
        }

        public Builder webViewCursiveFontFamily(String webViewCursiveFontFamily) {
            this.webViewCursiveFontFamily = webViewCursiveFontFamily;
            return this;
        }

        public Builder webViewFantasyFontFamily(String webViewFantasyFontFamily) {
            this.webViewFantasyFontFamily = webViewFantasyFontFamily;
            return this;
        }

        public Builder webViewMinimumFontSize(int webViewMinimumFontSize) {
            this.webViewMinimumFontSize = webViewMinimumFontSize;
            return this;
        }

        public Builder webViewMinimumLogicalFontSize(int webViewMinimumLogicalFontSize) {
            this.webViewMinimumLogicalFontSize = webViewMinimumLogicalFontSize;
            return this;
        }

        public Builder webViewDefaultFontSize(int webViewDefaultFontSize) {
            this.webViewDefaultFontSize = webViewDefaultFontSize;
            return this;
        }

        public Builder webViewDefaultFixedFontSize(int webViewDefaultFixedFontSize) {
            this.webViewDefaultFixedFontSize = webViewDefaultFixedFontSize;
            return this;
        }

        public Builder webViewLoadsImagesAutomatically(boolean webViewLoadsImagesAutomatically) {
            this.webViewLoadsImagesAutomatically = webViewLoadsImagesAutomatically;
            return this;
        }

        public Builder webViewBlockNetworkImage(boolean webViewBlockNetworkImage) {
            this.webViewBlockNetworkImage = webViewBlockNetworkImage;
            return this;
        }

        public Builder webViewBlockNetworkLoads(boolean webViewBlockNetworkLoads) {
            this.webViewBlockNetworkLoads = webViewBlockNetworkLoads;
            return this;
        }

        public Builder webViewJavaScriptEnabled(boolean webViewJavaScriptEnabled) {
            this.webViewJavaScriptEnabled = webViewJavaScriptEnabled;
            return this;
        }

        public Builder webViewAllowUniversalAccessFromFileURLs(boolean webViewAllowUniversalAccessFromFileURLs) {
            this.webViewAllowUniversalAccessFromFileURLs = webViewAllowUniversalAccessFromFileURLs;
            return this;
        }

        public Builder webViewAllowFileAccessFromFileURLs(boolean webViewAllowFileAccessFromFileURLs) {
            this.webViewAllowFileAccessFromFileURLs = webViewAllowFileAccessFromFileURLs;
            return this;
        }

        public Builder webViewGeolocationDatabasePath(String webViewGeolocationDatabasePath) {
            this.webViewGeolocationDatabasePath = webViewGeolocationDatabasePath;
            return this;
        }

        public Builder webViewAppCacheEnabled(boolean webViewAppCacheEnabled) {
            this.webViewAppCacheEnabled = webViewAppCacheEnabled;
            return this;
        }

        public Builder webViewAppCachePath(String webViewAppCachePath) {
            this.webViewAppCachePath = webViewAppCachePath;
            return this;
        }

        public Builder webViewDatabaseEnabled(boolean webViewDatabaseEnabled) {
            this.webViewDatabaseEnabled = webViewDatabaseEnabled;
            return this;
        }

        public Builder webViewDomStorageEnabled(boolean webViewDomStorageEnabled) {
            this.webViewDomStorageEnabled = webViewDomStorageEnabled;
            return this;
        }

        public Builder webViewGeolocationEnabled(boolean webViewGeolocationEnabled) {
            this.webViewGeolocationEnabled = webViewGeolocationEnabled;
            return this;
        }

        public Builder webViewJavaScriptCanOpenWindowsAutomatically(boolean webViewJavaScriptCanOpenWindowsAutomatically) {
            this.webViewJavaScriptCanOpenWindowsAutomatically = webViewJavaScriptCanOpenWindowsAutomatically;
            return this;
        }

        public Builder webViewDefaultTextEncodingName(String webViewDefaultTextEncodingName) {
            this.webViewDefaultTextEncodingName = webViewDefaultTextEncodingName;
            return this;
        }

        public Builder webViewUserAgentString(String webViewUserAgentString) {
            this.webViewUserAgentString = webViewUserAgentString;
            return this;
        }

        public Builder webViewNeedInitialFocus(boolean webViewNeedInitialFocus) {
            this.webViewNeedInitialFocus = webViewNeedInitialFocus;
            return this;
        }

        public Builder webViewCacheMode(int webViewCacheMode) {
            this.webViewCacheMode = webViewCacheMode;
            return this;
        }

        public Builder webViewMixedContentMode(int webViewMixedContentMode) {
            this.webViewMixedContentMode = webViewMixedContentMode;
            return this;
        }

        public Builder webViewOffscreenPreRaster(boolean webViewOffscreenPreRaster) {
            this.webViewOffscreenPreRaster = webViewOffscreenPreRaster;
            return this;
        }

        /**
         * 可设置每次加载界面时 自动加载的JavaScript
         *
         * @param injectJavaScript
         * @return
         */
        public Builder injectJavaScript(String injectJavaScript) {
            this.injectJavaScript = injectJavaScript;
            return this;
        }

        public Builder thirdPartyCookiesEnabled(boolean thirdPartyCookiesEnabled) {
            this.thirdPartyCookiesEnabled = thirdPartyCookiesEnabled;
            return this;
        }

        public Builder cookiesEnabled(boolean cookiesEnabled) {
            this.cookiesEnabled = cookiesEnabled;
            return this;
        }

        /**
         * webview 加载数据时{@link #data} 的数据mimeType
         *
         * @param mimeType
         * @return
         */
        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        /**
         * webview 的编码格式
         *
         * @param encoding
         * @return
         */
        public Builder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * webview 所要加载的数据
         *
         * @param data
         * @return
         */
        public Builder data(String data) {
            this.data = data;
            return this;
        }

        /**
         * webview 加载的url
         *
         * @param url
         * @return
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public AHWebView builder(AHWebView webView) {
            return webView.initWebViewWithBuilder(this);
        }
    }

}
