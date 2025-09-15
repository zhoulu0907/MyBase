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

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    @NotNull(message = "字段ID不能为空")
    private String fieldId;

    @Schema(description = "排序顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "排序顺序不能为空")
    private Integer sortOrder;

}
