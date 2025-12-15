package com.cmsr.onebase.plugin.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Demo插件调试启动器
 * <p>
 * 用于在开发IDE中直接调试demo插件，无需打包和部署到宿主
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-14
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.cmsr.onebase.plugin.runtime",
    "com.cmsr.onebase.plugin.demo"
})
public class PluginDebugLauncher {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PluginDebugLauncher.class);
        app.setAdditionalProfiles("debug");
        app.run(args);
    }
}
