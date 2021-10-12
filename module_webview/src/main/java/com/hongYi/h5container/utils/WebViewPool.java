package com.hongYi.h5container.utils;

import android.content.Context;
import android.content.MutableContextWrapper;

import com.hongYi.h5container.WebViewManager;
import com.hongYi.h5container.webview.X5WebView;

import java.util.Stack;

/**
 * author:HZWei
 * date:  2021/1/28
 * desc:  建议在{@link android.app.Application#onCreate}中初始化，可能会影响启动速度，
 * 可使用{@link WebViewManager#init(Context)}进行初始化
 */
public class WebViewPool {

    private static final int CACHED_WEBVIEW_MAX_NUM = 2;
    private  Stack<X5WebView> mWebViewStackCached = new Stack<>();

    private WebViewPool() {
    }

    private static class Internal {
        static final WebViewPool sPool = new WebViewPool();
    }

    public static WebViewPool getInstance() {
        return Internal.sPool;
    }


    public void prepare(Context context) {
        if (mWebViewStackCached.size() < CACHED_WEBVIEW_MAX_NUM) {
            X5WebView webView = createX5WebView(context);
            mWebViewStackCached.push(webView);
        }
    }

    private X5WebView createX5WebView(Context context) {
        return new X5WebView(new MutableContextWrapper(context.getApplicationContext()));
    }


    public X5WebView getX5WebView(Context context) {
//        if (mWebViewStackCached.isEmpty()) {
//            X5WebView webView = createX5WebView(context);
//            mWebViewStackCached.push(webView);
//            return webView;
//        }
        // 使用栈顶的
//        X5WebView webView = mWebViewStackCached.pop();
        X5WebView webView =  createX5WebView(context);
        // WebView不为空，则开始使用预创建的WebView，并且替换Context
        MutableContextWrapper contextWrapper = (MutableContextWrapper) webView.getContext();
        contextWrapper.setBaseContext(context.getApplicationContext());
        return webView;
    }

    public boolean isEmpty(){
        return mWebViewStackCached.isEmpty();
    }


    public int size(){
        return mWebViewStackCached.size();
    }


}
