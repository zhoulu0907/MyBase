package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.uid.UidGenerator;
import com.mybatisflex.core.keygen.IKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeGenerator implements IKeyGenerator {

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public Object generate(Object o, String s) {
        return uidGenerator.getUID();
    }
}
