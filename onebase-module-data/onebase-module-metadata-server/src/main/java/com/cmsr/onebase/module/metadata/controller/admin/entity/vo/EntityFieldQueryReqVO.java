package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 实体字段查询请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 实体字段查询请求 Request VO")
@Data
public class EntityFieldQueryReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "是否系统字段：0-不是，1-是", example = "1")
    private Integer isSystemField;

    @Schema(description = "是否是人员字段：0-不是，1-是；为1时仅返回 field_type=USER 的字段及 creator/updater 系统字段", example = "1")
    private Integer isPerson;

    @Schema(description = "搜索关键词", example = "name")
    private String keyword;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

}
