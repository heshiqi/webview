package com.hsq.webview.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshiqi on 16/5/8.
 */
public class PremissionManager {

    public static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 111;

    public static List<String> checkRequestPremissions(Context context, String... requestPermissions) {
        List<String> permissions = new ArrayList<String>(requestPermissions.length);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (String premission : requestPermissions) {
                int hasPermission = context.checkSelfPermission(premission);
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(premission);
                }
            }
        }
        return permissions;
    }
}
