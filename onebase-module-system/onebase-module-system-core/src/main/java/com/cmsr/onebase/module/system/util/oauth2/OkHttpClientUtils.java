package com.cmsr.onebase.module.system.util.oauth2;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpClientUtils {

    public static OkHttpClient createTrustingOkHttpClient() {
        try {
            // 创建信任所有证书的TrustManager
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // 创建SSL上下文
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // 创建hostname verifier，接受所有主机名
            final HostnameVerifier hostnameVerifier = (hostname, session) -> {
                log.warn("跳过主机名校验: {}", hostname);
                return true;
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(hostnameVerifier)
                    .connectTimeout(10, TimeUnit.SECONDS)  // 增加超时时间
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        log.info("发送请求: {} {}", request.method(), request.url());
                        Response response = chain.proceed(request);
                        log.info("收到响应: {} {}", response.code(), response.message());
                        return response;
                    })
                    .build();

            log.info("已创建跳过SSL验证的OkHttpClient");
            return client;

        } catch (Exception e) {
            log.error("创建信任所有证书的OkHttpClient失败", e);
            // 返回默认的客户端作为fallback
            return new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
    }

    public static String sendRequest(Request request) {

        try (Response response = createTrustingOkHttpClient().newCall(request).execute()) {

            if (response.body() != null) {
                String string = response.body().string();
                log.info("响应内容: {}", string);
                return string;
            } else {
                log.error("响应为空");
                throw new RuntimeException("访问" + request.url() + "失败: 响应为空");
            }

        } catch (Exception e) {
            log.error("访问失败", e);
            throw new RuntimeException("访问" + request.url() + "失败: " + e.getMessage());
        }
    }


}
