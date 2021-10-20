package com.hongYi.h5container;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.hongYi.h5container.loadsir.CustomCallback;
import com.hongYi.h5container.loadsir.EmptyCallback;
import com.hongYi.h5container.loadsir.ErrorCallback;
import com.hongYi.h5container.loadsir.LoadingCallback;
import com.hongYi.h5container.loadsir.TimeoutCallback;
import com.hongYi.h5container.utils.LogUtils;
import com.hongYi.h5container.webview.X5WebView;
import com.hongYi.h5container.webview.callback.WebViewCallback;
import com.hongYi.h5container.webview.settings.IWebViewSettings;
import com.hongYi.h5container.utils.WebViewPool;
import com.hongYi.h5container.webview.webchromeclient.WebChromeClientEx;
import com.hongYi.h5container.webview.webviewclinet.WebViewClientEx;
import com.kingja.loadsir.core.LoadSir;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:HZWei
 * date:  2021/1/19
 * desc:
 */
public class WebViewManager {

    private static final String TAG = "WebViewManager";

    private String mWebUrl;
    private String mBaseUrl;
    private Map<String, String> mParamsMap;
    private X5WebView mWebView;


    private static void init() {
        // 首次初始化冷启动优化
        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        X5WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);

        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())//添加各种状态页
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .addCallback(new TimeoutCallback())
                .addCallback(new CustomCallback())
                .setDefaultCallback(LoadingCallback.class)//设置默认状态页
                .commit();


    }

    /**
     * 默认在主进程初始化
     *
     * @param context
     */
    public static void init(Context context) {
        init(context, true);
    }

    /**
     * 初始化
     *
     * @param context
     * @param mainProcessNeed false：在非主进程初始化
     */
    public static void init(Context context, boolean mainProcessNeed) {
        String processName = getProcessName(context);
        String packageName = context.getApplicationContext().getPackageName();
        if (!mainProcessNeed && TextUtils.equals(processName, packageName))
            return;
        init();
        WebViewPool.getInstance().prepare(context);
        LogUtils.i(TAG, "init ProcessName : " + processName
                + " size: " + WebViewPool.getInstance().size());
    }


    public static String getProcessName(Context cxt) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }


    private WebViewManager() {
        //禁止外部初始化H5Container类
    }

    private WebViewManager(Builder builder) {
        this.mWebUrl = builder.mWebUrl;
        this.mBaseUrl = builder.mBaseUrl;
        this.mParamsMap = builder.mParamsMap;

        mWebView = WebViewPool.getInstance().getX5WebView(builder.mContext);
        if (mWebView == null) throw new NullPointerException("WebView没有初始化");
        attachView(builder);
        addLifecycleObserver(builder);
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 开启硬件加速后可能会抖动
        if (builder.mWebSettings != null) builder.mWebSettings.setSettings(mWebView);
        mWebView.setOnScrollChangedListener(builder.mScrollChangedListener);
        mWebView.setWebViewClient(builder.mWebViewClient == null ? createDefWebViewClient(builder) : builder.mWebViewClient);
        mWebView.setWebChromeClient(builder.mWebChromeClient == null ? createDefWebChromeClient(builder) : builder.mWebChromeClient);
        mWebView.registerJsInterface(builder.mJsObject);
    }

    private void addLifecycleObserver(Builder builder) {
        if (builder.mCurrentFragment != null) {
            builder.mCurrentFragment.getLifecycle().addObserver(mWebView);
        }else {
            if (builder.mContext instanceof FragmentActivity) {
                AppCompatActivity activity = (AppCompatActivity) builder.mContext;
                activity.getLifecycle().addObserver(mWebView);
            }
        }

    }

    private WebViewClientEx createDefWebViewClient(Builder builder) {
        return new WebViewClientEx()
                .setSSLCertificateFileName(builder.mSSLCertificateFileName)
                .setWebViewCallback(builder.mWebViewCallback);
    }

    private WebChromeClientEx createDefWebChromeClient(Builder builder) {
        return new WebChromeClientEx()
                .setWebViewCallback(builder.mWebViewCallback);
    }

    private void attachView(Builder builder) {
        if (builder.mRootView != null) {
            if (builder.mLayoutParams != null) {
                builder.mRootView.addView(mWebView, builder.mLayoutParams);
            } else {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) parent.removeView(mWebView);
                builder.mRootView.addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            throw new NullPointerException("mRootView没有初始化，调用setRootView()方法初始化");
        }
    }

    private void loadUrl() {
        LogUtils.i(TAG, "mWebUrl: " + mWebUrl);
        if (!TextUtils.isEmpty(mWebUrl)) {
            if (mWebUrl.startsWith("http") || mWebUrl.startsWith("file")) {
                if (mParamsMap != null) {
                    mWebView.loadUrl(mWebUrl, mParamsMap);
                } else {
                    mWebView.loadUrl(mWebUrl);
                }
            } else {
                // 用于加载html标签
                mWebView.loadDataWithBaseURL(mBaseUrl, mWebUrl, "text/html",
                        "utf-8", null);
            }
        } else {
            throw new NullPointerException("mWebUrl没有初始化，调用setWebUrl()方法初始化");
        }
    }


    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static Builder newBuilder(Fragment fragment) {
        return new Builder(fragment);
    }

    public X5WebView start() {
        loadUrl();
        return mWebView;
    }


    public static class Builder {

        private Context mContext;
        private Fragment mCurrentFragment;
        private ViewGroup mRootView;
        private ViewGroup.LayoutParams mLayoutParams;
        private IWebViewSettings mWebSettings;

        /**
         * <p>
         * 当url前缀是http、file，默认是通过{@link X5WebView#loadUrl(String)}加载，
         * 反之是使用{@link X5WebView#loadDataWithBaseURL(String, String, String, String, String)}
         * </p>
         */
        private String mWebUrl;

        /**
         * <p>
         * 在{@link X5WebView#loadDataWithBaseURL(String, String, String, String, String)}使用，
         * 是可选参数
         * </p>
         *
         * @see WebViewManager#loadUrl()
         */
        private String mBaseUrl;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private Map<String, String> mParamsMap;

        /**
         * ssl 证书名称
         * 需要存放到assets目录下
         */
        private String mSSLCertificateFileName;
        private X5WebView.OnScrollChangedListener mScrollChangedListener;
        private WebViewCallback mWebViewCallback;
        private String mJsObject;


        public Builder(Context context) {
            mContext = context;
        }

        public Builder(Fragment fragment) {
            mCurrentFragment = fragment;
            mContext = mCurrentFragment.getContext();
        }

        public Builder setWebSettings(@NonNull IWebViewSettings webSettings) {
            mWebSettings = webSettings;
            return this;
        }

        public Builder setWebUrl(@NonNull String webUrl) {
            mWebUrl = webUrl;
            return this;
        }


        public Builder setBaseUrl(String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        public Builder setRootView(@NonNull ViewGroup container) {
            mRootView = container;
            return this;
        }

        public Builder setRootView(@NonNull ViewGroup container, ViewGroup.LayoutParams layoutParams) {
            mRootView = container;
            mLayoutParams = layoutParams;
            return this;
        }

        public Builder setWebViewClient(WebViewClient webViewClient) {
            mWebViewClient = webViewClient;
            return this;
        }


        public Builder setWebChromeClient(WebChromeClient webChromeClient) {
            mWebChromeClient = webChromeClient;
            return this;
        }

        public Builder setParamsMap(Map<String, String> paramsMap) {
            mParamsMap = paramsMap;
            return this;
        }

        public Builder setSSLCertificateFileName(String fileName) {
            mSSLCertificateFileName = fileName;
            return this;
        }

        public Builder setScrollChangedListener(X5WebView.OnScrollChangedListener scrollChangedListener) {
            mScrollChangedListener = scrollChangedListener;
            return this;
        }

        public Builder setWebViewCallback(WebViewCallback webViewCallback) {
            mWebViewCallback = webViewCallback;
            return this;
        }

        public Builder injectedJsObject(String jsObject) {
            mJsObject = jsObject;
            return this;
        }

        public WebViewManager build() {
            return new WebViewManager(this);
        }

    }

}
