package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 字段查询请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段查询请求 Request VO")
@Data
public class ColumnQueryReqVO {

    @Schema(description = "数据源ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "数据源ID不能为空")
    private String datasourceId;

    @Schema(description = "表名", requiredMode = Schema.RequiredMode.REQUIRED, example = "users")
    @NotBlank(message = "表名不能为空")
    private String tableName;

    @Schema(description = "数据库模式名", example = "public")
    private String schemaName;

}
