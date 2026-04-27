package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 实体字段查询请求 DTO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "实体字段查询请求 DTO")
@Data
public class EntityFieldQueryReqDTO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "是否系统字段", example = "0")
    private Integer isSystemField;

    @Schema(description = "关键词", example = "用户名")
    private String keyword;

    @Schema(description = "字段编码", example = "USER_CODE")
    private String fieldCode;

    @Schema(description = "是否人员字段", example = "1")
    private Integer isPerson;
}
