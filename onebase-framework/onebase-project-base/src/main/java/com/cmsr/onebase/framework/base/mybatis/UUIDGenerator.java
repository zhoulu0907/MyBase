package com.cmsr.onebase.framework.base.mybatis;

import com.github.f4b6a3.uuid.UuidCreator;
import com.mybatisflex.core.keygen.IKeyGenerator;

public class UUIDGenerator implements IKeyGenerator {
    @Override
    public Object generate(Object o, String s) {
        return UuidCreator.getTimeOrdered().toString();
    }
}
