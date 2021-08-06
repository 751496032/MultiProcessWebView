package com.hongYi.h5container.utils;

import android.content.Context;

import java.io.File;

/**
 * author:HZWei
 * date:  2021/1/20
 * desc:
 */
public class FileUtils {

    public static String sWebCacheDirname = "WebCache";

    /**
     * 获取缓存路径
     *
     * @param context
     * @return WebView 的缓存路径
     */
    public static String getCachePath(Context context, String dirName) {
        return context.getCacheDir().getAbsolutePath() + File.separator + dirName;
    }


    public static String getWebCachePath(Context context) {
        return getCachePath(context, sWebCacheDirname);
    }

}