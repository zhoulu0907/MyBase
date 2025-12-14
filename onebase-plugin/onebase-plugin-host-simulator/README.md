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

#### 🎯 方式1：IDE 中直接运行（最推荐 ⭐）

```bash
# IntelliJ IDEA / Eclipse 等 IDE
# 1. 打开 PluginHostSimulatorApplication.java
# 2. 点击 Run (Shift+F10) 或 Debug (Shift+F9)
# 3. 等待 5-10 秒启动完成
```

**IDE 界面步骤**：
```
1. 在 onebase-plugin-host-simulator 模块中找到：
   src/main/java/com/cmsr/onebase/plugin/simulator/PluginHostSimulatorApplication.java
2. 右键点击 → Run 'PluginHostSimulatorApplication.main()'
   或 Debug 'PluginHostSimulatorApplication.main()'
3. 自动进入 dev 模式（默认）
```

#### 🎯 方式2：Maven 命令行

```bash
cd onebase-plugin/onebase-plugin-host-simulator

# 使用 Spring Boot Maven 插件启动（默认 dev 模式）
mvn spring-boot:run
```

#### 🎯 方式3：打包后直接运行（dev 模式）

```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 运行（默认进入 dev 模式）
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar
```

**特点**：
- ✅ **启动快**：5-10秒即可启动
- ✅ **无需打包**：直接扫描依赖中的插件（如 onebase-plugin-demo）
- ✅ **断点调试**：完整的 IDE 调试支持
- ✅ **热重载**：修改代码后快速重启验证
- ✅ **自动激活**：不指定 profile 时自动进入 dev 模式

**配置文件**：`application-dev.yml`

### 模式2：完整验证模式（推荐集成测试使用）⭐⭐⭐⭐

**适用场景**：集成测试、完整验证、发布前测试

**启动方式**：

#### 🎯 方式1：Maven 命令行

```bash
cd onebase-plugin/onebase-plugin-host-simulator

# 1. 打包
mvn clean package -DskipTests

# 2. 启动 prod 模式（指定 profile）
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

#### 🎯 方式2：IDE 配置运行参数

```
1. 在 IDE 中打开 Run Configuration
2. 设置 Program arguments: --spring.profiles.active=prod
3. Run 或 Debug
```

**IntelliJ IDEA 具体步骤**：
```
1. 点击 Run → Edit Configurations...
2. 找到或新建 'PluginHostSimulatorApplication' 配置
3. 在 Program arguments 中填入：--spring.profiles.active=prod
4. Apply 并点击 Run 或 Debug
```

**特点**：
- ✅ **完整生命周期**：测试插件的加载、启动、停止、卸载全流程
- ✅ **真实环境**：模拟生产环境的插件加载方式
- ✅ **ZIP 插件支持**：从 plugins 目录加载 ZIP 格式的插件

**配置文件**：`application-prod.yml`

## 📦 快速开始

### 第一次使用（IDE 快速调试）

```bash
# 1. 在 IDE 中打开 PluginHostSimulatorApplication.java
# 2. 右键选择 Debug 'PluginHostSimulatorApplication.main()'
# 3. 访问 http://localhost:8080
```

## ⚙️ 配置文件说明

### 🎯 application.yml（主配置文件 - 推荐编辑）⭐

```yaml
spring:
  profiles:
    active: dev    # 修改这里来切换模式：dev 或 prod
```

**说明**：
- ✅ 这是主配置文件，用于**快速切换 dev/prod 模式**
- 将 `dev` 改为 `prod`，然后运行即可切换模式
- 推荐在日常开发中通过修改这个文件来切换模式
- **优点**：比命令行参数更直观，修改一处即可

**使用方式**：
```bash
# 编辑 application.yml 中的 active: dev 或 prod
# 然后直接运行即可
mvn spring-boot:run

# 或在 IDE 中直接运行 PluginHostSimulatorApplication.java
```

### application-dev.yml（开发模式配置）

```yaml
spring:
  application:
    name: onebase-plugin-host-simulator

# 开发模式配置
onebase:
  plugin:
    # 插件目录：指向编译后的 target 目录
    # ComponentScan 会自动扫描依赖包中的插件（无需 ZIP）
    plugins:
      dir: ./target/dev-plugins
```

**开发模式特点**：
- 使用 Spring 的 `@ComponentScan` 扫描插件
- 无需打包成 ZIP，直接加载依赖中的插件
- 快速启动，适合快速迭代

### application-prod.yml（生产验证模式配置）

```yaml
spring:
  application:
    name: onebase-plugin-host-simulator

# 生产验证模式配置
onebase:
  plugin:
    # 插件目录：从相对路径 plugins 目录加载 ZIP 插件
    plugins:
      dir: ./plugins
```

**生产模式特点**：
- 从 `./plugins` 目录加载 ZIP 格式的插件文件
- 完整模拟生产环境的插件加载过程
- 测试插件的完整生命周期（加载、启动、停止、卸载）

## 🎮 Profile（配置文件）激活方式

### 🥇 方式1：修改 application.yml（推荐 ⭐⭐⭐⭐⭐）

这是**最简单、最推荐的方式**！

**文件位置**：`src/main/resources/application.yml`

```yaml
spring:
  profiles:
    active: dev    # ← 修改这里：dev 或 prod
```

**切换模式**：
```bash
# 改为 dev 模式
active: dev

# 改为 prod 模式
active: prod
```

**然后运行**：
```bash
# 方式A：IDE 中直接运行（Shift+F10）
# 方式B：Maven 命令
mvn spring-boot:run
```

**优点**：
- ✅ 最直观，一眼就能看出当前使用的是哪个模式
- ✅ 修改一处文件即可，不用记复杂命令
- ✅ IDE 和命令行启动效果一致
- ✅ 适合频繁切换模式的开发者

---

### 🥈 方式2：自动默认（推荐 ⭐⭐⭐⭐）

不修改任何配置，直接运行时自动进入 **dev 模式**。

```bash
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar
```

**PluginHostSimulatorApplication.java 中的代码**：
```java
@SpringBootApplication
public class PluginHostSimulatorApplication {
    public static void main(String[] args) {
        // 如果未指定 profile，默认使用 dev
        String[] profiles = SpringApplication.detect(args);
        if (profiles.length == 0) {
            System.setProperty("spring.profiles.active", "dev");
        }
        SpringApplication.run(PluginHostSimulatorApplication.class, args);
    }
}
```

---

### 🥉 方式3：命令行指定参数

```bash
# 进入 dev 模式
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev

# 进入 prod 模式
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**优点**：
- ✅ 不需要修改任何代码或配置文件
- ✅ 临时切换模式方便

**缺点**：
- ❌ 命令太长，容易记错
- ❌ 不适合频繁切换

---

### 方式4：IDE 运行配置（支持快速切换）

**创建 dev 模式配置**：
```
1. 点击 Run → Edit Configurations...
2. 点击 '+' 新建，选择 'Application'
3. Name: 输入 'Host Simulator Dev'
4. Main class: com.cmsr.onebase.plugin.simulator.PluginHostSimulatorApplication
5. Program arguments: (留空，使用 application.yml 中的配置)
6. Apply → OK
```

**创建 prod 模式配置**：
```
1. 点击 Run → Edit Configurations...
2. 点击 '+' 新建，选择 'Application'
3. Name: 输入 'Host Simulator Prod'
4. Main class: com.cmsr.onebase.plugin.simulator.PluginHostSimulatorApplication
5. Program arguments: --spring.profiles.active=prod
6. Apply → OK
```

之后可以直接在 IDE 右上角的运行配置下拉菜单中切换运行模式。

---

### 方式5：Maven 直接启动

```bash
# Dev 模式
mvn spring-boot:run

# Prod 模式
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=prod'
```

---

### 方式6：环境变量方式

```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar

# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE = "prod"
java -jar target/onebase-plugin-host-simulator-1.0.0-SNAPSHOT.jar
```

---

## 📊 模式对比表

| 功能 | Dev 模式 | Prod 模式 |
|------|---------|----------|
| 启动速度 | ⚡ 5-10秒 | ⏱️ 10-15秒 |
| 需要打包 | ❌ 否 | ✅ 是 |
| 断点调试 | ✅ 支持 | ✅ 支持 |
| 插件来源 | 依赖包 | ZIP 文件 |
| 配置文件 | application-dev.yml | application-prod.yml |
| 适用场景 | 日常开发 | 集成测试/发布前 |

## 🚀 快速切换模式

**最简单的方式**：编辑 `src/main/resources/application.yml`

```yaml
spring:
  profiles:
    active: dev    # ← 修改这里
```

然后直接运行：
```bash
# IDE 中按 Shift+F10 或
mvn spring-boot:run
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
