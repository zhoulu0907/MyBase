package com.cmsr.onebase.plugin.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * OneBase 插件宿主模拟器
 * <p>
 * 融合了快速调试和完整验证两种模式的插件宿主模拟器。
 * </p>
 * 
 * <h3>双模式支持</h3>
 * <ul>
 *     <li><b>模式1：IDE 快速调试模式（推荐开发使用）</b>
 *         <ul>
 *             <li>启动方式：IDE 中右键 Debug 此类的 main 方法</li>
 *             <li>启动时间：5-10 秒</li>
 *             <li>插件来源：通过 ComponentScan 自动扫描依赖中的插件（如 onebase-plugin-demo）</li>
 *             <li>优势：无需打包，支持断点调试、热重载，快速开发迭代</li>
 *             <li>Profile：使用 'dev' profile</li>
 *         </ul>
 *     </li>
 *     <li><b>模式2：完整验证模式（推荐集成测试使用）</b>
 *         <ul>
 *             <li>启动方式：{@code mvn clean package} 后执行 {@code java -jar onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar}</li>
 *             <li>启动时间：需要打包过程</li>
 *             <li>插件来源：从 plugins 目录加载 ZIP 插件包</li>
 *             <li>优势：完整的插件生命周期验证（加载、启动、停止、卸载）</li>
 *             <li>Profile：使用 'prod' profile</li>
 *         </ul>
 *     </li>
 * </ul>
 * 
 * <h3>访问地址</h3>
 * <ul>
 *     <li>应用首页：{@code http://localhost:8080}</li>
 *     <li>健康检查：{@code http://localhost:8080/health}</li>
 *     <li>插件列表：{@code http://localhost:8080/api/plugins}</li>
 *     <li>插件 HTTP 端点：{@code http://localhost:8080/plugin/{plugin-id}/{path}}</li>
 *     <li>示例：{@code http://localhost:8080/plugin/demo-plugin/hello}</li>
 * </ul>
 * 
 * <h3>配置说明</h3>
 * <ul>
 *     <li>application-dev.yml：开发模式配置（IDE 调试）</li>
 *     <li>application-prod.yml：生产模式配置（ZIP 插件验证）</li>
 * </ul>
 *
 * @author chengyuansen
 * @date 2025-12-14
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@ComponentScan(basePackages = {
    "com.cmsr.onebase.plugin.simulator"   // 仅扫描模拟器自身的组件，避免扫描 starter runtime 包
})
public class PluginHostSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PluginHostSimulatorApplication.class, args);
    }
}
