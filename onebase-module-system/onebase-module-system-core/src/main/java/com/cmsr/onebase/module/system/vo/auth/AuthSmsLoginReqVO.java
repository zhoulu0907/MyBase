package com.cmsr.onebase.module.system.vo.auth;

import com.cmsr.onebase.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 短信验证码的登录 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthSmsLoginReqVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebaseyuanma")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "验证码不能为空")
    private String code;

    @Schema(description = "设备ID，用于多设备管理和限制", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "web_chrome_a1b2c3d4e5f6")
    @NotEmpty(message = "设备ID不能为空")
    private String deviceId;

}
