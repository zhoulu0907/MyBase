package com.cmsr.onebase.framework.security.runtime;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/20 16:54
 */
@Data
public class DataPermissionFilter {

    private Long fieldId;

    //TODO 定义枚举值
    private String fieldOperator;


    //TODO 定义枚举值
    private String fieldValueType;


    //TODO 定义枚举值
    private String fieldValue;

}
