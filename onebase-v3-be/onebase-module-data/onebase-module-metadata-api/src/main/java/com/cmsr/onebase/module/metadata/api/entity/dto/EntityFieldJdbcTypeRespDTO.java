package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段JDBC类型响应DTO
 *
 * @author matianyu
 * @date 2025-09-17
 */
@Data
@Schema(description = "字段JDBC类型响应DTO")
public class EntityFieldJdbcTypeRespDTO {

    @Schema(description = "字段ID")
    private Long fieldId;

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "JDBC数据类型")
    private String jdbcType;

    @Schema(description = "字段类型编码（来自metadata_entity_field表）")
    private String fieldType;

}
