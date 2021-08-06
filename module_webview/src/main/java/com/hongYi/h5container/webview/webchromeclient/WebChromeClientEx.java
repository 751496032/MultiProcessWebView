package com.hongYi.h5container.webview.webchromeclient;

import com.hongYi.h5container.utils.LogUtils;
import com.hongYi.h5container.webview.callback.WebViewCallback;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * author:HZWei
 * date:  2021/1/20
 * desc:  主要是负责Js内核相关的回调
 */
public class WebChromeClientEx extends WebChromeClient {


    private WebViewCallback mWebViewCallback;

    public WebChromeClientEx() {
    }

    public WebChromeClientEx setWebViewCallback(WebViewCallback webViewCallback) {
        mWebViewCallback = webViewCallback;
        return this;
    }

    /**
     * 获取网页的加载进度
     *
     * @param webView
     * @param progress
     */
    @Override
    public void onProgressChanged(WebView webView, int progress) {
        super.onProgressChanged(webView, progress);
        if (mWebViewCallback != null) mWebViewCallback.onLoadingProgress(progress);
    }


    /**
     * 获取网页的标题
     *
     * @param webView
     * @param title
     */
    @Override
    public void onReceivedTitle(WebView webView, String title) {
        super.onReceivedTitle(webView, title);
        if (mWebViewCallback != null) mWebViewCallback.onUpdateTitle(title);
    }

    /**
     * 打印js相关日志
     *
     * @param consoleMessage
     * @return
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtils.i("onConsole", consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

}