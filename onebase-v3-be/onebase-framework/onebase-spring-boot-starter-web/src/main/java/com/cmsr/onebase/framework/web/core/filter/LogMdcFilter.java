package com.cmsr.onebase.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 日志MDC过滤器，用于在日志中添加请求相关信息
 */
public class LogMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 初始化MDC字段
        initMdcFields(request);
        
        long startTime = System.currentTimeMillis();
        try {
            // 继续处理请求
            filterChain.doFilter(request, response);
        } finally {
            long latency = System.currentTimeMillis() - startTime;
            
            // 更新响应相关的MDC字段
            updateMdcResponseFields(response, latency);
        }
    }
    
    /**
     * 初始化MDC字段
     */
    private void initMdcFields(HttpServletRequest request) {
        // 生成或获取trace_id
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("trace_id", traceId);
        
        // 请求相关信息
        MDC.put("req_method", request.getMethod());
        MDC.put("req_host", request.getServerName());
        MDC.put("req_path", request.getRequestURI());
        MDC.put("req_query", StrUtil.blankToDefault(request.getQueryString(), ""));
        MDC.put("req_ua", StrUtil.blankToDefault(request.getHeader("User-Agent"), ""));
        MDC.put("req_ip", getClientIP(request));
        MDC.put("req_body_len", String.valueOf(request.getContentLength()));
        MDC.put("log_ver", "1.0");
    }
    
    /**
     * 更新响应相关的MDC字段
     */
    private void updateMdcResponseFields(HttpServletResponse response, long latency) {
        MDC.put("resp_status", String.valueOf(response.getStatus()));
        // 简化处理，不计算响应体大小
        MDC.put("resp_body_len", "N/A");
        MDC.put("latency_ms", String.valueOf(latency));
        // 错误码将在业务逻辑中设置
        MDC.put("error_code", "");
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    @Override
    public void destroy() {
        // 清理MDC
        MDC.clear();
        super.destroy();
    }
}