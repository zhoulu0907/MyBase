package com.cmsr.onebase.framework.config;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 为请求统一添加 Token 认证头的 OkHttp 拦截器
 *
 * @author matianyu
 * @date 2025-10-15
 */
public class TokenInterceptor implements Interceptor {

    private final String headerName;
    private final String token;

    public TokenInterceptor(String headerName, String token) {
        this.headerName = headerName;
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request withToken = original.newBuilder()
                .header(headerName, token)
                .build();
        return chain.proceed(withToken);
    }
}
