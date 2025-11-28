package com.cmsr.onebase.module.bpm.core.dto.edge.condition;

import lombok.Data;

/**
 * 公式表达式
 *
 * @author liyang
 * @date 2025-11-25
 */
@Data
public class FormulaExpression {
    /**
     * 公式
     */
    private String formula;

    /**
     * 公式数据
     */
    private String formulaData;

    // private List<FormulaItem> formulaItems;
}
