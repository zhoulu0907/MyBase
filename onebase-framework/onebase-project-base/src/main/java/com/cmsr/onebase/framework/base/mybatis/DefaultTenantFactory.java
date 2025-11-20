package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.mybatisflex.core.tenant.TenantFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultTenantFactory implements TenantFactory {

    private final static List<String> TENANT_IGNORES;

    private final static long IGNORED_TENANT = -1L;

    static {
        TENANT_IGNORES = new ArrayList<>();
        TENANT_IGNORES.add("system_tenant");
    }


    @Override
    public Object[] getTenantIds() {
        List<Long> tenantIds = new ArrayList<>();
        tenantIds.add(IGNORED_TENANT);
        // Add tenant conditions
        tenantIds.add(TenantContextHolder.getRequiredTenantId());

        return tenantIds.toArray();
    }

    @Override
    public Object[] getTenantIds(String tableName) {
        if (TENANT_IGNORES.contains(tableName)) {
            return new Object[]{IGNORED_TENANT};
        }

        return this.getTenantIds();
    }
}
