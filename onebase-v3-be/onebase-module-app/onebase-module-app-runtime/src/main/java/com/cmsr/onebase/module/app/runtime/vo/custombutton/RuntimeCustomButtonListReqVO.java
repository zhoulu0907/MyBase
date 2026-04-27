package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "运行态-查询可用自定义按钮请求")
public class RuntimeCustomButtonListReqVO {

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID。查询该页面集下当前用户可见的自定义按钮", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "菜单ID。用于过滤当前用户在该菜单下有权限的按钮")
    private Long menuId;

    @Schema(description = "页面ID。用于后续按页面细分按钮时扩展")
    private Long pageId;

    @Deprecated
    @Schema(description = "已废弃：是否查询批量操作按钮。请改用 displayPosition", deprecated = true)
    private Boolean batch;

    @Schema(description = "显示位置：FORM 表单视图、ROW_ACTION 列表页行内操作、BATCH_ACTION 列表页批量操作；为空兼容 batch 参数")
    private String displayPosition;

    @Schema(description = "当前业务记录ID。表单视图或行内操作查询时建议传入，用于可用条件求值")
    private String recordId;

    @Schema(description = "当前记录字段上下文。key 支持字段编码或字段UUID，value 为当前记录值；用于可用条件求值")
    private Map<String, Object> conditionContext;
}
