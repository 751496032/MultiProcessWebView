package com.hongYi.h5container.webview.webviewclinet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.hongYi.h5container.utils.LogUtils;
import com.hongYi.h5container.webview.callback.WebViewCallback;
import com.hongYi.h5container.webview.ssl.SslCallback;
import com.hongYi.h5container.webview.ssl.SslManager;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * author:HZWei
 * date:  2021/1/20
 * desc:  主要是页面加载状态与拦截等相关回调，比如开始加载、结束加载、加载错误信息、SSL效验等
 */
public class WebViewClientEx extends WebViewClient {

    private static final String TAG = "WebViewClient";

    /**
     * ssl 证书名称，默认是放在assets目录下
     */
    private String mSSLCertificateFileName;

    /**
     * 同个域名验证通过后不重复验证
     */
    private boolean mIsSSLAuthSucceeds;


    private WebViewCallback mWebViewCallback;

    private boolean mIsCanClearHistory = true;


    public WebViewClientEx setSSLCertificateFileName(String fileName) {
        mSSLCertificateFileName = fileName;
        return this;
    }

    public WebViewClientEx setWebViewCallback(WebViewCallback webViewCallback) {
        mWebViewCallback = webViewCallback;
        return this;
    }

    /**
     * <p>
     * 控制是否在当前WebView打开网页
     * </p>
     *
     * @param webView
     * @param webResourceRequest
     * @return
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        String requestUrl = webResourceRequest.getUrl().toString();
        LogUtils.i(TAG, "shouldOverrideUrlLoadingN  requestUrl: " + requestUrl);
        LogUtils.i(TAG, "shouldOverrideUrlLoadingN originalUrl: " + webView.getOriginalUrl());
        return interceptUrlLoading(webView, requestUrl, webResourceRequest);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String requestUrl) {
        LogUtils.i(TAG, "shouldOverrideUrlLoading   requestUrl: " + requestUrl);
        LogUtils.i(TAG, "shouldOverrideUrlLoading  originalUrl: " + webView.getOriginalUrl());

        return interceptUrlLoading(webView, requestUrl, new WebResourceRequestImpl(requestUrl));
    }

    private boolean mIsPageLoading = false;

    /**
     * 加载网页正常流程是 onPageStarted -> onPageFinished
     * 如果发生了重定向，shouldOverrideUrlLoading会在onPageFinished之前触发，同时onPageStarted会调用多次，如下：
     * onPageStarted... -> shouldOverrideUrlLoading -> onPageFinished
     * @param webView
     * @param url
     * @param webResourceRequest
     * @return
     */
    private boolean interceptUrlLoading(WebView webView, String url, WebResourceRequest webResourceRequest) {
        // 处理重定向
        if (mIsPageLoading) {
            WebResourceRequestImpl request = null;
            if (webResourceRequest instanceof WebResourceRequestImpl){
                // 这里是兼容Android N以下低版本
                request = (WebResourceRequestImpl) webResourceRequest;
            }else {
                // 将X5内核的WebResourceRequest转换成自定义的对象
                LogUtils.i(TAG, "interceptUrlLoadingX5  isRedirect: " + webResourceRequest.isRedirect());
                request = new WebResourceRequestImpl(webResourceRequest);
            }
            request.setRedirect(true);
            webResourceRequest = request;
        }
        LogUtils.i(TAG, "interceptUrlLoading  isRedirect: " + webResourceRequest.isRedirect());
        LogUtils.i(TAG, "interceptUrlLoading  mIsPageLoading: " + mIsPageLoading);
        Context context = webView.getContext();
        if (TextUtils.isEmpty(url) || url.startsWith("http") || url.startsWith("file://")) {
            if (mWebViewCallback != null) {
                return mWebViewCallback.onInterceptLoading(webResourceRequest, url);
            }
            return false;
        } else {
            try {
                // scheme协议跳转到第三方
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (context != null) context.startActivity(intent);
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * 可以用于拦截替换资源，比如图片资源等
     *
     * @param webView
     * @param webResourceRequest
     * @return
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
        return super.shouldInterceptRequest(webView, webResourceRequest);
    }

    /**
     * 网页准备开始加载时调用
     *
     * @param webView
     * @param url
     * @param bitmap
     */
    @Override
    public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
        super.onPageStarted(webView, url, bitmap);
        LogUtils.i(TAG, "onPageStarted");
        mIsPageLoading = true;
        if (mIsCanClearHistory) {
            webView.clearHistory();
            mIsCanClearHistory = false;
        }

        if (mWebViewCallback != null) mWebViewCallback.onPageStarted();
    }


    /**
     * 网页加载结束时调用
     *
     * @param webView
     * @param url
     */
    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        LogUtils.i(TAG, "onPageFinished");
        mIsPageLoading = false;
        if (mWebViewCallback != null) mWebViewCallback.onPageFinished();
    }

    @Override
    public void onPageCommitVisible(WebView webView, String url) {
        super.onPageCommitVisible(webView, url);
        LogUtils.i(TAG, "onPageCommitVisible");
    }

    /**
     * 在doUpdateVisitedHistory方法中清除记录才会生效
     * 另外也可以在onPageStarted onPageFinished方法中清除
     *
     * @param webView
     * @param s
     * @param b
     */
    @Override
    public void doUpdateVisitedHistory(WebView webView, String s, boolean b) {
        super.doUpdateVisitedHistory(webView, s, b);
        LogUtils.i("doUpdateVisitedHistory");
    }

    /**
     * <p>
     * 网页加载错误时会调用
     * 在加载资源(iframe,image,js,css,ajax...)时收到了 HTTP 错误(状态码>=400)
     * </p>
     *
     * @param webView
     * @param webResourceRequest
     * @param webResourceResponse
     */
    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
        if (mWebViewCallback != null)
            mWebViewCallback.onPageError(webResourceResponse.getStatusCode(), "");

    }


    /**
     * <p>
     * 效验https协议证书
     * </p>
     *
     * @param webView
     * @param sslErrorHandler
     * @param sslError
     */
    @Override
    public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
        String url = webView.getUrl();
        if (!TextUtils.isEmpty(mSSLCertificateFileName) && !mIsSSLAuthSucceeds) {
            SslManager.verifySsl(webView.getContext().getApplicationContext(), url, mSSLCertificateFileName, new SslCallback(sslErrorHandler) {
                @Override
                public void verifyResult(boolean verifySuccess) {
                    mIsSSLAuthSucceeds = verifySuccess;
                }
            });
        }
    }


    /**
     * 加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
     *
     * @param webView
     * @param url
     */
    @Override
    public void onLoadResource(WebView webView, String url) {
        super.onLoadResource(webView, url);
    }


}
