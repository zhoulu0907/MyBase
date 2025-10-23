package com.cmsr.onebase.module.formula.api.formula.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class FormulaExecuteReqDTO {

    /**
     * 公式表达式
     */
    @NotBlank(message = "公式表达式不能为空")
    @Size(max = 1024, message = "公式长度不能超过1024个字符")
    private String formula;

    /**
     * 公式参数
     */
    private Map<String, Object> parameters;

    /**
     * 上下文参数
     */
    private Map<String, Object> contextData;

}
