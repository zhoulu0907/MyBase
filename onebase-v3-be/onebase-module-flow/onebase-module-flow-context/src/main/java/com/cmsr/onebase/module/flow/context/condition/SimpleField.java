package com.cmsr.onebase.module.flow.context.condition;

import lombok.Data;

/**
 * 界面输入等简单的内容
 *
 * @Author：huangjie
 * @Date：2025/12/15 14:45
 */
@Data
public class SimpleField {

    private String id;

    private String fieldName;

    private String fieldType;

    private Object value;

}
