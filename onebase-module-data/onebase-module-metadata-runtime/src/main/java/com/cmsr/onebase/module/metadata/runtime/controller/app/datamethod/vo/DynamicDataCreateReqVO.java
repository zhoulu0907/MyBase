package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
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

    /**
     * 业务追踪ID： 代表一次业务操作以及其带来的连锁反应的唯一标识；
     */
    @Schema(description = "业务追踪ID")
    private String traceId;

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    // TODO：数据内容的格式需要指定一下，应该分为 3 个，主实体数据、子表数据、关联表数据
    @Schema(description = "数据内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "数据内容不能为空")
    private Map<Long, Object> data;

    @Schema(description = "子表数据对象")
    private List<SubEntityVo> subEntities;

    @Schema(description = "方法编码（可选）")
    private String methodCode;

}
