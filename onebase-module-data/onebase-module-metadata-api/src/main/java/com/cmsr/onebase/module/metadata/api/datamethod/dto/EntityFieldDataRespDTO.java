package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EntityFieldDataRespDTO {
    @Schema(description = "字段ID", example = "1")
    private Long fieldId;
    @Schema(description = "字段名称", example = "姓名")
    private String fieldName;
    @Schema(description = "显示名称", example = "姓名")
    private String displayName;
    @Schema(description = "字段类型", example = "STRING")
    private String fieldType;
    @Schema(description = "JDBC类型", example = "VARCHAR")
    private String jdbcType;
    @Schema(description = "字段值", example = "张三")
    private Object fieldValue;
}
