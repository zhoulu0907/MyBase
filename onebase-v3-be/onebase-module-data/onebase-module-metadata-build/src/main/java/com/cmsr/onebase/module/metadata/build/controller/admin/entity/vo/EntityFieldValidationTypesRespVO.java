package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 批量查询字段可选校验类型 响应 VO
 *
 * @author matianyu
 * @date 2025-09-02
 */
@Data
public class EntityFieldValidationTypesRespVO {

    @Schema(description = "字段ID")
    private Long fieldId;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "字段类型编码（来自 metadata_entity_field.field_type 与 metadata_component_field_type.field_type_code 对应）")
    private String fieldTypeCode;

    @Schema(description = "可选校验类型列表")
    private List<ValidationTypeItemRespVO> validationTypes;
}
