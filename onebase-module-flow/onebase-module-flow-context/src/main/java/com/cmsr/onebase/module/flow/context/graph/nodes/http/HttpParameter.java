package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;

/**
 * HTTP参数定义
 *
 * <p>用于定义HTTP请求的参数,支持Path、Query、Header、Body四种位置
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class HttpParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名
     */
    private String name;

    /**
     * 参数类型
     * STRING/NUMBER/BOOLEAN/OBJECT/ARRAY
     */
    private String type;

    /**
     * 参数位置
     * PATH/QUERY/HEADER/BODY
     */
    private String location;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 示例值
     */
    private String example;
}
