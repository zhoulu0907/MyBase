package com.cmsr.onebase.module.formula.vo.formula;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 公式执行请求VO
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Data
public class FormulaExecuteReqVO {

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
}
