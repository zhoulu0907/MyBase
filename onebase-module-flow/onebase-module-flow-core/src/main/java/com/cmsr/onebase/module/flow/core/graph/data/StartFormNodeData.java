package com.cmsr.onebase.module.flow.core.graph.data;

import com.cmsr.onebase.module.flow.core.rule.ConditionItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:05
 */
@Data
public class StartFormNodeData {

    private String triggerUserType;

    private String triggerUserValue;

    private Long pageId;

    private Long fieldId;

    private List<String> triggerEvents;

    private List<ConditionItem> filterCondition;

    /**
     * 缓存的表达式
     */
    private Serializable compiledExpression;
}
