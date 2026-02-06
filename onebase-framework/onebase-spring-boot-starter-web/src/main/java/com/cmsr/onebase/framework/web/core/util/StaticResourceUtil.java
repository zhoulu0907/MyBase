package com.cmsr.onebase.framework.web.core.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 静态资源工具类
 */
public class StaticResourceUtil {

    public static final String PLUGIN_STATIC_PREFIX = "/plugins/static/";
    public static final String PLUGIN_STATIC_PATTERN = "/plugins/static/**";

    /**
     * 判断是否为静态资源请求
     *
     * @param request 请求
     * @return 是否为静态资源
     */
    public static boolean isStaticResource(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return isStaticResource(requestUri);
    }

    /**
     * 判断是否为静态资源请求
     *
     * @param uri 请求URI
     * @return 是否为静态资源
     */
    public static boolean isStaticResource(String uri) {
        if (uri == null) {
            return false;
        }
        // 插件静态资源
        return uri.contains(PLUGIN_STATIC_PREFIX);
    }
}
