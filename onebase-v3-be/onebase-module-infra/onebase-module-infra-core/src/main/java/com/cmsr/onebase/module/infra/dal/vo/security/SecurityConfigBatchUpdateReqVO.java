package com.cmsr.onebase.module.infra.dal.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 安全配置批量更新请求VO
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Schema(description = "管理后台 - 安全配置批量更新 Request VO")
@Data
public class SecurityConfigBatchUpdateReqVO {

    @Schema(description = "配置项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "配置项列表不能为空")
    @Valid
    private List<SecurityConfigUpdateReqVO> configs;

}
