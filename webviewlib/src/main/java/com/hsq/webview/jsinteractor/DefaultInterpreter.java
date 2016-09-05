package com.hsq.webview.jsinteractor;

import android.text.TextUtils;

import com.hsq.webview.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by heshiqi on 16/9/5.
 */
public class DefaultInterpreter implements Interpreter {

    /**
     * 解析js回调本地方法的消息
     *
     * @param messageString  actionfrom㊣{action:'methodName',params:["1","2","3"],callback:'call'}
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
            String[] params = getParams(message.optJSONArray(Constant.PARAMS));
            String callback=message.optString(Constant.CALLBACK);
            JsMessage message1=new JsMessage(action,params,callback);
            return message1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
         return null;
    }

    private String[] getParams(JSONArray jsonArray) {
        int size = jsonArray.length();
        String[] params = new String[size];
        for (int i = 0; i < size; i++) {
            params[i] = jsonArray.optString(i);
        }

        return params;
    }
}
