# OneBase Plugin Sandbox

> OneBase 插件沙箱安全隔离模块
>
> 通过自定义 SecurityManager 实现插件代码的权限控制和资源隔离

---

## 概述

`onebase-plugin-sandbox` 是 OneBase 插件系统的安全隔离模块，提供以下核心能力：

- ✅ **权限控制**: 文件访问、网络操作、反射等
- ✅ **资源限制**: 执行超时、线程数限制
- ✅ **故障隔离**: 插件异常不影响宿主系统
- ✅ **灵活配置**: 支持全局和插件级别配置

---

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sandbox</artifactId>
    <version>${revision}</version>
</dependency>
```

### 2. 启用沙箱

在 `application.yml` 中配置：

```yaml
onebase:
  plugin:
    sandbox:
      enabled: true
      max-threads: 100
      # 默认权限配置（严格模式）
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
```

### 3. 使用沙箱执行器

```java
@Service
public class PluginService {

    @Autowired
    private PluginSandboxExecutor sandboxExecutor;

    public void executePlugin(String pluginId) {
        // 使用默认权限执行
        SandboxResult<String> result = sandboxExecutor.execute(pluginId, () -> {
            // 插件代码
            return "执行成功";
        });

        if (result.isSuccess()) {
            log.info("插件 {} 执行成功，耗时: {}ms",
                    pluginId, result.getDuration());
        } else if (result.isSecurityViolation()) {
            log.warn("插件 {} 安全违规: {}",
                    pluginId, result.getError());
        } else if (result.isTimeout()) {
            log.warn("插件 {} 执行超时", pluginId);
        } else {
            log.error("插件 {} 执行失败: {}",
                    pluginId, result.getError());
        }
    }
}
```

### 4. 自定义权限

```java
// 创建宽松权限配置
PluginPermissions permissions = new PluginPermissions();
permissions.setAllowFileRead(true);
permissions.setAllowNetwork(true);
permissions.setMaxExecutionTime(30000);  // 30秒

SandboxResult<String> result = sandboxExecutor.execute(
        pluginId,
        () -> {
            // 插件代码
            return "执行成功";
        },
        permissions  // 自定义权限
);
```

---

## 配置说明

### 全局配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enabled` | boolean | true | 是否启用沙箱 |
| `max-threads` | int | 100 | 全局最大线程数 |

### 默认权限配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `allow-file-read` | boolean | false | 是否允许文件读取 |
| `allow-file-write` | boolean | false | 是否允许文件写入 |
| `allow-file-delete` | boolean | false | 是否允许文件删除 |
| `allow-network` | boolean | false | 是否允许网络操作 |
| `allow-reflection` | boolean | false | 是否允许反射 |
| `allow-create-classloader` | boolean | false | 是否允许创建类加载器 |
| `allow-modify-thread` | boolean | false | 是否允许修改线程 |
| `allow-modify-threadgroup` | boolean | false | 是否允许修改线程组 |
| `max-execution-time` | long | 60000 | 最大执行时间（毫秒） |
| `max-threads` | int | 10 | 最大线程数 |

### 插件特定权限配置

```yaml
onebase:
  plugin:
    sandbox:
      plugin-permissions:
        trusted-plugin:
          allow-file-read: true
          allow-network: true
          allow-reflection: true
          max-execution-time: 120000  # 2分钟
```

---

## API 参考

### PluginSandboxExecutor

**核心方法:**

```java
// 使用默认权限执行
<T> SandboxResult<T> execute(String pluginId, Callable<T> callable)

// 使用自定义权限执行
<T> SandboxResult<T> execute(String pluginId,
                            Callable<T> callable,
                            PluginPermissions permissions)

// 执行 Runnable
SandboxResult<Void> execute(String pluginId, Runnable runnable)
```

**返回值:**

```java
public class SandboxResult<T> {
    boolean isSuccess();          // 是否成功
    T getData();                   // 结果数据
    String getError();            // 错误信息
    long getDuration();           // 执行时长（毫秒）
    boolean isTimeout();          // 是否超时
    boolean isSecurityViolation(); // 是否安全违规
}
```

### PluginPermissions

**预设配置:**

```java
// 严格权限（拒绝所有）
PluginPermissions strict = PluginPermissions.strict();

// 宽松权限（允许所有）
PluginPermissions permissive = PluginPermissions.permissive();

// 默认权限
PluginPermissions defaults = PluginPermissions.defaultPermissions();
```

**自定义配置:**

```java
PluginPermissions permissions = new PluginPermissions();
permissions.setAllowFileRead(true);
permissions.setAllowNetwork(true);
permissions.setMaxExecutionTime(30000);
permissions.setMaxThreads(20);
```

---

## 测试验证

运行测试验证沙箱功能：

```bash
cd onebase-plugin/onebase-plugin-sandbox
mvn test
```

**测试场景:**

| 测试场景 | 说明 |
|---------|------|
| 文件读取控制 | 验证插件无法读取敏感文件 |
| 文件写入控制 | 验证插件无法写入文件 |
| 网络访问控制 | 验证插件无法访问网络 |
| 反射访问控制 | 验证插件无法使用反射 |
| 执行超时控制 | 验证插件执行超时被中断 |
| 线程数限制 | 验证插件创建线程数受限 |
| 宿主代码不受限 | 验证宿主代码不受沙箱限制 |

---

## 安全最佳实践

### 1. 最小权限原则

```yaml
# 推荐: 默认拒绝所有权限
default-permissions:
  allow-file-read: false
  allow-network: false
  # ... 其他权限都设置为 false
```

### 2. 插件分级管理

```yaml
# 内部开发插件（信任）
internal-plugin:
  allow-file-read: true
  allow-network: true
  allow-reflection: true

# 第三方插件（不信任）
third-party-plugin:
  allow-file-read: false
  allow-network: false
  allow-reflection: false
```

### 3. 资源限制

```yaml
default-permissions:
  max-execution-time: 60000  # 限制执行时间
  max-threads: 10            # 限制线程数
```

### 4. 监控和日志

```java
if (!result.isSuccess()) {
    // 记录安全违规
    if (result.isSecurityViolation()) {
        securityAuditService.record(
                pluginId,
                "SECURITY_VIOLATION",
                result.getError()
        );
    }

    // 发送告警
    alertService.sendAlert(
            "插件安全违规",
            String.format("插件 %s 违反安全规则: %s",
                    pluginId, result.getError())
    );
}
```

---

## 架构设计

### 类图

```
┌─────────────────────────────────────────────────────┐
│              PluginSandboxExecutor                 │
│  - execute(pluginId, callable, permissions)        │
│  - shutdown()                                      │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────┐
│            PluginSecurityManager                   │
│  - enterSandbox(pluginId)                          │
│  - exitSandbox()                                   │
│  - checkPermission(Permission)                    │
│  - configurePlugin(pluginId, permissions)          │
└─────────────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────┐
│               PluginPermissions                     │
│  - allowFileRead                                  │
│  - allowNetwork                                   │
│  - allowReflection                                │
│  - maxExecutionTime                               │
│  - maxThreads                                     │
└─────────────────────────────────────────────────────┘
```

### 执行流程

```
1. 配置权限
   securityManager.configurePlugin(pluginId, permissions)

2. 进入沙箱
   securityManager.enterSandbox(pluginId)

3. 执行代码（带超时和线程数检查）
   executorService.submit(callable)
   │
   ├─ 检查线程数限制
   ├─ 提交到线程池
   └─ 等待结果（带超时）

4. 权限检查（自动触发）
   │
   ├─ 文件操作 → checkFilePermission()
   ├─ 网络操作 → checkNetworkPermission()
   ├─ 反射操作 → checkReflectionPermission()
   └─ 运行时操作 → checkRuntimePermission()

5. 退出沙箱
   securityManager.exitSandbox()

6. 返回结果
   SandboxResult<T>
```

---

## 限制和注意事项

### 1. Java SecurityManager 已弃用

**问题:** Java SecurityManager 从 Java 17 开始已被弃用，未来版本可能移除。

**影响:** 需要关注 Java 版本升级，可能需要迁移到替代方案。

**建议:**
- 短期: 继续使用 SecurityManager（Java 17-21 仍可用）
- 中期: 考虑进程隔离方案（Docker）
- 长期: 关注 Java 模块化（JPMS）和 WASM 方案

### 2. 资源限制有限

**不支持:**
- CPU 使用率限制
- 内存使用量限制

**替代方案:**
- 使用 `-Xmx` 限制 JVM 堆内存
- 使用容器限制 CPU 和内存
- 使用监控工具（JMX、Micrometer）跟踪资源使用

### 3. 性能开销

**开销来源:**
- 权限检查（每次安全操作）
- 线程上下文切换
- 线程池管理

**优化建议:**
- 只对插件代码启用沙箱
- 宿主代码绕过检查
- 使用合理的超时时间
- 避免频繁的沙箱进入/退出

---

## 未来方向

### 短期（1-2个月）

- [ ] 完善单元测试覆盖率
- [ ] 添加性能基准测试
- [ ] 完善文档和示例

### 中期（3-6个月）

- [ ] 集成到 OneBase 插件管理器
- [ ] 添加资源监控
- [ ] 实现沙箱管理 API

### 长期（6-12个月）

- [ ] 考虑进程隔离方案（Docker）
- [ ] 研究 WASM 方案
- [ ] 建立插件安全认证体系

---

## 相关文档

- [插件安全分析](../../../docs/plugin-security-analysis.md)
- [插件安全澄清](../../../docs/plugin-sandbox-clarification.md)
- [插件架构深入分析](../../../docs/plugin-architecture-deep-dive.md)

---

## 许可证

Copyright (c) 2025 OneBase. All rights reserved.
