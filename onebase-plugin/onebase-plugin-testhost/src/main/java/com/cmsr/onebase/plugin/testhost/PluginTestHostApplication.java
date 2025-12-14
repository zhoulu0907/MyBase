package com.cmsr.onebase.plugin.testhost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

/**
 * OneBase 插件系统测试宿主服务器
 * <p>
 * 启动此应用，用于测试插件的加载、卸载、启动、停止等生命周期管理。
 * </p>
 * 
 * <h3>启动方式</h3>
 * <ul>
 *     <li>命令行：{@code mvn spring-boot:run}</li>
 *     <li>IDE：右键运行此类的 main 方法</li>
 *     <li>打包：{@code mvn clean package} 后执行 {@code java -jar onebase-plugin-testhost-1.0.0-SNAPSHOT.jar}</li>
 * </ul>
 * 
 * <h3>访问地址</h3>
 * <ul>
 *     <li>应用首页：{@code http://localhost:8080}</li>
 *     <li>插件管理API：{@code http://localhost:8080/plugin/list}</li>
 *     <li>插件信息：{@code http://localhost:8080/plugin/demo-plugin/api/info}</li>
 * </ul>
 * 
 * <h3>插件目录</h3>
 * <p>
 * 将编译好的插件 ZIP 包放入项目根目录的 {@code plugins} 文件夹中，
 * 应用启动时会自动加载和启动插件。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class PluginTestHostApplication {

    public static void main(String[] args) {
        SpringApplication.run(PluginTestHostApplication.class, args);
    }
}
