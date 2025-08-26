package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 动态数据更新请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 动态数据更新请求 VO")
@Data
public class DynamicDataUpdateReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "数据主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据主键ID不能为空")
    private Object id;

    @Schema(description = "数据内容，key为字段名，value为字段值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据内容不能为空")
    private Map<String, Object> data;

    @Schema(description = "方法编码(可选). 若提供, 将用于匹配执行计划", example = "metadata.user.update")
    private String methodCode;

}
