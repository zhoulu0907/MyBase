package com.cmsr.onebase.framework.security.runtime.filter;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
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

    private static final String X_APPLICATION_ID = "X-Application-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Long applicationId = ApplicationManager.getApplicationId();
        if (applicationId == null) {
            String applicationIdHeader = request.getHeader(X_APPLICATION_ID);
            applicationId = NumberUtils.toLong(applicationIdHeader, -1L);
            // TODO: 在这里判断用户是否有权限访问该应用
            ApplicationManager.setApplicationId(applicationId);
        }
        chain.doFilter(request, response);
    }
}
