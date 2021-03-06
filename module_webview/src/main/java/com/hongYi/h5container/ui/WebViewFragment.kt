package com.hongYi.h5container.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.hongYi.h5container.BuildConfig
import com.hongYi.h5container.WebViewManager
import com.hongYi.h5container.R
import com.hongYi.h5container.loadsir.ErrorCallback
import com.hongYi.h5container.loadsir.LoadingCallback
import com.hongYi.h5container.utils.Constants
import com.hongYi.h5container.utils.LogUtils
import com.hongYi.h5container.webview.X5WebView
import com.hongYi.h5container.webview.callback.WebViewCallback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import kotlinx.android.synthetic.main.fragment_webview.*

/**
 * @author: HZWei
 * @date:  2021/8/2
 * @desc:
 */
class WebViewFragment : Fragment(), WebViewCallback, OnRefreshListener, X5WebView.OnScrollChangedListener {


    private var mActivity: Activity? = null
    lateinit var mWebView: X5WebView
    private var mCanNativeRefresh = false
    private lateinit var mUrl: String
    private var mJsName = ""
    private var mIsError = false
    private var mLoadService: LoadService<*>? = null
    private var mIsPullRefresh = false
    private var mScrollY = -1 // 如果是-1说明webview没有滚动过，即没有回调onScrollChanged方法


    companion object {

        const val TAG = "WebViewFragment"

        fun newInstance(url: String, canNativeRefresh: Boolean, jsName: String): WebViewFragment {
            val args = Bundle()
            args.putString(Constants.URL, url)
            args.putBoolean(Constants.CAN_NATIVE_REFRESH, canNativeRefresh)
            args.putString(Constants.JS_OBJECT_NAME, jsName)
            val fragment = WebViewFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCanNativeRefresh = arguments?.getBoolean(Constants.CAN_NATIVE_REFRESH, true)!!
        mUrl = arguments?.getString(Constants.URL)!!
        mJsName = arguments?.getString(Constants.JS_OBJECT_NAME).toString()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_webview, container, false)
        mLoadService = LoadSir.getDefault().register(rootView) {
            mLoadService!!.showCallback(LoadingCallback::class.java)
            mWebView.reload()
        }

        return mLoadService?.loadLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smartRefreshLayout.setEnableAutoLoadMore(false)
        smartRefreshLayout.setEnableLoadMore(false)
        smartRefreshLayout.setEnableRefresh(mCanNativeRefresh)
        smartRefreshLayout.setRefreshHeader(ClassicsHeader(mActivity))
        smartRefreshLayout.setOnRefreshListener(this)

        initWebView()

//        addTouchEvent()

    }

    private fun initWebView() {
        mWebView = WebViewManager.newBuilder(this)
                .setRootView(webViewContainer)
                .injectedJsObject(mJsName)
                .setWebUrl(mUrl)
                .setWebViewCallback(this)
                .build()
                .start()
//        if (mActivity is WebViewActivity)
//            (mActivity as WebViewActivity).setWebView(mWebView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView.destroy()
    }

    override fun onLoadingProgress(progress: Int) {
        if (progress == 100) pageLoadedComplete()
    }

    override fun onUpdateTitle(title: String) {
        if (mActivity is WebViewActivity) {
            (mActivity as WebViewActivity).updateTitle(title)
        }
    }

    override fun onPageStarted() {
        if (!mIsPullRefresh && mLoadService?.currentCallback != SuccessCallback::class.java)
            mLoadService?.showCallback(LoadingCallback::class.java)
    }

    override fun onPageFinished() {
//        pageLoadedComplete()
    }

    private fun pageLoadedComplete() {
        if (smartRefreshLayout == null) return
        if (mIsError) {
            smartRefreshLayout.setEnableRefresh(true)
            mLoadService!!.showCallback(ErrorCallback::class.java)
        } else {
            smartRefreshLayout.setEnableRefresh(mCanNativeRefresh)
            mLoadService!!.showSuccess()
        }
        smartRefreshLayout.finishRefresh()
        mIsError = false
        mIsPullRefresh = false
        //        heightCalculate()
    }

    override fun onPageError(stateCode: Int, error: String) {
        mIsError = true
        smartRefreshLayout.finishRefresh()
    }

    private val mRequestCache = ArrayList<String>(8)
    override fun onInterceptLoading(webResourceRequest: WebResourceRequest, requestUrl: String): Boolean {
        if (webResourceRequest.isRedirect) {
            return false
        } else {
            if (mRequestCache.contains(requestUrl)) {
                val index = mRequestCache.indexOf(requestUrl)
                if (index == mRequestCache.size - 1) {
                    return false
                }
            }
            if (mRequestCache.size == 8) {
                mRequestCache.removeAt(0)
            }
            mRequestCache.add(requestUrl)
            startActivity(WebViewActivity::class.java, requestUrl, "")
            return true
        }

    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        mIsPullRefresh = true
        mWebView.reload()
    }


    private fun startActivity(clazz: Class<*>, url: String, title: String) {
        val intent = Intent(mActivity, clazz)
        intent.putExtra(Constants.URL, url)
        intent.putExtra(Constants.TITLE, title)
        intent.putExtra(Constants.JS_OBJECT_NAME, "hYi")
        intent.putExtra(Constants.CAN_NATIVE_REFRESH, false)
        startActivity(intent)
    }


    /*---------------------------------以下可以忽略------------------------------------------*/

    @Deprecated("测试")
    @SuppressLint("ClickableViewAccessibility")
    private fun addTouchEvent() {
        if (!BuildConfig.DEBUG) return
        val gestureDetector = GestureDetector(mActivity, OnGestureListenerImpl())
        mWebView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }


    private inner class OnGestureListenerImpl : GestureDetector.SimpleOnGestureListener() {
        private val TAG = "OnGestureListenerImpl"

        // 当手指按下的时候触发下面的方法
        override fun onDown(e: MotionEvent?): Boolean {
            LogUtils.i(TAG, "onDown")
            return true
        }

        //当用户手指在屏幕上按下,而且还未移动和松开的时候触发这个方法
        override fun onShowPress(e: MotionEvent?) {
            LogUtils.i(TAG, "onShowPress")
        }

        //当手指在屏幕上轻轻点击的时候触发下面的方法
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        //当手指在屏幕上滚动的时候触发这个方法
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            LogUtils.i(TAG, "onScroll distanceY: $distanceY")
            return true
        }

        //当用户手指在屏幕上长按的时候触发下面的方法
        override fun onLongPress(e: MotionEvent?) {
            LogUtils.i(TAG, "onLongPress")
        }

        //当用户的手指在触摸屏上拖过的时候触发下面的方法,velocityX代表横向上的速度,velocityY代表纵向上的速度
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            LogUtils.i(TAG, "onFling")
            return false
        }

    }


    override fun onScrollChanged(newY: Int, oldY: Int) {
        LogUtils.i(TAG, "curY: $newY  oldY: $oldY")
        mScrollY = newY
        heightCalculate()

    }

    /**
     * https://www.cnblogs.com/agilezhu/p/6689839.html
     */
    private fun heightCalculate() {
        val scale = mWebView.scale
        val totalContentHeight = mWebView.contentHeight.times(scale).toInt()
        val webViewHeight = mWebView.height

        when (webViewHeight.let { totalContentHeight.minus(it) }) {
            0, 1 -> {
                mIsCanScroll = false
            }
            else -> {
                mIsCanScroll = true
            }
        }

        LogUtils.i(TAG, "scale: $scale  contentHeight: ${mWebView.contentHeight} totalContentHeight: $totalContentHeight" + " webViewHeight：$webViewHeight")
    }


    private var mIsCanScroll = true
}
