package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 字段类型批量查询校验类型请求VO
 *
 * @author matianyu
 * @date 2025-12-10
 */
@Schema(description = "字段类型批量查询校验类型请求VO")
@Data
public class FieldTypeValidationTypesReqVO {

    @Schema(description = "字段类型编码列表（来自 metadata_component_field_type.field_type_code）", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"text\", \"number\", \"date\"]")
    @NotEmpty(message = "字段类型编码列表不能为空")
    private List<String> fieldTypeCodes;

}
