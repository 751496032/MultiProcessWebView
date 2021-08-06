package com.hongYi.h5container.webview.ssl;

import android.content.Context;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;

/**
 * author:HZWei
 * date:  2021/1/27
 * desc:
 */
public class SslManager {

    /**
     * ssl校验
     *
     * @param context
     * @param url
     * @param crtFileName 证书文件名称，后缀名.crt，这里默认放在assets下
     * @param sslCallback
     */
    public static void verifySsl(Context context, String url,
                                 String crtFileName, final SslCallback sslCallback) {

        if (!TextUtils.isEmpty(url)) {
            Request request = new Request.Builder().url(url).build();
            createHttpBuilder(context, url, crtFileName)
                    .build()
                    .newCall(request)
                    .enqueue(sslCallback);
        }
    }


    private static OkHttpClient.Builder createHttpBuilder(Context context, String url, String crtFileName) {
        OkHttpClient.Builder builder = null;
        if (!TextUtils.isEmpty(url) && context != null) {
            try {
                builder = setCertificates(new OkHttpClient.Builder(), context.getResources().getAssets().open(crtFileName));
            } catch (IOException e) {
                builder = new OkHttpClient.Builder();
            }
        }
        return builder;
    }


    private static OkHttpClient.Builder setCertificates(OkHttpClient.Builder client, InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//            X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
            Method method = Platform.class.getDeclaredMethod("trustManager", SSLSocketFactory.class);
            method.setAccessible(true);
            Object invoke = method.invoke(Platform.get(), sslSocketFactory);

            X509TrustManager trustManager = null;
            if (invoke instanceof X509TrustManager) {
                trustManager = (X509TrustManager) invoke;
            }

            if (trustManager != null) {
                client.sslSocketFactory(sslSocketFactory, trustManager);
                //hostName验证
                client.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        String peerHost = session.getPeerHost();//服务器返回的域名
                        try {
                            X509Certificate[] peerCertificates = (X509Certificate[]) session.getPeerCertificates();
                            for (X509Certificate c : peerCertificates) {
                                X500Principal subjectX500Principal = c.getSubjectX500Principal();
                                String name = subjectX500Principal.getName();
                                String[] split = name.split(",");
                                for (String s : split) {
                                    if (s.startsWith("CN")) {
                                        if (s.contains(hostname) && s.contains(peerHost)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        } catch (SSLPeerUnverifiedException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return client;
        }

    }
}