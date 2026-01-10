# OneBase Plugin Archetype

OneBase 插件项目脚手架，帮助开发者快速创建标准的 OneBase 插件项目。

## 功能特性

- ✅ 一键生成完整的插件项目结构
- ✅ 包含两个示例 Controller，展示核心功能
- ✅ 预配置 Maven 构建脚本，开箱即用
- ✅ 自动生成插件主类和配置文件

## 前置要求

- **Maven**: 3.8.0 或更高版本
- **Java**: JDK 17 或更高版本
- **OneBase**: 已安装 OneBase 依赖到本地 Maven 仓库

## 安装 Archetype

首先需要将 archetype 安装到本地 Maven 仓库（仅需执行一次）：

```bash
cd onebase-plugin-archetype
mvn clean install
```

## 使用方法

### 交互式生成项目

```bash
mvn archetype:generate `
  "-DarchetypeGroupId=com.cmsr.onebase" `
  "-DarchetypeArtifactId=onebase-plugin-archetype" `
  "-DarchetypeVersion=1.0.0-SNAPSHOT"
```

Maven 会提示您输入以下参数：
- `groupId`: 您的组织 ID（例如：`com.example`）
- `artifactId`: 插件项目名称（例如：`my-awesome-plugin`）
- `version`: 项目版本（默认：`1.0.0-SNAPSHOT`）
- `package`: Java 包名（默认：与 groupId 相同）

### 非交互式生成项目

**PowerShell (Windows)**:
```powershell
mvn archetype:generate `
  "-DarchetypeGroupId=com.cmsr.onebase" `
  "-DarchetypeArtifactId=onebase-plugin-archetype" `
  "-DarchetypeVersion=1.0.0-SNAPSHOT" `
  "-DgroupId=com.example" `
  "-DartifactId=my-plugin" `
  "-Dversion=1.0.0-SNAPSHOT" `
  "-Dpackage=com.example.plugin" `
  "-DinteractiveMode=false"
```

**Bash (Linux/Mac)**:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.cmsr.onebase \
  -DarchetypeArtifactId=onebase-plugin-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-plugin \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.example.plugin \
  -DinteractiveMode=false
```

> **⚠️ 重要提示 (Windows PowerShell)**  
> 在 PowerShell 中，必须使用引号包裹所有 `-D` 参数值（如 `"-DgroupId=com.example"`），否则会导致参数解析错误。

## 生成的项目结构

```
my-plugin/
├── pom.xml                                    # Maven 构建配置
└── src/main/java/com/example/plugin/
    ├── ContextDemoController.java             # 上下文服务使用示例
    └── HutoolCryptoController.java            # 第三方依赖使用示例
```

## 示例 Controller 说明

### 1. ContextDemoController

演示如何使用 `PluginContextService` 读取和管理插件配置：

- **获取所有配置**: `GET /plugin/{pluginId}/context/all`
- **获取租户ID**: `GET /plugin/{pluginId}/context/tenant-id`
- **获取指定配置**: `GET /plugin/{pluginId}/context/key/{key}`
- **配置应用演示**: `GET /plugin/{pluginId}/context/demo`
- **配置信息总览**: `GET /plugin/{pluginId}/context/info`

### 2. HutoolCryptoController

演示如何使用第三方依赖（Hutool 加密库）：

- **加密测试**: `GET /plugin/{pluginId}/crypto/test?text=xxx`
- **依赖检查**: `GET /plugin/{pluginId}/crypto/check`

## 下一步操作

### 1. 编译项目

```bash
cd my-plugin
mvn clean compile
```

### 2. 打包插件

```bash
mvn clean package
```

打包完成后，会在 `target/` 目录下生成：
- `my-plugin-1.0.0-SNAPSHOT.jar` - 插件 JAR 包
- `my-plugin-1.0.0-SNAPSHOT.zip` - 插件发布包（包含所有依赖）

### 3. 部署插件

将生成的 ZIP 文件复制到 OneBase 插件目录：

```bash
cp target/my-plugin-1.0.0-SNAPSHOT.zip /path/to/onebase/plugins/
```

### 4. 测试插件

启动 OneBase 应用后，访问插件接口：

```bash
# 测试上下文服务
curl http://localhost:8080/plugin/my-plugin/context/info

# 测试加密功能
curl http://localhost:8080/plugin/my-plugin/crypto/test?text=Hello
```

## 自定义开发

### 添加新的 Controller

1. 创建新的 Java 类，实现 `HttpHandler` 接口
2. 使用 `@RestController` 和 `@RequestMapping` 注解
3. 重新编译和打包

### 添加依赖

在 `pom.xml` 的 `<dependencies>` 中添加所需依赖：

```xml
<dependency>
    <groupId>your.group</groupId>
    <artifactId>your-artifact</artifactId>
    <version>1.0.0</version>
</dependency>
```

**注意**：
- 使用 `<scope>provided</scope>` 表示由宿主应用提供
- 不使用 scope 或使用 `<scope>compile</scope>` 表示打包到插件 ZIP 中

### 修改插件配置

编辑 `pom.xml` 中的插件配置参数：

```xml
<configuration>
    <pluginId>my-custom-plugin-id</pluginId>
    <pluginDescription>我的自定义插件描述</pluginDescription>
    <pluginProvider>Your Organization</pluginProvider>
</configuration>
```