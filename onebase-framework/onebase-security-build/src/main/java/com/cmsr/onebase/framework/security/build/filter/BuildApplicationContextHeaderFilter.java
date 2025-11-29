package com.cmsr.onebase.framework.security.build.filter;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 *
 */
@RequiredArgsConstructor
@Slf4j
public class BuildApplicationContextHeaderFilter extends OncePerRequestFilter {
    private static final String X_APPLICATION_ID = "X-Application-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String applicationIdStr = request.getHeader(X_APPLICATION_ID);
        long applicationId = NumberUtils.toLong(applicationIdStr, -1L);

        // TODO: 在这里判断用户是否有权限访问该应用

        ApplicationManager.setApplicationId(applicationId);

        chain.doFilter(request, response);
    }
}
