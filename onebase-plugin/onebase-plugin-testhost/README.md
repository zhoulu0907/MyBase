# OneBase Plugin Test Host

一个简单的 OneBase 插件系统测试宿主服务器，用于验证插件 SDK、启动器和 Maven 插件的功能。

## 🚀 快速开始

### 1. 编译项目

```bash
mvn clean package
```

### 2. 启动服务器

**方式1：使用 Spring Boot Maven 插件**
```bash
mvn spring-boot:run
```

**方式2：IDE中直接运行**
在IDE中右键运行 `PluginTestHostApplication.java` 的 main 方法。

**方式3：运行 JAR 包**
```bash
java -jar target/onebase-plugin-testhost-1.0.0-SNAPSHOT.jar
```

### 3. 访问应用

应用启动后，访问以下地址：

- **应用主页**：http://localhost:8080
- **健康检查**：http://localhost:8080/health
- **插件列表**：http://localhost:8080/plugin/list

## 📋 API 接口

### 插件管理 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/plugin/list` | GET | 获取所有插件列表 |
| `/plugin/{pluginId}/info` | GET | 获取插件详细信息 |
| `/plugin/{pluginId}/start` | POST | 启动插件 |
| `/plugin/{pluginId}/stop` | POST | 停止插件 |
| `/plugin/{pluginId}/reload` | POST | 重新加载插件 |
| `/plugin/start-all` | POST | 启动所有插件 |
| `/plugin/stop-all` | POST | 停止所有插件 |

### 系统 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/` | GET | 应用主页和 API 列表 |
| `/health` | GET | 健康检查 |

## 🔌 使用插件

### 1. 部署插件

将编译好的插件 ZIP 包放入项目根目录的 `plugins` 文件夹：

```
onebase-plugin-testhost/
├── pom.xml
├── src/
├── target/
└── plugins/              # ← 将插件 ZIP 包放这里
    └── onebase-plugin-demo-1.0.0.zip
```

### 2. 启动应用

应用启动时会自动扫描并加载 `plugins` 目录下的所有 ZIP 包。

### 3. 测试插件功能

启动应用后，可以通过以下方式测试插件：

**获取已加载的插件列表**
```bash
curl http://localhost:8080/plugin/list
```

**访问 Demo 插件的 HTTP 接口**
```bash
# 获取插件信息
curl http://localhost:8080/plugin/demo-plugin/api/info

# 获取插件状态
curl http://localhost:8080/plugin/demo-plugin/api/status

# 处理数据
curl -X POST http://localhost:8080/plugin/demo-plugin/api/process \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}'
```

**启动/停止插件**
```bash
# 启动插件
curl -X POST http://localhost:8080/plugin/demo-plugin/start

# 停止插件
curl -X POST http://localhost:8080/plugin/demo-plugin/stop

# 重新加载插件
curl -X POST http://localhost:8080/plugin/demo-plugin/reload
```

## ⚙️ 配置说明

编辑 `src/main/resources/application.yml` 配置文件：

```yaml
onebase:
  plugin:
    enabled: true              # 是否启用插件系统
    plugins-dir: plugins       # 插件目录（相对于应用工作目录）
    auto-load: true            # 是否自动加载插件
    auto-start: true           # 是否自动启动插件
```

## 📂 项目结构

```
onebase-plugin-testhost/
├── pom.xml                                    # Maven 配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cmsr/onebase/plugin/testhost/
│   │   │       ├── PluginTestHostApplication.java      # 应用入口
│   │   │       └── controller/
│   │   │           ├── HealthController.java           # 健康检查
│   │   │           └── PluginManagementController.java  # 插件管理 API
│   │   └── resources/
│   │       └── application.yml               # 应用配置
│   └── test/
└── plugins/                                   # 插件目录（运行时创建）
    └── [插件 ZIP 包]
```

## 🧪 测试场景

### 场景1：测试插件加载

1. 将 `onebase-plugin-demo-1.0.0.zip` 放入 `plugins` 目录
2. 启动应用
3. 访问 `http://localhost:8080/plugin/list` 验证插件已加载

### 场景2：测试插件 HTTP 接口

1. 启动应用（Demo 插件应已加载）
2. 访问以下接口：
   - `GET /plugin/demo-plugin/api/info`
   - `GET /plugin/demo-plugin/api/status`
   - `POST /plugin/demo-plugin/api/process`

### 场景3：测试插件生命周期

1. 启动应用
2. 调用 `POST /plugin/demo-plugin/stop` 停止插件
3. 验证插件状态已变为 STOPPED
4. 调用 `POST /plugin/demo-plugin/start` 重新启动
5. 验证插件状态变回 STARTED

### 场景4：测试多插件

1. 在 `plugins` 目录下放置多个插件 ZIP 包
2. 启动应用
3. 访问 `GET /plugin/list` 查看所有插件
4. 分别控制各个插件的启停

## 🔍 日志输出

应用运行时会输出详细的日志，包括：

- 插件加载信息
- 扩展点扫描结果
- HTTP 路由注册信息
- 事件分发日志

日志级别配置在 `application.yml` 的 `logging.level` 部分。

## 💡 常见问题

**Q: 应用无法找到插件目录？**  
A: 确保 `plugins` 目录存在且与应用工作目录的相对路径正确。可以使用绝对路径配置 `onebase.plugin.plugins-dir`。

**Q: 插件无法加载？**  
A: 检查插件 ZIP 包是否正确打包（需包含 `plugin.properties` 和 `META-INF/extensions.idx`）。

**Q: 访问插件接口返回 404？**  
A: 确保插件已被正确加载（通过 `/plugin/list` 验证），且插件的 HTTP 处理器遵循 `/plugin/{pluginId}/` 路由规范。

**Q: 如何查看详细的调试日志？**  
A: 在 `application.yml` 中设置 `logging.level.com.cmsr.onebase: DEBUG`。

## 📚 相关文档

- [onebase-plugin-sdk](../onebase-plugin-sdk/) - 插件 SDK 开发指南
- [onebase-plugin-demo](../onebase-plugin-demo/) - 插件开发示例
- [onebase-spring-boot-starter-plugin](../onebase-spring-boot-starter-plugin/) - 插件运行时
