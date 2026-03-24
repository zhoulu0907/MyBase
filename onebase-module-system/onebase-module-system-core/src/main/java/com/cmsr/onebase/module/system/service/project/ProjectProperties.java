package com.cmsr.onebase.module.system.service.project;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 项目功能配置属性
 *
 * 配置项前缀：onebase.project
 *
 * YAML 配置示例：
 * <pre>
 * onebase:
 *   project:
 *     enabled: true
 *     auto-create: true
 * </pre>
 */
@ConfigurationProperties(prefix = "onebase.project")
@RefreshScope
@Data
public class ProjectProperties {

    /**
     * 是否启用项目功能
     */
    private boolean enabled = false;

    /**
     * 项目功能启用时，是否自动创建不存在的项目
     */
    private boolean autoCreate = true;

}