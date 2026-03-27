package com.cmsr.onebase.framework.security.build.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * 禁用 multipart 解析的 Filter，用于 AI 代理路径。
 * 防止 Spring 的 multipart 解析器消费请求体，确保流式转发正常。
 */
public class DisableMultipartFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 包装请求，伪装 Content-Type 并阻止 getParameter 触发 multipart 解析
        chain.doFilter(new MultipartPassthroughRequest(request), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只处理 multipart 请求
        String contentType = request.getContentType();
        return contentType == null || !contentType.startsWith("multipart/");
    }

    /**
     * 包装请求，伪装 Content-Type 并阻止 getParameter 触发 multipart 解析。
     */
    private static class MultipartPassthroughRequest extends HttpServletRequestWrapper {
        private static final String MULTIPART = "multipart/";
        private final String realContentType;
        private final boolean isMultipart;

        MultipartPassthroughRequest(HttpServletRequest request) {
            super(request);
            this.realContentType = request.getContentType();
            this.isMultipart = realContentType != null && realContentType.startsWith(MULTIPART);
        }

        @Override
        public String getContentType() {
            // multipart 返回 null，其他原样返回
            if (isMultipart) {
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

        @Override
        public String getParameter(String name) {
            // multipart 请求返回 null，阻止 Tomcat 解析 body
            if (isMultipart) {
                return null;
            }
            return super.getParameter(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            // multipart 请求返回空 map，阻止 Tomcat 解析 body
            if (isMultipart) {
                return Collections.emptyMap();
            }
            return super.getParameterMap();
        }

        @Override
        public Enumeration<String> getParameterNames() {
            // multipart 请求返回空枚举，阻止 Tomcat 解析 body
            if (isMultipart) {
                return Collections.emptyEnumeration();
            }
            return super.getParameterNames();
        }

        @Override
        public String[] getParameterValues(String name) {
            // multipart 请求返回 null，阻止 Tomcat 解析 body
            if (isMultipart) {
                return null;
            }
            return super.getParameterValues(name);
        }
    }
}
