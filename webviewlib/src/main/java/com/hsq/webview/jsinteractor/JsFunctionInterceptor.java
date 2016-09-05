package com.hsq.webview.jsinteractor;


import com.hsq.webview.functions.Action;
import com.hsq.webview.functions.Action1;
import com.hsq.webview.functions.Func1;
import com.hsq.webview.functions.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heshiqi on 16/9/2.
 */
public class JsFunctionInterceptor {

    /**
     * 保存webview 中注册的回调
     */
    private Map<String, Function> functions= new HashMap<String, Function>();

    public void addJavascriptFunction(String name, Function function){
           functions.put(name,function);
    }

    /**
     *
     * @param messageString js 发过来的消息
     * @param interpreter  解析消息类
     * @return
     */
    public String jsCallJava( String messageString,Interpreter interpreter){

        String result="";
        JsMessage message=interpreter.parser(messageString);

        if (functions.containsKey(message.getMethodName())) {
            Function function=functions.get(message.getMethodName());
            if(function instanceof Action){
                  Action1 action1= (Action1) function;
                   action1.call(message);

            }else if(function instanceof Function){
                Func1<JsMessage,String> func1=(Func1)function;
                result= func1.call(message);
            }

        }
        return result;
    }
}
