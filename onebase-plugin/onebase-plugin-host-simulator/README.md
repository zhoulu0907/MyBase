# OneBase Plugin Host Simulator

OneBase 插件宿主模拟器 - 融合了快速调试和完整验证两种模式的统一解决方案。

## 🎯 设计目标

解决插件开发者的两大核心需求：
1. **快速开发调试**：无需打包，5-10秒启动，支持断点调试和热重载
2. **完整生命周期验证**：打包验证，测试插件的加载、启动、停止、卸载全流程

## 🚀 双模式支持

### 模式1：IDE 快速调试模式（推荐开发使用）⭐⭐⭐⭐⭐

**适用场景**：日常开发、快速调试、功能迭代

**启动方式**：
```bash
# 方式1：IDE 中直接运行（最推荐）
# 1. 找到 PluginHostSimulatorApplication.java
# 2. 右键选择 Debug 或 Run
# 3. 等待 5-10 秒启动完成

# 方式2：Maven 命令行
mvn spring-boot:run
```

**特点**：
- ✅ **启动快**：5-10秒即可启动
- ✅ **无需打包**：直接扫描依赖中的插件（如 onebase-plugin-demo）
- ✅ **断点调试**：完整的 IDE 调试支持
- ✅ **热重载**：修改代码后快速重启验证

**配置文件**：`application-dev.yml`

### 模式2：完整验证模式（推荐集成测试使用）⭐⭐⭐⭐

**适用场景**：集成测试、完整验证、发布前测试

**启动方式**：
```bash
# 1. 打包模拟器
mvn clean package -DskipTests

# 2. 准备插件 ZIP 包（放入 plugins 目录）
# 3. 启动模拟器
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**特点**：
- ✅ **完整生命周期**：测试插件的加载、启动、停止、卸载全流程
- ✅ **真实环境**：模拟生产环境的插件加载方式

**配置文件**：`application-prod.yml`

## 📦 快速开始

### 第一次使用（IDE 快速调试）

```bash
# 1. 在 IDE 中打开 PluginHostSimulatorApplication.java
# 2. 右键选择 Debug 'PluginHostSimulatorApplication.main()'
# 3. 访问 http://localhost:8080
```

### 测试插件功能

```bash
# 健康检查
curl http://localhost:8080/

# 获取所有插件列表
curl http://localhost:8080/api/plugin/list

# 访问插件的 HTTP 端点
curl http://localhost:8080/plugin/demo-plugin/hello
```

## 🔧 API 说明

| 端点 | 方法 | 说明 |
|------|------|------|
| `/` | GET | 应用首页和健康检查 |
| `/api/plugin/list` | GET | 获取所有插件列表 |
| `/api/plugin/{pluginId}` | GET | 获取指定插件信息 |
| `/plugin/{plugin-id}/{path}` | * | 插件 HTTP 端点 |
