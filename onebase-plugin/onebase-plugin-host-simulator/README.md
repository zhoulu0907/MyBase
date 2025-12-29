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

> ⚠️ **重要提示**：`onebase.plugin.mode` 必须严格配置为 `dev`、`staging` 或 `prod` 之一。如果未配置或配置了非法值，模拟器将**无法启动**并报错退出。

#### 配置示例
打开 `start-dev.bat` (Windows) 或 `start-dev.sh` (Linux/Mac)，在 `java -jar ...` 命令后追加参数：

**Windows 示例**:
```batch
java ... -jar ... 
  --onebase.plugin.mode=dev
  --onebase.plugin.dev-class-paths="D:/path1/my-plugin/target/classes，D:/path2/my-plugin/target/classes"
```

**Mac/Linux 示例**:
```bash
java ... -jar ... \
  --onebase.plugin.mode=dev \
  --onebase.plugin.dev-class-paths=/Users/dev/my-plugin/target/classes
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

### 3. 🐛 远程调试配置

启动脚本已默认配置 JVM 调试参数：
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

#### IntelliJ IDEA 远程调试配置步骤

**1. 创建 Remote JVM Debug 配置**
   - 打开：`Run` → `Edit Configurations...`
   - 点击 `+` → 选择 `Remote JVM Debug`

**2. 配置参数**
   ```
   名称: Attach to Plugin Simulator (可自定义)
   调试器模式: 附加到远程 JVM (Attach to remote JVM)
   主机: localhost
   端口: 5005
   选择模块类路径: 你的插件模块名
   ```

**3. 关键配置说明**

| 配置项 | 说明 |
|--------|------|
| **调试器模式** | 必须选择 **"附加到远程 JVM"**，因为模拟器作为服务器监听端口 |
| **使用模块类路径** | **必须选择你的插件模块**（如 `plugin-demo-hello`），否则断点不会生效 |

> **⚠️ 常见错误 1**：如果选择 `<无模块>` 或选择 `onebase-plugin-host-simulator` 模块，断点将无法触发！

> **⚠️ 常见错误 2**：必须先启动模拟器后，再在 IDE 中启动配置好的调试！

**4. 启动调试**
   - 先启动 `start-dev.bat` (或 `.sh`)
   - 在 IDE 中刚配置好的调试器上点击 Debug 按钮（绿色虫子图标）
   - 确认 IDE 底部显示：`Connected to the target VM, address: 'localhost:5005'`

**5. 设置断点**
   - 在**插件项目**（如 `plugin-demo-hello`）的源码中打断点
   - 在方法的**第一行代码**打断点（不是方法签名行）
   - 发送 HTTP 请求触发断点

#### 两种调试器模式的区别

| 模式 | IDE 角色 | 适用场景 | 你的场景 |
|------|----------|----------|----------|
| **附加到远程 JVM** | 客户端（主动连接） | 应用已启动并监听调试端口 | ✅ **使用此模式** |
| **侦听远程 JVM** | 服务器（被动等待） | IDE 先监听，应用启动时连接 | ❌ 不适用 |

#### 调试验证
```bash
# 发送测试请求
curl http://localhost:8080/plugin/hello-plugin/hello

# 如果配置正确，IDE 会在断点处暂停
# 你可以查看变量、单步执行、评估表达式等
```

---

## ⚙️ 运行模式说明

虽然主要使用 Dev 模式，但了解其他模式有助于全流程验证。

### 1. DEV 模式 (开发阶段)
- **配置**: `onebase.plugin.mode=dev`
- **加载策略**: 只加载 `dev-class-paths` 下的扩展点，故必须配置`dev-class-paths`
- **适用场景**:
    - IDE 中直接启动和调试
    - 支持代码热重载
    - 支持断点调试
    - 无需打包 ZIP/JAR
    - 快速开发迭代
- **特点**:
    - IDE 中直接启动
    - 断点调试：完整的 IDE 调试支持
    - 无需打包插件 ZIP/JAR
    - 仅扫描指定的 `dev-class-paths`

### 2. STAGING 模式 (预发布/全生命周期验证)
- **配置**: `onebase.plugin.mode=staging`
- **加载策略**: 只加载 `plugins-dir` 目录的 ZIP/JAR 包
- **适用场景**:
    - 验证插件完整生命周期（加载、启动、停止、卸载、删除）
    - 测试 ClassLoader 隔离
    - 验证 ZIP 包完整性
    - 模拟真实生产环境
- **特点**:
    - 需要打包插件 ZIP/JAR

### 3. PROD 模式 (生产模式)
- **配置**: `onebase.plugin.mode=prod`（默认值）
- **加载策略**: 使用 PF4J 默认策略，加载 `plugins-dir` 目录的 ZIP/JAR 包
- **适用场景**:
    - 生产环境
    - 提供最大灵活性
- **特点**:
    - 需要打包插件 ZIP/JAR

---

## 🔄 插件全生命周期管理

OneBase 插件系统致力于统一和规范化插件的生命周期管理。

### 1. 插件静态加载能力
Shimulator Host 或宿主应用启动时，根据配置参数一次性加载、启动所有插件。
- 适用配置：`onebase.plugin.enable=true`、`onebase.plugin.auto-load=true`、`onebase.plugin.auto-start=true`。

### 2. 插件动态管理能力
在宿主应用运行过程中，支持动态管理插件：
- **动态加载/启动**: 支持上传插件 ZIP 包并动态加载。
- **动态停止/卸载**: 支持在运行时停止或卸载指定插件。
- **热重载 (Dev Mode)**: 开发阶段支持基于文件变更的自动重载。

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
A: 不可以。Dev 模式设计为仅从 `dev-class-paths` 加载散落的 class 文件，以支持热重载。如果需要加载 ZIP，请切换到 staging 模式。

**Q: `dev-class-paths` 必须是绝对路径吗？**
A: 是的，必须使用绝对路径以避免运行目录不同导致的路径错误。

---

## 💡 最佳实践建议

1. **专注业务**：利用热重载特性，专注于业务逻辑的快速迭代。
2. **频发提交**：热重载让你能快速验证小改动，建议小步快跑。
3. **最终验证**：在提交代码前，务必打包一次 ZIP，使用 **staging 模式** 跑一遍，确保打包后也能正常工作（避免因打包配置错误导致类丢失）。

---

## 🎯 系统设计目标

OneBase 插件系统的核心设计目标：
1.  **多模式支持**：支持 DEV/STAGING/PROD 三种运行模式，完美覆盖开发、测试、生产全流程。
2.  **统一生命周期**：统一插件的加载、启动、停止、卸载管理，同时支持静态（启动时）和动态（运行时）管理。
3.  **高内聚低耦合**：模块化设计，职责分离，建立规范的模块合并流程。

