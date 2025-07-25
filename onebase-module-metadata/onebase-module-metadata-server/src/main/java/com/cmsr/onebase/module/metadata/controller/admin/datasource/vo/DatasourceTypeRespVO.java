package com.cmsr.onebase.module.metadata.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 数据源类型 Response VO")
@Data
public class DatasourceTypeRespVO {

    @Schema(description = "数据源类型编码", example = "POSTGRESQL")
    private String code;

    @Schema(description = "数据源类型名称", example = "PostgreSQL")
    private String name;

    @Schema(description = "数据源类型描述", example = "PostgreSQL数据库")
    private String description;

    @Schema(description = "是否支持", example = "true")
    private Boolean supported;

    @Schema(description = "默认端口", example = "5432")
    private Integer defaultPort;

}
