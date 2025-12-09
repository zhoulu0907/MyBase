package com.cmsr.onebase.module.infra.dal.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 安全配置分类VO
 *
 * @author mty
 * @date 2025-12-04
 */
@Schema(description = "管理后台 - 安全配置分类 Response VO")
@Data
public class SecurityConfigGetReqVO {

    @Schema(description = "空间ID", example = "1")
    private Long tenantId;

    @Schema(description = "AppID", example = "1")
    private Long appId;

    @Schema(description = "场景编码List")
    private List<String> categoryCode;

}
