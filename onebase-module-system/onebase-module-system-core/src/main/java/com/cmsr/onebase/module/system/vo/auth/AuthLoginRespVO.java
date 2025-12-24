package com.cmsr.onebase.module.system.vo.auth;

import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 登录 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthLoginRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "happy")
    private String accessToken;

    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "nice")
    private String refreshToken;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;
    
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "租户子域名", example = "onebase")
    private String tenantWebsite;

    @Schema(description = "企业ID", example = "1")
    private Long corpId;

    @Schema(description = "是否管理员", example = "")
    private Boolean adminFlag;

    @Schema(description = "登录来源", example = "")
    private String loginSource;


    @Schema(description = "昵称", example = "")
    private String nickName;

    @Schema(description = "邮箱", example = "")
    private String email;

    @Schema(description = "密码有效期检查信息")
    private PasswordExpiryCheckDTO passwordExpiryInfo;
}
