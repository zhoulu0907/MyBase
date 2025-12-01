package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 批量保存（增删改）实体字段 Request VO
 */
@Schema(description = "管理后台 - 批量保存（增删改）实体字段 Request VO")
@Data
public class EntityFieldBatchSaveReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12345")
    @NotNull(message = "应用ID不能为空")
    private String applicationId;

    @Schema(description = "字段变更项列表（可同时包含新增、更新、删除）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字段变更项列表不能为空")
    @Valid
    private List<EntityFieldUpsertItemVO> items;
}
