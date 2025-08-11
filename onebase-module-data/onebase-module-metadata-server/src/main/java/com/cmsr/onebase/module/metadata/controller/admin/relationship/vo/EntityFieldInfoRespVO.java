package com.cmsr.onebase.module.metadata.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 实体字段信息 Response VO
 *
 * @author matianyu
 * @date 2025-08-09
 */
@Schema(description = "管理后台 - 实体字段信息 Response VO")
@Data
public class EntityFieldInfoRespVO {

    @Schema(description = "字段ID", example = "1001")
    private String fieldID;

    @Schema(description = "字段名称", example = "用户名")
    private String fieldName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;
}
