package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 级联类型 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 级联类型 Response VO")
@Data
public class CascadeTypeRespVO {

    @Schema(description = "级联类型编码", example = "read")
    private String cascadeType;

    @Schema(description = "显示名称", example = "只读关联")
    private String displayName;

    @Schema(description = "描述信息", example = "仅支持读取关联数据，不进行任何级联操作")
    private String description;

}
