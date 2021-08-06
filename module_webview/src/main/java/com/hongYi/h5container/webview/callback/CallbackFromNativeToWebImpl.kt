package com.hongYi.h5container.webview.callback

import com.hongYi.h5container.webview.X5WebView

/**
 *@author: HZWei
 *@date:  2021/8/6
 *@desc:
 */
@Deprecated("过时")
class CallbackFromNativeToWebImpl(webView: X5WebView) : CallbackFromNativeToWeb {

    private val mWebView: X5WebView = webView

    override fun handleWebCallback(jsCallbackName: String, result: String) {
        mWebView.handleWebCallback(jsCallbackName, result)
    }
}