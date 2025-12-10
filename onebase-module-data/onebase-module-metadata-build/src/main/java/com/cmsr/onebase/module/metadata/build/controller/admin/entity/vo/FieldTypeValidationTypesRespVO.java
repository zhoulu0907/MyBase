package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 字段类型对应校验类型响应VO
 *
 * @author matianyu
 * @date 2025-12-10
 */
@Schema(description = "字段类型对应校验类型响应VO")
@Data
public class FieldTypeValidationTypesRespVO {

    @Schema(description = "字段类型编码（来自 metadata_component_field_type.field_type_code）", example = "text")
    private String fieldTypeCode;

    @Schema(description = "字段类型名称", example = "单行文本")
    private String fieldTypeName;

    @Schema(description = "字段类型描述", example = "单行文本字段类型")
    private String fieldTypeDesc;

    @Schema(description = "该字段类型支持的校验类型列表")
    private List<ValidationTypeItemRespVO> validationTypes;

}
