package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 数据源ER图信息 Response VO")
@Data
public class DatasourceErDiagramRespVO {

    @Schema(description = "数据源ID", example = "1024")
    private String datasourceId;

    @Schema(description = "数据源名称", example = "用户数据库")
    private String datasourceName;

    @Schema(description = "业务实体列表")
    private List<ErEntityRespVO> entities;

    @Schema(description = "实体关系列表")
    private List<ErRelationRespVO> relations;

}
