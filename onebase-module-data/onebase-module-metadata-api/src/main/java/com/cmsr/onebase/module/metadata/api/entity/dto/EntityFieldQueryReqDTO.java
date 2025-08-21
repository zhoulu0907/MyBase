package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 实体字段查询请求DTO
 *
 * @author matianyu
 * @date 2025-08-14
 */
@Schema(description = "RPC 服务 - 实体字段查询请求 DTO")
@Data
public class EntityFieldQueryReqDTO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "是否系统字段：1-是，0-不是", example = "1")
    private Integer isSystemField;

    @Schema(description = "搜索关键词", example = "name")
    private String keyword;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

}
