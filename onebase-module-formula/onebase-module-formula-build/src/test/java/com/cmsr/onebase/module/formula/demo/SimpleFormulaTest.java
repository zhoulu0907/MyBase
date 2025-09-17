package com.cmsr.onebase.module.formula.demo;

import com.cmsr.onebase.module.formula.build.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.build.service.engine.FormulaEngineServiceImpl;

/**
 * 简单的FormulaJS测试程序
 * 用于验证FormulaJS v4.5.3集成是否正确工作
 *
 * @author matianyu
 * @date 2025-09-02
 */
public class SimpleFormulaTest {

    public static void main(String[] args) {
        System.out.println("=== FormulaJS v4.5.3 集成测试 ===");

        try {
            // 初始化配置
            FormulaEngineProperties properties = new FormulaEngineProperties();
            properties.setEnabled(true);
            properties.setTimeoutMs(5000L);
            properties.setSecurityMode(true);
            properties.setMaxFormulaLength(1024);

            System.out.println("正在初始化公式引擎...");

            // 初始化公式引擎服务
            FormulaEngineServiceImpl formulaEngineService = new FormulaEngineServiceImpl(properties);

            System.out.println("公式引擎初始化成功！");

            // 测试LEFT函数
            System.out.println("\n测试LEFT函数:");
            String formula1 = "LEFT('Hello World', 5)";
            Object result1 = formulaEngineService.executeFormula(formula1);
            System.out.println("公式: " + formula1 + " = " + result1);

            // 测试SUM函数
            System.out.println("\n测试SUM函数:");
            String formula2 = "SUM(1, 2, 3, 4, 5)";
            Object result2 = formulaEngineService.executeFormula(formula2);
            System.out.println("公式: " + formula2 + " = " + result2);

            // 测试其他函数
            System.out.println("\n测试其他函数:");
            String formula3 = "UPPER('hello')";
            Object result3 = formulaEngineService.executeFormula(formula3);
            System.out.println("公式: " + formula3 + " = " + result3);

            String formula4 = "ROUND(3.14159, 2)";
            Object result4 = formulaEngineService.executeFormula(formula4);
            System.out.println("公式: " + formula4 + " = " + result4);

            System.out.println("\n=== 测试完成，FormulaJS集成成功！ ===");

        } catch (Exception e) {
            System.err.println("FormulaJS测试失败:");
            e.printStackTrace();
        }
    }
}
