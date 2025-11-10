package com.cmsr.onebase.module.etl.common.meta;

import lombok.Data;

@Data
public class MetaColumn {

    private String keyword;

    private String comment;

    /**
     * 展示字段
     */
    private String displayName;

    /**
     * 用户自定义描述
     */
    private String declaration;

    /**
     * 字段名
     */
    private String name;

    /**
     * 原始数据库中存储的类型（不一定准确，Anyline有替换）
     */
    private String originType;

    /**
     * `    * Flink类型
     */
    private String flinkType;

    /**
     * 列字段的排序
     */
    private Integer position;

    /**
     * 是否允许字段为空
     */
    private Boolean nullable;

    /**
     * 是否忽略字段的长度
     */
    private Integer ignoreLength;

    private Integer length;

    /**
     * 是否忽略字段的精度
     */
    private Integer ignorePrecision;

    private Integer precision;

    /**
     * 是否忽略字段的范围
     */
    private Integer ignoreScale;

    private Integer scale;

    /**
     * 是否为主键
     */
    private Boolean primary;

    /**
     * 是否为唯一键
     */
    private Boolean unique;

    /**
     * 默认值
     */
    private Object defaultValue;

}
