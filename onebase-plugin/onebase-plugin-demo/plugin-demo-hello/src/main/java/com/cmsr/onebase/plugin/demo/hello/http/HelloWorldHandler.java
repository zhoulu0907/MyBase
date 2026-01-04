package com.cmsr.onebase.plugin.demo.hello.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HelloWorld HTTP接口示例（新模式）
 * <p>
 * 使用标准Spring Controller方式开发插件HTTP接口：
 * <ul>
 *   <li>实现 {@link HttpHandler} 标记接口</li>
 *   <li>添加 {@code @RestController} 和 {@code @RequestMapping} 注解</li>
 *   <li>路由必须以 {@code /plugin/hello-plugin/} 开头</li>
 *   <li>可直接使用Spring MVC的所有特性</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@RestController
@Slf4j
@RequestMapping("/plugin/hello-plugin")
public class HelloWorldHandler implements HttpHandler {

    /**
     * Hello World接口
     * <p>访问路径：GET /plugin/hello-plugin/hello?name=xxx</p>
     */
    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(defaultValue = "World") String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "hello, " + name + "!");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.put("plugin", "hello-plugin-00135");
        
        // 动态检测加载方式
        String loadSource = detectLoadSource();
        result.put("loadSource", loadSource);
        result.put("version", getVersionBySource(loadSource));
        
        return result;
    }

    /**
     * 处理 JSON 数据接口（测试 @RequestBody）
     * <p>访问路径：POST /plugin/hello-plugin/process</p>
     * <p>请求体示例：{"name": "OneBase", "value": 100}</p>
     */
    @PostMapping("/process")
    public Map<String, Object> processData(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("received", data);
        result.put("size", data.size());
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.put("plugin", "hello-plugin");
        result.put("message", "数据处理成功");
        return result;
    }
    
    /**
     * 检测插件加载来源
     * 通过检查类加载器和资源路径来判断
     * <p>
     * 关键区分点：
     * <ul>
     *   <li>PluginClassLoader：PF4J动态加载的ZIP包插件</li>
     *   <li>BOOT-INF/lib/*.jar：Spring Boot打包的classpath依赖</li>
     *   <li>外部ZIP/JAR且非BOOT-INF：真正的ZIP包插件</li>
     *   <li>其他：纯classpath加载（DEV模式IDE直接运行）</li>
     * </ul>
     * </p>
     */
    private String detectLoadSource() {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            String className = classLoader.getClass().getName();
            log.info("[HelloWorldHandler] 检测加载来源 - ClassLoader类型: {}", className);
            
            // PF4J的PluginClassLoader表示从ZIP/JAR加载
            if (className.contains("PluginClassLoader")) {
                log.info("[HelloWorldHandler] 检测结果: ZIP_PACKAGE (PluginClassLoader)");
                return "ZIP_PACKAGE";
            }
            
            // 检查资源路径
            String resourcePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            log.info("[HelloWorldHandler] 检测加载来源 - 资源路径: {}", resourcePath);
            
            // 如果在BOOT-INF中，说明是Spring Boot打包的依赖，属于classpath方式
            if (resourcePath.contains("BOOT-INF")) {
                log.info("[HelloWorldHandler] 检测结果: CLASSPATH_DIRECT (BOOT-INF依赖)");
                return "CLASSPATH_DIRECT";
            }
            
            // 如果在Maven本地仓库(.m2/repository)或target/classes，也属于classpath方式
            if (resourcePath.contains(".m2/repository") || resourcePath.contains("target/classes") || resourcePath.contains("target\\classes")) {
                log.info("[HelloWorldHandler] 检测结果: CLASSPATH_DIRECT (Maven依赖或编译输出)");
                return "CLASSPATH_DIRECT";
            }
            
            // 如果包含.zip/.jar且不在上述目录，则是真正的ZIP包加载
            if (resourcePath.contains(".zip") || resourcePath.contains(".jar")) {
                log.info("[HelloWorldHandler] 检测结果: ZIP_PACKAGE (外部JAR/ZIP)");
                return "ZIP_PACKAGE";
            }
            
            // 默认是从classpath加载（DEV模式）
            log.info("[HelloWorldHandler] 检测结果: CLASSPATH_DIRECT (默认classpath)");
            return "CLASSPATH_DIRECT";
        } catch (Exception e) {
            log.error("[HelloWorldHandler] 检测加载来源失败: {}", e.getMessage());
            return "CLASSPATH_DIRECT";
        }
    }
    
    /**
     * 根据加载来源返回不同的版本标识
     */
    private String getVersionBySource(String source) {
        if ("ZIP_PACKAGE".equals(source)) {
            return "1.0.0-FROM-ZIP";
        }
        return "1.0.0-FROM-CLASSPATH";
    }
}

