package com.cmsr.onebase.module.system.build.controller.captcha;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.captcha.CaptchaReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 验证码")
@RestController("adminCaptchaController")
@RequestMapping("/system/captcha")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;



    @PostMapping({"/get"})
    @Operation(summary = "获得验证码")
    @PermitAll
    @TenantIgnore
    public CommonResult<ResponseModel> get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        assert request.getRemoteHost() != null;
        data.setBrowserInfo(getRemoteId(request));
        return success(captchaService.get(data));
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


    @PostMapping({"/send-captcha-code"})
    @Operation(summary = "向邮箱/手机发送验证码")
    @PermitAll
    @TenantIgnore
    public CommonResult<Boolean> sendCaptchaCode(@RequestBody CaptchaReqVO reqVO) {

        return success(true);
    }

}
