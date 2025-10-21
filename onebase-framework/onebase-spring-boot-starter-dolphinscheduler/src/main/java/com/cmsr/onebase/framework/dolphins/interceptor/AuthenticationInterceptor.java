package com.cmsr.onebase.framework.dolphins.interceptor;

import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerProperties;
import jakarta.annotation.Resource;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证拦截器 - 为所有请求添加 Token
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Component
public class AuthenticationInterceptor implements Interceptor {

    @Resource
    private DolphinSchedulerProperties properties;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // 添加 token 到请求头
        Request newRequest = originalRequest.newBuilder()
                .header("token", properties.getToken())
                // 明确声明期望返回 JSON，避免部分服务端因缺少 Accept 误判返回 415
                .header("Content-Type", "application/json")
                .build();
        
        return chain.proceed(newRequest);
    }
}
