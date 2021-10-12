package com.hongYi.h5container.webview.callback

import com.tencent.smtt.export.external.interfaces.WebResourceRequest

/**
 *@author: HZWei
 *@date:  2021/8/2
 *@desc:
 */
interface WebViewCallback {

    /**
     * 加载进度
     */
    fun onLoadingProgress(progress: Int)

    /**
     * 更新标题
     */
    fun onUpdateTitle(title: String)

    /**
     * 网页开始加载
     */
    fun onPageStarted()

    /**
     * 网页加载完成
     */
    fun onPageFinished()

    /**
     * 网页加载错误
     */
    fun onPageError(stateCode: Int, error: String)


    /**
     * 返回true，会拦截url加载，需要自定义加载逻辑
     * @see com.hongYi.h5container.webview.webviewclinet.WebViewClientEx#shouldOverrideUrlLoading()
     */
    fun onInterceptLoading(webResourceRequest: WebResourceRequest,requestUrl: String):Boolean
}
