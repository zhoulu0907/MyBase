package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 表信息 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 表信息 Response VO")
@Data
public class TableInfoRespVO {

    @Schema(description = "表名", example = "users")
    private String tableName;

    @Schema(description = "显示名称", example = "用户表")
    private String displayName;

    @Schema(description = "表注释", example = "系统用户信息表")
    private String tableComment;

    @Schema(description = "表类型", example = "TABLE")
    private String tableType;

    @Schema(description = "模式名", example = "public")
    private String schemaName;

    @Schema(description = "行数", example = "1250")
    private Long rowCount;

}
