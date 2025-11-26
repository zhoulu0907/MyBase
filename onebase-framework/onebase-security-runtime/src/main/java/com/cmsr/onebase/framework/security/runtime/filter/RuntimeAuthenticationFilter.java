package com.cmsr.onebase.framework.security.runtime.filter;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.cmsr.onebase.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
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

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.SEESION_TIMEOUT;

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

    private final SecurityConfigApi securityConfigApi;

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
        RuntimeLoginUser loginUser = buildLoginUserByHeader(request);
        // 情况二，基于 Token 获得用户
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
                boolean checkSuc = checkAndUpdateSessionIdle(loginUser, token);
                if (!checkSuc) {
                    log.error("[BuildAuthenticationFilter][长时间内无操作，自动登出。]");
                    CommonResult<?> result = CommonResult.error(SEESION_TIMEOUT);
                    ServletUtils.writeJSON(response, result);
                    return; // 中断
                }
            }
        }
        // 继续过滤链
        chain.doFilter(request, response);
    }

    private RuntimeLoginUser buildLoginUserByToken(String token, Integer userType) {
        try {
            // 校验访问令牌
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getCheckedData();
            if (accessToken == null) {
                return null;
            }

            // 暂不校验类型，打印日志
            log.info("buildLoginUserByToken userType:{}", userType);

            // 构建登录用户
            RuntimeLoginUser loginUser = new RuntimeLoginUser();
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
    private RuntimeLoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }
        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
        RuntimeLoginUser loginUser = new RuntimeLoginUser();
        loginUser.setId(userId).setUserType(userType).setTenantId(WebFrameworkUtils.getTenantIdFromHeader(request));
        return loginUser;
    }

    private RuntimeLoginUser buildLoginUserByHeader(HttpServletRequest request) {
        String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
        if (StrUtil.isEmpty(loginUserStr)) {
            return null;
        }
        try {
            loginUserStr = URLDecoder.decode(loginUserStr, StandardCharsets.UTF_8); // 解码，解决中文乱码问题
            RuntimeLoginUser loginUser = JsonUtils.parseObject(loginUserStr, RuntimeLoginUser.class);
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
        // 检查是否包含login或logout或register（不区分大小写）
        return lastSegment.toLowerCase().contains("login")
                || lastSegment.toLowerCase().contains("logout")
                || lastSegment.toLowerCase().contains("register")
                ;
    }

    /**
     * 检查并更新会话空闲状态
     * <p>
     * 实现逻辑：
     * 1. 从token中反查deviceId
     * 2. 调用updateSessionIdleKey更新Redis key的TTL和value
     * 3. 如果更新返回false（Redis key已过期），则调用登出接口并抛出会话超时异常
     *
     * @param loginUser 登录用户
     * @param token     访问令牌
     */
    private boolean checkAndUpdateSessionIdle(RuntimeLoginUser loginUser, String token) {
        if (loginUser == null || StrUtil.isBlank(token)) {
            return true;
        }

        // 步骤1：通过token反查deviceId
        CommonResult<String> deviceIdResult = securityConfigApi.findDeviceIdByToken(loginUser.getTenantId(), loginUser.getId(), token);

        if (deviceIdResult == null || StrUtil.isBlank(deviceIdResult.getData())) {
            log.warn("[checkAndUpdateSessionIdle][无法反查deviceId，跳过会话空闲检查] userId={}, token={}", loginUser.getId(), token);
            return true;
        }

        String deviceId = deviceIdResult.getData();

        // 步骤2：更新会话空闲Redis key的TTL和value
        CommonResult<Boolean> updateResult = securityConfigApi.updateSessionIdleKey(loginUser.getTenantId(), loginUser.getId(), deviceId);

        // 步骤3：如果更新失败（返回false），表示Redis key已过期，执行登出操作
        if (updateResult == null || updateResult.getData() == null || !updateResult.getData()) {
            log.info("[checkAndUpdateSessionIdle][会话已超时，执行强制登出] userId={}, deviceId={}", loginUser.getId(), deviceId);

            // 删除accessToken
            oauth2TokenApi.removeAccessToken(token);
            // 删除在线设备记录
            securityConfigApi.removeOnlineDevice(loginUser.getTenantId(), loginUser.getId(), token);

            return false;
        }

        log.debug("[checkAndUpdateSessionIdle][会话空闲key更新成功] userId={}, deviceId={}", loginUser.getId(), deviceId);
        return true;
    }

}
