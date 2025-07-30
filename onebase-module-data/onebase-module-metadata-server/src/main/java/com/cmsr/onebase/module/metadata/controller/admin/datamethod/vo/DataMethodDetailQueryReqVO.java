package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 数据方法详情查询请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法详情查询请求 Request VO")
@Data
public class DataMethodDetailQueryReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "方法编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "create_single")
    @NotBlank(message = "方法编码不能为空")
    private String methodCode;

} 