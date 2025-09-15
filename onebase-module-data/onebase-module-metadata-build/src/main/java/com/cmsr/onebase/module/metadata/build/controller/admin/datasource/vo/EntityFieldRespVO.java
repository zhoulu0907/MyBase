package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 实体字段信息 Response VO")
@Data
public class EntityFieldRespVO {

    @Schema(description = "字段名称", example = "id")
    private String fieldName;

    @Schema(description = "字段类型", example = "BIGINT")
    private String fieldType;

    @Schema(description = "字段长度", example = "20")
    private Integer fieldLength;

    @Schema(description = "是否主键", example = "true")
    private Boolean isPrimaryKey;

    @Schema(description = "是否允许为空", example = "false")
    private Boolean isNullable;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "字段注释", example = "主键ID")
    private String comment;

}
