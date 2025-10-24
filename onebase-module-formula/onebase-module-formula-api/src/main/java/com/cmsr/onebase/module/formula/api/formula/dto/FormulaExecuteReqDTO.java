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
     * 公式执行参数，寻找公式中的参数并替换为唯一标志ID（编辑态存的）
     */
    private Map<String, Object> parameters;

    /**
     * 公式执行数据，上游给的上下文数据（运行时给出）
     */
    private Map<String, Object> contextData;

}
