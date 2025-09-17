package com.cmsr.onebase.module.flow.core.rule;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/16 22:10
 */
@Data
public class Condition {

    /**
     * 条件项，逻辑关系是OR关系
     */
    private List<ConditionItem> conditions;

    /**
     * 缓存的表达式
     */
    private Serializable compiledExpression;
}
