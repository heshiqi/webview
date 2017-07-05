package com.hsq.webview.functions;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hsq.webview.AHWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by heshiqi on 16/9/7.
 */
public abstract class AbstractFunc<T,R> implements Func<T,R> {

    static final String CALLBACK_JS_FORMAT = "javascript:%s('%s');";

    static final String CALLBACK_JS_FORMAT_2 = "javascript:AHJavascriptBridge.holdCallback('%s','%s');";

    static Handler Handler=new Handler(Looper.myLooper());

    protected WeakReference<AHWebView> mWebViewRef;


    public AbstractFunc(AHWebView webView) {
        this.mWebViewRef = new WeakReference<AHWebView>(webView);
    }


    /**
     *   执行Js方法
     * @param callFunc  js方法名
     * @param param  js方法所传的参数
     */
   public void executeJsFunction(String callFunc,String param){
       final String execJs = String.format(CALLBACK_JS_FORMAT, callFunc, param);
       if (mWebViewRef != null && mWebViewRef.get() != null) {
           Handler.post(new Runnable() {
               @Override
               public void run() {
                   mWebViewRef.get().loadUrl(execJs);
               }
           });

       }

   }

    /**
     *   执行Js方法
     * @param callFunc  js方法名
     * @param param  js方法所传的参数
     */
    public void executeJsFunction2(String callFunc,String param){
        final String execJs = String.format(CALLBACK_JS_FORMAT_2, callFunc, param);
        Log.d("hh","call Js:"+execJs);
        if (mWebViewRef != null && mWebViewRef.get() != null) {
            Handler.post(new Runnable() {
                @Override
                public void run() {
                    mWebViewRef.get().loadUrl(execJs);
                }
            });

        }
    }

    /**
     * 生成默认的返回成功json数据
     * @return
     */
    public String generateSuccessResult(){
        JSONObject json=new JSONObject();
        try {
            json.put("recorncode",0);
            json.put("message","ok");
            json.put("result",new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 生成默认的返回失败json数据
     * @return
     */
    public String generateFailResult(){
        JSONObject json=new JSONObject();
        try {
            json.put("recorncode",1);
            json.put("message","参数解析出错");
            json.put("result",new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public void result(String callbackName, String resultParam) {

    }

    @Override
    public void result(String resultParam) {

    }
}
