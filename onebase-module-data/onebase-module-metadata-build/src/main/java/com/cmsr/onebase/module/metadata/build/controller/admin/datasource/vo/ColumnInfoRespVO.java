package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 字段信息 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段信息 Response VO")
@Data
public class ColumnInfoRespVO {

    @Schema(description = "字段名", example = "id")
    private String columnName;

    @Schema(description = "显示名称", example = "主键ID")
    private String displayName;

    @Schema(description = "数据类型", example = "BIGINT")
    private String dataType;

    @Schema(description = "数据长度", example = "20")
    private Integer dataLength;

    @Schema(description = "小数位数", example = "0")
    private Integer decimalPlaces;

    @Schema(description = "是否可为空", example = "false")
    private Boolean isNullable;

    @Schema(description = "是否主键", example = "true")
    private Boolean isPrimaryKey;

    @Schema(description = "是否自增", example = "true")
    private Boolean isAutoIncrement;

    @Schema(description = "默认值", example = "null")
    private String defaultValue;

    @Schema(description = "字段注释", example = "主键ID")
    private String columnComment;

    @Schema(description = "字段顺序", example = "1")
    private Integer ordinalPosition;

}
