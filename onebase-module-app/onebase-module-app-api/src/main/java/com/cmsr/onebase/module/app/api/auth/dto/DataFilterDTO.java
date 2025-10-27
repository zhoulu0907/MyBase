package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 17:47
 */
@Data
public class DataFilterDTO {

    private Long id;
    /**
     * 数据权限组Id
     */
    private Long fieldId;

    /**
     * 字段值类型
     */
    private String fieldValueType;

    /**
     * 比较操作符号
     */
    private String fieldOperator;

    /**
     * 字段值
     */
    private String fieldValue;
}
