package com.cmsr.onebase.framework.security.runtime.filter;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.cmsr.onebase.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.security.runtime.RTLoginUser;
import com.cmsr.onebase.framework.web.core.handler.GlobalExceptionHandler;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 */
@RequiredArgsConstructor
@Slf4j
public class RuntimeAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    private final GlobalExceptionHandler globalExceptionHandler;

    private final OAuth2TokenCommonApi oauth2TokenApi;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication authentication = SecurityFrameworkUtils.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }
        // 情况一，基于 header[login-user] 获得用户，例如说来自 Gateway 或者其它服务透传
        RTLoginUser loginUser = buildLoginUserByHeader(request);
        // 情况二，基于 Token 获得用户
        if (loginUser == null) {
            String token = SecurityFrameworkUtils.obtainAuthorization(request,
                    securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
            if (StrUtil.isNotEmpty(token)) {
                Integer userType = WebFrameworkUtils.getLoginUserType(request);
                try {
                    // 1.1 基于 token 构建登录用户
                    loginUser = buildLoginUserByToken(token, userType);
                    // 1.2 模拟 Login 功能，方便日常开发调试
                    if (loginUser == null) {
                        loginUser = mockLoginUser(request, token, userType);
                    }
                } catch (Throwable ex) {
                    CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                    ServletUtils.writeJSON(response, result);
                    return;
                }
            }
        }
        // 设置当前用户
        if (loginUser != null) {
            SecurityFrameworkUtils.setLoginUser(loginUser, request);
        }
        // 继续过滤链
        chain.doFilter(request, response);
    }

    private RTLoginUser buildLoginUserByToken(String token, Integer userType) {
        try {
            // 校验访问令牌
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getCheckedData();
            if (accessToken == null) {
                return null;
            }

            // 暂不校验类型，打印日志
            log.info("buildLoginUserByToken userType:{}", userType);

            // 构建登录用户
            RTLoginUser loginUser = new RTLoginUser();
            loginUser.setApplicationId(accessToken.getAppId())
                    .setId(accessToken.getUserId()).setUserType(accessToken.getUserType())
                    .setInfo(accessToken.getUserInfo()) // 额外的用户信息
                    .setTenantId(accessToken.getTenantId()).setScopes(accessToken.getScopes())
                    .setExpiresTime(accessToken.getExpiresTime());
            return loginUser;
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }

    /**
     * 模拟登录用户，方便日常开发调试
     * <p>
     * 注意，在线上环境下，一定要关闭该功能！！！
     *
     * @param request  请求
     * @param token    模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
     * @param userType 用户类型
     * @return 模拟的 LoginUser
     */
    private RTLoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        RTLoginUser loginUser = new RTLoginUser();
        loginUser.setId(userId).setUserType(userType).setTenantId(WebFrameworkUtils.getTenantId(request));
        return loginUser;
    }

    private RTLoginUser buildLoginUserByHeader(HttpServletRequest request) {
        String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
        if (StrUtil.isEmpty(loginUserStr)) {
            return null;
        }
        try {
            loginUserStr = URLDecoder.decode(loginUserStr, StandardCharsets.UTF_8); // 解码，解决中文乱码问题
            RTLoginUser loginUser = JsonUtils.parseObject(loginUserStr, RTLoginUser.class);
            return loginUser;
        } catch (Exception ex) {
            log.error("[buildLoginUserByHeader][解析 LoginUser({}) 发生异常]", loginUserStr, ex);
            ;
            throw ex;
        }
    }

}
