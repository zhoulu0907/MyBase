package com.cmsr.onebase.module.system.vo.auth;

import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.catcha.SendTypeEnum;
import com.cmsr.onebase.module.system.enums.login.LongTypeEnum;
import com.cmsr.onebase.module.system.vo.CaptchaVerificationReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "管理后台 - 账号密码登录 Request VO，如果登录并绑定社交用户，需要传递 social 开头的参数")
@Data
public class ThirdAuthLoginReqVO extends CaptchaVerificationReqVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotEmpty(message = "登录手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    private String password;

    @Schema(description = "验证码",  example = "10")
    private String verifyCode;

    @Schema(description = "密码/验证码",  example = "10")
    @NotBlank(message = "登录方式不能为空")
    @InEnum(value = LongTypeEnum.class, message = "返回值类型必须是 {value}")
    private String loginType;

}