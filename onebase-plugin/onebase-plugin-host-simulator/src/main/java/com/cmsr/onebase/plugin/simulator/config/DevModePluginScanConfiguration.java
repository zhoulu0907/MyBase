package com.cmsr.onebase.plugin.simulator.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 开发模式下的插件组件扫描配置
 * <p>
 * 在dev profile下，额外扫描插件demo包，以便在IDE中直接调试插件功能。
 * 在staging和prod模式下，插件应通过插件系统加载，不应在此处扫描。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-19
 */
@Configuration
@Profile("dev")  // 只在dev模式下生效
@ComponentScan(basePackages = {
    "com.cmsr.onebase.plugin.demo"  // 插件demo包，用于开发模式下扫描插件组件
})
public class DevModePluginScanConfiguration {
    
    // 此配置类专门用于在dev模式下扫描插件组件
    // 在staging和prod模式下不会生效
    
}