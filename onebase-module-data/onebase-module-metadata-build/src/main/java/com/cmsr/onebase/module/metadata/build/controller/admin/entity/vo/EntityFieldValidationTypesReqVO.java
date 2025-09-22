package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量查询字段可选校验类型 请求 VO
 *
 * @author matianyu
 * @date 2025-09-02
 */
@Data
public class EntityFieldValidationTypesReqVO {

    @Schema(description = "字段ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "fieldIdList 不能为空")
    private List<String> fieldIdList;
}
