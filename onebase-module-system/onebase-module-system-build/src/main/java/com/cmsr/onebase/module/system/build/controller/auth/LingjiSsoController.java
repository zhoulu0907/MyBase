package com.cmsr.onebase.module.system.build.controller.auth;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.module.system.service.auth.LingjiSsoService;
import com.cmsr.onebase.module.system.vo.auth.AuthLoginRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 灵畿平台 SSO 登录 Controller
 *
 * @author claude
 * @date 2025-03
 */
@Tag(name = "管理后台 - 灵畿SSO登录")
@RestController
@RequestMapping("/system/lingji-sso")
@Validated
@Slf4j
public class LingjiSsoController {

    @Resource
    private LingjiSsoService lingjiSsoService;

    @Resource
    private SecurityProperties securityProperties;

    @GetMapping("/login")
    @PermitAll
    @ApiSignIgnore
    @Operation(summary = "灵畿平台单点登录")
    public CommonResult<AuthLoginRespVO> login(
            @RequestParam @NotBlank(message = "授权码不能为空") String code,
            @RequestParam(required = false) String deviceId,
            HttpServletRequest request,
            HttpServletResponse response) {
        AuthLoginRespVO result = lingjiSsoService.login(code, deviceId);
        addTokenCookie(response, result.getAccessToken());
        log.info("[LingjiSsoController][SSO登录成功] userId={}, tenantId={}, deviceId={}, cookieName={}, token={}, referer={}, userAgent={}",
                result.getUserId(), result.getTenantId(), deviceId, securityProperties.getTokenHeader(),
                maskToken(result.getAccessToken()), defaultValue(request.getHeader("Referer")),
                defaultValue(request.getHeader("User-Agent")));
        return success(result);
    }

    private void addTokenCookie(HttpServletResponse response, String accessToken) {
        response.addHeader("Set-Cookie", String.format("%s=%s; Path=/; HttpOnly",
                securityProperties.getTokenHeader(), accessToken));
    }

    private String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "-";
        }
        int visibleLength = Math.min(8, token.length());
        return token.substring(0, visibleLength) + "...(len=" + token.length() + ")";
    }

    private String defaultValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
