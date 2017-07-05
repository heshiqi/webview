package com.hsq.webview.functions;

public interface Func<T, R> extends Function {

    /**
     * js调用本地方法
     * @param t
     * @return
     */
    R call(T t);

    /**
     * 回调js方法
     * @param callbackName js方法名
     * @param resultParam  js方法传入的参数
     */
    void result(String callbackName,String resultParam);


    /**
     * 回调js方法 js方法名
     * @param resultParam
     */
    void result(String resultParam);
}
