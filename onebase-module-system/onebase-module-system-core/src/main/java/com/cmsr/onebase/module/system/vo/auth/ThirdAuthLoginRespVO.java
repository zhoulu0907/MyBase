package com.cmsr.onebase.module.system.vo.auth;

import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 登录 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ThirdAuthLoginRespVO extends AuthLoginRespVO {

    @Schema(description = "是否关联应用", example = "")
    private Boolean userAppRelationFlag;

    @Schema(description = "是否第一次登录", example = "")
    private Boolean fistLoginFlag;


}
