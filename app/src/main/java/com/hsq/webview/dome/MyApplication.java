package com.hsq.webview.dome;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by heshiqi on 16/9/5.
 */
public class MyApplication extends Application {


    private static MyApplication application;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        handler=new Handler(Looper.myLooper());
    }

    public static MyApplication getInstance(){
        return application;
    }

    public void  postRunnable(Runnable runnable){

        handler.post(runnable);

    }
}
