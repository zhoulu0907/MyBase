package com.cmsr.onebase.framework.license.config;

import com.cmsr.onebase.framework.license.core.filter.LicenseCheckFilter;
import com.cmsr.onebase.framework.license.core.handler.LicenseCheckHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class OneBaseLicenseConfiguration {

    /**
     * Token 认证过滤器 Bean`
     */
    @Bean
    public LicenseCheckFilter licenseCheckFilter() {
        return new LicenseCheckFilter();
    }


    @Bean
    public LicenseCheckHandler licenseCheckHandler() {
        return new LicenseCheckHandler();
    }

}
