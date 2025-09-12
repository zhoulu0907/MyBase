package com.cmsr.onebase.module.infra.framework.file.config;

import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 */
@Configuration(proxyBeanMethods = false)
public class OneBaseFileAutoConfiguration {

    // 移除 fileClientFactory Bean 配置，改为使用 @Component 自动扫描

}
