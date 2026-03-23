package com.cmsr.onebase.framework.security.build.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 禁用 multipart 解析的 Filter，用于 AI 代理路径。
 * 防止 Spring 的 multipart 解析器消费请求体，确保流式转发正常。
 */
public class DisableMultipartFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 包装请求，伪装 Content-Type，让 Spring 不解析 multipart
        chain.doFilter(new MultipartPassthroughRequest(request), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 处理所有包含 /build/ai/ 的路径（如 /admin-api/build/ai/...）
        // todo 待完善
        return !request.getRequestURI().startsWith("/admin-api/build/ai");
    }

    /**
     * 包装请求，伪装 Content-Type 为 null，让 Spring 不解析 multipart。
     */
    private static class MultipartPassthroughRequest extends HttpServletRequestWrapper {
        private static final String MULTIPART = "multipart/";
        private final String realContentType;

        MultipartPassthroughRequest(HttpServletRequest request) {
            super(request);
            this.realContentType = request.getContentType();
        }

        @Override
        public String getContentType() {
            // multipart 返回 null，其他原样返回
            if (realContentType != null && realContentType.startsWith(MULTIPART)) {
                return null;
            }
            return realContentType;
        }

        @Override
        public String getHeader(String name) {
            if ("content-type".equalsIgnoreCase(name)) {
                return getContentType();
            }
            return super.getHeader(name);
        }
    }
}
