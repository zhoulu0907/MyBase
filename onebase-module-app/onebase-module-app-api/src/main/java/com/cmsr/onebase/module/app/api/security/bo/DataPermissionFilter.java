package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/20 16:54
 */
@Data
public class DataPermissionFilter {

    private Long fieldId;

    private String fieldOperator;

    private String fieldValueType;

    private String fieldValue;

}
