package com.cmsr.onebase.module.system.vo.captcha;

import com.cmsr.onebase.framework.common.enums.ReturnTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.catcha.SendTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 验证码 Request VO")
@Data
public class CaptchaReqVO {
    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "手机")
    private String userMobile;

    @NotBlank(message = "返回值类型不能为空")
    @InEnum(value = SendTypeEnum.class, message = "返回值类型必须是 {value}")
    private String sendType;
}
