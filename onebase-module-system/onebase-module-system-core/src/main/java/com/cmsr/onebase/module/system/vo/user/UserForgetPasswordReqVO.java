package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 用户更新密码 Request VO")
@Data
public class UserForgetPasswordReqVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "验证码",  example = "10")
    @NotEmpty(message = "验证码不能为空")
    private String verifyCode;
}
