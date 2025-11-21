package com.cmsr.onebase.framework.security.core.filter;

import com.cmsr.onebase.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.cmsr.onebase.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.core.handler.GlobalExceptionHandler;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 *
 */
@RequiredArgsConstructor
@Slf4j
public class BuildAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    private final GlobalExceptionHandler globalExceptionHandler;

    private final OAuth2TokenCommonApi oauth2TokenApi;

    private final SecurityConfigApi securityConfigApi;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 情况一，基于 header[login-user] 获得用户，例如说来自 Gateway 或者其它服务透传
        LoginUser loginUser = buildLoginUserByHeader(request);

        // 情况二，基于 Token 获得用户
        // 注意，这里主要满足直接使用 Nginx 直接转发到 Spring Cloud 服务的场景。
        String token = null;
        if (loginUser == null) {
            token = SecurityFrameworkUtils.obtainAuthorization(request,
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
            
            // 会话空闲检查：排除登录和登出请求
            if (!isLoginOrLogoutRequest(request)) {
                checkAndUpdateSessionIdle(loginUser, token, request, response);
            }
        }
        // 继续过滤链
        chain.doFilter(request, response);
    }

    private LoginUser buildLoginUserByToken(String token, Integer userType) {
        try {
            // 校验访问令牌
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getCheckedData();
            if (accessToken == null) {
                return null;
            }

            // 这里，需要屏蔽用户类型（管理员vs普通用户）匹配逻辑
            // 注意：只有 /admin-api/* 和 /app-api/* 有 userType，才需要比对用户类型，类似 WebSocket 的 /ws/* 连接地址，是不需要比对用户类型的
            // if (userType != null
            //         && ObjUtil.notEqual(accessToken.getUserType(), userType)) {
            //     throw new AccessDeniedException("错误的用户类型");
            // }
            log.info("buildLoginUserByToken userType:{}", userType);

            // 构建登录用户
            return new LoginUser().setId(accessToken.getUserId()).setUserType(accessToken.getUserType())
                    .setCorpId(accessToken.getCorpId())
                    .setInfo(accessToken.getUserInfo()) // 额外的用户信息
                    .setTenantId(accessToken.getTenantId()).setScopes(accessToken.getScopes())
                    .setExpiresTime(accessToken.getExpiresTime());
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
    private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        return new LoginUser().setId(userId).setUserType(userType)
                .setTenantId(WebFrameworkUtils.getTenantId(request));
    }

    private LoginUser buildLoginUserByHeader(HttpServletRequest request) {
        String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
        if (StrUtil.isEmpty(loginUserStr)) {
            return null;
        }
        try {
            loginUserStr = URLDecoder.decode(loginUserStr, StandardCharsets.UTF_8); // 解码，解决中文乱码问题
            LoginUser loginUser = JsonUtils.parseObject(loginUserStr, LoginUser.class);

            // 这里，需要屏蔽用户类型（管理员vs普通用户）匹配逻辑
            // 注意：只有 /admin-api/* 和 /app-api/* 有 userType，才需要比对用户类型，类似 WebSocket 的 /ws/* 连接地址，是不需要比对用户类型的
            // Integer userType = WebFrameworkUtils.getLoginUserType(request);
            // if (userType != null
            //         && loginUser != null
            //         && ObjUtil.notEqual(loginUser.getUserType(), userType)) {
            //     throw new AccessDeniedException("错误的用户类型");
            // }

            return loginUser;
        } catch (Exception ex) {
            log.error("[buildLoginUserByHeader][解析 LoginUser({}) 发生异常]", loginUserStr, ex);
            ;
            throw ex;
        }
    }

    /**
     * 判断是否为登录或登出请求
     * 
     * @param request HTTP请求
     * @return 是否为登录/登出请求
     */
    private boolean isLoginOrLogoutRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        // 获取路径的最后一段
        String lastSegment = uri.substring(uri.lastIndexOf('/') + 1);
        // 检查是否包含login或logout（不区分大小写）
        return lastSegment.toLowerCase().contains("login") || lastSegment.toLowerCase().contains("logout");
    }

    /**
     * 检查并更新会话空闲状态
     * 
     * 实现逻辑：
     * 1. 从token中反查deviceId
     * 2. 调用updateSessionIdleKey更新Redis key的TTL和value
     * 3. 如果更新返回false（Redis key已过期），则调用登出接口并抛出会话超时异常
     * 
     * @param loginUser 登录用户
     * @param token 访问令牌
     * @param request HTTP请求
     * @param response HTTP响应
     */
    private void checkAndUpdateSessionIdle(LoginUser loginUser, String token, 
                                           HttpServletRequest request, HttpServletResponse response) {
        if (loginUser == null || StrUtil.isBlank(token)) {
            return;
        }

        // 步骤1：通过token反查deviceId
        CommonResult<String> deviceIdResult = securityConfigApi.findDeviceIdByToken(loginUser.getId(), token);

        if (deviceIdResult == null || StrUtil.isBlank(deviceIdResult.getData())) {
            log.warn("[checkAndUpdateSessionIdle][无法反查deviceId，跳过会话空闲检查] userId={}, token={}", loginUser.getId(), token);
            return;
        }

        String deviceId = deviceIdResult.getData();

        // 步骤2：更新会话空闲Redis key的TTL和value
        CommonResult<Boolean> updateResult = securityConfigApi.updateSessionIdleKey(loginUser.getId(), deviceId);

        // 步骤3：如果更新失败（返回false），表示Redis key已过期，执行登出操作
        if (updateResult == null || updateResult.getData() == null || !updateResult.getData()) {
            log.info("[checkAndUpdateSessionIdle][会话已超时，执行强制登出] userId={}, deviceId={}", loginUser.getId(), deviceId);

            // 删除accessToken
            oauth2TokenApi.removeAccessToken(token);
            // 删除在线设备记录
            securityConfigApi.removeOnlineDevice(loginUser.getId(), token);
        }

        log.debug("[checkAndUpdateSessionIdle][会话空闲key更新成功] userId={}, deviceId={}", loginUser.getId(), deviceId);
    }

}
