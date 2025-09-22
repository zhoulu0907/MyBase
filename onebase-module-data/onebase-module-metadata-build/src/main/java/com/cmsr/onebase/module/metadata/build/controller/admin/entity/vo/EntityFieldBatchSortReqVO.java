package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 批量排序实体字段 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 批量排序实体字段 Request VO")
@Data
public class EntityFieldBatchSortReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "字段排序列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字段排序列表不能为空")
    @Valid
    private List<EntityFieldSortItemVO> fieldSorts;

}
