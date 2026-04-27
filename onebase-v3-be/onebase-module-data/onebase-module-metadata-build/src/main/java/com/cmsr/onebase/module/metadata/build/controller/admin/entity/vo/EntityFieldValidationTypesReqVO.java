package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "字段ID列表(当tableName为空时必传)")
    private List<String> fieldIdList;

    @Schema(description = "表名(可选,当传入表名时查询该表所有字段的校验类型)")
    private String tableName;
}
