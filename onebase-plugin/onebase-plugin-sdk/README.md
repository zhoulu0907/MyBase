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

OneBase 插件系统提供以下三个核心扩展点：

### 1. 数据处理器（DataProcessor）

用于实现数据的 ETL、转换、脱敏等处理逻辑，在数据流处理、数据导入等场景中使用。

```java
import org.pf4j.Extension;
import com.cmsr.onebase.plugin.api.DataProcessor;
import com.cmsr.onebase.plugin.context.PluginContext;

@Extension
public class MyDataProcessor implements DataProcessor {
    
    @Override
    public String name() {
        return "my-processor";
    }
    
    @Override
    public String description() {
        return "我的数据处理器";
    }
    
    @Override
    public Object process(PluginContext context, Object input, Map<String, Object> config) {
        // 在这里实现数据处理逻辑
        // input: 输入的数据对象
        // config: 处理器的配置参数
        return processedData;
    }
}
```

### 2. 事件监听器（EventListener）

用于监听平台事件（如数据创建、更新、删除等），实现业务流程的自动化和联动。

```java
import org.pf4j.Extension;
import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.event.PluginEvent;

@Extension
public class MyEventListener implements EventListener {
    
    @Override
    public String[] eventTypes() {
        // 指定要监听的事件类型
        return new String[]{"data.created", "data.updated", "data.deleted"};
    }
    
    @Override
    public int order() {
        // 监听器执行顺序，数字越小越优先
        return 0;
    }
    
    @Override
    public void onEvent(PluginContext context, PluginEvent event) {
        // 处理事件逻辑
        String eventType = event.getEventType();
        Map<String, Object> eventData = event.getData();
        
        // 可以通过 context 访问平台服务
        // 例如：发送通知、记录日志、触发其他操作等
    }
}
```

### 3. HTTP 处理器（HttpHandler）

用于暴露自定义 REST API 接口，实现插件与前端或外部系统的交互。

> 📌 **路由约定**：所有插件 HTTP 处理器的路由必须遵循 `/plugin/{pluginId}/` 前缀规范。

```java
import org.pf4j.Extension;
import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.context.PluginContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Extension
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

## 🔌 平台服务访问

插件可通过 `PluginContext` 访问以下平台服务：

### 数据服务（DataService）

```java
@Override
public void onEvent(PluginContext context, PluginEvent event) {
    DataService dataService = context.getService(DataService.class);
    
    // 查询数据
    Map<String, Object> record = dataService.query("table_name", conditions);
    
    // 保存数据
    Long recordId = dataService.save("table_name", data);
}
```

### 用户服务（UserService）

```java
@Override
public Object process(PluginContext context, Object input, Map<String, Object> config) {
    UserService userService = context.getService(UserService.class);
    
    // 获取当前用户
    String userId = context.getUserId();
    
    // 查询用户信息
    UserInfo user = userService.getUserById(userId);
    
    return user;
}
```

### 缓存服务（CacheService）

```java
@Override
public void onEvent(PluginContext context, PluginEvent event) {
    CacheService cacheService = context.getService(CacheService.class);
    
    // 设置缓存
    cacheService.set("cache_key", value, 3600);
    
    // 获取缓存
    Object cachedValue = cacheService.get("cache_key");
}
```

### 文件服务（FileService）

```java
@Override
public Object process(PluginContext context, Object input, Map<String, Object> config) {
    FileService fileService = context.getService(FileService.class);
    
    // 上传文件
    String fileUrl = fileService.upload("file_path", bytes);
    
    // 下载文件
    byte[] fileContent = fileService.download(fileUrl);
    
    return fileUrl;
}
```

## 🔐 权限和上下文

### 租户隔离

插件在各自的租户上下文中运行：

```java
@Override
public void onEvent(PluginContext context, PluginEvent event) {
    String tenantId = context.getTenantId();
    String userId = context.getUserId();
    
    // 所有数据操作都自动在当前租户下进行
    DataService dataService = context.getService(DataService.class);
    // 此查询只会返回当前租户的数据
}
```

## 📋 开发最佳实践

### 1. 异常处理

避免吞掉异常，允许异常向上传播由框架处理：

```java
@Override
public Object process(PluginContext context, Object input, Map<String, Object> config) 
    throws Exception {
    // ✅ 推荐：抛出异常让框架处理
    if (invalid) {
        throw new IllegalArgumentException("Invalid input");
    }
    
    // ❌ 避免：吞掉异常
    try {
        // ...
    } catch (Exception e) {
        e.printStackTrace();  // 不要这样做
        return null;
    }
}
```

### 2. 避免状态保存

插件应该是无状态的，不要在字段中保存状态：

```java
@Extension
public class MyProcessor implements DataProcessor {
    // ❌ 避免：在字段中保存状态
    private int counter = 0;
    
    @Override
    public Object process(PluginContext context, Object input, Map<String, Object> config) {
        counter++;  // 线程不安全且会导致状态污染
        return input;
    }
}
```

### 3. 使用 @Extension 标记

确保所有扩展点实现类都标记了 `@Extension` 注解：

```java
// ✅ 正确
@Extension
public class MyListener implements EventListener {
    // ...
}

// ❌ 错误：缺少 @Extension 注解，无法被扫描
public class MyListener implements EventListener {
    // ...
}
```

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

在 `application.properties` 或 `application.yml` 中配置插件系统：

```properties
# 是否启用插件系统（默认：true）
# enabled=true: 加载并启动插件
# enabled=false: 不加载也不启动插件
onebase.plugin.enabled=true

# 插件目录（默认：plugins）
onebase.plugin.plugins-dir=plugins
```

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

