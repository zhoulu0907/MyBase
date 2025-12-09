package com.cmsr.onebase.module.system.vo.auth;

import com.cmsr.onebase.module.system.vo.CaptchaVerificationReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Schema(description = "管理后台 - 手机密码登录 Request VO")
@Data
public class MobileLoginReqVO extends CaptchaVerificationReqVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotEmpty(message = "登录手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "短信/邮箱验证码",  example = "10")
    private String verifyCode;
}
