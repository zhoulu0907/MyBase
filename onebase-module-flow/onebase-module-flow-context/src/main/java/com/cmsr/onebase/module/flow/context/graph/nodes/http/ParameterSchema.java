package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 参数Schema定义
 *
 * <p>用于描述输入输出参数的结构，供前端生成表单
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class ParameterSchema implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     * STRING/NUMBER/BOOLEAN/OBJECT/ARRAY
     */
    private String fieldType;

    /**
     * 字段描述
     */
    private String fieldDesc;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 示例值
     */
    private String example;

    /**
     * 嵌套字段（用于OBJECT和ARRAY类型）
     */
    private List<ParameterSchema> nestedFields;
}
