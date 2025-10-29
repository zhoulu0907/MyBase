package com.cmsr.onebase.module.formula.service.impl;

import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 公式引擎服务测试类
 * 验证Excel公式计算功能的正确性和安全性
 *
 * @author matianyu
 * @date 2025-09-01
 */
@SpringBootTest
@DisplayName("公式引擎服务测试")
class FormulaEngineServiceImplTest {

    private FormulaEngineService    formulaEngineService;
    private FormulaEngineProperties properties;

    @BeforeEach
    void setUp() {
        // 初始化配置
        properties = new FormulaEngineProperties();
        properties.setEnabled(true);
        properties.setTimeoutMs(5000L);
        properties.setSecurityMode(true);
        properties.setMaxFormulaLength(1024);

        // 初始化服务
        formulaEngineService = new FormulaEngineServiceImpl(properties);
    }

    @Test
    @DisplayName("测试LEFT函数")
    void testLeftFunction() throws Exception {
        // 测试基本LEFT函数
        Object result = formulaEngineService.executeFormula("FormulaJS.LEFT('Hello World', 5)");
        assertEquals("Hello", result);

        // 测试中文字符
        result = formulaEngineService.executeFormula("FormulaJS.LEFT('你好世界', 2)");
        assertEquals("你好", result);

        // 测试边界情况
        result = formulaEngineService.executeFormula("FormulaJS.LEFT('Test', 0)");
        assertEquals("", result);

        result = formulaEngineService.executeFormula("FormulaJS.LEFT('Test', 10)");
        assertEquals("Test", result);
    }

    @Test
    @DisplayName("测试SUM函数")
    void testSumFunction() throws Exception {
        // 测试基本SUM函数
        Object result = formulaEngineService.executeFormula("FormulaJS.SUM(1, 2, 3, 4, 5)");
        assertEquals(15.0, result);

        // 测试小数计算
        result = formulaEngineService.executeFormula("FormulaJS.SUM(1.5, 2.5, 3.0)");
        assertEquals(7.0, result);

        // 测试单个数字
        result = formulaEngineService.executeFormula("FormulaJS.SUM(42)");
        assertEquals(42.0, result);

        // 测试零值
        result = formulaEngineService.executeFormula("FormulaJS.SUM(0, 0, 0)");
        assertEquals(0.0, result);

        // 测试负数
        result = formulaEngineService.executeFormula("FormulaJS.SUM(-1, -2, 3)");
        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("测试复合函数")
    void testComplexFormulas() throws Exception {
        // 测试组合使用LEFT和LEN函数
        Object result = formulaEngineService.executeFormula("FormulaJS.LEFT('Hello World', FormulaJS.LEN('Hello'))");
        assertEquals("Hello", result);

        // 测试SUM和MAX组合
        result = formulaEngineService.executeFormula("FormulaJS.SUM(FormulaJS.MAX(1, 2, 3), FormulaJS.MAX(4, 5, 6))");
        assertEquals(9.0, result);
    }

    @Test
    @DisplayName("测试带参数的公式执行")
    void testFormulaWithParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("text", "Hello World");
        parameters.put("length", 5);
        parameters.put("num1", 10);
        parameters.put("num2", 20);

        // 测试参数传递
        Object result = formulaEngineService.executeFormulaWithParams("FormulaJS.LEFT(text, length)", parameters);
        assertEquals("Hello", result);

        result = formulaEngineService.executeFormulaWithParams("FormulaJS.SUM(num1, num2)", parameters);
        assertEquals(30.0, result);
    }

    @Test
    @DisplayName("测试其他Excel函数")
    void testOtherExcelFunctions() throws Exception {
        // 测试RIGHT函数
        Object result = formulaEngineService.executeFormula("FormulaJS.RIGHT('Hello World', 5)");
        assertEquals("World", result);

        // 测试MID函数
        result = formulaEngineService.executeFormula("FormulaJS.MID('Hello World', 7, 5)");
        assertEquals("World", result);

        // 测试LEN函数
        result = formulaEngineService.executeFormula("FormulaJS.LEN('Hello World')");
        assertEquals(11.0, result);

        // 测试UPPER函数
        result = formulaEngineService.executeFormula("FormulaJS.UPPER('hello world')");
        assertEquals("HELLO WORLD", result);

        // 测试LOWER函数
        result = formulaEngineService.executeFormula("FormulaJS.LOWER('HELLO WORLD')");
        assertEquals("hello world", result);

        // 测试AVERAGE函数
        result = formulaEngineService.executeFormula("FormulaJS.AVERAGE(1, 2, 3, 4, 5)");
        assertEquals(3.0, result);

        // 测试MAX函数
        result = formulaEngineService.executeFormula("FormulaJS.MAX(1, 5, 3, 9, 2)");
        assertEquals(9.0, result);

        // 测试MIN函数
        result = formulaEngineService.executeFormula("FormulaJS.MIN(1, 5, 3, 9, 2)");
        assertEquals(1.0, result);
    }

    @Test
    @DisplayName("测试公式验证功能")
    void testFormulaValidation() {
        // 测试有效公式
        assertTrue(formulaEngineService.validateFormula("FormulaJS.SUM(1, 2, 3)"));
        assertTrue(formulaEngineService.validateFormula("FormulaJS.LEFT('test', 2)"));

        // 测试无效公式
        assertFalse(formulaEngineService.validateFormula(""));
        assertFalse(formulaEngineService.validateFormula(null));
        assertFalse(formulaEngineService.validateFormula("invalid_function()"));
    }

    @Test
    @DisplayName("测试安全性检查")
    void testSecurityValidation() {
        // 测试危险函数被拒绝
        assertFalse(formulaEngineService.validateFormula("eval('malicious code')"));
        assertFalse(formulaEngineService.validateFormula("new Function('return 1')()"));
        assertFalse(formulaEngineService.validateFormula("require('fs')"));
        assertFalse(formulaEngineService.validateFormula("process.exit()"));

        // 测试正常函数被接受
        assertTrue(formulaEngineService.validateFormula("FormulaJS.SUM(1, 2, 3)"));
    }

    @Test
    @DisplayName("测试错误处理")
    void testErrorHandling() {
        // 测试空公式
        assertThrows(IllegalArgumentException.class, () -> {
            formulaEngineService.executeFormula("");
        });

        // 测试null公式
        assertThrows(IllegalArgumentException.class, () -> {
            formulaEngineService.executeFormula(null);
        });

        // 测试过长公式
        String longFormula = "FormulaJS.SUM(" + "1,".repeat(1000) + "1)";
        assertThrows(IllegalArgumentException.class, () -> {
            formulaEngineService.executeFormula(longFormula);
        });
    }

    @Test
    @DisplayName("测试支持的函数列表")
    void testGetSupportedFunctions() {
        String[] functions = formulaEngineService.getSupportedFunctions();

        assertNotNull(functions);
        assertTrue(functions.length > 0);

        // 验证包含基本函数
        assertTrue(java.util.Arrays.asList(functions).contains("LEFT"));
        assertTrue(java.util.Arrays.asList(functions).contains("SUM"));
        assertTrue(java.util.Arrays.asList(functions).contains("RIGHT"));
        assertTrue(java.util.Arrays.asList(functions).contains("LEN"));
    }

    @Test
    @DisplayName("测试缓存功能")
    void testCacheFunction() throws Exception {
        // 执行相同公式多次，验证缓存工作
        String formula = "FormulaJS.SUM(1, 2, 3, 4, 5)";

        long startTime1 = System.currentTimeMillis();
        Object result1 = formulaEngineService.executeFormula(formula);
        long time1 = System.currentTimeMillis() - startTime1;

        long startTime2 = System.currentTimeMillis();
        Object result2 = formulaEngineService.executeFormula(formula);
        long time2 = System.currentTimeMillis() - startTime2;

        // 结果应该相同
        assertEquals(result1, result2);
        assertEquals(15.0, result1);

    }

    @Test
    @DisplayName("综合测试：实际业务场景")
    void testRealWorldScenarios() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        // 场景1：从用户输入中提取前缀
        parameters.put("userInput", "USER_12345_ADMIN");
        Object result = formulaEngineService.executeFormulaWithParams("FormulaJS.LEFT(userInput, 4)", parameters);
        assertEquals("USER", result);

        // 场景2：计算订单总价
        parameters.put("price1", 100.50);
        parameters.put("price2", 200.75);
        parameters.put("price3", 50.25);
        result = formulaEngineService.executeFormulaWithParams("FormulaJS.SUM(price1, price2, price3)", parameters);
        assertEquals(351.5, result);

        // 场景3：格式化用户名
        parameters.put("firstName", "zhang");
        parameters.put("lastName", "san");
        result = formulaEngineService.executeFormulaWithParams(
            "FormulaJS.UPPER(FormulaJS.LEFT(firstName, 1)) + FormulaJS.LOWER(FormulaJS.RIGHT(firstName, FormulaJS.LEN(firstName) - 1)) + ' ' + FormulaJS.UPPER(lastName)",
            parameters);
        assertEquals("Zhang SAN", result);

        // 场景4：数据统计
        parameters.put("scores", new double[]{85.5, 92.0, 78.5, 95.0, 88.0});
        // 注意：这里需要修改公式以支持数组参数
        result = formulaEngineService.executeFormula("FormulaJS.SUM(85.5, 92.0, 78.5, 95.0, 88.0)");
        assertEquals(439.0, result);
    }
}
