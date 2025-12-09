# OneBase 插件系统

OneBase插件系统基于PF4J实现，为低代码平台提供Java高代码扩展能力。

## 模块结构

```
onebase-framework/
├── onebase-plugin-sdk/          # 插件开发SDK
├── onebase-plugin-maven-plugin/ # Maven打包插件
└── onebase-plugin-runtime/      # 插件运行时
```

## 快速开始

### 1. 创建插件项目

```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <version>${onebase.version}</version>
    <scope>provided</scope>
</dependency>
```

### 2. 实现扩展点

#### 自定义函数
```java
@Extension
public class MyFunction implements CustomFunction {
    @Override
    public String name() { return "MY_FUNCTION"; }
    
    @Override
    public Object execute(PluginContext context, Object... args) {
        // 业务逻辑
        return result;
    }
}
```

#### 数据处理器
```java
@Extension
public class MyProcessor implements DataProcessor {
    @Override
    public String type() { return "my-processor"; }
    
    @Override
    public Object process(PluginContext context, Object input, Map<String, Object> config) {
        // 处理逻辑
        return output;
    }
}
```

#### 事件监听器
```java
@Extension
public class MyListener implements EventListener {
    @Override
    public String[] eventTypes() { return new String[]{"data.created"}; }
    
    @Override
    public void onEvent(PluginContext context, PluginEvent event) {
        // 处理事件
    }
}
```

#### HTTP处理器
```java
@Extension
public class MyHandler implements HttpHandler {
    @Override
    public String pathPattern() { return "/custom/api"; }
    
    @Override
    public HttpResponse handle(PluginContext context, HttpRequest request) {
        return HttpResponse.ok("{\"status\":\"ok\"}");
    }
}
```

### 3. 配置Maven打包

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.cmsr.onebase</groupId>
            <artifactId>onebase-plugin-maven-plugin</artifactId>
            <version>${onebase.version}</version>
            <configuration>
                <pluginId>my-plugin</pluginId>
                <pluginVersion>1.0.0</pluginVersion>
                <pluginDescription>我的插件</pluginDescription>
                <pluginProvider>开发者</pluginProvider>
                <generatedPackage>com.example.plugin</generatedPackage>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-properties</goal>
                        <goal>generate-plugin-class</goal>
                        <goal>scan-extensions</goal>
                        <goal>generate-spi</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 4. 打包部署

```bash
mvn clean package
```

将生成的JAR文件放入平台的`plugins`目录即可。

## 平台服务

插件可通过`PluginContext`访问以下平台服务：

| 服务 | 接口 | 说明 |
|------|------|------|
| 数据服务 | DataService | CRUD操作、SQL查询 |
| 用户服务 | UserService | 用户信息、权限校验 |
| 文件服务 | FileService | 文件上传、下载 |
| 缓存服务 | CacheService | Redis缓存操作 |

示例：
```java
public Object execute(PluginContext context, Object... args) {
    // 获取数据服务
    DataService dataService = context.getDataService();
    
    // 查询数据
    Map<String, Object> user = dataService.getById("user", 1L);
    
    // 获取当前用户
    UserService userService = context.getUserService();
    UserInfo currentUser = userService.getCurrentUser();
    
    return result;
}
```

## REST API

插件管理API：

| 接口 | 方法 | 说明 |
|------|------|------|
| /plugin/list | GET | 获取插件列表 |
| /plugin/functions | GET | 获取自定义函数列表 |
| /plugin/functions/{name}/execute | POST | 执行自定义函数 |
| /plugin/{id}/start | POST | 启动插件 |
| /plugin/{id}/stop | POST | 停止插件 |
| /plugin/{id}/reload | POST | 重新加载插件 |

## 配置项

```properties
# 是否启用插件系统
onebase.plugin.enabled=true

# 插件目录路径
onebase.plugin.plugins-dir=plugins

# 是否自动加载插件
onebase.plugin.auto-load=true

# 是否自动启动插件
onebase.plugin.auto-start=true
```

## 开发建议

1. **SDK封装**：SDK已封装PF4J细节，开发者只需关注业务接口
2. **依赖管理**：SDK设为`provided`作用域，避免依赖冲突
3. **热部署**：支持运行时加载/卸载插件，无需重启平台
4. **隔离性**：每个插件独立类加载器，互不干扰
5. **服务注入**：通过PluginContext获取平台服务，避免直接依赖
