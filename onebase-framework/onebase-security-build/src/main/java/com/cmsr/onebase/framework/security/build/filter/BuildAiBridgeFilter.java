package com.cmsr.onebase.framework.security.build.filter;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.security.build.config.AiBridgeProperties;
import com.cmsr.onebase.framework.security.build.context.AiBridgeContextHolder;
import com.cmsr.onebase.framework.security.build.util.AiBridgeCryptoUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.web.core.util.StaticResourceUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuildAiBridgeFilter extends OncePerRequestFilter {

    private static final String HDR_KEY_ID = "X-AI-KeyId";
    private static final String HDR_SIG = "X-AI-Signature";
    private static final String HDR_TS = "X-AI-Timestamp";
    private static final String HDR_NONCE = "X-AI-Nonce";
    private static final String HDR_REQ_ID = "X-AI-Request-Id";
    private static final String HDR_USER_ID = "X-AI-User-Id";
    private static final String HDR_TENANT_ID = "X-AI-Tenant-Id";
    private static final String HDR_APP_ID = "X-AI-App-Id";
    // Meta 加密已移除，保留最简签名校验

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AiBridgeProperties properties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }
        if (StaticResourceUtil.isStaticResource(request)) {
            return true;
        }
        List<String> patterns = properties.getPathPatterns();
        if (patterns == null || patterns.isEmpty()) {
            return !hasAiHeaders(request);
        }
        String uri = request.getRequestURI();
        for (String p : patterns) {
            if (PATH_MATCHER.match(p, uri)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 对于 multipart 请求，包装 request 让 Spring 不解析，确保流可转发
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
            request = new MultipartPassthroughRequest(request);
        }

        try {
            if (!hasAiHeaders(request)) {
                chain.doFilter(request, response);
                return;
            }
            String keyId = StringUtils.trimToEmpty(request.getHeader(HDR_KEY_ID));
            String ts = StringUtils.trimToEmpty(request.getHeader(HDR_TS));
            String nonce = StringUtils.trimToEmpty(request.getHeader(HDR_NONCE));
            String signature = StringUtils.trimToEmpty(request.getHeader(HDR_SIG));
            String reqId = StringUtils.trimToEmpty(request.getHeader(HDR_REQ_ID));
            String userId = StringUtils.trimToEmpty(request.getHeader(HDR_USER_ID));
            String tenantId = StringUtils.trimToEmpty(request.getHeader(HDR_TENANT_ID));
            String appId = StringUtils.trimToEmpty(request.getHeader(HDR_APP_ID));
            String body = ServletUtils.getBody(request);
            String bodyHash = AiBridgeCryptoUtils.sm3Hex(body);

            String canonical = request.getMethod() + "|" + request.getRequestURI() + "|" + ts + "|" + nonce + "|" + userId + "|" + tenantId + "|" + appId + "|" + bodyHash;
            if (StringUtils.isNotBlank(properties.getSm3Key())) {
                String expect = AiBridgeCryptoUtils.hmacSm3Hex(properties.getSm3Key(), canonical);
                if (!StringUtils.equalsIgnoreCase(expect, signature)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            Map<String, Object> meta = Collections.emptyMap();

            AiBridgeContextHolder.set(new AiBridgeContextHolder.Context(reqId, keyId, meta, userId, tenantId, appId));
            if (StringUtils.isNotBlank(tenantId)) {
                try {
                    TenantContextHolder.setTenantId(Long.parseLong(tenantId));
                } catch (NumberFormatException ignored) {
                }
            }
            // 注入一个模拟登录用户，免除常规 Token 认证；并通过 visitTenantId 触发权限跳过逻辑
            LoginUser aiLoginUser = new LoginUser();
            try {
                aiLoginUser.setId(StringUtils.isNotBlank(userId) ? Long.parseLong(userId) : 0L);
            } catch (NumberFormatException ignored) {
                aiLoginUser.setId(0L);
            }
            try {
                Long tid = StringUtils.isNotBlank(tenantId) ? Long.parseLong(tenantId) : null;
                aiLoginUser.setTenantId(tid);
                if (tid != null) {
                    aiLoginUser.setVisitTenantId(-1L);
                }
            } catch (NumberFormatException ignored) {
                aiLoginUser.setTenantId(null);
            }
            aiLoginUser.setUserType(UserTypeEnum.TENANT.getValue());
            aiLoginUser.setRunMode("build");
            aiLoginUser.setLoginPlatform("ai");
            SecurityFrameworkUtils.setLoginUser(aiLoginUser, request);
            response.setHeader(HDR_REQ_ID, reqId);
            chain.doFilter(request, response);
        } finally {
            AiBridgeContextHolder.clear();
            TenantContextHolder.clear();
        }
    }

    private boolean hasAiHeaders(HttpServletRequest request) {
        return StringUtils.isNotBlank(request.getHeader(HDR_KEY_ID)) ||
                StringUtils.isNotBlank(request.getHeader(HDR_SIG)) ||
                StringUtils.isNotBlank(request.getHeader(HDR_REQ_ID)) ||
                StringUtils.isNotBlank(request.getHeader(HDR_TENANT_ID));
    }

    /**
     * 包装请求，伪装 Content-Type，让 Spring 不解析 multipart。
     * 确保原始输入流可被下游代理转发。
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
            // 返回 null，Spring 不会识别为 multipart
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

        @Override
        public jakarta.servlet.http.Part getPart(String name) throws IOException, ServletException {
            // 不解析，直接返回 null
            return null;
        }

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() throws IOException, ServletException {
            // 不解析，返回空集合
            return java.util.Collections.emptyList();
        }
    }
}
