package com.cmsr.onebase.module.formula.controller.admin.formula.vo;

import lombok.Data;

/**
 * 公式执行响应VO
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Data
public class FormulaExecuteRespVO {

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
    private Long executionTime;

    /**
     * 是否使用了缓存
     */
    private Boolean fromCache = false;

    public static FormulaExecuteRespVO success(Object result, Long executionTime) {
        FormulaExecuteRespVO vo = new FormulaExecuteRespVO();
        vo.setResult(result);
        vo.setResultType(result != null ? result.getClass().getSimpleName() : "null");
        vo.setExecutionTime(executionTime);
        return vo;
    }
}
