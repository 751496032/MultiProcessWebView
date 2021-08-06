package com.hongYi.h5container.webview.ssl;

import com.tencent.smtt.export.external.interfaces.SslErrorHandler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author:HZWei
 * date:  2021/1/28
 * desc:
 */
public abstract  class SslCallback implements Callback {

    private SslErrorHandler mSslErrorHandler;

    public SslCallback(SslErrorHandler sslErrorHandler) {
        mSslErrorHandler = sslErrorHandler;
    }

    public abstract void verifyResult(boolean verifySuccess);


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        mSslErrorHandler.proceed();
        verifyResult(true);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mSslErrorHandler.cancel();
        verifyResult(false);
    }
}
