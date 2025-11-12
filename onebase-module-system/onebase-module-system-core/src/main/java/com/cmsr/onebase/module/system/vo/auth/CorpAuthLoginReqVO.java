package com.cmsr.onebase.module.system.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 账号密码登录 Request VO，如果登录并绑定社交用户，需要传递 social 开头的参数")
@Data
public class CorpAuthLoginReqVO extends AuthLoginReqVO {

    @Schema(description = "企业ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "企业ID不能为空")
    private Long corpId;

}