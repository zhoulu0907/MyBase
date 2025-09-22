package com.cmsr.onebase.module.flow.core.rule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testBuildExpression() {
        // 测试表达式构建功能
        String expression = expressionAssistant.buildExpression("field_1", OpEnum.EQUALS, new Object[]{25});
        assertEquals("field_1 == 25", expression);

        expression = expressionAssistant.buildExpression("field_2", OpEnum.NOT_EQUALS, new Object[]{"INACTIVE"});
        assertEquals("field_2 != 'INACTIVE'", expression);

        expression = expressionAssistant.buildExpression("field_3", OpEnum.CONTAINS, new Object[]{"VIP"});
        assertEquals("field_3 contains 'VIP'", expression);
    }


}