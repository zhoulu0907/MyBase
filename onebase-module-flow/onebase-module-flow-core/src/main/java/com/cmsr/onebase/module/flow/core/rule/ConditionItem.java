package com.cmsr.onebase.module.flow.core.rule;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/16 21:15
 */
@Data
public class ConditionItem {

    /**
     * 规则项，一个条件项下可以有多个规则项，逻辑关系是AND关系
     */
    private List<RuleItem> rules;

}
