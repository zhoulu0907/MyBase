package com.cmsr.onebase.framework.tenant.config;

import com.cmsr.onebase.framework.tenant.core.rpc.TenantRequestInterceptor;
import com.cmsr.onebase.framework.common.biz.system.tenant.TenantCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "onebase.tenant", value = "enable", matchIfMissing = true)
// @EnableFeignClients(clients = TenantCommonApi.class) // 主要是引入相关的 API 服务
public class OneBaseTenantRpcAutoConfiguration {

    @Bean
    public TenantRequestInterceptor tenantRequestInterceptor() {
        return new TenantRequestInterceptor();
    }

}
