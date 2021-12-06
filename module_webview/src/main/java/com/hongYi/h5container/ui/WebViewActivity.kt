package com.hongYi.h5container.ui

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.hongYi.h5container.R
import com.hongYi.h5container.utils.ActivityHelper
import com.hongYi.h5container.utils.Constants
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

    // 用于处理多任务
    open class Small1 : WebViewActivity() {}
    open class Small2 : WebViewActivity() {}
    open class Small3 : WebViewActivity() {}
    open class Small4 : WebViewActivity() {}

    private var url = ""
    private var title = ""
    private var jsObjectName = ""
    private var canNativeRefresh = true
    private var isShowActionBar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.statusColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(getLayoutRes())
//        supportActionBar?.hide()
        initTaskDescription()

        intentParamsGet()

        initToolbar(toolbar)

        commitWebViewFragment()


    }

    override fun onResume() {
        super.onResume()
        ActivityHelper.INSTANCE.printProcess(this)
    }


    private fun initTaskDescription() {
        val description = ActivityManager.TaskDescription()
        setTaskDescription(description)
    }



    @LayoutRes
    fun getLayoutRes(): Int {
        return R.layout.activity_webview
    }

    fun initToolbar(toolbar: Toolbar) {
        toolbar.visibility = if (isShowActionBar) View.VISIBLE else View.GONE
        toolbar.navigationIcon = resources.getDrawable(R.mipmap.ic_left_arrow, null)
        toolbar.title = ""
        toolbar_title.text = title
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun intentParamsGet() {
        url = intent.getStringExtra(Constants.URL).toString()
        title = intent.getStringExtra(Constants.TITLE).toString()
        canNativeRefresh = intent.getBooleanExtra(Constants.CAN_NATIVE_REFRESH, true)
        isShowActionBar = intent.getBooleanExtra(Constants.IS_SHOW_ACTION_BAR, true)
        jsObjectName = intent.getStringExtra(Constants.JS_OBJECT_NAME).toString()
    }


    private fun commitWebViewFragment() {
        val fragment = WebViewFragment.newInstance(url, canNativeRefresh, jsObjectName)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_web_view, fragment, FRAGMENT_TAG)
                .commitAllowingStateLoss()

        supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
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
