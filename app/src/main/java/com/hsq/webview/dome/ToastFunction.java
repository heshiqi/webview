package com.hsq.webview.dome;

import android.widget.Toast;

import com.hsq.webview.functions.Func1;
import com.hsq.webview.jsinteractor.JsMessage;


/**
 * Created by heshiqi on 16/9/2.
 */
public class ToastFunction implements Func1<JsMessage, String> {


    @Override
    public String call(JsMessage jsMessage) {

        String[] params=jsMessage.getParams();


        Toast.makeText(MyApplication.getInstance(), params[0], Toast.LENGTH_SHORT).show();

        return "同步返回的结果 哇哈哈";
    }
}
