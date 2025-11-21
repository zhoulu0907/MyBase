package com.cmsr.onebase.framework.base.mybatis;

import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.mybatisflex.core.tenant.TenantFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DefaultTenantFactory implements TenantFactory {

    private final static Set<String> TENANT_IGNORES;

    private final static long IGNORED_TENANT = 1L;

    static {
        TENANT_IGNORES = new HashSet<>();
        TENANT_IGNORES.add("etl_flink_mapping");
        TENANT_IGNORES.add("etl_flink_function");
        TENANT_IGNORES.add("flow_node_category");
        TENANT_IGNORES.add("flow_node_type");
    }


    @Override
    public Object[] getTenantIds() {
        List<Long> tenantIds = new ArrayList<>();
        // Add tenant conditions
        tenantIds.add(TenantContextHolder.getTenantId());

        return tenantIds.toArray();
    }

    @Override
    public Object[] getTenantIds(String tableName) {
        if (TenantContextHolder.isIgnore() ||
                ignoredRule(tableName)) {
            return new Object[]{IGNORED_TENANT};
        }

        return this.getTenantIds();
    }

    private boolean ignoredRule(String tableName) {
        return tableName.toLowerCase().startsWith("system") || TENANT_IGNORES.contains(tableName.toLowerCase());
    }
}
