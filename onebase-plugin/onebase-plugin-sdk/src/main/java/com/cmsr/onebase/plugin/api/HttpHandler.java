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
    
    /**
     * 获取当前插件的 ID
     * <p>
     * 默认实现：通过 {@link com.cmsr.onebase.plugin.util.PluginPropertiesUtil} 工具类读取。
     * 使用懒加载缓存机制，同一个类只会读取一次配置文件。
     * 子类可以直接调用此方法获取插件 ID，无需重复实现。
     * </p>
     *
     * @return 插件 ID
     */
    default String getPluginId() {
        return PluginInfoCache.getPluginId(this.getClass());
    }
    
    /**
     * 获取当前插件的版本
     * <p>
     * 默认实现：通过 {@link com.cmsr.onebase.plugin.util.PluginPropertiesUtil} 工具类读取。
     * 使用懒加载缓存机制，同一个类只会读取一次配置文件。
     * 子类可以直接调用此方法获取插件版本，无需重复实现。
     * </p>
     *
     * @return 插件版本
     */
    default String getPluginVersion() {
        return PluginInfoCache.getPluginVersion(this.getClass());
    }
    
    /**
     * 插件信息缓存
     * <p>
     * 使用 ThreadLocal 缓存每个类的插件信息，避免重复读取 plugin.properties 文件。
     * 由于插件信息在运行时不会改变，因此可以安全地缓存。
     * </p>
     */
    class PluginInfoCache {
        private static final java.util.Map<Class<?>, PluginInfo> CACHE = 
            new java.util.concurrent.ConcurrentHashMap<>();
        
        static String getPluginId(Class<?> clazz) {
            return getPluginInfo(clazz).pluginId;
        }
        
        static String getPluginVersion(Class<?> clazz) {
            return getPluginInfo(clazz).pluginVersion;
        }
        
        private static PluginInfo getPluginInfo(Class<?> clazz) {
            return CACHE.computeIfAbsent(clazz, key -> {
                String pluginId = com.cmsr.onebase.plugin.util.PluginPropertiesUtil.getPluginId(key);
                String pluginVersion = com.cmsr.onebase.plugin.util.PluginPropertiesUtil.getPluginVersion(key);
                return new PluginInfo(pluginId, pluginVersion);
            });
        }

        private record PluginInfo(String pluginId, String pluginVersion) { }
    }
}
