package com.restproject.mobile.api_helpers;
import android.annotation.SuppressLint;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UnsafeVolleyStack extends HurlStack {

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        if (url.getProtocol().equals("https")) {
            trustAllHosts();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
            return httpsURLConnection;
        } else {
            return super.createConnection(url);
        }
    }

    private static void trustAllHosts() {
        @SuppressLint("CustomX509TrustManager") TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[] {}; }
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}

