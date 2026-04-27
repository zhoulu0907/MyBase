package com.cmsr.onebase.module.infra.dal.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 安全配置更新请求VO
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Schema(description = "管理后台 - 安全配置更新 Request VO")
@Data
public class SecurityConfigUpdateReqVO {

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED, example = "minLength")
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private String configValue;

}
