package com.cmsr.onebase.module.formula.demo;

import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineServiceImpl;

/**
 * 组合函数统一验证入口
 * 集中执行文本、数学、日期、逻辑、财务等多层嵌套函数示例
 * <p>
 * 使用方法：直接运行 main；或单独调用对应静态方法
 *
 * @author matianyu
 * @date 2025-09-25
 */
public class CombinedFormulaFunctionsTest {

    private static FormulaEngineServiceImpl engine;

    /**
     * 初始化公式引擎（单例复用）
     */
    private static void initEngineIfNecessary() {
        if (engine != null) {
            return;
        }
        FormulaEngineProperties properties = new FormulaEngineProperties();
        properties.setEnabled(true);
        properties.setTimeoutMs(5000L);
        properties.setSecurityMode(true);
        properties.setMaxFormulaLength(1024);
        engine = new FormulaEngineServiceImpl(properties);
    }

    /**
     * 文本函数组合：UPPER + LEFT
     * 公式：UPPER(LEFT('Hello World',5)) => HELLO
     */
    public static void testTextFunctions() {
        initEngineIfNecessary();
        String formula = "UPPER(LEFT('Hello World',5))";
        Object result = engine.executeFormula(formula);
        System.out.println("[文本] " + formula + " = " + result + " (期望: HELLO)");
    }

    /**
     * 数学函数组合：POWER + SQRT + ROUND
     * 公式：ROUND(SQRT(POWER(3,2)+POWER(4,2)),0) => 5
     */
    public static void testMathFunctions() {
        initEngineIfNecessary();
        String formula = "ROUND(SQRT(POWER(3,2)+POWER(4,2)),0)";
        Object result = engine.executeFormula(formula);
        System.out.println("[数学] " + formula + " = " + result + " (期望: 5)");
    }

    /**
     * 日期函数组合：DATE + YEAR + MONTH + TODAY
     * 公式：DATE(YEAR(TODAY()), MONTH(TODAY()), 1)
     * 输出当月第一天
     */
    public static void testDateFunctions() {
        initEngineIfNecessary();
        String formula = "DATE(YEAR(TODAY()), MONTH(TODAY()), 1)";
        Object result = engine.executeFormula(formula);
        System.out.println("[日期] " + formula + " = " + result);
    }

    /**
     * 逻辑函数组合：IF + AND + OR + GT + EQ
     * 公式：IF(AND(GT(5,3), OR(GT(2,3), EQ(4,4))), 'OK', 'NO') => OK
     */
    public static void testLogicalFunctions() {
        initEngineIfNecessary();
        String formula = "IF(AND(GT(5,3), OR(GT(2,3), EQ(4,4))), 'OK', 'NO')";
        Object result = engine.executeFormula(formula);
        System.out.println("[逻辑] " + formula + " = " + result + " (期望: OK)");
    }

    /**
     * 财务函数组合：PMT + ABS + ROUND
     * 公式：ROUND(ABS(PMT(0.05/12, 24, 10000)), 2) ≈ 438.71
     */
    public static void testFinancialFunctions() {
        initEngineIfNecessary();
        String formula = "ROUND(ABS(PMT(0.05/12, 24, 10000)), 2)";
        Object result = engine.executeFormula(formula);
        System.out.println("[财务] " + formula + " = " + result + " (约期望: 438.71)");
    }

    /**
     * 用户函数
     */
    public static void testUserFunctions() {
        initEngineIfNecessary();
        String formula = "GETUSER()";
        Object result = engine.executeFormula(formula);
        System.out.println("[用户] " + formula + " = " + result);

        String formula1 = "GET_NAME(GETUSER())";
        Object result1 = engine.executeFormula(formula1);
        System.out.println("[用户姓名] " + formula1 + " = " + result1);
    }

    /**
     * 执行全部组合验证
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        System.out.println("=== 组合函数统一测试开始 ===");
        testTextFunctions();
        testMathFunctions();
        testDateFunctions();
        testLogicalFunctions();
        testFinancialFunctions();
        testUserFunctions();
        System.out.println("=== 组合函数统一测试结束 ===");
    }
}

