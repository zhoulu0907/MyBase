package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 校验类型选项 Response VO
 *
 * @author matianyu
 * @date 2025-12-06
 */
@Data
public class ValidationTypeItemRespVO {

    @Schema(description = "校验类型编码")
    private String code;

    @Schema(description = "校验类型名称")
    private String name;

    @Schema(description = "校验类型描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;
}
