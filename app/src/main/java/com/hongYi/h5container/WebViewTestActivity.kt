package com.hongYi.h5container

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hongYi.h5container.utils.Constants
import com.hongYi.h5container.webview.X5WebView
import com.hongYi.h5container.webview.webviewclinet.WebViewClientEx

/**
 *@author: HZWei
 *@date:  2021/8/5
 *@desc:
 */
class WebViewTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_test)

        val webView = findViewById<X5WebView>(R.id.webview)
        webView.webViewClient = WebViewClientEx()
        webView.loadUrl(intent.getStringExtra(Constants.URL))


    }
}