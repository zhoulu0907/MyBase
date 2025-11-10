package com.cmsr.onebase.module.flow.context.condition;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/16 21:08
 */
@Data
public class ConditionItem implements java.io.Serializable {

    @JsonAlias(value = {"field", "fieldId"})
    private String fieldId;

    private String op;

    private String operatorType;

    private String jdbcType;

    private String fieldType;

    /**
     * 如果是operatorType是值，value可能是字符串，也可能是数组，也可能是Map。
     * 如果是operatorType是变量，value是变字符串
     */
    private Object value;

}
