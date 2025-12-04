package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

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

    @Schema(description = "字段UUID", example = "uuid-1001")
    private String fieldUuid;

    @Schema(description = "字段名称", example = "用户名")
    private String fieldName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "是否系统字段：0-是，1-不是", example = "1")
    private Integer isSystemField;


    @Schema(description = "显示名称", example = "用户名")
    private String displayName;

    /**
     * 设置字段ID（兼容旧代码）
     * @deprecated 请使用 setFieldUuid()
     */
    @Deprecated
    public void setFieldId(String fieldId) {
        this.fieldUuid = fieldId;
    }
}
