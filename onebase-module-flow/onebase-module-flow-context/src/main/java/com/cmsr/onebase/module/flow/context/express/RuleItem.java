package com.cmsr.onebase.module.flow.context.express;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/16 21:08
 */
@Data
public class RuleItem {

    private Long fieldId;

    private String op;

    private String operatorType;

    private List<String> value;

    //查询到的字段名称
    private String fieldName;

    //查询到的字段jdbc类型
    private String fieldJdbcType;

    //根据字段类型，转换实际的值，比如Integer、Date等
    private Object fieldValue;

}
