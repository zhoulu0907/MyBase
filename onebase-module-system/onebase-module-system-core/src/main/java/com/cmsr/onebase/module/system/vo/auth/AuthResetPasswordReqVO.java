package com.cmsr.onebase.module.system.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Schema(description = "管理后台 - 短信重置账号密码 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthResetPasswordReqVO {

    @Schema(description = "userId", requiredMode = Schema.RequiredMode.REQUIRED, example = "007")
    @NotNull(message = "userId 不能为空")
    private Long userId;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @NotEmpty(message = "密码不能为空")
    private String password;

    // @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13312341234")
    // @NotEmpty(message = "手机号不能为空")
    // @Mobile
    // private String mobile;

    // @Schema(description = "手机短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    // @NotEmpty(message = "手机手机短信验证码不能为空")
    // private String code;
}