# 快速启动指南

## 🎯 目标
使用 `onebase-plugin-testhost` 测试 OneBase 插件系统的 SDK、Starter 和 Maven 插件功能。

## 📦 前置条件

- ✅ 已编译 onebase-plugin 及其所有子模块
- ✅ onebase-plugin-testhost JAR 包已生成

## 🚀 启动步骤

### 第 1 步：准备插件目录

在项目根目录创建 `plugins` 文件夹：

```powershell
cd d:\cmsr\10_cmsr\CodingSpace\Java\onebase-v3-be\onebase-plugin\onebase-plugin-testhost

# 创建插件目录
mkdir plugins
```

### 第 2 步：部署示例插件

将编译好的 Demo 插件复制到 plugins 目录：

```powershell
# 复制 Demo 插件到 plugins 目录
Copy-Item "d:\cmsr\10_cmsr\CodingSpace\Java\onebase-v3-be\onebase-plugin\onebase-plugin-demo\target\onebase-plugin-demo-1.0.0.zip" `
  -Destination "plugins\"
```

验证插件已复制：
```powershell
dir plugins\
```

你应该看到：
```
    目录: d:\cmsr\10_cmsr\CodingSpace\Java\onebase-v3-be\onebase-plugin\onebase-plugin-testhost\plugins

Mode                 LastWriteTime         Length Name
----                 -----------         ------ ----
-a---           2025-12-13    18:10       10850 onebase-plugin-demo-1.0.0.zip
```

### 第 3 步：启动服务器

有三种启动方式可选：

#### 方式 A：使用 Spring Boot Maven 插件（推荐用于开发）

```powershell
cd d:\cmsr\10_cmsr\CodingSpace\Java\onebase-v3-be\onebase-plugin\onebase-plugin-testhost

mvn spring-boot:run
```

#### 方式 B：运行生成的 JAR 包（推荐用于测试）

```powershell
cd d:\cmsr\10_cmsr\CodingSpace\Java\onebase-v3-be\onebase-plugin\onebase-plugin-testhost

java -jar target/onebase-plugin-testhost-1.0.0-SNAPSHOT.jar
```

#### 方式 C：在 IDE 中运行

在 IDE 中打开 `PluginTestHostApplication.java`，右键选择"Run"或"Debug"。

### 第 4 步：验证应用启动

应该看到类似以下的日志输出：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

... 日志输出 ...

2025-12-13T18:15:00.000+08:00  INFO 12345 --- [main] c.c.o.p.t.PluginTestHostApplication: Started PluginTestHostApplication in 5.123 seconds
```

当看到 `Started PluginTestHostApplication` 时，应用已成功启动。

## 🧪 测试插件功能

应用启动后（默认端口 8080），可以进行以下测试：

### 1️⃣ 访问应用主页

```powershell
curl http://localhost:8080
```

响应示例：
```json
{
  "status": "ok",
  "app": "OneBase Plugin Test Host",
  "version": "1.0.0",
  "description": "用于测试 OneBase 插件系统的宿主服务器",
  "apis": { ... }
}
```

### 2️⃣ 获取插件列表

```powershell
curl http://localhost:8080/plugin/list
```

响应示例（插件已加载）：
```json
{
  "success": true,
  "plugins": [
    {
      "pluginId": "demo-plugin",
      "description": "OneBase插件开发示例",
      "version": "1.0.0",
      "state": "STARTED"
    }
  ],
  "count": 1
}
```

### 3️⃣ 访问插件的 HTTP 接口

```powershell
# 获取插件信息
curl http://localhost:8080/plugin/demo-plugin/api/info

# 获取插件状态
curl http://localhost:8080/plugin/demo-plugin/api/status

# 处理数据（POST 请求）
$body = @{ test = "data" } | ConvertTo-Json
curl -X POST http://localhost:8080/plugin/demo-plugin/api/process `
  -ContentType "application/json" `
  -Body $body
```

### 4️⃣ 测试插件生命周期

```powershell
# 停止插件
curl -X POST http://localhost:8080/plugin/demo-plugin/stop

# 重新启动插件
curl -X POST http://localhost:8080/plugin/demo-plugin/start

# 重新加载插件
curl -X POST http://localhost:8080/plugin/demo-plugin/reload
```

### 5️⃣ 批量控制插件

```powershell
# 启动所有插件
curl -X POST http://localhost:8080/plugin/start-all

# 停止所有插件
curl -X POST http://localhost:8080/plugin/stop-all
```

## 📊 测试场景

### 场景 1：验证插件自动加载

**目标**：验证应用启动时自动加载并启动插件

**步骤**：
1. 启动应用
2. 查看日志，确认出现"已加载 X 个插件"和"已启动 X 个插件"
3. 访问 `/plugin/list` 确认插件状态为 STARTED

### 场景 2：验证 HTTP 处理器

**目标**：验证插件的 HTTP 接口可以正确访问

**步骤**：
1. 启动应用
2. 访问 `/plugin/demo-plugin/api/info`
3. 验证返回正确的 JSON 响应
4. 访问 `/plugin/demo-plugin/api/process` 并发送 POST 数据

### 场景 3：验证事件监听器

**目标**：验证事件监听器能够捕获平台事件

**步骤**：
1. 启动应用
2. 观察应用日志
3. Demo 插件的 `DataChangeListener` 应该在相关事件发生时输出日志

### 场景 4：验证数据处理器

**目标**：验证数据处理器能够正确处理数据

**步骤**：
1. 启动应用
2. 调用数据处理相关的 API（如果实现）
3. 验证数据已按预期处理

### 场景 5：验证插件隔离

**目标**：验证多个插件可以独立运行，互不影响

**步骤**：
1. 在 plugins 目录放置多个插件 ZIP 包
2. 启动应用
3. 访问 `/plugin/list` 确认所有插件都已加载
4. 分别访问各个插件的 HTTP 接口
5. 停止其中一个插件，验证其他插件仍正常运行

## 📝 日志查看

应用输出的日志包含了插件加载、扩展点注册等重要信息。

### 查找插件加载日志

搜索日志中的以下关键词：

```
加载插件:
已加载 X 个插件
已启动 X 个插件
发现扩展点:
路由校验通过:
共发现 X 个扩展点实现类
```

### 调整日志级别

编辑 `src/main/resources/application.yml`，将日志级别改为 DEBUG：

```yaml
logging:
  level:
    com.cmsr.onebase: DEBUG
```

然后重新启动应用。

## 🔧 故障排查

### 问题 1：应用无法启动

**症状**：启动时报错或无响应

**检查清单**：
- [ ] 端口 8080 是否被占用？改用其他端口：`--server.port=8081`
- [ ] Java 版本是否为 17 或更高？运行 `java -version` 检查
- [ ] 依赖是否下载完整？尝试 `mvn clean package`

### 问题 2：插件无法加载

**症状**：日志显示"已加载 0 个插件"

**检查清单**：
- [ ] plugins 目录是否存在？
- [ ] plugins 目录中是否有 ZIP 文件？
- [ ] ZIP 文件是否为有效的插件包（包含 plugin.properties 和 META-INF/extensions.idx）？
- [ ] 检查日志中是否有加载错误信息

### 问题 3：访问插件接口返回 404

**症状**：`curl http://localhost:8080/plugin/demo-plugin/api/info` 返回 404

**检查清单**：
- [ ] 插件是否已加载？访问 `/plugin/list` 验证
- [ ] 插件状态是否为 STARTED？
- [ ] 路由路径是否正确（应以 `/plugin/{pluginId}/` 开头）？
- [ ] 检查日志中是否有路由注册信息

### 问题 4：无法停止应用

**症状**：按 Ctrl+C 无响应

**解决方法**：
- 直接关闭终端窗口，或
- 另开终端运行：`curl -X POST http://localhost:8080/plugin/stop-all`

## 🎓 下一步

- 📖 阅读 [SDK 文档](../onebase-plugin-sdk/README.md) 了解如何开发插件
- 🔍 查看 [Demo 示例](../onebase-plugin-demo/README.md) 学习具体实现
- 🛠️ 开发自己的插件并在此测试宿主上验证

## 📞 反馈

如遇到问题，请检查：
1. 应用日志输出
2. 插件 ZIP 包的完整性
3. 防火墙是否阻止了 8080 端口
