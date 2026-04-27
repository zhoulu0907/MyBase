package com.cmsr.onebase.module.formula.demo;

import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Excel公式计算功能演示类
 * 专门验证LEFT函数和SUM函数的基本使用
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Slf4j
public class FormulaEngineDemo {

    private final FormulaEngineService formulaEngineService;

    public FormulaEngineDemo() {
        // 初始化配置
        FormulaEngineProperties properties = new FormulaEngineProperties();
        properties.setEnabled(true);
        properties.setTimeoutMs(5000L);
        properties.setSecurityMode(true);
        properties.setMaxFormulaLength(1024);

        // 初始化公式引擎服务
        this.formulaEngineService = new FormulaEngineServiceImpl(properties);

        log.info("=== Excel公式计算引擎演示程序 ===");
        log.info("基于Formula.js + GraalVM实现");
        log.info("支持安全的Excel函数计算");
    }

    /**
     * 演示LEFT函数的使用
     */
    public void demonstrateLeftFunction() {
        log.info("\n=== LEFT函数演示 ===");

        try {
            // 基本LEFT函数测试 - 使用新的FormulaJS v4.5.3 API
            String formula1 = "LEFT('Hello World', 5)";
            Object result1 = formulaEngineService.executeFormula(formula1);
            log.info("公式: {} = {}", formula1, result1);

            // 中文字符测试
            String formula2 = "LEFT('你好世界测试', 4)";
            Object result2 = formulaEngineService.executeFormula(formula2);
            log.info("公式: {} = {}", formula2, result2);

            // 边界情况测试
            String formula3 = "LEFT('Test', 0)";
            Object result3 = formulaEngineService.executeFormula(formula3);
            log.info("公式: {} = '{}'", formula3, result3);

            // 超长提取测试
            String formula4 = "LEFT('Short', 100)";
            Object result4 = formulaEngineService.executeFormula(formula4);
            log.info("公式: {} = {}", formula4, result4);

            // 使用参数的LEFT函数
            Map<String, Object> params = new HashMap<>();
            params.put("text", "Excel公式计算引擎");
            params.put("length", 5);
            String formula5 = "LEFT(text, length)";
            Object result5 = formulaEngineService.executeFormulaWithParams(formula5, params);
            log.info("公式: {} (text='{}', length={}) = {}",
                    formula5, params.get("text"), params.get("length"), result5);

        } catch (Exception e) {
            log.error("LEFT函数演示失败", e);
        }
    }

    /**
     * 演示SUM函数的使用
     */
    public void demonstrateSumFunction() {
        log.info("\n=== SUM函数演示 ===");

        try {
            // 基本SUM函数测试 - 使用新的FormulaJS v4.5.3 API
            String formula1 = "SUM(1, 2, 3, 4, 5)";
            Object result1 = formulaEngineService.executeFormula(formula1);
            log.info("公式: {} = {}", formula1, result1);

            // 小数计算测试
            String formula2 = "SUM(10.5, 20.3, 15.7)";
            Object result2 = formulaEngineService.executeFormula(formula2);
            log.info("公式: {} = {}", formula2, result2);

            // 负数计算测试
            String formula3 = "SUM(-10, 5, 15, -3)";
            Object result3 = formulaEngineService.executeFormula(formula3);
            log.info("公式: {} = {}", formula3, result3);

            // 单个数字测试
            String formula4 = "SUM(42)";
            Object result4 = formulaEngineService.executeFormula(formula4);
            log.info("公式: {} = {}", formula4, result4);

            // 零值测试
            String formula5 = "SUM(0, 0, 0, 0)";
            Object result5 = formulaEngineService.executeFormula(formula5);
            log.info("公式: {} = {}", formula5, result5);

            // 使用参数的SUM函数
            Map<String, Object> params = new HashMap<>();
            params.put("amount1", 100.50);
            params.put("amount2", 200.75);
            params.put("amount3", 50.25);
            String formula6 = "SUM(amount1, amount2, amount3)";
            Object result6 = formulaEngineService.executeFormulaWithParams(formula6, params);
            log.info("公式: {} (amount1={}, amount2={}, amount3={}) = {}",
                    formula6, params.get("amount1"), params.get("amount2"),
                    params.get("amount3"), result6);

        } catch (Exception e) {
            log.error("SUM函数演示失败", e);
        }
    }

    /**
     * 演示组合函数的使用
     */
    public void demonstrateCombinedFunctions() {
        log.info("\n=== 组合函数演示 ===");

        try {
            // LEFT + LEN组合
            String formula1 = "LEFT('Hello World', LEN('Hello'))";
            Object result1 = formulaEngineService.executeFormula(formula1);
            log.info("公式: {} = {}", formula1, result1);

            // SUM + MAX组合
            String formula2 = "SUM(MAX(1, 5, 3), MAX(2, 8, 4))";
            Object result2 = formulaEngineService.executeFormula(formula2);
            log.info("公式: {} = {}", formula2, result2);

            // 复杂业务场景：提取用户ID前缀并计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("userId1", "USER001");
            params.put("userId2", "USER002");
            params.put("count1", 10);
            params.put("count2", 20);

            String formula3 = "LEFT(userId1, 4)";
            Object result3 = formulaEngineService.executeFormulaWithParams(formula3, params);
            log.info("提取前缀: {} = {}", formula3, result3);

            String formula4 = "SUM(count1, count2)";
            Object result4 = formulaEngineService.executeFormulaWithParams(formula4, params);
            log.info("计算总数: {} = {}", formula4, result4);

        } catch (Exception e) {
            log.error("组合函数演示失败", e);
        }
    }

    /**
     * 演示公式验证功能
     */
    public void demonstrateValidation() {
        log.info("\n=== 公式验证演示 ===");

        // 验证有效公式
        String validFormula1 = "LEFT('test', 2)";
        boolean isValid1 = formulaEngineService.validateFormula(validFormula1);
        log.info("公式: {} - 验证结果: {}", validFormula1, isValid1 ? "有效" : "无效");

        String validFormula2 = "SUM(1, 2, 3)";
        boolean isValid2 = formulaEngineService.validateFormula(validFormula2);
        log.info("公式: {} - 验证结果: {}", validFormula2, isValid2 ? "有效" : "无效");

        // 验证无效公式
        String invalidFormula1 = "invalid_function()";
        boolean isValid3 = formulaEngineService.validateFormula(invalidFormula1);
        log.info("公式: {} - 验证结果: {}", invalidFormula1, isValid3 ? "有效" : "无效");

        // 验证危险公式（安全性检查）
        String dangerousFormula = "eval('alert(1)')";
        boolean isValid4 = formulaEngineService.validateFormula(dangerousFormula);
        log.info("危险公式: {} - 验证结果: {}", dangerousFormula, isValid4 ? "有效" : "无效");
    }

    /**
     * 演示性能测试
     */
    public void demonstratePerformance() {
        log.info("\n=== 性能测试演示 ===");

        try {
            String formula = "SUM(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)";
            int iterations = 100;

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < iterations; i++) {
                formulaEngineService.executeFormula(formula);
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            double avgTime = (double) totalTime / iterations;

            log.info("性能测试结果:");
            log.info("- 公式: {}", formula);
            log.info("- 执行次数: {}", iterations);
            log.info("- 总耗时: {}ms", totalTime);
            log.info("- 平均耗时: {}ms", String.format("%.2f", avgTime));

        } catch (Exception e) {
            log.error("性能测试失败", e);
        }
    }

    /**
     * 演示支持的函数列表
     */
    public void demonstrateSupportedFunctions() {
        log.info("\n=== 支持的函数列表 ===");

        String[] functions = formulaEngineService.getSupportedFunctions();
        log.info("当前支持的Excel函数共{}个:", functions.length);

        for (int i = 0; i < functions.length; i++) {
            log.info("{}. {}", i + 1, functions[i]);
        }
    }


    /**
     * 演示LEFT函数的���用
     */
    public void testLeftFunctionPerformace() {
        log.info("\n=== LEFT函数性能探测 ===");

        // 基本LEFT函数测试
        String formula1 = "LEFT('Hello World', 5)";
        // 中文字符测试
        String formula2 = "LEFT('你好世界测试', 4)";
        // 边界情况测试
        String formula3 = "LEFT('Test', 0)";
        // 超长提取测试
        String formula4 = "LEFT('Short', 100)";

        long startTime = System.currentTimeMillis();
        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            Object result1 = formulaEngineService.executeFormula(formula1);
            // log.info("公式: {} = {}", formula1, result1);

            Object result2 = formulaEngineService.executeFormula(formula2);
            // log.info("公式: {} = {}", formula2, result2);

            Object result3 = formulaEngineService.executeFormula(formula3);
            // log.info("公式: {} = '{}'", formula3, result3);

            Object result4 = formulaEngineService.executeFormula(formula4);
            // log.info("公式: {} = {}", formula4, result4);
        }
        long endTime = System.currentTimeMillis();
        log.info("\n=== LEFT函数性能(4组Text Left 函数执行 {} 次)，耗时：{} MS", iterations, endTime - startTime);
        log.info("\n=== 单个函数执行耗时：{} MS", (endTime - startTime) / iterations / 4);

    }

    /**
     * 演示SUM函数的使用
     */
    public void testSumFunctionPerformace() {
        log.info("\n=== SUM函数性能探测 ===");

        // 基本SUM函数测试
        String formula1 = "SUM(1, 2, 3, 4, 5)";

        // 小数计算测试
        String formula2 = "SUM(10.5, 20.3, 15.7)";

        // 负数计算测试
        String formula3 = "SUM(-10, 5, 15, -3)";

        // 单个数字测试
        String formula4 = "SUM(42)";


        long startTime = System.currentTimeMillis();
        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            Object result1 = formulaEngineService.executeFormula(formula1);
            // log.info("公式: {} = {}", formula1, result1);
            Object result2 = formulaEngineService.executeFormula(formula2);
            // log.info("公式: {} = {}", formula2, result2);
            Object result3 = formulaEngineService.executeFormula(formula3);
            // log.info("公式: {} = {}", formula3, result3);
            Object result4 = formulaEngineService.executeFormula(formula4);
            // log.info("公式: {} = {}", formula4, result4);

        }
        long endTime = System.currentTimeMillis();
        log.info("\n=== SUM函数性能(4组 SUM 函数执行 {} 次)，耗时：{} MS", iterations, endTime - startTime);
        log.info("\n=== 单个函数执行耗时：{} MS", (endTime - startTime) / iterations / 4);

    }


    /**
     * 主函数：运行所有演示
     */
    public static void main(String[] args) {
        FormulaEngineDemo demo = new FormulaEngineDemo();

        try {
            // 运行executeFormula测试用例

            // 运行原有演示（注释掉以专注于新测试）
            demo.demonstrateLeftFunction();
            demo.demonstrateSumFunction();
            demo.demonstrateCombinedFunctions();
            demo.demonstrateValidation();
            demo.demonstratePerformance();
            demo.testLeftFunctionPerformace();
            demo.testSumFunctionPerformace();

            log.info("\n=== executeFormulaWithParams测试完成 ===");
            log.info("所有测试用例执行完成！");

        } catch (Exception e) {
            log.error("测试程序执行失败", e);
        }
    }
}
