package com.cmsr.onebase.module.system.service.auth;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 灵畿 SSO 配置类
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LingjiSsoProperties.class)
public class LingjiSsoConfiguration {
}