package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 动态数据删除请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 动态数据删除请求 VO")
@Data
public class DynamicDataDeleteReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "数据主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据主键ID不能为空")
    private Object id;

    @Schema(description = "方法编码(可选). 若提供, 将用于匹配执行计划", example = "metadata.user.delete")
    private String methodCode;

}
