package com.cmsr.onebase.framework.security.runtime.config;

import com.cmsr.onebase.framework.security.runtime.permission.filter.AppPermissionCheckFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class OneBaseRuntimeConfiguration {

    /**
     * Token 认证过滤器 Bean`
     */
    @Bean
    public AppPermissionCheckFilter appPermissionCheckFilter() {
        return new AppPermissionCheckFilter();
    }

}
