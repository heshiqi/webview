package com.hsq.webview.jsinteractor;

import com.hsq.webview.functions.Func;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heshiqi on 16/9/2.
 */
public class JsFunctionInterceptor {

    /**
     * 保存webview 中注册的回调
     */
    private Map<String, Func> functions = new HashMap<String, Func>();

    public void addJavascriptFunction(String name, Func function) {
        functions.put(name, function);
    }

    /**
     * @param messageString js 发过来的消息
     * @param interpreter   解析消息类
     * @return
     */
    public String jsCallJava(String messageString, Interpreter interpreter) {


        JsMessage message = interpreter.parser(messageString);
        if (message == null) {
            return null;
        }
        String result = "";
        if (functions.containsKey(message.getMethodName())) {
            Func func = functions.get(message.getMethodName());
            result = (String) func.call(message);

        }
        return result;
    }

    public Func getFunc(String funcName) {
        return functions.get(funcName);
    }

    public void clear() {
        functions.clear();
    }
}
