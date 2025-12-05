package com.cmsr.onebase.framework.orm.config;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.mybatisflex.core.tenant.TenantFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
            log.warn("未获取到租户编号，使用默认租户编号!");
            return new Object[]{DEFAULT_TENANT_ID};
        } else {
            return new Object[]{tenantId};
        }
    }

}
