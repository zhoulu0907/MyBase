package com.cmsr.onebase.module.flow.context.condition;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/16 21:08
 */
@Data
public class RuleItem {

    private String fieldId;

    private String op;

    private String operatorType;

    private List<String> value;

}
