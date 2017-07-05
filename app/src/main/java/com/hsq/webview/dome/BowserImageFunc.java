package com.hsq.webview.dome;

import android.text.TextUtils;
import android.widget.Toast;

import com.hsq.webview.AHWebView;
import com.hsq.webview.functions.AbstractFunc;
import com.hsq.webview.functions.Func;
import com.hsq.webview.jsinteractor.JsMessage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by heshiqi on 16/9/2.
 */
public class BowserImageFunc extends AbstractFunc<JsMessage,String> {

    public interface BowserImageListener {

        public void bowserImage(int index, String currentUrl, List<String> images);
    }

    private BowserImageListener loginListener;


    public BowserImageFunc(AHWebView webView, BowserImageListener listener) {
        super(webView);
        this.loginListener = listener;
    }

    @Override
    public String call(JsMessage message) {

        Map<String, Object> params = message.getParams();
        String[] callbacks = message.getCallbackFunction();
        if (callbacks == null||params==null) {
            return FAIL;
        }
        String current = (String) params.get("currenturl");
        List<String> imageUrls = getImages((String) params.get("imageurls"));
        int index = imageUrls.indexOf(current);
        executeJsFunction2(callbacks[0], generateSuccessResult());
        loginListener.bowserImage(index, current, imageUrls);

        return SUCCESS;
    }

    public List<String> getImages(String images) {
        if (TextUtils.isEmpty(images)) {
            return Collections.emptyList();
        }
        String[] imgs = images.split(",");
        List<String> imageUrls = new ArrayList<String>(imgs.length);
        for (String imagurl : imgs) {
            imageUrls.add(imagurl);
        }
        return imageUrls;
    }
}
