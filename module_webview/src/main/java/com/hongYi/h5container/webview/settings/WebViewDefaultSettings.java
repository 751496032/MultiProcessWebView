package com.hongYi.h5container.webview.settings;


import android.os.Build;
import android.view.View;

import com.hongYi.h5container.utils.Constants;
import com.hongYi.h5container.utils.FileUtils;
import com.hongYi.h5container.utils.LogUtils;
import com.hongYi.h5container.utils.NetworkUtils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * author:HZWei
 * date:  2021/1/20
 * desc:
 */
@SuppressWarnings("all")
public class WebViewDefaultSettings implements IWebViewSettings {

    private static final String TAG = "WebViewDefaultSettings";
    private WebSettings mWebSettings;
    private static WebViewDefaultSettings INSTANCE;


    private WebViewDefaultSettings() {
    }

    public static WebViewDefaultSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WebViewDefaultSettings();
        }
        return INSTANCE;
    }


    @Override
    public void setSettings(WebView webView) {
        if (webView == null) return;
        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(true);
        if (NetworkUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebSettings.setTextZoom(100);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSupportMultipleWindows(false);
        // 是否阻塞加载网络图片  协议http or https
        mWebSettings.setBlockNetworkImage(false);
        // 允许加载本地文件html  file协议
        mWebSettings.setAllowFileAccess(true);
        // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        mWebSettings.setAllowFileAccessFromFileURLs(true);
        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebSettings.setLoadWithOverviewMode(false);
        mWebSettings.setUseWideViewPort(false);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(10);//设置 WebView 支持的最小字体大小，默认为 8
        mWebSettings.setGeolocationEnabled(true);
        String dir = FileUtils.getWebCachePath(webView.getContext());
        LogUtils.i(TAG, "dir:" + dir + "   appcache:" + dir);
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        mWebSettings.setGeolocationDatabasePath(dir);
        mWebSettings.setDatabasePath(dir);
        mWebSettings.setAppCachePath(dir);
        //缓存文件最大值
        mWebSettings.setAppCacheMaxSize(Integer.MAX_VALUE);
        mWebSettings.setUserAgentString(mWebSettings
                .getUserAgentString()
                .concat(Constants.WEB_VERSION));

    }

    public WebSettings getWebSettings() {
        if (mWebSettings==null){
            throw new NullPointerException("没有调用setSettings()方法");
        }
        return mWebSettings;
    }
}