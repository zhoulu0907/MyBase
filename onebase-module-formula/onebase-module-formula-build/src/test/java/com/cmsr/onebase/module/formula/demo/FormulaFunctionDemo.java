package com.cmsr.onebase.module.formula.demo;

import com.cmsr.onebase.module.formula.build.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.build.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.build.service.engine.FormulaEngineServiceImpl;
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
public class FormulaFunctionDemo {

    private final FormulaEngineService formulaEngineService;

    public FormulaFunctionDemo() {
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
     * 主函数：运行所有演示
     */
    public static void main(String[] args) {
        FormulaFunctionDemo demo = new FormulaFunctionDemo();

        try {
            // 运行executeFormulaWithParams测试用例
            demo.demonstrateBasicParameterFormulas();
            log.info("\n=== executeFormulaWithParams测试完成 ===");
            log.info("所有测试用例执行完成！");
        } catch (Exception e) {
            log.error("测试程序执行失败", e);
        }
    }

    /**
     * 演示executeFormulaWithParams的基本参数传递功能
     */
    public void demonstrateBasicParameterFormulas() {
        log.info("\n=== executeFormulaWithParams基本参数测试 ===");

        try {
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


}
