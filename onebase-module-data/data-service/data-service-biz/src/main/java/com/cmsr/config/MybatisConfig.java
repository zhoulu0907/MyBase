package com.cmsr.config;


import com.cmsr.commons.utils.MybatisInterceptorConfig;
import com.cmsr.datasource.dao.auto.entity.CoreDatasource;
import com.cmsr.datasource.dao.auto.entity.CoreDeEngine;
import com.cmsr.interceptor.MybatisInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class MybatisConfig {

    @Bean
    @ConditionalOnMissingBean
    public MybatisInterceptor dbInterceptor() {
        MybatisInterceptor interceptor = new MybatisInterceptor();
        List<MybatisInterceptorConfig> configList = new ArrayList<>();
        configList.add(new MybatisInterceptorConfig(CoreDeEngine.class, "configuration"));
        configList.add(new MybatisInterceptorConfig(CoreDatasource.class, "configuration"));
        interceptor.setInterceptorConfigList(configList);
        return interceptor;
    }
}
