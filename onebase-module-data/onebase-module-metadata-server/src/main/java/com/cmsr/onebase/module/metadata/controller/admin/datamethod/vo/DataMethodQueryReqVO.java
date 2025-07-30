package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 数据方法查询请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法查询请求 Request VO")
@Data
public class DataMethodQueryReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "方法类型", example = "CREATE")
    private String methodType;

    @Schema(description = "搜索关键词", example = "新增")
    private String keyword;

} 