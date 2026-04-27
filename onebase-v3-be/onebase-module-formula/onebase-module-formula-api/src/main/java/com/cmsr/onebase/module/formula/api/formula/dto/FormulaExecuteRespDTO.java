package com.cmsr.onebase.module.formula.api.formula.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class FormulaExecuteRespDTO {
    /**
     * 执行结果
     */
    private Object result;

    /**
     * 结果类型
     */
    private String resultType;

    /**
     * 执行时间（毫秒）
     */
    @JsonIgnore
    private Long executionTime;

}
