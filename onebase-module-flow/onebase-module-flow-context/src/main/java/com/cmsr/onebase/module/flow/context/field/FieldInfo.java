package com.cmsr.onebase.module.flow.context.field;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/26 17:41
 */
@Data
public class FieldInfo {

    private Long fieldId;

    private String fieldName;

    private String jdbcType;
}
