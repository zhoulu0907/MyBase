package com.cmsr.onebase.module.infra.framework.file.config;

import com.cmsr.onebase.module.infra.framework.file.core.client.FileClientFactory;
import com.cmsr.onebase.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 */
@Configuration(proxyBeanMethods = false)
public class YudaoFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
