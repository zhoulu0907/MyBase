# OneBase 插件SDK

OneBase 插件系统基于 PF4J 框架实现，为低代码平台提供灵活的 Java 高代码扩展能力。`onebase-plugin-sdk` 是插件开发的核心模块，提供扩展点接口、上下文管理和平台服务访问能力。

## 📦 快速开始

### 1. 引入依赖

在插件项目的 `pom.xml` 中添加 SDK 依赖：

```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <version>${onebase.version}</version>
    <scope>provided</scope>
</dependency>
```

> ⚠️ **重要**：`scope` 必须设为 `provided`，SDK 由平台提供，插件包中不应包含 SDK。

### 2. 配置 Maven 插件

在同一个 `pom.xml` 的 `<build>` 部分添加以下配置：

```xml
<build>
    <plugins>
        <!-- OneBase 插件 Maven 插件 -->
        <plugin>
            <groupId>com.cmsr.onebase</groupId>
            <artifactId>onebase-plugin-maven-plugin</artifactId>
            <version>${onebase.version}</version>
            <executions>
                <!-- 生成插件入口类 -->
                <execution>
                    <id>generate-plugin-class</id>
                    <goals>
                        <goal>generate-plugin-class</goal>
                    </goals>
                </execution>
                <!-- 生成插件属性文件 -->
                <execution>
                    <id>generate-properties</id>
                    <goals>
                        <goal>generate-properties</goal>
                    </goals>
                </execution>
                <!-- 扫描并注册扩展点实现 -->
                <execution>
                    <id>scan-extensions</id>
                    <goals>
                        <goal>scan-extensions</goal>
                    </goals>
                </execution>
                <!-- 打包插件 -->
                <execution>
                    <id>package-plugin</id>
                    <goals>
                        <goal>package-plugin</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <pluginId>my-plugin</pluginId>
                <pluginVersion>${project.version}</pluginVersion>
                <pluginDescription>插件描述信息</pluginDescription>
                <pluginProvider>开发者或组织名称</pluginProvider>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 🎯 扩展点实现

OneBase 插件系统提供以下核心扩展点：

### 1. HTTP 处理器（HttpHandler）

用于暴露自定义 REST API 接口，实现插件与前端或外部系统的交互。

> 📌 **路由约定**：所有插件 HTTP 处理器的路由必须遵循 `/plugin/{pluginId}/` 前缀规范。

```java
import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.context.PluginContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/plugin/my-plugin")
public class MyHttpHandler implements HttpHandler {
    
    /**
     * 获取处理器信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return Map.of(
            "name", "My Plugin",
            "version", "1.0.0",
            "status", "active"
        );
    }
    
    /**
     * 处理业务请求
     */
    @PostMapping("/process")
    public Map<String, Object> processData(@RequestBody Map<String, Object> request) {
        // 处理请求并返回结果
        return Map.of("success", true, "data", result);
    }
}
```

> 🔍 **实现细节**：
> - `HttpHandler` 现在是纯标记接口，用于 PF4J 扫描识别
> - 实际的 HTTP 处理由标准 Spring MVC 完成
> - 开发时使用 `@RestController` 和 Spring 注解进行路由定义
> - Maven 插件在构建时验证所有路由是否遵循 `/plugin/{pluginId}/` 前缀
> - 运行时通过 `PluginSecurityInterceptor` 二次校验路由合规性

### 4. 依赖管理

在 `pom.xml` 中管理插件自身的依赖（除 SDK 外）：

```xml
<!-- ✅ 推荐：依赖 SDK（作用域为 provided） -->
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <scope>provided</scope>
</dependency>

<!-- ✅ 推荐：依赖其他库（会被打入插件包） -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

## 🚀 构建和部署

### 构建插件

```bash
mvn clean package
```

构建完成后，在 `target` 目录下生成 `your-plugin-id-version.zip` 文件。

### 部署插件

1. **通过平台管理界面上传**：在 OneBase 平台的插件管理页面上传 ZIP 文件
2. **放置到插件目录**：或直接将 ZIP 文件放入平台配置的插件目录

### 插件生命周期

```
加载（load） → 启动（start） → 运行 → 停止（stop） → 卸载（unload）
```

插件支持热加载和卸载，无需重启平台。

## ⚙️ 配置参考

在 `application.yml` 中配置插件系统：

## 📚 相关文档

- [onebase-plugin-demo](../onebase-plugin-demo/) - 完整的插件开发示例
- [onebase-plugin-maven-plugin](../onebase-plugin-maven-plugin/) - Maven 插件构建工具
- [onebase-spring-boot-starter-plugin](../onebase-spring-boot-starter-plugin/) - 插件运行时配置

## 💡 常见问题

**Q: SDK 作用域为什么要设为 `provided`？**  
A: SDK 由平台在运行时提供，插件包中不应包含 SDK，避免版本冲突和包体积浪费。

**Q: 插件如何调用平台的服务？**  
A: 通过方法参数中的 `PluginContext` 对象，调用 `context.getService(ServiceClass.class)` 获取服务实例。

**Q: 多个插件之间能相互调用吗？**  
A: 不推荐。插件间应通过平台提供的统一接口（如 REST API）进行通信，保持松耦合。

**Q: 插件可以依赖外部库吗？**  
A: 可以。外部库依赖会被打入插件 ZIP 包中，运行时由插件的独立类加载器加载。

**Q: 插件的 HTTP 路由有限制吗？**  
A: 有。所有插件 HTTP 处理器的路由必须以 `/plugin/{pluginId}/` 开头，这是为了避免路由冲突和便于权限管理。

