package com.hongYi.h5container.ui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hongYi.h5container.R
import com.hongYi.h5container.utils.Constants
import com.hongYi.h5container.utils.ToolBarUtils
import com.hongYi.h5container.webview.X5WebView
import kotlinx.android.synthetic.main.activity_webview.*

/**
 *author:HZWei
 *date:  2021/1/20
 *desc:
 */
open class WebViewActivity : AppCompatActivity() {

    private lateinit var mX5WebView: X5WebView
    private val FRAGMENT_TAG = "web_fragment"

    inner class Small1 : WebViewActivity(){}
    inner class Small2 : WebViewActivity(){}
    inner class Small3 : WebViewActivity(){}
    inner class Small4 : WebViewActivity(){}
    inner class Small5 : WebViewActivity(){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTaskDescription(ActivityManager.TaskDescription())
        window.statusBarColor = resources.getColor(R.color.statusColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_webview)
//        supportActionBar?.hide()
        val url = intent.getStringExtra(Constants.URL)
        val title = intent.getStringExtra(Constants.TITLE)
        val canNativeRefresh = intent.getBooleanExtra(Constants.CAN_NATIVE_REFRESH, true)
        val isShowActionBar = intent.getBooleanExtra(Constants.IS_SHOW_ACTION_BAR, true)
        val jsObjectName = intent.getStringExtra(Constants.JS_OBJECT_NAME)

        toolbar.visibility = if (isShowActionBar) View.VISIBLE else View.GONE
        toolbar.navigationIcon = resources.getDrawable(R.mipmap.ic_left_arrow, null)
        toolbar.title = ""
        toolbar_title.text = title
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val fragment = WebViewFragment.newInstance(url!!, canNativeRefresh, jsObjectName!!)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_web_view, fragment, FRAGMENT_TAG)
                .commitAllowingStateLoss()

//       LogUtils.d( supportFragmentManager.findFragmentByTag(FRAGMENT_TAG).hashCode().toString() + FRAGMENT_TAG)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_picture_preview -> {
                Toast.makeText(this, "图片预览", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_picture_selector -> {
                Toast.makeText(this, "图片选择器", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_scan -> {
                Toast.makeText(this, "扫码", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_picture_share -> {
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_pay -> {
                Toast.makeText(this, "支付", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun getFragment(): WebViewFragment {
        return supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as WebViewFragment
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragment().mWebView.canGoBack()) {
                getFragment().mWebView.goBack()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }


    fun updateTitle(title: String) {
        toolbar_title.text = title
    }

    @Deprecated("过时")
    fun setWebView(webView: X5WebView) {
        mX5WebView = webView
    }


}
