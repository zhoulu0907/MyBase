package com.cmsr.onebase.module.infra.framework.file.config;

import com.cmsr.onebase.framework.common.config.FileUploadSecurityProperties;
import com.cmsr.onebase.framework.common.util.file.FileValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * 文件配置类
 *
 * <p>负责：
 * <ul>
 *   <li>启用 FileUploadSecurityProperties 配置属性</li>
 *   <li>将配置注入到 FileValidateUtil 工具类</li>
 * </ul>
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FileUploadSecurityProperties.class)
@Slf4j
public class OneBaseFileAutoConfiguration {

    /**
     * 应用启动后，将配置注入到 FileValidateUtil
     *
     * <p>使用 EventListener 确保在所有 Bean 初始化完成后注入，
     * 避免 Bean 初始化顺序问题。</p>
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationReady(ContextRefreshedEvent event) {
        FileUploadSecurityProperties properties = event.getApplicationContext()
                .getBean(FileUploadSecurityProperties.class);

        FileValidateUtil.setProperties(properties);
        log.info("[OneBaseFileAutoConfiguration] 文件上传安全配置已注入，enabled={}, 自定义白名单大小={}",
                properties.isEnabled(),
                properties.getAllowedExtensions() != null ? properties.getAllowedExtensions().size() : 0);
    }

}