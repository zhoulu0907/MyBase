package com.cmsr.onebase.module.flow.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/4 17:34
 */
@Data
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
    private String triggerRange;


    /**
     * 触发事件
     */
    @Schema(description = "字段触发事件")
    private String fieldTriggerEvents;

    /**
     * 记录触发事件
     */
    @Schema(description = "记录触发事件")
    private List<String> recordTriggerEvents;

    /**
     * 页面Id
     */
    @Schema(description = "页面Id")
    private Long pageId;

    /**
     * 字段Id
     */
    @Schema(description = "字段Id")
    private String fieldId;


    /**
     * 是否允许关联子表触发
     */
    @Schema(description = "是否允许关联子表触发")
    private Boolean isChildTriggerAllowed;
}
