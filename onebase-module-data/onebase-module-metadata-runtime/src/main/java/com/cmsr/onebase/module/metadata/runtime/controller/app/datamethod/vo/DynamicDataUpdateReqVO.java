package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态数据更新请求VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "动态数据更新请求VO")
public class DynamicDataUpdateReqVO {

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(description = "数据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据ID不能为空")
    private Object id;

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "数据内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "数据内容不能为空")
    private Map<Long, Object> data;

    @Schema(description = "子表数据对象")
    private List<SubEntityVo> subEntities = new ArrayList<>();

    @Schema(description = "方法编码（可选）")
    private String methodCode;
}
