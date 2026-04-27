package com.cmsr.onebase.module.formula.demo;

import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.dto.ContextData;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
            // demo.testSumLeftFormulas();
            // 新增：条件/日期/逻辑相关函数测试
            // demo.testConditionDateLogicFormulas();

            // 返回类型测试
            demo.testReturnType();

            // demo.testMetaDataFormulas();
            log.info("\n=== executeFormulaWithParams测试完成 ===");
            log.info("所有测试用例执行完成！");
        } catch (Exception e) {
            log.error("测试程序执行失败", e);
        }
    }
    /**
     * 演示executeFormulaWithParams的基本参数传递功能
     */
    public void testReturnType() {
        System.out.println("\n测试时间函数:");
        String formula6 = "NOW()";
        Object result6 = formulaEngineService.executeFormula(formula6);
        System.out.println("公式: " + formula6 + " = " + result6);

        System.out.println("\n测试YEAR函数:");
        String formula7 = "YEAR('2025-10-27')";
        Object result7 = formulaEngineService.executeFormula(formula7);
        System.out.println("公式: " + formula7 + " = " + result7);

    }
    /**
     * 演示executeFormulaWithParams的基本参数传递功能
     */
    public void testSumLeftFormulas() {
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
    /**
     * 条件汇总/计数与日期/逻辑函数验证
     * 覆盖：SUMIF、SUMIFS、COUNTIFS、DAY、MONTH、DATEDIF、IF
     *
     * @author matianyu
     * @date 2025-09-01
     */
    public void testMetaDataFormulas1() {
        log.info("\n=== 元数据函数测试（含ContextData解析） ===");

        // 构造上下文数据：recordList
        ContextData contextData = new ContextData();
        contextData.setRecordList(new ArrayList<>());

        Map<String, Object> r1 = new HashMap<>();
        r1.put("id", 2);
        r1.put("status", "完成");
        r1.put("amount", 100);
        contextData.getRecordList().add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("id", 1);
        r2.put("status", "未完成");
        r2.put("amount", 50);
        contextData.getRecordList().add(r2);

        // COUNT：COUNT([$recordList.id]) => 2
        String c1 = "COUNT($查询节点.主键ID)";
        // 参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("$查询节点", "data_id_10");
        parameters.put("$查询节点.主键ID", "f_id_10");


        // Object rc1 = formulaEngineService.executeFormulaWithParams(c1, new HashMap<>(), contextData);
        // log.info("COUNT: {} = {}", c1, rc1);

    }
        /**
         * 条件汇总/计数与日期/逻辑函数验证
         * 覆盖：SUMIF、SUMIFS、COUNTIFS、DAY、MONTH、DATEDIF、IF
         *
         * @author matianyu
         * @date 2025-09-01
         */
    // public void testMetaDataFormulas() {
    //     log.info("\n=== 元数据函数测试（含ContextData解析） ===");
    //
    //     // 构造上下文数据：recordList
    //     ContextData contextData = new ContextData();
    //     contextData.setRecordList(new ArrayList<>());
    //
    //     Map<String, Object> r1 = new HashMap<>();
    //     r1.put("id", 2);
    //     r1.put("status", "完成");
    //     r1.put("amount", 100);
    //     contextData.getRecordList().add(r1);
    //
    //     Map<String, Object> r2 = new HashMap<>();
    //     r2.put("id", 1);
    //     r2.put("status", "未完成");
    //     r2.put("amount", 50);
    //     contextData.getRecordList().add(r2);
    //
    //     // COUNT：COUNT([$recordList.id]) => 2
    //     String c1 = "COUNT($recordList.id)";
    //     Object rc1 = formulaEngineService.executeFormulaWithParams(c1, new HashMap<>(), contextData);
    //     log.info("COUNT: {} = {}", c1, rc1);
    //
    //     // SUMIF：id > 1 时累计 amount => 仅记录1，结果 100
    //     String s1 = "SUMIF($recordList.id, \">1\", $recordList.amount)";
    //     Object rs1 = formulaEngineService.executeFormulaWithParams(s1, new HashMap<>(), contextData);
    //     log.info("SUMIF: {} = {}", s1, rs1);
    //
    //     // SUMIFS：id>=1 且 status=完成 时累计 amount => 100
    //     String s2 = "SUMIFS($recordList.amount, $recordList.id, \">=1\", $recordList.status, \"=完成\")";
    //     Object rs2 = formulaEngineService.executeFormulaWithParams(s2, new HashMap<>(), contextData);
    //     log.info("SUMIFS: {} = {}", s2, rs2);
    //
    //     // COUNTIFS：status=完成 且 id>=1 => 1
    //     String s3 = "COUNTIFS($recordList.status, \"=完成\", $recordList.id, \">=1\")";
    //     Object rs3 = formulaEngineService.executeFormulaWithParams(s3, new HashMap<>(), contextData);
    //     log.info("COUNTIFS: {} = {}", s3, rs3);
    //
    //     // DAY：取“日” => 15
    //     String d1 = "DAY(DATEVALUE(\"2025-01-15\"))";
    //     Object rd1 = formulaEngineService.executeFormulaWithParams(d1, new HashMap<>(), contextData);
    //     log.info("DAY: {} = {}", d1, rd1);
    //
    //     // MONTH：取“月” => 7
    //     String d2 = "MONTH(DATEVALUE(\"2025-07-25\"))";
    //     Object rd2 = formulaEngineService.executeFormulaWithParams(d2, new HashMap<>(), contextData);
    //     log.info("MONTH: {} = {}", d2, rd2);
    //
    //     // DATEDIF：日差 => 30
    //     String d3 = "DATEDIF(DATEVALUE(\"2025-01-01\"), DATEVALUE(\"2025-01-31\"), \"D\")";
    //     Object rd3 = formulaEngineService.executeFormulaWithParams(d3, new HashMap<>(), contextData);
    //     log.info("DATEDIF: {} = {}", d3, rd3);
    //
    //     // IF：根据recordList的amount求和判断
    //     String f1 = "IF(SUM($recordList.amount)>100, \"OK\", \"NO\")";
    //     Object rf1 = formulaEngineService.executeFormulaWithParams(f1, new HashMap<>(), contextData);
    //     log.info("IF: {} = {}", f1, rf1);
    // }

    /**
     * 条件汇总/计数与日期/逻辑函数验证
     * 覆盖：SUMIF、SUMIFS、COUNTIFS、DAY、MONTH、DATEDIF、IF
     *
     * @author matianyu
     * @date 2025-09-01
     */
    public void testConditionDateLogicFormulas() {
        log.info("\n=== 条件/日期/逻辑函数测试 ===");

        try {
            // 1) SUMIF：对大于3的元素求和 => 4 + 5 = 9
            String f1 = "SUMIF([1,2,3,4,5], \">3\")";
            Object r1 = formulaEngineService.executeFormulaWithParams(f1, new HashMap<>());
            log.info("SUMIF: {} = {}", f1, r1);

            // 2) SUMIFS：多条件求和（按两个条件范围筛选）
            // 条件：range1 >= 3 且 range2 < 4 => 命中索引3,4,5 -> sum_range 30+40+50=120
            String f2 = "SUMIFS([10,20,30,40,50], [1,2,3,4,5], \">=3\", [5,4,3,2,1], \"<4\")";
            Object r2 = formulaEngineService.executeFormulaWithParams(f2, new HashMap<>());
            log.info("SUMIFS: {} = {}", f2, r2);

            // 3) COUNTIFS：统计同时满足的次数 => “A”且数值>2 => 命中一次
            String f3 = "COUNTIFS([\"A\",\"B\",\"A\",\"C\"], \"=A\", [1,5,3,2], \">2\")";
            Object r3 = formulaEngineService.executeFormulaWithParams(f3, new HashMap<>());
            log.info("COUNTIFS: {} = {}", f3, r3);

            // 4) DAY：取日期中的“日”
            String f4 = "DAY(DATEVALUE(\"2025-01-15\"))";
            Object r4 = formulaEngineService.executeFormulaWithParams(f4, new HashMap<>());
            log.info("DAY: {} = {}", f4, r4);

            // 5) MONTH：取日期中的“月”
            String f5 = "MONTH(DATEVALUE(\"2025-07-25\"))";
            Object r5 = formulaEngineService.executeFormulaWithParams(f5, new HashMap<>());
            log.info("MONTH: {} = {}", f5, r5);

            // 6) DATEDIF：年/月/日差
            String f6 = "DATEDIF(DATEVALUE(\"2024-01-01\"), DATEVALUE(\"2025-01-01\"), \"Y\")";
            Object r6 = formulaEngineService.executeFormulaWithParams(f6, new HashMap<>());
            log.info("DATEDIF-年差: {} = {}", f6, r6);

            String f7 = "DATEDIF(DATEVALUE(\"2025-01-01\"), DATEVALUE(\"2025-07-01\"), \"M\")";
            Object r7 = formulaEngineService.executeFormulaWithParams(f7, new HashMap<>());
            log.info("DATEDIF-月差: {} = {}", f7, r7);

            String f8 = "DATEDIF(DATEVALUE(\"2025-01-01\"), DATEVALUE(\"2025-01-31\"), \"D\")";
            Object r8 = formulaEngineService.executeFormulaWithParams(f8, new HashMap<>());
            log.info("DATEDIF-日差: {} = {}", f8, r8);

            // 7) IF：基本逻辑判断
            String f9 = "IF(SUM([1,2,3])>5, \"OK\", \"NO\")";
            Object r9 = formulaEngineService.executeFormulaWithParams(f9, new HashMap<>());
            log.info("IF: {} = {}", f9, r9);

        } catch (Exception e) {
            log.error("条件/日期/逻辑函数测试失败", e);
        }
    }
}
