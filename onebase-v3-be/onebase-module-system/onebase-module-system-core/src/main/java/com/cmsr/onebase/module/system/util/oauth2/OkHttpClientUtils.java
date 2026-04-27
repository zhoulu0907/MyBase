package com.cmsr.onebase.module.system.util.oauth2;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okio.Buffer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
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
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // 创建hostname verifier，接受所有主机名
            final HostnameVerifier hostnameVerifier = (hostname, session) -> {
                log.warn("跳过主机名校验: {}", hostname);
                return true;
            };

                ConnectionSpec tlsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
                    .build();

                OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(hostnameVerifier)
                    .connectionSpecs(Arrays.asList(tlsSpec, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
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
        return sendRequest(request, false);
    }

    public static String sendRequest(Request request, boolean logDetail) {
        if (logDetail) {
            log.info("HTTP请求详情: {}", buildRequestDebugInfo(request));
        }

        try (Response response = createTrustingOkHttpClient().newCall(request).execute()) {

            if (response.body() != null) {
                String string = response.body().string();
                if (logDetail) {
                    log.info("HTTP响应详情: code={}, message={}, body={}", response.code(), response.message(), string);
                } else {
                    log.debug("HTTP响应: code={}, message={}, bodyLength={}", response.code(), response.message(), string.length());
                }
                if (!response.isSuccessful()) {
                    throw new RuntimeException("访问" + request.url() + "失败: HTTP " + response.code() + ", body=" + string);
                }
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

    private static String buildRequestDebugInfo(Request request) {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("method=").append(request.method())
                .append(", url=").append(request.url())
                .append(", headers=").append(request.headers());

        RequestBody requestBody = request.body();
        if (requestBody == null) {
            debugInfo.append(", body=");
            return debugInfo.toString();
        }

        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            String body = buffer.readString(StandardCharsets.UTF_8);
            debugInfo.append(", body=").append(body);
        } catch (Exception e) {
            debugInfo.append(", body=<读取失败: ").append(e.getMessage()).append(">");
        }
        return debugInfo.toString();
    }


}
