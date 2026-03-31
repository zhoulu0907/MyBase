package com.cmsr.onebase.module.formula.service.engine;

import com.cmsr.onebase.module.formula.config.FormulaEngineProperties;
import com.cmsr.onebase.module.formula.service.extendsion.FormulaExtendsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * 公式引擎 SUMPRODUCT 参数替换回归测试
 *
 * @author matianyu
 * @date 2026-03-27
 */
class FormulaEngineServiceImplSumproductTest {

    private FormulaEngineServiceImpl formulaEngineService;

    @BeforeEach
    void setUp() {
        FormulaEngineProperties properties = new FormulaEngineProperties();
        properties.setEnabled(true);
        properties.setTimeoutMs(5000L);
        properties.setSecurityMode(true);
        properties.setMaxFormulaLength(2048);

        formulaEngineService = new FormulaEngineServiceImpl(properties);

        FormulaExtendsService noOpExtendsService = (formula, parameters) -> {
            // no-op
        };
        ReflectionTestUtils.setField(formulaEngineService, "formulaExtendsService", noOpExtendsService);
    }

    /**
     * 验证 SUMPRODUCT 在数组参数场景下能返回乘积之和
     */
    @Test
    @DisplayName("SUMPRODUCT-数组参数返回正确结果")
    void shouldReturnCorrectValueWhenSumproductUsesArrayParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("$销售订单明细表_子表.销售数量", Arrays.asList(2));
        parameters.put("$销售订单明细表_子表.单价", Arrays.asList(3));

        Object result = formulaEngineService.executeFormulaWithParams(
                "SUMPRODUCT($销售订单明细表_子表.销售数量,$销售订单明细表_子表.单价)",
                parameters
        );

        assertInstanceOf(Number.class, result);
        assertEquals(6L, ((Number) result).longValue());
    }

    /**
     * 验证 SUMPRODUCT 在小数数组场景下按加权求和返回结果
     */
    @Test
    @DisplayName("SUMPRODUCT-小数数组按加权求和")
    void shouldReturnWeightedSumWhenSumproductUsesDecimalArrayParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("$a", Arrays.asList(1, 2, 3));
        parameters.put("$b", Arrays.asList(0.1, 0.2, 0.3));

        Object result = formulaEngineService.executeFormulaWithParams("SUMPRODUCT($a,$b)", parameters);

        assertInstanceOf(Number.class, result);
        assertEquals(1.4D, ((Number) result).doubleValue(), 1e-10);
    }
}

