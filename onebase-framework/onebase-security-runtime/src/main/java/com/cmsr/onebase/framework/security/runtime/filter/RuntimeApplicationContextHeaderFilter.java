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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN_APP;

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

    private RequestMatcher systemRequestMatcher = new AntPathRequestMatcher("/runtime/system/**");
    private RequestMatcher corpRequestMatcher   = new AntPathRequestMatcher("/runtime/corp/**");
    private RequestMatcher appGetRequestMatcher = new AntPathRequestMatcher("/runtime/app/application/get");

    /**
     * 企业接口和系统接口不做App校验
     *
     * @param request
     * @return
     */
    private boolean doFilter(HttpServletRequest request) {
        return systemRequestMatcher.matches(request) || corpRequestMatcher.matches(request) || appGetRequestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            // 如果是系统、企业相关接口，不做App校验，直接放行
            if (doFilter(request)) {
                chain.doFilter(request, response);
                return;
            }

            if (dataIsolation) {
                ApplicationManager.setVersionTag(VersionTagEnum.RUNTIME.getValue());
            } else {
                ApplicationManager.setVersionTag(VersionTagEnum.BUILD.getValue());
            }
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            Long applicationId = null;
            if (loginUser instanceof RuntimeLoginUser runtimeLoginUser) {
                applicationId = runtimeLoginUser.getApplicationId();
            }
            if (applicationId == null) {
                // 企业账号可以登录多个application，所以要从头里面获取
                log.warn("登录用户无应用ID，从请求头中获取应用ID，loginUser={}", loginUser);
                String applicationIdHeader = request.getHeader(X_APPLICATION_ID);
                applicationId = NumberUtils.toLong(applicationIdHeader, -1L);
            }
            if (applicationId <= 0) {
                CommonResult<?> result = CommonResult.error(FORBIDDEN_APP.getCode(), "应用ID为空");
                ServletUtils.writeJSON(response, result);
                return;
            }
            ApplicationManager.setApplicationId(applicationId);
            if (!hasApplicationPermission(loginUser.getId(), ApplicationManager.getApplicationId())) {
                CommonResult<?> result = CommonResult.error(FORBIDDEN_APP);
                ServletUtils.writeJSON(response, result);
                return;
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
