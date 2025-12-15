package com.cmsr.onebase.framework.security.runtime.filter;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 */
@Slf4j
@Setter
@Component
public class RuntimeApplicationContextHeaderFilter extends OncePerRequestFilter {

    @Value("${data.isolation:true}")
    private boolean dataIsolation;

    @Autowired
    private AppAuthSecurityApi appAuthSecurityApi;

    private static final String X_APPLICATION_ID = "X-Application-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            if (dataIsolation) {
                ApplicationManager.setVersionTag(VersionTagEnum.RUNTIME.getValue());
            } else {
                ApplicationManager.setVersionTag(VersionTagEnum.BUILD.getValue());
            }
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if (loginUser != null) {
                if (loginUser instanceof RuntimeLoginUser runtimeLoginUser) {
                    ApplicationManager.setApplicationId(runtimeLoginUser.getApplicationId());
                } else {
                    //企业账号可以登录多个application，所以要从头里面获取
                    String applicationIdHeader = request.getHeader(X_APPLICATION_ID);
                    Long applicationId = NumberUtils.toLong(applicationIdHeader, -1L);
                    ApplicationManager.setApplicationId(applicationId);
                }
                // TODO: 在这里判断用户是否有权限访问该应用
                if (!hasApplicationPermission(loginUser.getId(), ApplicationManager.getApplicationId())) {
                    CommonResult<?> result = CommonResult.error(401, "无权访问");
                    response.setStatus(401);
                    ServletUtils.writeJSON(response, result);
                    return;
                }
            }
            chain.doFilter(request, response);
        } finally {
            ApplicationManager.clearAll();
        }
    }

    private boolean hasApplicationPermission(Long userId, Long applicationId) {
        return appAuthSecurityApi.hasApplicationPermission(userId, applicationId);
    }
}
