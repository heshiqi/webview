package com.hsq.webview.jsinteractor;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by heshiqi on 16/9/2.
 */
public class JsMessage implements Serializable {


    /**
     * 索要执行的本地方法名称
     */
    private String methodName;

    /**
     * 方法中要输入的参数 返回的为json数据
     */
    private Map<String,Object> params;

    /**
     * 如果js调用的本地方法为异步的 但是js需要知道本地的执行结果,这个属性标识js的回调方法名
     */
    private String callbackFunctions[];

    public JsMessage(String methodName, Map<String,Object> params, String callbackFunctions[]) {
        this.methodName = methodName;
        this.params = params;
        this.callbackFunctions = callbackFunctions;
    }


    public String getMethodName() {
        return methodName;
    }

    public Map<String,Object> getParams() {
        return params;
    }

    public String[] getCallbackFunction() {
        return callbackFunctions;
    }


}
