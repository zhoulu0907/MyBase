package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

import org.springframework.stereotype.Component;

/**
 * 单值字段存储策略，保持原始值不变
 *
 * @author matianyu
 * @date 2025-11-15
 */
@Component
public class SingleValueFieldStorageStrategy implements FieldValueStorageStrategy {

    @Override
    public boolean supports(String fieldType) {
        return true;
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode) {
        return rawValue;
    }
}

