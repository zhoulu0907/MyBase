# OneBase插件开发示例

本项目演示如何使用OneBase插件SDK开发低代码平台的扩展插件。

## 项目结构

```
onebase-plugin-demo/
├── pom.xml                          # Maven配置，引入SDK和Maven插件
├── src/
│   ├── assembly/
│   │   └── plugin.xml               # 插件打包配置
│   └── main/java/
│       └── com/cmsr/onebase/plugin/demo/
│           ├── processor/           # 数据处理器实现
│           │   ├── DataMaskProcessor.java
│           │   └── DataAggregationProcessor.java
│           ├── listener/            # 事件监听器实现
│           │   ├── DataChangeListener.java
│           │   └── UserBehaviorListener.java
│           └── http/                # HTTP接口处理器
│               └── CustomApiHandler.java
```

## 快速开始

### 1. 引入SDK依赖

```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### 2. 配置Maven插件

```xml
<plugin>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>generate-properties</id>
            <goals>
                <goal>generate-properties</goal>
            </goals>
            <configuration>
                <pluginId>your-plugin-id</pluginId>
                <pluginVersion>${project.version}</pluginVersion>
                <pluginDescription>插件描述</pluginDescription>
            </configuration>
        </execution>
        <execution>
            <id>generate-plugin-class</id>
            <goals>
                <goal>generate-plugin-class</goal>
            </goals>
        </execution>
        <execution>
            <id>scan-extensions</id>
            <goals>
                <goal>scan-extensions</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 3. 实现扩展点

#### 数据处理器

```java
@Extension
public class MyProcessor implements DataProcessor {
    @Override
    public String name() {
        return "MY_PROCESSOR";
    }

    @Override
    public List<Map<String, Object>> process(List<Map<String, Object>> data, Map<String, Object> config) {
        // 实现数据处理逻辑
        return processedData;
    }
}
```

#### 事件监听器

```java
@Extension
public class MyListener implements EventListener {
    @Override
    public String[] eventTypes() {
        return new String[]{"data.created", "data.updated"};
    }

    @Override
    public void onEvent(PluginEvent event) {
        // 处理事件
    }
}
```

#### HTTP接口处理器

HTTP处理器使用标准Spring MVC `@RestController` 注解实现。所有路由必须以 `/plugin/{pluginId}/` 开头。

```java
@Extension
@RestController
@RequestMapping("/plugin/demo-plugin/api")
public class CustomApiHandler implements HttpHandler {
    
    /**
     * 获取插件信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("plugin", "demo-plugin");
        info.put("version", "1.0.0");
        info.put("status", "running");
        return info;
    }
    
    /**
     * 处理数据
     */
    @PostMapping("/process")
    public Map<String, Object> processData(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("receivedData", data);
        return result;
    }
}
```

> 🔗 **访问方式**：`GET /plugin/demo-plugin/api/info`、`POST /plugin/demo-plugin/api/process`

### 4. 访问平台服务

在扩展点实现中，通过 `PluginContext` 参数访问平台提供的服务：

```java
// 在 DataProcessor 中
@Override
public Object process(PluginContext context, Object input, Map<String, Object> config) {
    // 获取数据服务
    DataService dataService = context.getService(DataService.class);
    List<Map<String, Object>> results = dataService.query("table_name", conditions);
    
    // 获取用户服务
    UserService userService = context.getService(UserService.class);
    String userId = context.getUserId();
    String tenantId = context.getTenantId();
    
    // 获取缓存服务
    CacheService cacheService = context.getService(CacheService.class);
    cacheService.set("key", value, 3600);
    
    // 获取文件服务
    FileService fileService = context.getService(FileService.class);
    String fileUrl = fileService.upload("path", bytes);
    
    return processedData;
}
```

### 5. 构建插件

```bash
mvn clean package
```

构建完成后，在`target`目录下生成`onebase-plugin-demo-1.0.0.zip`插件包。

### 6. 部署插件

将插件ZIP包上传到OneBase平台的插件管理页面，或直接放置到平台的`plugins`目录下。

## 扩展点说明

| 扩展点 | 接口 | 用途 |
|--------|------|------|
| 数据处理器 | `DataProcessor` | 数据的ETL、转换、脱敏等处理 |
| 事件监听器 | `EventListener` | 监听平台事件，实现业务联动 |
| HTTP处理器 | `HttpHandler` | 暴露自定义REST API接口 |

## 本示例包含的功能

### 数据处理器
- **DATA_MASK**: 数据脱敏（手机号、身份证、邮箱、银行卡、姓名、地址）
- **DATA_AGGREGATE**: 数据聚合（分组、求和、计数、平均值、最大最小值）

### 事件监听器
- **DataChangeListener**: 监听数据变更事件（创建、更新、删除）
- **UserBehaviorListener**: 监听用户行为事件（登录、登出、权限变更）

### HTTP接口
- **GET /plugin/demo-plugin/api/info**: 获取插件信息
- **GET /plugin/demo-plugin/api/status**: 获取插件状态
- **POST /plugin/demo-plugin/api/process**: 处理数据

## 开发最佳实践

1. **依赖管理**：SDK依赖的`scope`必须设置为`provided`，避免打包时包含SDK
2. **扩展点标记**：所有扩展点实现类必须使用`@Extension`注解
3. **HTTP路由规范**：HTTP处理器的所有路由必须以`/plugin/{pluginId}/`开头
4. **无状态设计**：插件应该是无状态的，避免使用实例变量存储状态
5. **上下文获取**：通过方法参数中的`PluginContext`获取平台服务，不要自己创建实例
6. **异常处理**：避免吞掉异常，允许异常向上传播由框架处理
7. **注解使用**：HTTP处理器使用标准Spring注解（`@RestController`、`@GetMapping`、`@PostMapping`等）

## 扩展阅读

- [onebase-plugin-sdk](../onebase-plugin-sdk/) - SDK详细文档和API说明
- [onebase-plugin-maven-plugin](../onebase-plugin-maven-plugin/) - Maven插件使用说明
