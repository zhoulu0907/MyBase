package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 字段排序项 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段排序项 VO")
@Data
public class EntityFieldSortItemVO {

    @Schema(description = "字段UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-3001")
    @NotNull(message = "字段UUID不能为空")
    private String fieldUuid;

    @Schema(description = "排序顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "排序顺序不能为空")
    private Integer sortOrder;

}
