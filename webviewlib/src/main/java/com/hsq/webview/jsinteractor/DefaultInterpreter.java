package com.hsq.webview.jsinteractor;

import android.text.TextUtils;

import com.hsq.webview.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by heshiqi on 16/9/5.
 */
public class DefaultInterpreter implements Interpreter {

    /**
     * 解析js回调本地方法的消息
     *
     * @param messageString  action㊣{"action":"toast","param":{"name":"1","key":"2","val":"3"},"callback":"[call,other]"}
     * @return
     */
    @Override
    public JsMessage parser(String messageString) {
        if (TextUtils.isEmpty(messageString) || !messageString.startsWith(Constant.JS_PROTOCOL)) {
            return null;
        }
        String[] msg = messageString.split("㊣");
        if (msg == null || msg.length < 2) {
            return null;
        }
        String msgBody = msg[1];
        try {
            JSONObject message = new JSONObject(msgBody);
            String action = message.optString(Constant.ACTION);//调用的方法名
            Map<String,Object> params = getParams(message.optJSONObject(Constant.PARAMS));
            String[] callback=getCallbacks(message.optJSONArray(Constant.CALLBACK));
            JsMessage message1=new JsMessage(action,params,callback);
            return message1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
         return null;
    }

    private Map<String,Object> getParams(JSONObject json) {
        Map<String, Object> params = new HashMap<String, Object>();
        Iterator<String> iter = json.keys();
        String key=null;
        Object value=null;
        while (iter.hasNext()) {
            key=iter.next();
            value=json.opt(key);
            params.put(key, value);
        }
        return params;
    }


       private String[] getCallbacks(JSONArray jsonArray) {
           if(jsonArray==null){
               return null;
           }
           int size = jsonArray.length();
           String[] callbacks = new String[size];
           for (int i = 0; i < size; i++) {
               callbacks[i] = jsonArray.optString(i);

           }
           return callbacks;

       }
}
