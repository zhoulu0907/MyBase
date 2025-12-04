package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ER图响应VO
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Schema(description = "管理后台 - ER图响应")
@Data
public class ERDiagramRespVO {

    @Schema(description = "数据源UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "01onal1s-0000-0000-0000-000000000001")
    private String datasourceId;

    @Schema(description = "数据源名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户数据库")
    private String datasourceName;

    @Schema(description = "实体列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<EREntityVO> entities;

    @Schema(description = "关联关系列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ERRelationshipVO> relationships;
}
