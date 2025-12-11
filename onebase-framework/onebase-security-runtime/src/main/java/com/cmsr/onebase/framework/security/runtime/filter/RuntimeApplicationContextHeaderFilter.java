package com.cmsr.onebase.framework.security.runtime.filter;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class RuntimeApplicationContextHeaderFilter extends OncePerRequestFilter {

    @Value("${data.isolation:true}")
    private Boolean dataIsolation;

    private static final String X_APPLICATION_ID = "X-Application-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            if (dataIsolation) {
                ApplicationManager.setVersionTag(VersionTagEnum.RUNTIME.getValue());
            } else {
                ApplicationManager.setVersionTag(0L);
            }
            RuntimeLoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if (loginUser != null && loginUser.getApplicationId() != null) {
                ApplicationManager.setApplicationId(loginUser.getApplicationId());
            } else if (loginUser != null) {
                //企业账号可以登录多个application，所以要从头里面获取
                String applicationIdHeader = request.getHeader(X_APPLICATION_ID);
                Long applicationId = NumberUtils.toLong(applicationIdHeader, -1L);
                ApplicationManager.setApplicationId(applicationId);
                // TODO: 在这里判断用户是否有权限访问该应用
            }
            chain.doFilter(request, response);
        } finally {
            ApplicationManager.clearAll();
        }
    }
}
