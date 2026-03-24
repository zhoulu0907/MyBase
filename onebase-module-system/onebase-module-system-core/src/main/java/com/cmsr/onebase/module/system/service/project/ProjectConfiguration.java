package com.cmsr.onebase.module.system.service.project;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 项目功能配置类
 *
 * @author claude
 * @date 2026-03-23
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ProjectProperties.class)
public class ProjectConfiguration {
}