package com.cmsr.onebase.module.flow.graph.data;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:05
 */
@Data
public class StartFormNodeData {

    private String triggerScope;

    private String triggerUserType;

    private String triggerUserValue;

    private Long pageId;

    private Long fieldId;

    private String[] triggerEvents;

    private String[][] filterCondition;

    private Integer isChildTriggerAllowed;
}
