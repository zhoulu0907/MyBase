package com.cmsr.onebase.plugin.api;

import org.pf4j.ExtensionPoint;

/**
 * 插件HTTP扩展点标记接口
 * <p>
 * 用于标记插件提供的HTTP Controller，便于平台扫描和识别。
 * 实现此接口的类应为标准的Spring {@code @RestController}，遵循Spring MVC规范。
 * </p>
 *
 * <h3>开发规范（强制）</h3>
 * <ol>
 *   <li>实现类必须添加 {@code @RestController} 注解</li>
 *   <li>所有路由路径必须以 {@code /plugin/{pluginId}/} 开头，其中 pluginId 为插件唯一标识</li>
 *   <li>使用标准的Spring MVC注解（如 {@code @GetMapping}、{@code @PostMapping} 等）</li>
 *   <li>可直接使用Spring的依赖注入、参数校验等功能</li>
 * </ol>
 *
 * <h3>使用示例</h3>
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/plugin/demo-plugin/api")
 * public class DemoApiController implements HttpHandler {
 *
 *     @GetMapping("/hello")
 *     public String hello(@RequestParam(defaultValue = "World") String name) {
 *         return "Hello, " + name + "!";
 *     }
 *
 *     @PostMapping("/data")
 *     public Map<String, Object> processData(@RequestBody Map<String, Object> data) {
 *         // 业务处理逻辑
 *         return Map.of("success", true, "data", data);
 *     }
 * }
 * }
 * </pre>
 *
 * <h3>路由访问</h3>
 * <p>
 * 前端统一通过宿主系统访问插件接口，例如：
 * <ul>
 *   <li>{@code GET http://localhost:48080/plugin/demo-plugin/api/hello?name=张三}</li>
 *   <li>{@code POST http://localhost:48080/plugin/demo-plugin/api/data}</li>
 * </ul>
 * </p>
 *
 * <h3>本地调试</h3>
 * <p>
 * 插件可独立启动进行本地调试，路由保持一致即可。
 * 开发完成后，通过 maven package 打包成插件 ZIP 包，上传至宿主系统。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
public interface HttpHandler extends ExtensionPoint {
    // 纯标记接口，无需定义任何方法
    // 插件开发者直接使用Spring MVC注解即可
}
