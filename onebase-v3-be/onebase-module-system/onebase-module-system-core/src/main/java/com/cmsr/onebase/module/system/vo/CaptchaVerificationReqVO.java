package com.cmsr.onebase.module.system.vo;

import com.cmsr.onebase.framework.common.enums.TerminalEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 验证码 Request VO")
@Data
public class CaptchaVerificationReqVO {

    // ========== 图片验证码相关 ==========
    @Schema(description = "验证码，验证码开启时，需要传递", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PfcH6mgr8tpXuMWFjvW6YVaqrswIuwmWI5dsVZSg7sGpWtDCUbHuDEXl3cFB1+VvCC/rAkSwK8Fad52FSuncVg==")
    @NotEmpty(message = "验证码不能为空", groups = CodeEnableGroup.class)
    private String captchaVerification;

    // ========== 设备标识相关 ==========
    @Schema(description = "设备ID，用于多设备管理和限制", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "web_chrome_a1b2c3d4e5f6")
    @NotEmpty(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 平台类型：pc、mobile等
     */
    @Schema(description = "登录端类型", example = "pc/mobile...")
    @InEnum(value = TerminalEnum.class ,message = "登录来源类型必须为{value}")
    private String loginPlatform;

    /**
     * 开启验证码的 Group
     */
    public interface CodeEnableGroup {
    }
}
