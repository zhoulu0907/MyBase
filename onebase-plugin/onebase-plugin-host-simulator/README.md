# 插件运行模式说明

## 三种运行模式

### 1. DEV模式（开发模式）
- **配置**: `onebase.plugin.mode=dev`
- **加载策略**: 只加载classpath下的扩展点
- **适用场景**: 
  - IDE中直接启动和调试
  - 支持断点调试
  - 无需打包ZIP/JAR
  - 快速开发迭代
- **特点**:
  - IDE中直接启动
  - 断点调试：完整的 IDE 调试支持
  - 无需打包插件ZIP/JAR
  - 仅扫描classpath

### 2. STAGING模式（预发布模式/全生命周期验证模式）
- **配置**: `onebase.plugin.mode=staging`
- **加载策略**: 只加载plugin目录的ZIP/JAR包，不扫描classpath
- **适用场景**:
  - 验证插件完整生命周期（加载、启动、热插拔、卸载）
  - 测试ClassLoader隔离
  - 验证ZIP包完整性
  - 模拟真实生产环境
- **特点**:
  - 需要打包插件ZIP/JAR
  - 仅扫描pugin目录

### 3. PROD模式（生产模式）
- **配置**: `onebase.plugin.mode=prod`（默认值）
- **加载策略**: 使用PF4J默认策略，同时扫描classpath和plugin目录
- **适用场景**:
  - 生产环境
  - 提供最大灵活性
- **特点**:
  - 需要打包插件ZIP/JAR
  - classpath + plugin目录都扫描
  - 灵活但不同加载器可能重复加载扩展点

## 配置方式
### 方式1：配置文件激活方式
#### 第一步：在 `application.yml` 中配置默认Profile
```yaml
spring:
  profiles:
    default: dev # 可选值：dev, staging, prod
```

#### 第二步：使用Spring Profile
```yaml
# application-dev.yml
onebase:
  plugin:
    mode: dev

# application-staging.yml
onebase:
  plugin:
    mode: staging

# application-prod.yml
onebase:
  plugin:
    mode: prod
```

### 方式2：启动参数激活方式
```bash
java -jar app.jar --onebase.plugin.mode=staging
```

## 模式验证

### 有效值
- `dev` (不区分大小写)
- `staging` (不区分大小写)
- `prod` (不区分大小写)
- 空值或未配置：默认使用 `prod`

### 无效值处理
如果配置了无效的模式值（如 `test`, `invalid` 等），系统将：
1. 抛出 `IllegalArgumentException` 异常
2. 拒绝启动

## 测试无效模式

### 测试步骤
1. 修改 `application.yml`:
```yaml
onebase:
  plugin:
    mode: invalid-mode
```

2. 启动应用，预期结果：
```
***************************
APPLICATION FAILED TO START
***************************

Description:

Binding to target org.springframework.boot.context.properties.bind.BindException:
Failed to bind properties under 'onebase.plugin' to com.cmsr.onebase.plugin.runtime.config.PluginProperties

Reason: java.lang.IllegalArgumentException: onebase.plugin.mode 配置错误: xxx，仅支持 [dev, staging, prod]，请检查配置！
```

## 最佳实践

1. **开发阶段**: 使用 `dev` 模式
2. **集成测试**: 使用 `staging` 模式验证插件生命周期
3. **生产部署**: 使用 `prod` 模式
