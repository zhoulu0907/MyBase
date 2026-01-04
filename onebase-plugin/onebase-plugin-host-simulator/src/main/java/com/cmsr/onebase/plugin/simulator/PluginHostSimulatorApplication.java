package com.cmsr.onebase.plugin.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * OneBase 插件宿主模拟器
 * <p>
 * 融合了快速调试和完整验证两种模式的插件宿主模拟器。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-14
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.cmsr.onebase.plugin.simulator"})
public class PluginHostSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PluginHostSimulatorApplication.class, args);
    }
}
