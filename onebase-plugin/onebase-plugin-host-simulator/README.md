# OneBase 插件开发模拟器 (Plugin Host Simulator)

本工具专为 OneBase 插件开发者设计，支持独立的插件开发、调试和热重载。作为插件开发者，你无需运行庞大的 OneBase 后端主服务，只需使用本轻量级模拟器即可快速开发和验证插件功能。

## 🚀 快速开始

### 1. 准备工作
- 获取 `onebase-plugin-host-simulator` JAR 包及启动脚本 `start-dev.bat` (Windows) 或 `start-dev.sh` (Mac/Linux)
- 准备你的插件项目（Maven 工程）

### 2. 配置启动参数
请直接修改启动脚本内的参数 (`start-dev.bat` 或 `start-dev.sh`)，可选参数如下。

#### 核心配置参数说明
| 参数项 | 说明                                                        | 示例                               |
| :--- |:----------------------------------------------------------|:---------------------------------|
| `onebase.plugin.mode` | 运行模式 (可选值: `dev`, `staging`，`prod`)                       | `dev`                            |
| `onebase.plugin.dev-class-paths` | 仅dev模式有效<br> **[关键]** 插件编译输出目录 (绝对路径)。<br>支持配置多个，用英文逗号分隔。 | `D:/work/plugin1/target/classes` |
| `onebase.plugin.plugins-dir` | 插件 ZIP 包存放目录 (绝对路径，仅 staging、Prod 模式有效)                   | `D:/plugins`                     |
| `onebase.plugin.auto-load` | 是否自动加载插件 (默认为 `true`)                                     | `true`                           |
| `onebase.plugin.auto-start` | 加载后是否自动启动 (默认为 `true`)                                    | `true`                           |

#### 配置示例
打开 `start-dev.bat` (Windows) 或 `start-dev.sh` (Linux/Mac)，在 `java -jar ...` 命令后追加参数：

**Windows 示例**:
```batch
java ... -jar ... ^
  --onebase.plugin.mode=dev ^
  --onebase.plugin.dev-class-paths=D:/workspace/my-plugin/target/classes
```

**Mac/Linux 示例**:
```bash
java ... -jar ... \
  --onebase.plugin.mode=dev \
  --onebase.plugin.dev-class-paths=/Users/dev/workspace/my-plugin/target/classes
```

### 3. 启动开发环境
运行启动脚本：
- **Windows**: 双击 `start-dev.bat`
- **Mac/Linux**: 运行 `./start-dev.sh`

模拟器启动后：
- HTTP 服务监听端口：`8080`
- 远程调试监听端口：`5005`

---

## 💻 核心开发工作流程

我们推荐使用 **"独立模拟器 + 远程调试 + 热重载"** 的高效融合工作流。

**前提**：你拥有模拟器 JAR 包和自己的插件源码。

1. **编写代码**：在你的 IDE（IntelliJ IDEA / Eclipse / VS Code）中打开插件项目。
2. **启动模拟器**：确认 `dev-class-paths` 指向了你项目的 `target/classes`，然后运行 `start-dev` 脚本。
3. **连接调试**：
   - 在 IDE 中创建一个 **Remote JVM Debug** 配置。
   - 连接到 `localhost:5005`。
   - 此时你可以像开发本地应用一样设置断点调试。
4. **修改与热重载**：
   - 修改你的插件 Java 代码（例如修改一个 Controller 方法）。
   - 在 IDE 中点击 **Compile** 或 **Build**（确保生成新的 `.class` 文件）。
   - 模拟器监听到文件变化，**自动触发热重载**，应用新逻辑，无需重启！
5. **验证结果**：通过 Postman 或浏览器访问接口，验证修改是否生效。

---

## 🛠️ 三大核心特性详解

### 1. Spring 依赖注入支持
在 Dev 模式下，OneBase 插件系统与 Spring 容器深度集成。
- **自动注册 Bean**：你的 `HttpHandler` 和其他组件会被自动注册为 Spring Bean。
- **依赖注入**：完全支持 `@Resource`, `@Autowired`。你可以轻松注入 OneBase 提供的核心服务（如 `UserService`）或 Spring 的 `ApplicationContext`。

### 2. 🔥 智能热重载 (Hot Reload)
告别反复重启！模拟器内置了基于文件监听的热重载机制。
- **监听机制**：实时监控 `dev-class-paths` 下的 `.class` 文件。
- **防抖设计**：智能合并短时间内的多次编译，避免重复重载。
- **精确更新**：支持按类卸载和注册路由，最大程度保证运行时稳定性。

### 3. 🐛 开箱即用的远程调试
启动脚本已默认配置 JVM 调试参数：
`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`
这意味着你可以随时连接调试，观察变量状态，排查疑难问题。

---

## ⚙️ 运行模式说明

虽然主要使用 Dev 模式，但了解其他模式有助于全流程验证。

### 1. DEV 模式 (开发阶段)
- **配置**: `onebase.plugin.mode=dev`
- **加载源**: 也就是 `dev-class-paths` 指定的磁盘目录。
- **特点**: 支持热重载、IDE 调试。

### 2. STAGING 模式 (打包装箱验证)
- **配置**: `onebase.plugin.mode=staging`
- **加载源**: `plugins-dir` 目录下的 **ZIP 包**。
- **场景**: 当你开发完成，打包成 ZIP 后，放入指定目录，重启模拟器使用此模式。用于验证插件打包结构是否正确，以及插件启动、停止、加载、卸载、重启等功能是否符合预期。

### 3. PROD 模式 (生产模拟)
- **配置**: `onebase.plugin.mode=prod`
- **场景**: 模拟真实的生产环境加载策略（混合策略）。通常开发者较少使用。

---

## ❓ 常见问题 (FAQ)

**Q: 我修改了代码并保存了，为什么热重载没触发？**
A: 模拟器监听的是 `.class` 文件，而不是 `.java` 源码。请确保你在 IDE 中执行了 **Compile** (Ctrl+F9 / Cmd+F9) 或 **Build** 操作，生成了新的 class 文件。

**Q: 远程调试连接不上？**
A: 
1. 检查模拟器窗口是否已关闭或报错退出。
2. 检查端口 `5005` 是否被占用。
3. 确保你的 IDE 配置的主机是 `localhost`。

**Q: 可以在 Dev 模式下加载 ZIP 包吗？**
A: 不可以。Dev 模式设计为仅从 `dev-class-paths` 加载散落的 class 文件，以支持热重载。如果需要加载 ZIP，请切换到 Staging 模式。

**Q: `dev-class-paths` 必须是绝对路径吗？**
A: 是的，必须使用绝对路径以避免运行目录不同导致的路径错误。

---

## 💡 最佳实践建议

1. **专注业务**：利用热重载特性，专注于业务逻辑的快速迭代。
2. **频发提交**：热重载让你能快速验证小改动，建议小步快跑。
3. **最终验证**：在提交代码前，务必打包一次 ZIP，使用 **staging 模式** 跑一遍，确保打包后也能正常工作（避免因打包配置错误导致类丢失）。
