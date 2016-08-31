package com.hsq.webview.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

/**
 * Created by heshiqi on 16/8/31.
 */
public class Utils {

    public static final String PACKAGE_NAME_DOWNLOAD_MANAGER = "com.android.providers.downloads";

    public static String  getFileUploadPromptLabel(){
        return "选择文件";
    }

    public static String makeUrlUnique(final String url) {
        StringBuilder unique = new StringBuilder();
        unique.append(url);

        if (url.contains("?")) {
            unique.append('&');
        }
        else {
            if (url.lastIndexOf('/') <= 7) {
                unique.append('/');
            }
            unique.append('?');
        }

        unique.append(System.currentTimeMillis());
        unique.append('=');
        unique.append(1);

        return unique.toString();
    }

    /**
     * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
     *
     * @return whether file uploads can be used
     */
    public static boolean isFileUploadAvailable() {
        return isFileUploadAvailable(false);
    }




    /**
     * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
     *
     * On Android 4.4.3/4.4.4, file uploads may be possible but will come with a wrong MIME type
     *
     * @param needsCorrectMimeType whether a correct MIME type is required for file uploads or `application/octet-stream` is acceptable
     * @return whether file uploads can be used
     */
    public static boolean isFileUploadAvailable(final boolean needsCorrectMimeType) {
        if (Build.VERSION.SDK_INT == 19) {
            final String platformVersion = (Build.VERSION.RELEASE == null) ? "" : Build.VERSION.RELEASE;

            return !needsCorrectMimeType && (platformVersion.startsWith("4.4.3") || platformVersion.startsWith("4.4.4"));
        }
        else {
            return true;
        }
    }


    /**
     *
     * 下载一个文件保存到SD卡
     * 需要打开两个权限 android.permission.INTERNET 和 android.permission.WRITE_EXTERNAL_STORAGE
     * api 2.3(9)以上
     */
    @SuppressLint("NewApi")
    public static boolean handleDownload(final Context context, final String fromUrl, final String toFilename) {
        if (Build.VERSION.SDK_INT < 9) {
            throw new RuntimeException("Method requires API level 9 or above");
        }

        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fromUrl));
        if (Build.VERSION.SDK_INT >= 11) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, toFilename);

        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            try {
                dm.enqueue(request);
            }
            catch (SecurityException e) {
                if (Build.VERSION.SDK_INT >= 11) {
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                }
                dm.enqueue(request);
            }

            return true;
        }
        // if the download manager app has been disabled on the device
        catch (IllegalArgumentException e) {
            // show the settings screen where the user can enable the download manager app again
            openAppSettings(context, Utils.PACKAGE_NAME_DOWNLOAD_MANAGER);

            return false;
        }
    }


    @SuppressLint("NewApi")
    private static boolean openAppSettings(final Context context, final String packageName) {
        if (Build.VERSION.SDK_INT < 9) {
            throw new RuntimeException("Method requires API level 9 or above");
        }

        try {
            final Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
