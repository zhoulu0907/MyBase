package com.cmsr.onebase.module.metadata.api.validation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 返回 DTO - 数据权限 操作符与字段类型关联
 *
 * 对应列：id, field_type_code, field_type_name, validation_code, validation_name
 *
 * @author bty418
 * @date 2025-08-14
 */
@Data
public class PermitRefOtftRespDTO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "字段类型编码")
    private String fieldTypeCode;

    @Schema(description = "字段类型名称")
    private String fieldTypeName;

    @Schema(description = "操作符编码")
    private String validationCode;

    @Schema(description = "操作符名称")
    private String validationName;
}


