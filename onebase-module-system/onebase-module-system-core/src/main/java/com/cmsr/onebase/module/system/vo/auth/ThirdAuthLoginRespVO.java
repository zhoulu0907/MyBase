package com.cmsr.onebase.module.system.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(description = "管理后台 - 登录 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ThirdAuthLoginRespVO extends AuthLoginRespVO {

    @Schema(description = "是否关联应用", example = "")
    private Boolean userAppRelationFlag;


    @Schema(description = "用户是否注册（验证码方式需跳转到补充资料，完成注册）", example = "true")
    private Boolean userUnRegistFlag;
}
