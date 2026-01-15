package com.cmsr.onebase.plugin.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OneBase 插件宿主模拟器
 * <p>
 * 融合了快速调试和完整验证两种模式的插件宿主模拟器。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-14
 */
@SpringBootApplication(excludeName = {
        "org.anyline.environment.spring.data.driver.actuator.jdbc.SpringJDBCActuator",
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
public class PluginHostSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PluginHostSimulatorApplication.class, args);
    }
}
