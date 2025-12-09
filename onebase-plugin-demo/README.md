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
│           ├── function/            # 自定义函数实现
│           │   ├── StringFunctions.java
│           │   └── MathFunctions.java
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

#### 自定义函数

```java
@Extension
public class MyFunction implements CustomFunction {
    @Override
    public String name() {
        return "MY_FUNC";
    }

    @Override
    public Object execute(Map<String, Object> params) {
        // 实现函数逻辑
        return result;
    }
}
```

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

```java
@Extension
public class MyHandler implements HttpHandler {
    @Override
    public String path() {
        return "/api/plugin/my-api";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        // 处理HTTP请求
        return HttpResponse.ok(result);
    }
}
```

### 4. 使用平台服务

通过`PluginContextHolder`获取上下文，访问平台提供的服务：

```java
var context = PluginContextHolder.getContext();

// 获取当前用户信息
String userId = context.getUserId();
String tenantId = context.getTenantId();

// 使用数据服务
DataService dataService = context.getService(DataService.class);
List<Map<String, Object>> results = dataService.query("table_name", conditions);

// 使用缓存服务
CacheService cacheService = context.getService(CacheService.class);
cacheService.set("key", value, 3600);

// 使用文件服务
FileService fileService = context.getService(FileService.class);
String fileUrl = fileService.upload("path", bytes);

// 使用用户服务
UserService userService = context.getService(UserService.class);
UserInfo user = userService.getUserById(userId);
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
| 自定义函数 | `CustomFunction` | 在公式、规则中使用的自定义函数 |
| 数据处理器 | `DataProcessor` | 数据的ETL、转换、脱敏等处理 |
| 事件监听器 | `EventListener` | 监听平台事件，实现业务联动 |
| HTTP处理器 | `HttpHandler` | 暴露自定义REST API接口 |

## 本示例包含的功能

### 自定义函数
- **STR_UTILS**: 字符串工具函数（格式化、截取、替换、反转）
- **MATH_CALC**: 数学计算函数（加减乘除、百分比、四舍五入）

### 数据处理器
- **DATA_MASK**: 数据脱敏（手机号、身份证、邮箱、银行卡、姓名、地址）
- **DATA_AGGREGATE**: 数据聚合（分组、求和、计数、平均值、最大最小值）

### 事件监听器
- **DataChangeListener**: 监听数据变更事件（创建、更新、删除）
- **UserBehaviorListener**: 监听用户行为事件（登录、登出、权限变更）

### HTTP接口
- **GET /api/plugin/demo**: 获取插件信息
- **GET /api/plugin/demo?action=status**: 获取插件状态
- **GET /api/plugin/demo?action=stats**: 获取统计信息
- **POST /api/plugin/demo?action=query**: 执行数据查询
- **POST /api/plugin/demo?action=process**: 处理数据

## 注意事项

1. SDK依赖的`scope`必须设置为`provided`，避免打包时包含SDK
2. 使用`@Extension`注解标记扩展点实现类
3. 插件应该是无状态的，避免使用实例变量存储状态
4. 使用`PluginContextHolder`获取上下文，不要自己创建服务实例
5. 异常应该被正确处理，避免影响平台稳定性
