package com.cmsr.onebase.module.bpm.core.dto.edge.condition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * 流程条件项
 *
 * @author liyang
 * @date 2025-11-25
 *
 */
@Data
public class BpmConditionItem {
    /**
     * 业务字段类型
     */
    @NotBlank(message = "业务字段类型不能为空")
    private String fieldScope;

    @NotBlank(message = "业务字段ID不能为空")
    private String fieldId;

    @NotBlank(message = "业务字段操作符不能为空")
    private String op;

    @NotBlank(message = "操作类型不能为空")
    private String operatorType;

    @NotBlank(message = "业务字段类型不能为空")
    private String fieldType;

    /**
     * 如果是operatorType是值，value可能是字符串，也可能是数组，也可能是Map。
     * 如果是operatorType是变量，value是变字符串
     * 如果是operatorType是公式，value则为对象
     *
     */
    @NotNull(message = "业务字段值不能为空")
    private Object value;
}
