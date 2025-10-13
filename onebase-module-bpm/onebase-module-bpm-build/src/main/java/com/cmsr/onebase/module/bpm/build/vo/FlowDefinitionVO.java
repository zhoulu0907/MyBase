package com.cmsr.onebase.module.bpm.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程定义表 实体类
 */
@Schema(description = "流程定义表")
@Data
public class FlowDefinitionVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程编码")
    private String flowCode;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）")
    private String modelValue = "CLASSICS";

    @Schema(description = "流程类别")
    private String category;

    @Schema(description = "流程版本")
    private String version;

    @Schema(description = "是否发布（0未发布 1已发布 9失效）")
    private Integer isPublish = 0;

    @Schema(description = "审批表单是否自定义（Y是 N否）")
    private String formCustom = "N";

    @Schema(description = "审批表单路径")
    private String formPath;

    @Schema(description = "流程激活状态（0挂起 1激活）")
    private Integer activityStatus = 1;

    @Schema(description = "监听器类型")
    private String listenerType;

    @Schema(description = "监听器路径")
    private String listenerPath;

    @Schema(description = "扩展字段，预留给业务系统使用")
    private String ext;

}
