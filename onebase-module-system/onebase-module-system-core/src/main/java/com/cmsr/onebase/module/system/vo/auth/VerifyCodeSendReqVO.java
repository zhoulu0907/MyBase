package com.cmsr.onebase.module.system.vo.auth;

import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.catcha.SendTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 发送手机验证码 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyCodeSendReqVO {

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "手机号")
    private String userMobile;

    @Schema(description = "验证码类型：email 邮箱，mobile 手机号")
    @NotBlank(message = "返回值类型不能为空")
    @InEnum(value = SendTypeEnum.class, message = "返回值类型必须是 {value}")
    private String sendType;

}
