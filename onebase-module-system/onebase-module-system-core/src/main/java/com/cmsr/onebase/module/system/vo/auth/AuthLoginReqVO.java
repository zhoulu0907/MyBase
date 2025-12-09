package com.cmsr.onebase.module.system.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 账号密码登录 Request VO，如果登录并绑定社交用户，需要传递 social 开头的参数")
@Data
public class AuthLoginReqVO extends UserLoginReqVO {

    // ========== 绑定社交登录时，需要传递如下参数（暂移除，后续需要绑定社交账号） ==========

    // @Schema(description = "社交平台的类型，参见 SocialTypeEnum 枚举值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    // @InEnum(SocialTypeEnum.class)
    // private Integer socialType;
    //
    // @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    // private String socialCode;
    //
    // @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    // private String socialState;

}