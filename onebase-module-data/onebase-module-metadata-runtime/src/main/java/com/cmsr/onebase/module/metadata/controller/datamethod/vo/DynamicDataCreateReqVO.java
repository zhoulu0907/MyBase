package com.cmsr.onebase.module.metadata.controller.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * 动态数据创建请求VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "动态数据创建请求VO")
public class DynamicDataCreateReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "数据内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "数据内容不能为空")
    private Map<String, Object> data;

    @Schema(description = "方法编码（可选）")
    private String methodCode;
}
