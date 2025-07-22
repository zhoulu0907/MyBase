package com.cmsr.onebase.framework.mybatis.config;

import jakarta.persistence.AttributeConverter;

public class IntBoolConverter implements AttributeConverter<Integer, Boolean> {
    @Override
    public Boolean convertToDatabaseColumn(Integer integer) {
        return integer != null && integer > 0;
    }

    @Override
    public Integer convertToEntityAttribute(Boolean attribute) {
        return (attribute != null && attribute) ? 1 : 0;
    }
}
