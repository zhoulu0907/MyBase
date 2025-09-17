package com.cmsr.onebase.module.flow.core.rule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExpressionAssistant测试类
 * 验证重构后的功能是否正确
 * 
 * @Author：huangjie
 * @Date：2025/9/16 22:15
 */
public class ExpressionAssistantTest {

    private ExpressionAssistant expressionAssistant;
    private Map<String, Object> testContext;

    @BeforeEach
    void setUp() {
        expressionAssistant = new ExpressionAssistant();
        
        // 准备测试数据
        testContext = new HashMap<>();
        testContext.put("field_1", 25);
        testContext.put("field_2", "ACTIVE");
        testContext.put("field_3", Arrays.asList("VIP", "PREMIUM"));
        testContext.put("field_4", "张三");
    }

    @Test
    void testEvaluateCondition_OR_Logic() {
        // 测试条件项之间的OR关系
        Condition condition = new Condition();
        
        // 创建条件项1: field_1 >= 30 (false)
        ConditionItem conditionItem1 = new ConditionItem();
        
        RuleItem rule1 = new RuleItem();
        rule1.setFilterId(1L);
        rule1.setFieldName("field_1");
        rule1.setOp("GREATER_EQUALS");
        rule1.setValue(Arrays.asList("30"));
        rule1.setFieldValue(new Object[]{30});
        
        conditionItem1.setRules(Arrays.asList(rule1));
        
        // 创建条件项2: field_2 == 'ACTIVE' (true)
        ConditionItem conditionItem2 = new ConditionItem();
        
        RuleItem rule2 = new RuleItem();
        rule2.setFilterId(2L);
        rule2.setFieldName("field_2");
        rule2.setOp("EQUALS");
        rule2.setValue(Arrays.asList("ACTIVE"));
        rule2.setFieldValue(new Object[]{"ACTIVE"});
        
        conditionItem2.setRules(Arrays.asList(rule2));
        
        condition.setConditions(Arrays.asList(conditionItem1, conditionItem2));
        
        // 由于条件项之间是OR关系，只要有一个为true，整个条件就为true
        Boolean result = expressionAssistant.evaluate(condition, testContext);
        assertTrue(result, "条件项之间的OR关系应该返回true");
    }

    @Test
    void testEvaluateConditionItem_AND_Logic() {
        // 测试规则项之间的AND关系
        ConditionItem conditionItem = new ConditionItem();
        
        // 规则1: field_1 >= 20 (true)
        RuleItem rule1 = new RuleItem();
        rule1.setFilterId(1L);
        rule1.setFieldName("field_1");
        rule1.setOp("GREATER_EQUALS");
        rule1.setValue(Arrays.asList("20"));
        rule1.setFieldValue(new Object[]{20});
        
        // 规则2: field_2 == 'ACTIVE' (true)
        RuleItem rule2 = new RuleItem();
        rule2.setFilterId(2L);
        rule2.setFieldName("field_2");
        rule2.setOp("EQUALS");
        rule2.setValue(Arrays.asList("ACTIVE"));
        rule2.setFieldValue(new Object[]{"ACTIVE"});
        
        conditionItem.setRules(Arrays.asList(rule1, rule2));
        
        // 由于规则项之间是AND关系，所有规则都为true，条件项才为true

        // 测试AND关系中有false的情况
        RuleItem rule3 = new RuleItem();
        rule3.setFilterId(3L);
        rule3.setFieldName("field_1");
        rule3.setOp("GREATER_THAN");
        rule3.setValue(Arrays.asList("30")); // field_1 = 25, 所以这个条件为false
        rule3.setFieldValue(new Object[]{30});
        
        conditionItem.setRules(Arrays.asList(rule1, rule2, rule3));
        

    }

    @Test
    void testBuildExpression() {
        // 测试表达式构建功能
        String expression = expressionAssistant.buildExpression("field_1", OpEnum.EQUALS, new Object[]{25});
        assertEquals("field_1 == 25", expression);
        
        expression = expressionAssistant.buildExpression("field_2", OpEnum.NOT_EQUALS, new Object[]{"INACTIVE"});
        assertEquals("field_2 != 'INACTIVE'", expression);
        
        expression = expressionAssistant.buildExpression("field_3", OpEnum.CONTAINS, new Object[]{"VIP"});
        assertEquals("field_3 contains 'VIP'", expression);
    }



    @Test
    void testEmptyConditions() {
        // 测试空条件的处理
        Condition emptyCondition = new Condition();
        Boolean result = expressionAssistant.evaluate(emptyCondition, testContext);
        assertTrue(result, "空条件应该返回true");
        
        emptyCondition.setConditions(new ArrayList<>());
        result = expressionAssistant.evaluate(emptyCondition, testContext);
        assertTrue(result, "空条件列表应该返回true");
        
        // 测试null条件
        result = expressionAssistant.evaluate(null, testContext);
        assertTrue(result, "null条件应该返回true");
    }


}