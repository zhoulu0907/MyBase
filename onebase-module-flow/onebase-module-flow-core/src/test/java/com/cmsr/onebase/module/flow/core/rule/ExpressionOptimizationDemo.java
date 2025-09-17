package com.cmsr.onebase.module.flow.core.rule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 表达式优化演示类
 * 展示优化前后的性能差异和表达式构建效果
 * 
 * @Author：huangjie
 * @Date：2025/9/16 22:30
 */
public class ExpressionOptimizationDemo {

    private ExpressionAssistant expressionAssistant;
    private Map<String, Object> testContext;

    @BeforeEach
    void setUp() {
        expressionAssistant = new ExpressionAssistant();
        
        // 准备测试数据
        testContext = new HashMap<>();
        testContext.put("age", 25);
        testContext.put("status", "ACTIVE");
        testContext.put("tags", Arrays.asList("VIP", "PREMIUM"));
        testContext.put("score", 85);
        testContext.put("level", "GOLD");
    }

    @Test
    void testOptimizedExpressionBuilding() {
        // 创建复杂的条件结构
        Condition condition = createComplexCondition();
        
        // 测试优化后的评估方法
        long startTime = System.nanoTime();
        Boolean result = expressionAssistant.evaluate(condition, testContext);
        long endTime = System.nanoTime();
        
        System.out.println("=== 优化后的表达式评估 ===");
        System.out.println("执行时间: " + (endTime - startTime) / 1000000.0 + " ms");
        System.out.println("评估结果: " + result);
        
        assertTrue(result, "复杂条件应该返回true");
    }

    @Test
    void testExpressionCaching() {
        Condition condition = createComplexCondition();
        
        // 第一次执行（会编译和缓存表达式）
        long startTime1 = System.nanoTime();
        Boolean result1 = expressionAssistant.evaluate(condition, testContext);
        long endTime1 = System.nanoTime();
        
        // 第二次执行（使用缓存的表达式）
        long startTime2 = System.nanoTime();
        Boolean result2 = expressionAssistant.evaluate(condition, testContext);
        long endTime2 = System.nanoTime();
        
        System.out.println("=== 表达式缓存效果 ===");
        System.out.println("第一次执行时间: " + (endTime1 - startTime1) / 1000000.0 + " ms");
        System.out.println("第二次执行时间: " + (endTime2 - startTime2) / 1000000.0 + " ms");

        assertEquals(result1, result2, "两次执行结果应该相同");
        assertTrue((endTime2 - startTime2) <= (endTime1 - startTime1), "第二次执行应该更快（使用缓存）");
    }

    @Test
    void testComplexExpressionStructure() {
        Condition condition = createComplexCondition();
        
        // 手动构建期望的表达式结构来验证
        // 条件1: (age >= 18 && status == 'ACTIVE')
        // 条件2: (tags contains 'VIP' && score > 80)
        // 条件3: (level == 'GOLD')
        // 最终: ((age >= 18 && status == 'ACTIVE') || (tags contains 'VIP' && score > 80) || (level == 'GOLD'))
        
        Boolean result = expressionAssistant.evaluate(condition, testContext);
        
        System.out.println("=== 复杂表达式结构测试 ===");
        System.out.println("条件结构:");
        System.out.println("  条件项1: age >= 18 AND status == 'ACTIVE'");
        System.out.println("  条件项2: tags contains 'VIP' AND score > 80");
        System.out.println("  条件项3: level == 'GOLD'");
        System.out.println("  逻辑关系: 条件项1 OR 条件项2 OR 条件项3");
        System.out.println("评估结果: " + result);
        
        assertTrue(result, "复杂条件应该返回true");
    }

    @Test
    void testPerformanceComparison() {
        Condition condition = createLargeCondition();
        
        // 测试多次执行的性能
        int iterations = 1000;
        
        long totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            expressionAssistant.evaluate(condition, testContext);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        double avgTime = totalTime / (double) iterations / 1000000.0;
        
        System.out.println("=== 性能测试 ===");
        System.out.println("执行次数: " + iterations);
        System.out.println("平均执行时间: " + avgTime + " ms");

        assertTrue(avgTime < 1.0, "平均执行时间应该小于1ms");
    }

    /**
     * 创建复杂的条件结构用于测试
     */
    private Condition createComplexCondition() {
        Condition condition = new Condition();
        List<ConditionItem> conditionItems = new ArrayList<>();
        
        // 条件项1: age >= 18 AND status == 'ACTIVE'
        ConditionItem item1 = new ConditionItem();
        RuleItem rule1_1 = new RuleItem();
        rule1_1.setFilterId(1L);
        rule1_1.setFieldName("age");
        rule1_1.setOp("GREATER_EQUALS");
        rule1_1.setValue(Arrays.asList("18"));
        rule1_1.setFieldValue(new Object[]{18});
        
        RuleItem rule1_2 = new RuleItem();
        rule1_2.setFilterId(2L);
        rule1_2.setFieldName("status");
        rule1_2.setOp("EQUALS");
        rule1_2.setValue(Arrays.asList("ACTIVE"));
        rule1_2.setFieldValue(new Object[]{"ACTIVE"});
        
        item1.setRules(Arrays.asList(rule1_1, rule1_2));
        conditionItems.add(item1);
        
        // 条件项2: tags contains 'VIP' AND score > 80
        ConditionItem item2 = new ConditionItem();
        RuleItem rule2_1 = new RuleItem();
        rule2_1.setFilterId(3L);
        rule2_1.setFieldName("tags");
        rule2_1.setOp("CONTAINS");
        rule2_1.setValue(Arrays.asList("VIP"));
        rule2_1.setFieldValue(new Object[]{"VIP"});
        
        RuleItem rule2_2 = new RuleItem();
        rule2_2.setFilterId(4L);
        rule2_2.setFieldName("score");
        rule2_2.setOp("GREATER_THAN");
        rule2_2.setValue(Arrays.asList("80"));
        rule2_2.setFieldValue(new Object[]{80});
        
        item2.setRules(Arrays.asList(rule2_1, rule2_2));
        conditionItems.add(item2);
        
        // 条件项3: level == 'GOLD'
        ConditionItem item3 = new ConditionItem();
        RuleItem rule3_1 = new RuleItem();
        rule3_1.setFilterId(5L);
        rule3_1.setFieldName("level");
        rule3_1.setOp("EQUALS");
        rule3_1.setValue(Arrays.asList("GOLD"));
        rule3_1.setFieldValue(new Object[]{"GOLD"});
        
        item3.setRules(Arrays.asList(rule3_1));
        conditionItems.add(item3);
        
        condition.setConditions(conditionItems);
        return condition;
    }

    /**
     * 创建大型条件结构用于性能测试
     */
    private Condition createLargeCondition() {
        Condition condition = new Condition();
        List<ConditionItem> conditionItems = new ArrayList<>();
        
        // 创建10个条件项，每个条件项包含3个规则项
        for (int i = 0; i < 10; i++) {
            ConditionItem item = new ConditionItem();
            List<RuleItem> rules = new ArrayList<>();
            
            for (int j = 0; j < 3; j++) {
                RuleItem rule = new RuleItem();
                rule.setFilterId((long) (i * 3 + j + 1));
                rule.setFieldName("field_" + (i * 3 + j + 1));
                rule.setOp("EQUALS");
                rule.setValue(Arrays.asList("value_" + (i * 3 + j + 1)));
                rules.add(rule);
            }
            
            item.setRules(rules);
            conditionItems.add(item);
        }
        
        condition.setConditions(conditionItems);
        return condition;
    }
}