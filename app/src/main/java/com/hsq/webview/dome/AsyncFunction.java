package com.hsq.webview.dome;

import android.widget.Toast;

import com.hsq.webview.AHWebView;
import com.hsq.webview.functions.Action1;
import com.hsq.webview.jsinteractor.JsMessage;

import java.lang.ref.WeakReference;

/**
 * Created by heshiqi on 16/9/2.
 */
public class AsyncFunction implements Action1<JsMessage> {

    private WeakReference<AHWebView>webView;

    public AsyncFunction(AHWebView webView) {
        this.webView = new WeakReference<AHWebView>(webView);
    }

    @Override
    public void call(JsMessage jsMessage) {

       final String callback=jsMessage.getCallbackFunction();
        String[] params=jsMessage.getParams();

        Toast.makeText(MyApplication.getInstance(), params[1], Toast.LENGTH_SHORT).show();

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                    MyApplication.getInstance().postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            AHWebView view=webView.get();
                            if(view!=null) {
//                                view.loadUrl("javascript:+"+callback+"(异步调用我返回来啦 go go go)");
                                view.loadUrl("javascript:+"+callback+"('异步调用我返回来啦')");
                            }
                        }
                    });

            }
        }.start();

    }
}
