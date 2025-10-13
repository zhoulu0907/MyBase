package com.cmsr.onebase.module.formula.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程定义表 实体类
 */
@Data
public class FlowDefinitionVO {

    /** 主键id */
    private Long id;

    /** 流程编码 */
    private String flowCode;

    /** 流程名称 */
    private String flowName;

    /** 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型） */
    private String modelValue = "CLASSICS";

    /** 流程类别 */
    private String category;

    /** 流程版本 */
    private String version;

    /** 是否发布（0未发布 1已发布 9失效） */
    private Integer isPublish = 0;

    /** 审批表单是否自定义（Y是 N否） */
    private String formCustom = "N";

    /** 审批表单路径 */
    private String formPath;

    /** 流程激活状态（0挂起 1激活） */
    private Integer activityStatus = 1;

    /** 监听器类型 */
    private String listenerType;

    /** 监听器路径 */
    private String listenerPath;

    /** 扩展字段，预留给业务系统使用 */
    private String ext;

}