# OneBase 插件框架架构深度分析

> 最后更新: 2026-01-11
> 分析工具: Claude Code Explore Agent
> 文档版本: 1.0

---

## 文档概述

本文档深入分析 OneBase v3 插件框架的设计与实现，包括模块结构、生命周期管理、扩展点机制、Spring 集成等核心内容。

---

## 1. 插件框架概述

### 1.1 设计理念

OneBase 插件框架基于 **PF4J (Plugin Framework for Java)** 构建，实现了企业级插件化扩展能力。核心设计理念包括：

- **热插拔支持**: 运行时动态加载/卸载插件，无需重启宿主应用
- **Spring 生态集成**: 无缝集成 Spring Boot，充分利用 Spring 生态
- **开发友好**: 提供 DEV 模式支持 IDE 热重载，加速开发调试
- **生产就绪**: 支持插件隔离、版本管理、安全控制

### 1.2 核心特性

| 特性 | 说明 | 技术实现 |
|------|------|----------|
| **生命周期管理** | 加载、启动、停止、卸载完整生命周期 | PF4J PluginManager 封装 |
| **扩展点机制** | 接口驱动的扩展点定义和发现 | ExtensionPoint + ASM 扫描 |
| **HTTP 路由** | 插件可提供 REST API | 动态 Controller 注册 |
| **类加载隔离** | 插件间类加载器隔离 | PluginClassLoader |
| **开发模式** | 支持 IDE 热重载 | DevModePluginManager |
| **构建工具** | Maven 插件自动化构建 | onebase-plugin-maven-plugin |

### 1.3 技术栈

```xml
<!-- 核心依赖 -->
<dependency>
    <groupId>org.pf4j</groupId>
    <artifactId>pf4j</artifactId>
    <version>3.14.0</version>
</dependency>
<dependency>
    <groupId>org.pf4j</groupId>
    <artifactId>pf4j-spring</artifactId>
    <version>0.10.0</version>
</dependency>
```

---

## 2. 模块架构

### 2.1 模块结构总览

```
onebase-plugin/
├── onebase-plugin-common/              # 公共模块
│   ├── scanner/                        # 扩展点扫描器
│   ├── constant/                       # 常量定义
│   └── enums/                          # 枚举类型
│
├── onebase-plugin-sdk/                 # 开发SDK
│   ├── api/                            # 扩展点接口
│   │   └── HttpHandler.java           # HTTP处理器扩展点
│   └── base/                           # 基类
│
├── onebase-plugin-core/                # 核心模块
│   ├── dal/                            # 数据访问层
│   │   ├── dataobject/                 # 数据对象
│   │   └── mapper/                     # MyBatis映射器
│   └── message/                        # 消息定义
│
├── onebase-plugin-build/               # 编辑态服务
│   ├── controller/                     # REST控制器
│   │   ├── plugin/                     # 插件管理
│   │   └── plugin-package/             # 插件包管理
│   └── service/                        # 业务服务
│
├── onebase-plugin-runtime/             # 运行时服务
│   ├── controller/                     # REST控制器
│   ├── service/                        # 业务服务
│   └── handler/                        # 消息处理器
│
├── onebase-spring-boot-starter-plugin/ # Spring Boot集成
│   ├── autoconfigure/                  # 自动配置
│   ├── config/                         # 配置类
│   ├── manager/                        # 插件管理器
│   ├── event/                          # 事件定义
│   └── registrar/                      # 动态注册
│
├── onebase-plugin-maven-plugin/        # Maven插件
│   ├── mojos/                          # Mojo实现
│   │   ├── GeneratePluginClassMojo.java
│   │   ├── GeneratePropertiesMojo.java
│   │   ├── ScanExtensionsMojo.java
│   │   └── PackagePluginMojo.java
│   └── util/                           # 工具类
│
├── onebase-plugin-demo/                # 开发示例
│   └── hello-world-plugin/             # Hello World示例
│
└── onebase-plugin-host-simulator/      # 宿主模拟器
    └── simulator/                      # 模拟器实现
```

### 2.2 模块职责详解

#### 2.2.1 onebase-plugin-sdk

插件开发者的核心依赖库，**scope=provided**，不打入插件包。

**核心API:**
```java
// 扩展点标记接口
public interface ExtensionPoint {
    // 标记接口，无方法定义
}

// HTTP处理器扩展点
public interface HttpHandler extends ExtensionPoint {
    /**
     * 获取插件ID
     * @return 插件ID
     */
    default String getPluginId() {
        return PluginInfoCache.getPluginId(this.getClass());
    }
}
```

**使用方式:**
```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <scope>provided</scope>
</dependency>
```

#### 2.2.2 onebase-plugin-common

公共工具和常量定义。

**扩展点常量:**
```java
public class ExtensionPointConstants {
    public static final List<Class<?>> EXTENSION_POINT_CLASSES = Arrays.asList(
        HttpHandler.class
        // 可扩展其他扩展点类型
    );
}
```

**运行模式枚举:**
```java
public enum PluginRuntimeMode {
    DEV,        // 开发模式：支持热重载
    STAGING,    // 预发模式：插件目录加载
    PROD        // 生产模式：完整的生命周期管理
}
```

**扩展点扫描器:**

| 扫描器 | 实现方式 | 用途 |
|--------|----------|------|
| **ExtensionPointScannerSpring** | 反射扫描 | 运行时扫描、开发模式 |
| **ExtensionPointScannerASM** | 字节码分析 | 编译时扫描、Maven构建 |

#### 2.2.3 onebase-plugin-core

核心数据模型和数据访问层。

**数据对象:**
```java
// 插件信息
@Table(value = "plugin_info")
public class PluginInfoDO {
    private Long id;
    private String pluginId;           // 插件ID
    private String version;            // 版本号
    private String description;        // 描述
    private String state;              // 状态
    private String provider;           // 提供者
    private String license;            // 许可证
    private String runtimeMode;        // 运行模式
    // ... 审计字段
}

// 插件包信息
@Table(value = "plugin_package_info")
public class PluginPackageInfoDO {
    private Long id;
    private String packageFileName;    // 包文件名
    private String packagePath;        // 包路径
    private Long fileSize;             // 文件大小
    private String md5;                // MD5校验
    // ... 审计字段
}
```

**消息定义:**
```java
// 插件命令消息
public class PluginCommandMessage extends BaseMessage {
    private String command;            // LOAD, START, STOP, UNLOAD, DELETE
    private String pluginId;
    private String pluginPath;
    private Map<String, Object> data;
}
```

#### 2.2.4 onebase-spring-boot-starter-plugin

Spring Boot 集成核心，提供自动配置和运行时管理。

**自动配置类:**
```java
@Configuration
@ConditionalOnProperty(prefix = "onebase.plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({
    HotReloadConfiguration.class,
    PluginManagerConfiguration.class
})
@EnableConfigurationProperties(PluginProperties.class)
public class PluginRuntimeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PluginManager pluginManager(PluginProperties properties,
                                      ApplicationContext applicationContext) {
        // 根据运行模式选择不同的PluginManager实现
        PluginRuntimeMode mode = properties.getMode();

        if (mode == PluginRuntimeMode.DEV) {
            return new DevModePluginManager(applicationContext, properties);
        } else {
            return new OneBasePluginManager(applicationContext, properties);
        }
    }

    @Bean
    public PluginControllerRegistrar pluginControllerRegistrar(
            PluginManager pluginManager,
            RequestMappingHandlerMapping handlerMapping) {
        return new PluginControllerRegistrar(pluginManager, handlerMapping);
    }
}
```

**配置属性:**
```java
@ConfigurationProperties(prefix = "onebase.plugin")
public class PluginProperties {
    private boolean enabled = true;           // 启用插件系统
    private PluginRuntimeMode mode = PROD;    // 运行模式
    private boolean autoLoad = true;          // 自动加载
    private boolean autoStart = true;         // 自动启动
    private String pluginsDir = "plugins";    // 插件目录
    private List<String> devClassPaths;       // 开发模式classpath

    // 热重载配置
    private HotReload hotReload = new HotReload();

    public static class HotReload {
        private boolean enabled = false;
        private long watchInterval = 1000;     // 监控间隔(ms)
        private long debounceInterval = 2000;  // 防抖间隔(ms)
    }
}
```

**插件管理器封装:**
```java
public class OneBasePluginManager extends SpringPluginManager {

    private final ApplicationContext applicationContext;
    private final PluginControllerRegistrar controllerRegistrar;
    private final Map<String, PluginState> stateTransitions;

    /**
     * 加载并启动插件（原子操作）
     */
    public String loadAndStartPlugin(Path pluginPath) {
        // 1. 加载插件
        String pluginId = loadPlugin(pluginPath);

        // 2. 启动插件
        startPlugin(pluginId);

        return pluginId;
    }

    /**
     * 停止并卸载插件（原子操作）
     */
    public boolean stopAndUnloadPlugin(String pluginId) {
        // 1. 停止插件
        stopPlugin(pluginId);

        // 2. 卸载插件
        return unloadPlugin(pluginId);
    }

    @Override
    public String loadPlugin(Path pluginPath) {
        // 1. 检查重复加载（路径智能匹配）
        String pluginId = checkAlreadyLoaded(pluginPath);
        if (pluginId != null) {
            return pluginId;
        }

        // 2. 加载插件
        try {
            pluginId = super.loadPlugin(pluginPath);
        } catch (PluginAlreadyLoadedException e) {
            // 处理竞态条件
            pluginId = extractPluginId(pluginPath);
        }

        // 3. 发布事件
        publishEvent(new PluginLoadedEvent(pluginId));

        return pluginId;
    }

    @Override
    public PluginState startPlugin(String pluginId) {
        // 1. 防御性检查
        validatePluginState(pluginId, PluginState.RESOLVED);

        // 2. 注册HTTP Controller
        List<HttpHandler> handlers = getExtensions(HttpHandler.class, pluginId);
        controllerRegistrar.registerControllers(pluginId, handlers);

        // 3. 启动插件
        PluginState state = super.startPlugin(pluginId);

        // 4. 发布事件
        publishEvent(new PluginStartedEvent(pluginId));

        return state;
    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        // 1. 防御性检查
        validatePluginState(pluginId, PluginState.STARTED);

        // 2. 注销HTTP Controller
        controllerRegistrar.unregisterControllers(pluginId);

        // 3. 停止插件
        PluginState state = super.stopPlugin(pluginId);

        // 4. 发布事件
        publishEvent(new PluginStoppedEvent(pluginId));

        return state;
    }
}
```

#### 2.2.5 onebase-plugin-maven-plugin

Maven 构建插件，自动化插件构建流程。

**构建目标 (Goals):**

| Goal | 功能 | 执行阶段 |
|------|------|----------|
| **generate-plugin-class** | 生成插件主类 | generate-sources |
| **generate-properties** | 生成 plugin.properties | generate-resources |
| **scan-extensions** | 扫描扩展点 | process-classes |
| **package-plugin** | 打包 ZIP 插件 | package |

**插件配置示例:**
```xml
<plugin>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-maven-plugin</artifactId>
    <version>${revision}</version>
    <executions>
        <!-- 生成插件主类 -->
        <execution>
            <id>generate-plugin-class</id>
            <goals>
                <goal>generate-plugin-class</goal>
            </goals>
            <configuration>
                <generatedPackage>com.example.plugin</generatedPackage>
                <generatedClassName>GeneratedPlugin</generatedClassName>
            </configuration>
        </execution>

        <!-- 生成plugin.properties -->
        <execution>
            <id>generate-properties</id>
            <goals>
                <goal>generate-properties</goal>
            </goals>
            <configuration>
                <pluginId>my-plugin</pluginId>
                <pluginDescription>我的第一个插件</pluginDescription>
                <pluginVersion>1.0.0</pluginVersion>
                <pluginProvider>com.example</pluginProvider>
                <pluginLicense>Apache-2.0</pluginLicense>
            </configuration>
        </execution>

        <!-- 扫描扩展点 -->
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
</plugin>
```

**插件包结构:**
```
my-plugin-1.0.0.zip
├── plugin.properties                  # 插件元数据
├── lib/
│   ├── my-plugin-1.0.0.jar           # 主JAR（包含生成的插件主类）
│   ├── spring-boot-starter-web.jar   # 依赖（可选，根据配置）
│   └── other-dependencies.jar        # 其他运行时依赖
└── META-INF/
    └── extensions.idx                 # 扩展点索引（自动生成）
```

**生成的插件主类:**
```java
package com.example.plugin;

import org.pf4j.PluginWrapper;

public class GeneratedPlugin extends BasePlugin {

    @Override
    public void start() {
        log.info("GeneratedPlugin started!");
        System.out.println("GeneratedPlugin started!");
    }

    @Override
    public void stop() {
        log.info("GeneratedPlugin stopped!");
        System.out.println("GeneratedPlugin stopped!");
    }
}
```

---

## 3. 插件生命周期管理

### 3.1 生命周期状态机

```
                    ┌──────────────┐
                    │   CREATED    │ 插件文件已创建
                    └──────┬───────┘
                           │ loadPlugin()
                           ▼
                    ┌──────────────┐
                    │   RESOLVED   │ 插件已解析
                    └──────┬───────┘
                           │ startPlugin()
                           ▼
                    ┌──────────────┐
                    │   STARTED    │ 插件运行中 ◄────┐
                    └──────┬───────┘               │
                           │ stopPlugin()          │ startPlugin()
                           ▼                       │
                    ┌──────────────┐               │
                    │   STOPPED    │ 插件已停止     │
                    └──────┬───────┘               │
                           │ unloadPlugin()        │
                           ▼                       │
                    ┌──────────────┐               │
                    │  DISABLED    │ 插件已卸载 ────┘
                    └──────────────┘
```

### 3.2 生命周期方法详解

#### 3.2.1 加载插件 (loadPlugin)

```java
public String loadPlugin(Path pluginPath) {
    // 1. 路径验证
    if (!Files.exists(pluginPath)) {
        throw new PluginNotFoundException("Plugin not found: " + pluginPath);
    }

    // 2. 重复加载检查（路径智能匹配）
    for (PluginWrapper plugin : getPlugins()) {
        Path loadedPath = plugin.getPluginPath();
        if (isSamePlugin(pluginPath, loadedPath)) {
            return plugin.getPluginId();
        }
    }

    // 3. 加载插件
    String pluginId;
    try {
        pluginId = super.loadPlugin(pluginPath);
    } catch (PluginAlreadyLoadedException e) {
        // 处理并发加载的竞态条件
        Path unzippedPath = pluginsRoot.resolve(pluginId);
        pluginId = extractPluginId(unzippedPath);
    }

    // 4. 发布加载事件
    applicationContext.publishEvent(new PluginLoadedEvent(pluginId));

    return pluginId;
}
```

**路径智能匹配:**
```java
private boolean isSamePlugin(Path path1, Path path2) {
    // 处理ZIP路径和解压路径的比较
    String normalized1 = normalizePath(path1);
    String normalized2 = normalizePath(path2);
    return normalized1.equals(normalized2);
}
```

#### 3.2.2 启动插件 (startPlugin)

```java
public PluginState startPlugin(String pluginId) {
    // 1. 状态验证
    PluginWrapper plugin = getPlugin(pluginId);
    if (plugin.getPluginState() != PluginState.RESOLVED) {
        throw new IllegalPluginStateException(
            "Plugin '" + pluginId + "' is not in RESOLVED state");
    }

    // 2. 注册HTTP Controller
    List<HttpHandler> handlers = getExtensions(HttpHandler.class, pluginId);
    controllerRegistrar.registerControllers(pluginId, handlers);

    // 3. 调用插件启动方法
    PluginState state = super.startPlugin(pluginId);

    // 4. 发布启动事件
    applicationContext.publishEvent(new PluginStartedEvent(pluginId));

    return state;
}
```

#### 3.2.3 停止插件 (stopPlugin)

```java
public PluginState stopPlugin(String pluginId) {
    // 1. 状态验证
    PluginWrapper plugin = getPlugin(pluginId);
    if (plugin.getPluginState() != PluginState.STARTED) {
        throw new IllegalPluginStateException(
            "Plugin '" + pluginId + "' is not in STARTED state");
    }

    // 2. 注销HTTP Controller
    controllerRegistrar.unregisterControllers(pluginId);

    // 3. 调用插件停止方法
    PluginState state = super.stopPlugin(pluginId);

    // 4. 发布停止事件
    applicationContext.publishEvent(new PluginStoppedEvent(pluginId));

    return state;
}
```

#### 3.2.4 卸载插件 (unloadPlugin)

```java
public boolean unloadPlugin(String pluginId) {
    // 1. 先停止插件（如果正在运行）
    PluginWrapper plugin = getPlugin(pluginId);
    if (plugin.getPluginState() == PluginState.STARTED) {
        stopPlugin(pluginId);
    }

    // 2. 确保Controller已注销
    controllerRegistrar.unregisterControllers(pluginId);

    // 3. 卸载插件
    boolean unloaded = super.unloadPlugin(pluginId);

    // 4. 发布卸载事件
    applicationContext.publishEvent(new PluginUnloadedEvent(pluginId));

    return unloaded;
}
```

### 3.3 事件机制

#### 3.3.1 事件类型

```java
// 基础事件类
@Getter
public class BasePluginEvent extends ApplicationEvent {
    private final String pluginId;
    private final LocalDateTime timestamp;

    public BasePluginEvent(String pluginId) {
        super(pluginId);
        this.pluginId = pluginId;
        this.timestamp = LocalDateTime.now();
    }
}

// 具体事件类型
public class PluginLoadedEvent extends BasePluginEvent { }
public class PluginStartedEvent extends BasePluginEvent { }
public class PluginStoppedEvent extends BasePluginEvent { }
public class PluginUnloadedEvent extends BasePluginEvent { }
public class PluginDeletedEvent extends BasePluginEvent { }
```

#### 3.3.2 事件监听

```java
@Component
public class PluginEventListener {

    @EventListener
    public void onPluginLoaded(PluginLoadedEvent event) {
        log.info("Plugin loaded: {}", event.getPluginId());
        // 处理插件加载后的逻辑
    }

    @EventListener
    @Async
    public void onPluginStarted(PluginStartedEvent event) {
        log.info("Plugin started: {}", event.getPluginId());
        // 异步处理插件启动后的逻辑
    }

    @EventListener
    public void onPluginStopped(PluginStoppedEvent event) {
        log.info("Plugin stopped: {}", event.getPluginId());
        // 清理插件资源
    }
}
```

---

## 4. 扩展点机制

### 4.1 扩展点定义

#### 4.1.1 标记接口

```java
/**
 * 扩展点标记接口
 * 所有扩展点都必须继承此接口
 */
public interface ExtensionPoint {
    // 标记接口，无需定义方法
}
```

#### 4.1.2 HttpHandler 扩展点

```java
/**
 * HTTP处理器扩展点
 * 插件通过实现此接口提供REST API能力
 */
public interface HttpHandler extends ExtensionPoint {

    /**
     * 获取插件ID
     * @return 插件ID
     */
    default String getPluginId() {
        return PluginInfoCache.getPluginId(this.getClass());
    }

    /**
     * 获取插件版本
     * @return 插件版本
     */
    default String getPluginVersion() {
        return PluginInfoCache.getPluginVersion(this.getClass());
    }
}
```

### 4.2 扩展点实现

#### 4.2.1 创建扩展点实现

```java
@RestController
@RequestMapping("/plugin/my-demo-plugin/api")  // 必须以 /plugin/{pluginId}/ 开头
public class DemoApiController implements HttpHandler {

    @Autowired
    private SomeService someService;  // 可注入Spring Bean

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
            "message", "Hello from Demo Plugin!",
            "pluginId", getPluginId(),
            "version", getPluginVersion(),
            "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        return Map.of(
            "echo", data,
            "pluginId", getPluginId()
        );
    }
}
```

#### 4.2.2 扩展点注解

```java
/**
 * PF4J 扩展点注解
 * 标记在扩展点实现类上
 */
@Extension
public class DemoExtension implements HttpHandler {
    // ...
}

// 或者使用@Component（Spring会自动识别）
@Component
public class DemoExtension implements HttpHandler {
    // ...
}
```

### 4.3 扩展点扫描

#### 4.3.1 Spring 扫描器（运行时）

```java
public class ExtensionPointScannerSpring {

    private final ApplicationContext applicationContext;
    private final List<String> devClassPaths;

    public <T> List<T> scanExtensions(Class<T> extensionType) {
        List<T> extensions = new ArrayList<>();

        for (String classPath : devClassPaths) {
            File classesDir = new File(classPath);
            if (!classesDir.exists()) {
                continue;
            }

            // 扫描.class文件
            List<Class<?>> classes = scanClasses(classesDir);
            for (Class<?> clazz : classes) {
                if (extensionType.isAssignableFrom(clazz)) {
                    // 创建实例并注入Spring依赖
                    T extension = createExtension(clazz);
                    extensions.add(extension);
                }
            }
        }

        return extensions;
    }

    private List<Class<?>> scanClasses(File directory) {
        List<Class<?>> classes = new ArrayList<>();
        scanClassesRecursive(directory, directory, classes);
        return classes;
    }

    private void scanClassesRecursive(File root, File directory, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanClassesRecursive(root, file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = getClassName(root, file);
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    // 忽略无法加载的类
                }
            }
        }
    }

    private <T> T createExtension(Class<T> clazz) {
        try {
            // 创建实例
            T instance = clazz.getDeclaredConstructor().newInstance();

            // 注入Spring依赖
            applicationContext.getAutowireCapableBeanFactory()
                .autowireBean(instance);

            return instance;
        } catch (Exception e) {
            throw new ExtensionCreationException("Failed to create extension: " + clazz, e);
        }
    }
}
```

#### 4.3.2 ASM 扫描器（编译时）

```java
public class ExtensionPointScannerASM {

    public List<String> scanExtensions(File classesDirectory, Class<?> extensionType) {
        List<String> extensionClasses = new ArrayList<>();
        String extensionTypeName = extensionType.getName().replace('.', '/');

        try {
            // 递归扫描.class文件
            scanClassesRecursive(classesDirectory, classesDirectory,
                extensionTypeName, extensionClasses);
        } catch (IOException e) {
            throw new ExtensionScanException("Failed to scan extensions", e);
        }

        return extensionClasses;
    }

    private void scanClassesRecursive(File root, File directory,
                                      String extensionTypeName,
                                      List<String> extensionClasses) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanClassesRecursive(root, file, extensionTypeName, extensionClasses);
            } else if (file.getName().endsWith(".class")) {
                // 使用ASM分析字节码
                if (isExtension(file, extensionTypeName)) {
                    String className = getClassName(root, file);
                    extensionClasses.add(className);
                }
            }
        }
    }

    private boolean isExtension(File classFile, String extensionTypeName) throws IOException {
        ClassReader reader = new ClassReader(new FileInputStream(classFile));
        ExtensionChecker visitor = new ExtensionChecker(extensionTypeName);
        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return visitor.isExtension();
    }

    static class ExtensionChecker extends ClassVisitor {
        private final String extensionTypeName;
        private boolean isExtension = false;

        public ExtensionChecker(String extensionTypeName) {
            super(Opcodes.ASM9);
            this.extensionTypeName = extensionTypeName;
        }

        @Override
        public void visit(int version, int access, String name,
                         String signature, String superName, String[] interfaces) {
            // 检查是否实现了扩展点接口
            if (interfaces != null) {
                for (String iface : interfaces) {
                    if (iface.equals(extensionTypeName)) {
                        isExtension = true;
                        break;
                    }
                }
            }
        }
    }
}
```

### 4.4 扩展点注册

#### 4.4.1 扩展点索引

```
META-INF/extensions.idx
```

```
# OneBase Plugin Extensions Index
# Generated: 2025-12-18 10:00:00

com.example.plugin.DemoApiController=com.cmsr.onebase.plugin.sdk.api.HttpHandler
```

#### 4.4.2 运行时注册

```java
@Component
public class ExtensionPointRegistry {

    private final Map<Class<?>, List<Object>> extensions = new ConcurrentHashMap<>();

    @PostConstruct
    public void registerExtensions() {
        // 从Spring容器获取所有扩展点实现
        Map<String, Object> extensionBeans = applicationContext
            .getBeansWithAnnotation(Extension.class);

        for (Object extension : extensionBeans.values()) {
            registerExtension(extension);
        }
    }

    public <T> void registerExtension(T extension) {
        // 获取扩展点接口
        Class<?>[] interfaces = extension.getClass().getInterfaces();
        for (Class<?> iface : interfaces) {
            if (ExtensionPoint.class.isAssignableFrom(iface)) {
                extensions.computeIfAbsent(iface, k -> new ArrayList<>())
                    .add(extension);
            }
        }
    }

    public <T> List<T> getExtensions(Class<T> type) {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) extensions.getOrDefault(type, Collections.emptyList());
        return new ArrayList<>(result);
    }
}
```

---

## 5. HTTP Controller 动态注册

### 5.1 Controller 注册器

```java
public class PluginControllerRegistrar {

    private final PluginManager pluginManager;
    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<String, Set<RequestMappingInfo>> pluginMappings = new ConcurrentHashMap<>();

    /**
     * 注册插件的HTTP Controller
     */
    public void registerControllers(String pluginId, List<HttpHandler> handlers) {
        Set<RequestMappingInfo> mappings = new HashSet<>();

        for (HttpHandler handler : handlers) {
            if (handler instanceof Class<?> == false && handler instanceof Object) {
                Object controller = handler;

                // 获取@RequestMapping注解信息
                RequestMapping requestMapping = controller.getClass()
                    .getAnnotation(RequestMapping.class);

                if (requestMapping != null) {
                    // 验证路由前缀
                    validateRoutePrefix(requestMapping, pluginId);

                    // 注册所有@RequestMapping方法
                    registerControllerMethods(controller, pluginId, mappings);
                }
            }
        }

        // 记录映射关系（用于卸载）
        pluginMappings.put(pluginId, mappings);
    }

    /**
     * 注销插件的HTTP Controller
     */
    public void unregisterControllers(String pluginId) {
        Set<RequestMappingInfo> mappings = pluginMappings.get(pluginId);
        if (mappings != null) {
            for (RequestMappingInfo mapping : mappings) {
                // 注销路由映射
                handlerMapping.unregisterMapping(mapping);
            }
            pluginMappings.remove(pluginId);
        }
    }

    /**
     * 验证路由前缀
     */
    private void validateRoutePrefix(RequestMapping requestMapping, String pluginId) {
        String[] paths = requestMapping.value();
        for (String path : paths) {
            if (!path.startsWith("/plugin/" + pluginId + "/")) {
                throw new InvalidRouteException(
                    "Plugin route must start with '/plugin/" + pluginId + "/': " + path);
            }
        }
    }

    /**
     * 注册Controller方法
     */
    private void registerControllerMethods(Object controller, String pluginId,
                                          Set<RequestMappingInfo> mappings) {
        Class<?> controllerClass = controller.getClass();

        // 获取所有方法
        Method[] methods = controllerClass.getDeclaredMethods();

        for (Method method : methods) {
            // 获取@GetMapping/@PostMapping等注解
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            // ... 其他映射注解

            if (requestMapping != null || getMapping != null || postMapping != null) {
                // 创建RequestMappingInfo
                RequestMappingInfo mappingInfo = createMappingInfo(
                    method, pluginId, controllerClass);

                // 注册到Spring MVC
                handlerMapping.registerMapping(mappingInfo, controller, method);

                // 记录映射
                mappings.add(mappingInfo);
            }
        }
    }

    /**
     * 创建RequestMappingInfo
     */
    private RequestMappingInfo createMappingInfo(Method method, String pluginId,
                                                 Class<?> controllerClass) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths("");

        // 设置路径
        String classPath = getClassPath(controllerClass);
        String methodPath = getMethodPath(method);
        builder.paths(classPath + methodPath);

        // 设置HTTP方法
        Set<RequestMethod> methods = getHttpMethods(method);
        builder.methods(methods.toArray(new RequestMethod[0]));

        // 设置参数、头等
        // ...

        return builder.build();
    }
}
```

### 5.2 插件路由规范

```java
/**
 * 插件路由规范
 * 所有插件的HTTP路由必须遵循以下规范：
 *
 * 1. 路由前缀：/plugin/{pluginId}/
 * 2. API版本：推荐使用 /api/v1/ 或直接 /api/
 * 3. 资源命名：使用复数形式，如 /users, /orders
 * 4. HTTP方法：遵循RESTful规范
 *
 * 示例：
 * /plugin/my-plugin/api/v1/users
 * /plugin/my-plugin/api/orders/{orderId}
 *
 * 不推荐：
 * /plugin/my-plugin/getUser (不使用RESTful)
 * /my-plugin/api/users (缺少plugin前缀)
 */
@RestController
@RequestMapping("/plugin/my-plugin/api")  // 符合规范
public class MyPluginController implements HttpHandler {

    @GetMapping("/users")  // 完整路径: /plugin/my-plugin/api/users
    public List<User> listUsers() {
        return userService.listUsers();
    }

    @GetMapping("/users/{id}")  // 完整路径: /plugin/my-plugin/api/users/{id}
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")  // 完整路径: /plugin/my-plugin/api/users
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

---

## 6. 插件开发指南

### 6.1 创建插件项目

#### 6.1.1 Maven 项目结构

```
my-first-plugin/
├── pom.xml                           # Maven配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── plugin/
│   │   │               ├── api/     # REST Controller
│   │   │               └── service/ # 业务服务
│   │   └── resources/
│   │       └── application.yml      # 可选配置
│   └── test/
│       └── java/                    # 测试代码
└── target/                          # 构建输出
```

#### 6.1.2 pom.xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.cmsr.onebase</groupId>
        <artifactId>onebase-plugin-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>my-first-plugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>My First Plugin</name>
    <description>我的第一个OneBase插件</description>

    <properties>
        <revision>1.0.0-SNAPSHOT</revision>
    </properties>

    <dependencies>
        <!-- OneBase Plugin SDK (provided) -->
        <dependency>
            <groupId>com.cmsr.onebase</groupId>
            <artifactId>onebase-plugin-sdk</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- Spring Web (provided) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- 其他依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- OneBase Plugin Maven Plugin -->
            <plugin>
                <groupId>com.cmsr.onebase</groupId>
                <artifactId>onebase-plugin-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>generate-plugin-class</id>
                        <goals>
                            <goal>generate-plugin-class</goal>
                        </goals>
                        <configuration>
                            <generatedPackage>com.example.plugin</generatedPackage>
                        </configuration>
                    </execution>
                    <execution>
                        <id>generate-properties</id>
                        <goals>
                            <goal>generate-properties</goal>
                        </goals>
                        <configuration>
                            <pluginId>my-first-plugin</pluginId>
                            <pluginDescription>我的第一个OneBase插件</pluginDescription>
                            <pluginVersion>${revision}</pluginVersion>
                            <pluginProvider>com.example</pluginProvider>
                        </configuration>
                    </execution>
                    <execution>
                        <id>scan-extensions</id>
                        <goals>
                            <goal>scan-extensions</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>package-plugin</id>
                        <goals>
                            <goal>package-plugin</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### 6.2 实现插件功能

#### 6.2.1 创建 REST Controller

```java
package com.example.plugin.api;

import com.cmsr.onebase.plugin.sdk.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 示例插件API控制器
 */
@Slf4j
@RestController
@RequestMapping("/plugin/my-first-plugin/api")
public class HelloWorldController implements HttpHandler {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from My First Plugin!");
        response.put("pluginId", getPluginId());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("pluginId", getPluginId());
        response.put("version", getPluginVersion());
        response.put("description", "我的第一个OneBase插件");
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        log.info("Received echo request: {}", data);
        Map<String, Object> response = new HashMap<>();
        response.put("echo", data);
        response.put("pluginId", getPluginId());
        return response;
    }
}
```

#### 6.2.2 添加业务服务

```java
package com.example.plugin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 示例业务服务
 */
@Slf4j
@Service
public class DemoService {

    private final Map<String, Object> dataStore = new ConcurrentHashMap<>();

    /**
     * 保存数据
     */
    public void save(String key, Object value) {
        log.info("Saving data: key={}, value={}", key, value);
        dataStore.put(key, value);
    }

    /**
     * 获取数据
     */
    public Object get(String key) {
        return dataStore.get(key);
    }

    /**
     * 列出所有数据
     */
    public List<Map.Entry<String, Object>> listAll() {
        return new ArrayList<>(dataStore.entrySet());
    }

    /**
     * 删除数据
     */
    public void delete(String key) {
        log.info("Deleting data: key={}", key);
        dataStore.remove(key);
    }
}
```

### 6.3 开发模式调试

#### 6.3.1 配置开发模式

```yaml
# application-dev.yml
onebase:
  plugin:
    enabled: true
    mode: dev                      # 开发模式
    auto-load: true
    auto-start: true
    dev-class-paths:              # 开发模式classpath
      - ./my-first-plugin/target/classes
    hot-reload:
      enabled: true               # 启用热重载
      watch-interval: 1000        # 监控间隔(ms)
      debounce-interval: 2000     # 防抖间隔(ms)
```

#### 6.3.2 启动调试

```bash
# 方式1: Maven直接运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 方式2: IDE运行
# 1. 在IDE中运行OneBase主应用
# 2. 配置Dev模式运行参数
# 3. 修改插件代码后自动重新加载
```

### 6.4 生产环境部署

#### 6.4.1 构建插件

```bash
# 1. 编译插件
mvn clean compile

# 2. 打包插件
mvn package

# 输出: target/my-first-plugin-1.0.0.zip
```

#### 6.4.2 部署插件

```bash
# 1. 上传插件到服务器
scp target/my-first-plugin-1.0.0.zip user@server:/opt/onebase/plugins/

# 2. 通过API加载插件
curl -X POST http://localhost:48080/admin-api/plugin/load \
  -F "file=@/opt/onebase/plugins/my-first-plugin-1.0.0.zip"

# 3. 启动插件
curl -X POST http://localhost:48080/admin-api/plugin/my-first-plugin/start

# 4. 测试插件API
curl http://localhost:48080/plugin/my-first-plugin/api/hello
```

#### 6.4.3 生产配置

```yaml
# application-prod.yml
onebase:
  plugin:
    enabled: true
    mode: prod                     # 生产模式
    auto-load: true               # 自动加载plugins目录下的插件
    auto-start: true              # 自动启动已加载的插件
    plugins-dir: /opt/onebase/plugins
    hot-reload:
      enabled: false              # 生产环境关闭热重载
```

---

## 7. 插件管理 API

### 7.1 插件管理接口

```java
@RestController
@RequestMapping("/admin-api/plugin")
public class PluginManagementController {

    @Autowired
    private PluginManager pluginManager;

    /**
     * 列出所有插件
     */
    @GetMapping
    public CommonResult<List<PluginRespVO>> listPlugins() {
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        List<PluginRespVO> result = plugins.stream()
            .map(this::toPluginVO)
            .collect(Collectors.toList());
        return CommonResult.success(result);
    }

    /**
     * 获取插件详情
     */
    @GetMapping("/{pluginId}")
    public CommonResult<PluginRespVO> getPlugin(@PathVariable String pluginId) {
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (plugin == null) {
            return CommonResult.error(404, "Plugin not found");
        }
        return CommonResult.success(toPluginVO(plugin));
    }

    /**
     * 加载插件
     */
    @PostMapping("/load")
    public CommonResult<PluginLoadRespVO> loadPlugin(
            @RequestParam("file") MultipartFile file) {
        try {
            // 保存插件文件
            Path pluginPath = savePluginFile(file);

            // 加载插件
            String pluginId = pluginManager.loadPlugin(pluginPath);

            PluginLoadRespVO response = new PluginLoadRespVO();
            response.setPluginId(pluginId);
            response.setPath(pluginPath.toString());

            return CommonResult.success(response);
        } catch (Exception e) {
            return CommonResult.error(500, "Failed to load plugin: " + e.getMessage());
        }
    }

    /**
     * 启动插件
     */
    @PostMapping("/{pluginId}/start")
    public CommonResult<Void> startPlugin(@PathVariable String pluginId) {
        try {
            pluginManager.startPlugin(pluginId);
            return CommonResult.success(null);
        } catch (Exception e) {
            return CommonResult.error(500, "Failed to start plugin: " + e.getMessage());
        }
    }

    /**
     * 停止插件
     */
    @PostMapping("/{pluginId}/stop")
    public CommonResult<Void> stopPlugin(@PathVariable String pluginId) {
        try {
            pluginManager.stopPlugin(pluginId);
            return CommonResult.success(null);
        } catch (Exception e) {
            return CommonResult.error(500, "Failed to stop plugin: " + e.getMessage());
        }
    }

    /**
     * 卸载插件
     */
    @DeleteMapping("/{pluginId}")
    public CommonResult<Void> unloadPlugin(@PathVariable String pluginId) {
        try {
            pluginManager.unloadPlugin(pluginId);
            return CommonResult.success(null);
        } catch (Exception e) {
            return CommonResult.error(500, "Failed to unload plugin: " + e.getMessage());
        }
    }

    /**
     * 删除插件
     */
    @DeleteMapping("/{pluginId}/delete")
    public CommonResult<Void> deletePlugin(@PathVariable String pluginId) {
        try {
            // 先卸载
            pluginManager.unloadPlugin(pluginId);

            // 删除文件
            deletePluginFiles(pluginId);

            return CommonResult.success(null);
        } catch (Exception e) {
            return CommonResult.error(500, "Failed to delete plugin: " + e.getMessage());
        }
    }

    /**
     * 获取插件扩展点
     */
    @GetMapping("/{pluginId}/extensions")
    public CommonResult<List<ExtensionInfoVO>> getExtensions(
            @PathVariable String pluginId,
            @RequestParam(required = false) String type) {
        List<ExtensionInfoVO> extensions = new ArrayList<>();

        if (type == null || "HttpHandler".equals(type)) {
            List<HttpHandler> handlers = pluginManager.getExtensions(
                HttpHandler.class, pluginId);
            for (HttpHandler handler : handlers) {
                ExtensionInfoVO info = new ExtensionInfoVO();
                info.setType("HttpHandler");
                info.setClassName(handler.getClass().getName());
                extensions.add(info);
            }
        }

        return CommonResult.success(extensions);
    }
}
```

### 7.2 VO 定义

```java
/**
 * 插件响应VO
 */
@Data
public class PluginRespVO {
    private String pluginId;
    private String version;
    private String description;
    private String provider;
    private String license;
    private String state;           // RESOLVED, STARTED, STOPPED, DISABLED
    private LocalDateTime loadTime;
    private Map<String, Object> metadata;
}

/**
 * 插件加载响应VO
 */
@Data
public class PluginLoadRespVO {
    private String pluginId;
    private String path;
    private String state;
}

/**
 * 扩展点信息VO
 */
@Data
public class ExtensionInfoVO {
    private String type;
    private String className;
    private Map<String, Object> properties;
}
```

---

## 8. 最佳实践

### 8.1 开发规范

#### 8.1.1 命名规范

```java
// 插件ID命名: 小写字母、数字、连字符
pluginId = "my-first-plugin"  // 推荐
pluginId = "MyFirstPlugin"   // 不推荐

// 路由命名: RESTful风格
/plugin/my-plugin/api/users          // 推荐
/plugin/my-plugin/getAllUsers        // 不推荐
```

#### 8.1.2 依赖管理

```xml
<!-- 宿主提供的依赖，使用provided -->
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <scope>provided</scope>  <!-- 必须 -->
</dependency>

<!-- 插件私有依赖，正常打包 -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>some-library</artifactId>
    <!-- 不设置scope，会打入插件包 -->
</dependency>
```

#### 8.1.3 异常处理

```java
@RestController
@RequestMapping("/plugin/my-plugin/api")
public class MyPluginController implements HttpHandler {

    @GetMapping("/data/{id}")
    public CommonResult<Data> getData(@PathVariable Long id) {
        try {
            Data data = dataService.getData(id);
            if (data == null) {
                return CommonResult.error(404, "Data not found");
            }
            return CommonResult.success(data);
        } catch (BusinessException e) {
            log.error("Business error: {}", e.getMessage());
            return CommonResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("System error", e);
            return CommonResult.error(500, "Internal server error");
        }
    }
}
```

### 8.2 性能优化

#### 8.2.1 类加载优化

```yaml
# 开发模式: 使用DEV模式加速
onebase:
  plugin:
    mode: dev  # 直接使用classpath，避免解压ZIP

# 生产模式: 使用PROD模式稳定
onebase:
  plugin:
    mode: prod
```

#### 8.2.2 插件隔离

```java
// 避免插件间共享可变状态
@Component
public class PluginStateService {
    // 不推荐: 静态变量
    // private static Map<String, Object> state = new HashMap<>();

    // 推荐: 实例变量，每个插件独立
    private final Map<String, Object> state = new ConcurrentHashMap<>();
}
```

### 8.3 安全考虑

#### 8.3.1 输入验证

```java
@PostMapping("/data")
public CommonResult<Data> createData(@RequestBody @Valid DataRequest request) {
    // 使用@Valid进行参数校验
    // ...
}

@Data
public class DataRequest {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name too long")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid code format")
    private String code;
}
```

#### 8.3.2 权限控制

```java
// 插件应尊重宿主的权限体系
@GetMapping("/sensitive-data")
@PreAuthorize("hasAuthority('system:sensitive:data:view')")
public CommonResult<Data> getSensitiveData() {
    // ...
}
```

---

## 9. 故障排查

### 9.1 常见问题

#### 问题1: 插件加载失败

```
错误信息: PluginNotFoundException: Plugin not found: /path/to/plugin.zip

解决方案:
1. 检查插件文件是否存在
2. 检查文件权限
3. 检查插件文件是否损坏
```

#### 问题2: 扩展点未注册

```
错误信息: No extensions found for type: HttpHandler

解决方案:
1. 检查是否实现了HttpHandler接口
2. 检查是否添加了@Extension或@Component注解
3. 检查extensions.idx文件是否生成
4. 清理并重新构建: mvn clean package
```

#### 问题3: 路由注册失败

```
错误信息: InvalidRouteException: Plugin route must start with '/plugin/{pluginId}/'

解决方案:
1. 检查@RequestMapping的value属性
2. 确保路由以/plugin/{pluginId}/开头
3. pluginId必须与plugin.properties中的plugin.id一致
```

#### 问题4: 类冲突

```
错误信息: ClassNotFoundException或NoSuchMethodError

解决方案:
1. 检查依赖版本是否与宿主兼容
2. 使用mvn dependency:tree分析依赖
3. 将宿主已提供的依赖设置为provided
```

### 9.2 调试技巧

#### 9.2.1 启用调试日志

```yaml
logging:
  level:
    com.cmsr.onebase.plugin: DEBUG
    org.pf4j: DEBUG
```

#### 9.2.2 检查插件状态

```bash
# 列出所有插件
curl http://localhost:48080/admin-api/plugin

# 查看插件详情
curl http://localhost:48080/admin-api/plugin/my-plugin

# 查看插件扩展点
curl http://localhost:48080/admin-api/plugin/my-plugin/extensions
```

---

## 10. 总结

OneBase 插件框架是一个功能完善的企业级插件化解决方案，具有以下优势：

### 10.1 核心优势

1. **热插拔**: 运行时动态加载/卸载，无需重启
2. **Spring集成**: 无缝集成Spring生态，开发体验好
3. **开发友好**: DEV模式支持热重载，IDE友好
4. **生产就绪**: 完整的生命周期管理、事件机制、错误处理
5. **扩展性强**: 支持自定义扩展点，易于扩展

### 10.2 适用场景

- **功能扩展**: 动态添加新功能模块
- **行业定制**: 为不同行业提供定制功能
- **第三方集成**: 集成第三方系统和服务
- **A/B测试**: 动态加载不同版本的实现

### 10.3 未来展望

- **更多扩展点**: 支持数据库连接器、流程节点等扩展
- **插件市场**: 建立插件生态和分发机制
- **权限控制**: 细粒度的插件权限管理
- **性能监控**: 插件性能指标和监控

---

**相关文档:**
- [项目概述](./project-overview.md)
- [架构深入分析](./architecture-deep-dive.md)
- [开发指南](./development-guide.md)
- [插件开发手册](./插件开发手册.md)
