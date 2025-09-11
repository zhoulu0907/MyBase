package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * 数据方法详情查询请求 VO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "管理后台 - 数据方法详情查询请求 VO")
@Data
public class DataMethodDetailQueryReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "方法代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "getUserList")
    @NotBlank(message = "方法代码不能为空")
    private String methodCode;
}
