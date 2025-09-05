package com.cmsr.onebase.module.flow.controller.admin.exec.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:34
 */
public class QueryFormTriggerRespVO {

    /**
     * 流程ID
     */
    @Schema(description = "流程ID")
    private Long processId;

    /**
     * 触发范围：record/field
     */
    @Schema(description = "触发范围：record/field")
    private String triggerScope;


    /**
     * 触发事件
     */
    @Schema(description = "触发事件")
    private String triggerEvent;


    /**
     * 字段Id
     */
    @Schema(description = "字段Id")
    private Long fieldId;

    /**
     * 数据范围，全部数据、变更数据
     */
    @Schema(description = "数据范围，全部数据、变更数据")
    private String dataScope;

    /**
     * 是否允许关联子表触发 0/1布尔值
     */
    @Schema(description = "是否允许关联子表触发 0/1布尔值")
    private Integer isChildTriggerAllowed;
}
