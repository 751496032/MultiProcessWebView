package com.hongYi.h5container.webview.webviewclinet

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceRequest

/**
 *@author: HZWei
 *@date:  2021/10/12
 *@desc:
 */
class WebResourceRequestImpl : WebResourceRequest {

    var request: WebResourceRequest? = null
    var url: String
    var redirect = false

    constructor(requestUrl: String) {
        this.url = requestUrl
    }

    constructor(webResourceRequest: WebResourceRequest) : this(webResourceRequest.url.toString()) {
        this.request = webResourceRequest
        this.redirect = webResourceRequest.isRedirect
    }

    override fun getUrl(): Uri {
        return Uri.parse(url)
    }

    override fun isForMainFrame(): Boolean {
        return request?.isForMainFrame ?: false
    }

    override fun isRedirect(): Boolean {
        return redirect
    }

    override fun hasGesture(): Boolean {
        return if (request != null) request!!.hasGesture() else false
    }

    override fun getMethod(): String {
        return if (request != null) request!!.method else ""
    }

    override fun getRequestHeaders(): MutableMap<String, String> {
        return if (request != null) request!!.requestHeaders else HashMap()
    }

}
