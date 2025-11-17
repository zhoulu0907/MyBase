package com.cmsr.onebase.module.system.build.controller.captcha;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.api.security.SecurityConfigApi;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 验证码")
@RestController("adminCaptchaController")
@RequestMapping("/system/captcha")
@Slf4j
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    @Resource
    private SecurityConfigApi securityConfigApi;

    @PostMapping({"/get"})
    @Operation(summary = "获得验证码")
    @PermitAll
    @TenantIgnore
    public CommonResult<ResponseModel> get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        assert request.getRemoteHost() != null;
        String sessionKey = getRemoteId(request);
        data.setBrowserInfo(sessionKey);
        
        // 检查刷新间隔（checkCanRefreshCaptcha内部已通过SETNX原子性完成记录）
        CommonResult<Boolean> checkResult = securityConfigApi.checkCanRefreshCaptcha(sessionKey);
        if (checkResult == null || !checkResult.isSuccess()) {
            log.warn("刷新间隔检查失败");
        }
        
        // 生成验证码
        ResponseModel response = captchaService.get(data);
        
        return success(response);
    }

    @PostMapping("/check")
    @Operation(summary = "校验验证码")
    @PermitAll
    @TenantIgnore
    public CommonResult<ResponseModel> check(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));
        return success(captchaService.check(data));
    }

    public static String getRemoteId(HttpServletRequest request) {
        String ip = ServletUtils.getClientIP(request);
        String ua = request.getHeader("user-agent");
        if (StrUtil.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

}
