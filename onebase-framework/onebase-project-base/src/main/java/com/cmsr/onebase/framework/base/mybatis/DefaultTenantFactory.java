package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.mybatisflex.core.tenant.TenantFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultTenantFactory implements TenantFactory {
    private final static long DEFAULT_TENANT_ID = -1L;

    @Override
    public Object[] getTenantIds() {
        return getTenantIds(null);
    }

    @Override
    public Object[] getTenantIds(String tableName) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return new Object[]{DEFAULT_TENANT_ID};
        } else {
            return new Object[]{tenantId};
        }
    }

}
