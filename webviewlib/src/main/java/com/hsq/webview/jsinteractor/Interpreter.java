package com.hsq.webview.jsinteractor;

/**
 * Created by heshiqi on 16/9/5.
 */
public interface Interpreter {

    JsMessage parser(String messageString);
}
