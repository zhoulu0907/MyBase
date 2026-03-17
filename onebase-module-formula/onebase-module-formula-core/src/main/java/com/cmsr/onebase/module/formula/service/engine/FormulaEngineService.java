package com.cmsr.onebase.module.formula.service.engine;

import java.util.Map;

/**
 * 公式引擎服务接口
 *
 * @author matianyu
 * @date 2025-09-01
 */
public interface FormulaEngineService {

    /**
     * 执行公式计算
     *
     * @param formula 公式表达式
     * @return 计算结果
     */
    Object executeFormula(String formula);

    /**
     * 执行公式计算（带参数）
     *
     * @param formula 公式表达式
     * @param parameters 参数映射
     * @return 计算结果
     */
    Object executeFormulaWithParams(String formula, Map<String, Object> parameters);

    Object executeFormulaWithParamsForFlow(String formula, Map<String, Object> parameters, Map<String, Object> contextData);

    /**
     * 验证公式语法
     *
     * @param formula 公式表达式
     * @return 是否有效
     */
    boolean validateFormula(String formula);

    /**
     * 获取支持的函数列表
     *
     * @return 函数名称列表
     */
    String[] getSupportedFunctions();

    Object executeJS(String script);
}
