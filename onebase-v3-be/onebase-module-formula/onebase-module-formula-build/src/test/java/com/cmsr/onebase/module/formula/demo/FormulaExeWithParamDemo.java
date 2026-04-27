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
public class FormulaExeWithParamDemo {

    private final FormulaEngineService formulaEngineService;

    public FormulaExeWithParamDemo() {
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
     * 演示executeFormulaWithParams的基本参数传递功能
     */
    public void demonstrateBasicParameterFormulas() {
        log.info("\n=== executeFormulaWithParams基本参数测试 ===");

        try {
            // 字符串参数测试
            Map<String, Object> params1 = new HashMap<>();
            params1.put("text", "Hello World");
            params1.put("length", 5);
            String formula1 = "LEFT(text, length)";
            Object result1 = formulaEngineService.executeFormulaWithParams(formula1, params1);
            log.info("字符串参数: {} = {}", formula1, result1);

            // 数字参数测试
            Map<String, Object> params2 = new HashMap<>();
            params2.put("num1", 100);
            params2.put("num2", 200);
            params2.put("num3", 300);
            String formula2 = "SUM(num1, num2, num3)";
            Object result2 = formulaEngineService.executeFormulaWithParams(formula2, params2);
            log.info("数字参数: {} = {}", formula2, result2);

            // 混合参数测试
            Map<String, Object> params3 = new HashMap<>();
            params3.put("name", "张三");
            params3.put("score1", 85.5);
            params3.put("score2", 92.0);
            params3.put("score3", 78.5);
            String formula3 = "SUM(score1, score2, score3)";
            Object result3 = formulaEngineService.executeFormulaWithParams(formula3, params3);
            log.info("学生成绩计算: {} = {}", formula3, result3);

            String formula4 = "LEFT(name, 1)";
            Object result4 = formulaEngineService.executeFormulaWithParams(formula4, params3);
            log.info("姓氏提取: {} = {}", formula4, result4);

        } catch (Exception e) {
            log.error("基本参数测试失败", e);
        }
    }

    /**
     * 演示executeFormulaWithParams的多种数据类型支持
     */
    public void demonstrateDataTypeParameters() {
        log.info("\n=== executeFormulaWithParams数据类型测试 ===");

        try {
            Map<String, Object> params = new HashMap<>();

            // 整数类型
            params.put("intValue", 42);
            String formula1 = "SUM(intValue, 8)";
            Object result1 = formulaEngineService.executeFormulaWithParams(formula1, params);
            log.info("整数参数: {} = {}", formula1, result1);

            // 浮点数类型
            params.put("floatValue", 3.14159);
            String formula2 = "ROUND(floatValue, 2)";
            Object result2 = formulaEngineService.executeFormulaWithParams(formula2, params);
            log.info("浮点数参数: {} = {}", formula2, result2);

            // 负数类型
            params.put("negativeValue", -25);
            String formula3 = "ABS(negativeValue)";
            Object result3 = formulaEngineService.executeFormulaWithParams(formula3, params);
            log.info("负数参数: {} = {}", formula3, result3);

            // 零值类型
            params.put("zeroValue", 0);
            String formula4 = "IF(zeroValue == 0, '零', '非零')";
            Object result4 = formulaEngineService.executeFormulaWithParams(formula4, params);
            log.info("零值参数: {} = {}", formula4, result4);

            // 中文字符串
            params.put("chineseText", "中文测试内容");
            String formula5 = "LEN(chineseText)";
            Object result5 = formulaEngineService.executeFormulaWithParams(formula5, params);
            log.info("中文字符串: {} = {}", formula5, result5);

            // 空字符串
            params.put("emptyText", "");
            String formula6 = "IF(LEN(emptyText) == 0, '空字符串', '有内容')";
            Object result6 = formulaEngineService.executeFormulaWithParams(formula6, params);
            log.info("空字符串: {} = {}", formula6, result6);

        } catch (Exception e) {
            log.error("数据类型测试失败", e);
        }
    }

    /**
     * 演示executeFormulaWithParams的复杂业务场景
     */
    public void demonstrateBusinessScenarios() {
        log.info("\n=== executeFormulaWithParams业务场景测试 ===");

        try {
            // 场景1：员工薪资计算
            Map<String, Object> salaryParams = new HashMap<>();
            salaryParams.put("baseSalary", 8000);
            salaryParams.put("performanceBonus", 2000);
            salaryParams.put("allowance", 1500);
            salaryParams.put("deduction", 500);

            String salaryFormula = "SUM(baseSalary, performanceBonus, allowance) - deduction";
            Object totalSalary = formulaEngineService.executeFormulaWithParams(salaryFormula, salaryParams);
            log.info("员工薪资计算: {} = {}", salaryFormula, totalSalary);

            // 场景2：用户标识处理
            Map<String, Object> userParams = new HashMap<>();
            userParams.put("userId", "USER20240901001");
            userParams.put("userName", "李明");
            userParams.put("department", "技术部");

            String userCodeFormula = "LEFT(userId, 4)";
            Object userCode = formulaEngineService.executeFormulaWithParams(userCodeFormula, userParams);
            log.info("用户代码提取: {} = {}", userCodeFormula, userCode);

            String fullNameFormula = "CONCATENATE(department, '-', userName)";
            Object fullName = formulaEngineService.executeFormulaWithParams(fullNameFormula, userParams);
            log.info("完整名称: {} = {}", fullNameFormula, fullName);

            // 场景3：订单金额计算
            Map<String, Object> orderParams = new HashMap<>();
            orderParams.put("quantity", 5);
            orderParams.put("unitPrice", 99.99);
            orderParams.put("discountRate", 0.1);
            orderParams.put("taxRate", 0.13);

            String subtotalFormula = "quantity * unitPrice";
            Object subtotal = formulaEngineService.executeFormulaWithParams(subtotalFormula, orderParams);
            log.info("小计金额: {} = {}", subtotalFormula, subtotal);

            String discountFormula = "quantity * unitPrice * discountRate";
            Object discount = formulaEngineService.executeFormulaWithParams(discountFormula, orderParams);
            log.info("折扣金额: {} = {}", discountFormula, discount);

            String totalFormula = "(quantity * unitPrice - quantity * unitPrice * discountRate) * (1 + taxRate)";
            Object total = formulaEngineService.executeFormulaWithParams(totalFormula, orderParams);
            log.info("订单总额: {} = {}", totalFormula, total);

            // 场景4：学生成绩等级评定
            Map<String, Object> gradeParams = new HashMap<>();
            gradeParams.put("mathScore", 85);
            gradeParams.put("englishScore", 92);
            gradeParams.put("scienceScore", 78);

            String avgFormula = "AVERAGE(mathScore, englishScore, scienceScore)";
            Object avgScore = formulaEngineService.executeFormulaWithParams(avgFormula, gradeParams);
            log.info("平均成绩: {} = {}", avgFormula, avgScore);

            String gradeFormula = "IF(AVERAGE(mathScore, englishScore, scienceScore) >= 90, '优秀', IF(AVERAGE(mathScore, englishScore, scienceScore) >= 80, '良好', IF(AVERAGE(mathScore, englishScore, scienceScore) >= 70, '中等', '不及格')))";
            Object grade = formulaEngineService.executeFormulaWithParams(gradeFormula, gradeParams);
            log.info("成绩等级: {} = {}", gradeFormula, grade);

        } catch (Exception e) {
            log.error("业务场景测试失败", e);
        }
    }

    /**
     * 演示executeFormulaWithParams的错误处理
     */
    public void demonstrateParameterErrorHandling() {
        log.info("\n=== executeFormulaWithParams错误处理测试 ===");

        try {
            // 测试1：参数为null
            Map<String, Object> nullParams = null;
            String formula1 = "SUM(1, 2, 3)";
            Object result1 = formulaEngineService.executeFormulaWithParams(formula1, nullParams);
            log.info("null参数测试: {} = {}", formula1, result1);

            // 测试2：空参数映射
            Map<String, Object> emptyParams = new HashMap<>();
            String formula2 = "SUM(5, 10)";
            Object result2 = formulaEngineService.executeFormulaWithParams(formula2, emptyParams);
            log.info("空参数映射: {} = {}", formula2, result2);

            // 测试3：缺失必要参数（应该抛出异常）
            Map<String, Object> incompleteParams = new HashMap<>();
            incompleteParams.put("value1", 10);
            String formula3 = "SUM(value1, value2)"; // value2未定义
            try {
                Object result3 = formulaEngineService.executeFormulaWithParams(formula3, incompleteParams);
                log.info("缺失参数测试: {} = {}", formula3, result3);
            } catch (Exception e) {
                log.warn("预期的缺失参数异常: {}", e.getMessage());
            }

            // 测试4：参数名包含特殊字符
            Map<String, Object> specialParams = new HashMap<>();
            specialParams.put("value_1", 100);
            specialParams.put("value2", 200);
            String formula4 = "SUM(value_1, value2)";
            Object result4 = formulaEngineService.executeFormulaWithParams(formula4, specialParams);
            log.info("特殊字符参数: {} = {}", formula4, result4);

        } catch (Exception e) {
            log.error("错误处理测试异常", e);
        }
    }

    /**
     * 演示executeFormulaWithParams的性能测试
     */
    public void demonstrateParameterPerformance() {
        log.info("\n=== executeFormulaWithParams性能测试 ===");

        try {
            // 准备测试参数
            Map<String, Object> params = new HashMap<>();
            params.put("base", 100);
            params.put("rate1", 0.05);
            params.put("rate2", 0.08);
            params.put("rate3", 0.12);
            params.put("text", "性能测试文本内容");
            params.put("length", 4);

            // 测试公式
            String[] formulas = {
                "SUM(base * rate1, base * rate2, base * rate3)",
                "LEFT(text, length)",
                "ROUND(base * (rate1 + rate2 + rate3), 2)",
                "IF(base > 50, '大于50', '不大于50')"
            };

            int iterations = 100;
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < iterations; i++) {
                for (String formula : formulas) {
                    formulaEngineService.executeFormulaWithParams(formula, params);
                }
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            double avgTime = (double) totalTime / (iterations * formulas.length);

            log.info("executeFormulaWithParams性能测试结果:");
            log.info("- 测试公式数: {}", formulas.length);
            log.info("- 执行轮次: {}", iterations);
            log.info("- 总执行次数: {}", iterations * formulas.length);
            log.info("- 总耗时: {}ms", totalTime);
            log.info("- 平均耗时: {}ms", String.format("%.3f", avgTime));

        } catch (Exception e) {
            log.error("性能测试失败", e);
        }
    }

    /**
     * 演示executeFormulaWithParams与executeFormula的对比测试
     */
    public void demonstrateParameterVsDirectComparison() {
        log.info("\n=== executeFormulaWithParams vs executeFormula 对比测试 ===");

        try {
            // 相同结果的不同实现方式对比
            int iterations = 100;

            // 方式1：直接使用executeFormula
            long startTime1 = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                formulaEngineService.executeFormula("SUM(100, 200, 300)");
                formulaEngineService.executeFormula("LEFT('测试文本', 2)");
            }
            long endTime1 = System.currentTimeMillis();

            // 方式2：使用executeFormulaWithParams
            Map<String, Object> params = new HashMap<>();
            params.put("val1", 100);
            params.put("val2", 200);
            params.put("val3", 300);
            params.put("text", "测试文本");
            params.put("len", 2);

            long startTime2 = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                formulaEngineService.executeFormulaWithParams("SUM(val1, val2, val3)", params);
                formulaEngineService.executeFormulaWithParams("LEFT(text, len)", params);
            }
            long endTime2 = System.currentTimeMillis();

            log.info("性能对比结果（各执行{}次）:", iterations);
            log.info("- executeFormula总耗时: {}ms", endTime1 - startTime1);
            log.info("- executeFormulaWithParams总耗时: {}ms", endTime2 - startTime2);
            log.info("- executeFormula平均耗时: {}ms", String.format("%.3f", (double)(endTime1 - startTime1) / (iterations * 2)));
            log.info("- executeFormulaWithParams平均耗时: {}ms", String.format("%.3f", (double)(endTime2 - startTime2) / (iterations * 2)));

            // 结果一致性验证
            Object directResult1 = formulaEngineService.executeFormula("SUM(100, 200, 300)");
            Object paramResult1 = formulaEngineService.executeFormulaWithParams("SUM(val1, val2, val3)", params);
            log.info("结果一致性验证 - SUM: {} vs {} = {}", directResult1, paramResult1, directResult1.equals(paramResult1));

            Object directResult2 = formulaEngineService.executeFormula("LEFT('测试文本', 2)");
            Object paramResult2 = formulaEngineService.executeFormulaWithParams("LEFT(text, len)", params);
            log.info("结果一致性验证 - LEFT: {} vs {} = {}", directResult2, paramResult2, directResult2.equals(paramResult2));

        } catch (Exception e) {
            log.error("对比测试失败", e);
        }
    }

    /**
     * 主函数：运行所有演示
     */
    public static void main(String[] args) {
        FormulaExeWithParamDemo demo = new FormulaExeWithParamDemo();

        try {
            // 运行executeFormulaWithParams测试用例
            demo.demonstrateBasicParameterFormulas();
            demo.demonstrateDataTypeParameters();
            demo.demonstrateBusinessScenarios();
            demo.demonstrateParameterErrorHandling();
            demo.demonstrateParameterPerformance();
            demo.demonstrateParameterVsDirectComparison();
            log.info("\n=== executeFormulaWithParams测试完成 ===");
            log.info("所有测试用例执行完成！");

        } catch (Exception e) {
            log.error("测试程序执行失败", e);
        }
    }
}
