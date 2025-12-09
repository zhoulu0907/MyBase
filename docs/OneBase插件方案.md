# OneBase 插件方案

## 一、项目现状分析

项目已基于 PF4J 搭建了相当完善的插件框架：

| 模块 | 作用 |
|------|------|
| `onebase-plugin-sdk` | 插件开发 SDK，定义扩展点接口（`CustomFunction`、`DataProcessor`、`EventListener`、`HttpHandler`）、上下文（`PluginContext`）、平台服务接口 |
| `onebase-plugin-runtime` | 插件运行时，包含 `OneBasePluginManager`、`CustomFunctionExecutor`、`EventDispatcher`、服务实现、自动配置 |
| `onebase-plugin-maven-plugin` | Maven 打包插件，自动生成 `plugin.properties`、插件类、扩展点扫描 |
| `onebase-plugin-demo` | 示例插件项目 |

---

## 二、架构全景图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           OneBase 低代码平台                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  公式引擎   │    │  流程引擎   │    │  数据引擎   │    │  事件总线   │  │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘  │
│         │                  │                  │                  │         │
│         └──────────────────┴──────────────────┴──────────────────┘         │
│                                    │                                        │
│                     ┌──────────────▼──────────────┐                        │
│                     │     CustomFunctionExecutor   │                        │
│                     │     EventDispatcher          │                        │
│                     │     HttpHandlerDispatcher    │                        │
│                     └──────────────┬──────────────┘                        │
│                                    │                                        │
│  ┌─────────────────────────────────▼─────────────────────────────────────┐ │
│  │                      OneBasePluginManager (PF4J)                       │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │ │
│  │  │  Plugin A   │ │  Plugin B   │ │  Plugin C   │ │     ...     │     │ │
│  │  │ (算法插件)  │ │ (OCR插件)   │ │ (支付插件)  │ │             │     │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                    │                                        │
│                     ┌──────────────▼──────────────┐                        │
│                     │        PluginContext         │                        │
│                     │  ┌─────────────────────────┐│                        │
│                     │  │ DataService │UserService││                        │
│                     │  │ FileService │CacheService│                        │
│                     │  └─────────────────────────┘│                        │
│                     └─────────────────────────────┘                        │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、四大扩展点设计

| 扩展点 | 接口 | 用途 | 调用场景 |
|--------|------|------|----------|
| **自定义函数** | `CustomFunction` | 公式中调用的算法/计算逻辑 | 公式引擎、规则引擎、字段计算 |
| **数据处理器** | `DataProcessor` | ETL、脱敏、聚合、转换 | 数据导入导出、数据流水线 |
| **事件监听器** | `EventListener` | 业务联动、审计、通知 | 数据变更、流程节点、用户行为 |
| **HTTP处理器** | `HttpHandler` | 暴露自定义 REST API | 第三方集成、Webhook 回调 |

---

## 四、用户开发插件流程（简化版）

```
1. 创建 Maven 项目
   └── 引入 onebase-plugin-sdk (scope=provided)
   
2. 实现扩展点
   └── @Extension + implements CustomFunction/DataProcessor/...
   
3. 配置 Maven 插件
   └── onebase-plugin-maven-plugin 自动生成元数据
   
4. 打包
   └── mvn package → 生成 plugin.jar 或 plugin.zip
   
5. 部署
   └── 上传到平台插件管理 或 放入 plugins 目录
   
6. 使用
   └── 在公式/流程/规则中引用函数名，如 MY_OCR(image)
```

---

## 五、核心增强建议

### 1. 插件市场与租户隔离

```java
// 插件元数据增强
public class PluginMetadata {
    private String pluginId;
    private String name;
    private String description;
    private String author;
    private String version;
    private String[] tags;           // 标签：算法、AI、支付...
    private String iconUrl;          // 图标
    private boolean global;          // 是否全局插件（平台级）
    private Long[] allowedTenants;   // 允许使用的租户（null=所有）
}
```

### 2. 插件沙箱与安全

```java
// 安全管理器
public class PluginSecurityManager {
    // 资源限制
    private long maxMemory = 256 * 1024 * 1024;  // 256MB
    private long maxExecutionTime = 30_000;       // 30秒
    private int maxThreads = 5;
    
    // 权限控制
    private Set<String> allowedHosts;            // 允许访问的外部地址
    private boolean allowFileAccess = false;     // 是否允许文件系统访问
    private boolean allowNetworkAccess = true;   // 是否允许网络访问
}
```

### 3. 插件配置化

```java
// 插件支持配置项
public interface ConfigurablePlugin {
    /**
     * 定义配置项
     */
    List<ConfigDef> configDefinitions();
    
    /**
     * 初始化时接收配置
     */
    void configure(Map<String, Object> config);
}

// 配置项定义
public class ConfigDef {
    private String key;
    private String label;
    private String type;        // string, number, boolean, select, password
    private Object defaultValue;
    private boolean required;
    private String[] options;   // select 类型的选项
}
```

用户可在平台 UI 配置插件参数（如 API Key、阈值等），无需改代码。

### 4. 插件版本管理与热更新

```java
// 插件版本管理
public interface PluginVersionManager {
    // 上传新版本
    void uploadVersion(String pluginId, InputStream jar, String version);
    
    // 回滚到指定版本
    void rollback(String pluginId, String version);
    
    // 热更新（不中断服务）
    void hotReload(String pluginId);
    
    // 灰度发布（部分租户先用新版本）
    void grayRelease(String pluginId, String version, Long[] tenantIds);
}
```

### 5. 插件依赖与组合

```java
// 插件可以声明依赖其他插件
@PluginDependency(id = "base-ai-plugin", version = ">=1.0.0")
public class MyAdvancedAIPlugin extends OneBasePlugin {
    // 可以调用 base-ai-plugin 提供的函数
}
```

---

## 六、平台侧集成点

### 1. 公式引擎集成

```java
// 在公式引擎中注册插件函数
public class FormulaEngine {
    @Resource
    private CustomFunctionExecutor functionExecutor;
    
    public Object evaluate(String formula, Map<String, Object> context) {
        // 解析公式，遇到函数调用时
        if (functionExecutor.hasFunction(functionName)) {
            return functionExecutor.execute(functionName, args);
        }
        // 内置函数处理...
    }
}
```

### 2. 事件总线集成

```java
// 数据变更时触发插件事件
@Service
public class DataChangeEventPublisher {
    @Resource
    private EventDispatcher eventDispatcher;
    
    public void onDataCreated(String tableName, Map<String, Object> data) {
        PluginEvent event = new PluginEvent("data.created", Map.of(
            "table", tableName,
            "data", data
        ));
        eventDispatcher.dispatch(event);
    }
}
```

### 3. 流程引擎集成

```java
// 流程节点调用插件
public class PluginTaskHandler implements TaskHandler {
    @Resource
    private CustomFunctionExecutor functionExecutor;
    
    @Override
    public void execute(TaskContext context) {
        String functionName = context.getConfig("function");
        Object[] args = context.getConfig("args");
        Object result = functionExecutor.execute(functionName, args);
        context.setVariable("result", result);
    }
}
```

---

## 七、用户体验优化

### 1. 插件模板生成器

提供 CLI 或 Web 界面一键生成插件项目骨架：

```bash
onebase-cli plugin create --name my-plugin --type function
# 生成完整的 Maven 项目结构
```

### 2. 在线 IDE / 轻量脚本

对于简单逻辑，支持在线编写 Groovy/JavaScript 脚本，无需打包部署：

```java
// 轻量脚本扩展点
public interface ScriptFunction {
    String name();
    String script();      // Groovy/JS 脚本内容
    String language();    // groovy / javascript
}
```

### 3. 插件市场

- **官方插件**：平台提供的常用插件（OCR、支付、短信等）
- **第三方插件**：认证开发者上传的插件
- **私有插件**：企业内部插件，仅本租户可见

---

## 八、与其他方案对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| **PF4J（当前）** | 成熟稳定、类隔离、热插拔 | 需要 Java 开发、打包部署 | 复杂算法、性能敏感 |
| **Groovy 脚本** | 无需打包、在线编辑 | 性能较低、安全风险 | 简单逻辑、快速验证 |
| **WASM** | 多语言、沙箱安全 | 生态不成熟、调试困难 | 未来方向 |
| **外部 HTTP 服务** | 语言无关、独立部署 | 网络延迟、运维复杂 | 已有服务集成 |

---

## 九、建议路线图

| 阶段 | 内容 | 优先级 |
|------|------|--------|
| P0 | 完善现有 PF4J 插件体系，补充文档和示例 | 高 |
| P1 | 插件配置化支持，让用户可以在 UI 配置插件参数 | 高 |
| P1 | 插件市场 UI，支持上传、启停、版本管理 | 高 |
| P2 | 轻量脚本支持（Groovy），满足简单场景 | 中 |
| P2 | 插件沙箱与资源限制，增强安全性 | 中 |
| P3 | 插件模板生成器 CLI/Web | 低 |
| P3 | 插件灰度发布、租户隔离 | 低 |

---

## 十、总结

现有的 PF4J 方案已经相当完善，建议继续沿用并增强，重点补充：

1. **配置化**：让用户在 UI 配置插件参数
2. **易用性**：提供模板生成器、在线文档
3. **安全性**：沙箱隔离、资源限制
4. **轻量补充**：支持 Groovy 脚本满足简单场景

通过以上增强，可以让插件系统更加易用、安全、灵活，满足零代码平台用户对高代码扩展能力的需求。
