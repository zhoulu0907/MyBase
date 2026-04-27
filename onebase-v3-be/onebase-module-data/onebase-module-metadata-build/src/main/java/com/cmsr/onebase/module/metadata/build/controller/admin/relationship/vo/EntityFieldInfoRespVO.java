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

    @Schema(description = "字段ID（兼容旧接口）", example = "165890174290853889")
    private String fieldId;

    @Schema(description = "字段UUID", example = "019ae8db-5443-7175-b816-6c3a5b1441f2")
    private String fieldUuid;

    @Schema(description = "字段名称", example = "用户名")
    private String fieldName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "是否系统字段：0-是，1-不是", example = "1")
    private Integer isSystemField;

    @Schema(description = "显示名称", example = "用户名")
    private String displayName;
}
