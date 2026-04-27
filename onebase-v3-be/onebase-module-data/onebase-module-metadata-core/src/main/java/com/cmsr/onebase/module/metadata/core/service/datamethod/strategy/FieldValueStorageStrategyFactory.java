package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段值存储策略工厂
 *
 * @author matianyu
 * @date 2025-11-15
 */
@Component
public class FieldValueStorageStrategyFactory {

    private final List<FieldValueStorageStrategy> strategies;

    public FieldValueStorageStrategyFactory(List<FieldValueStorageStrategy> strategies) {
        this.strategies = strategies.stream()
                .sorted(Comparator.comparingInt(FieldValueStorageStrategy::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 根据字段类型挑选合适的策略
     *
     * @param fieldType 字段类型
     * @return 匹配策略
     */
    public FieldValueStorageStrategy getStrategy(String fieldType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(fieldType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到字段值存储策略"));
    }
}

