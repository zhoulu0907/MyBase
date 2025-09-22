package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 数据方法查询请求 VO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "管理后台 - 数据方法查询请求 VO")
@Data
public class DataMethodQueryReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "方法类型", example = "query")
    private String methodType;

    @Schema(description = "关键词", example = "用户")
    private String keyword;
}
