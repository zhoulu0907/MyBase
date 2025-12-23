# OneBase Plugin Maven Plugin

## 1. 插件简介与应用场景

**onebase-plugin-maven-plugin** 是专为 OneBase 低代码平台插件开发设计的 Maven 插件。它的主要作用是**简化插件开发流程，自动化处理繁琐的配置和打包工作**。

### 具体作用
*   **自动生成样板代码**：自动生成插件入口类 `GeneratedPlugin`，开发者无需手动编写继承 `OneBasePlugin` 的样板代码。
*   **自动生成元数据**：根据 POM 配置自动生成 `plugin.properties` 描述文件。
*   **自动扫描扩展点**：在编译阶段自动扫描实现了 `DataProcessor`、`EventListener`、`HttpHandler` 等接口的类，并在jar包的 `META-INF/extensions.idx` 中生成索引，无需手动注册。
*   **标准化打包**：将插件及其运行时依赖（排除 SDK）打包成符合 OneBase 插件规范的 ZIP 包。

### 应用场景
*   **插件开发**：开发人员在开发 OneBase 插件时，必须在 `pom.xml` 中引入此插件并配置pluginId等相关参数。
*   **CI/CD 流水线**：在持续集成环境中，使用此插件自动构建标准化的插件发布包。

---

## 2. 插件 Goal 详解

该插件提供了四个核心 Goal，分别对应插件构建生命周期的不同阶段：

| Goal | 默认绑定阶段 (Phase) | 作用描述 |
| :--- | :--- | :--- |
| **`generate-plugin-class`** | `generate-sources` | **生成插件入口类**。<br>自动在 `target/generated-sources` 下生成继承自 `OneBasePlugin` 的 `GeneratedPlugin.java` 类。这使得开发者无需关心底层插件加载机制，只需关注业务逻辑。 |
| **`generate-properties`** | `generate-resources` | **生成插件描述文件**。<br>读取 POM 中的配置（如 `pluginId`, `version`, `description` 等），在 `target/classes` 下生成 `plugin.properties` 文件。这是插件被系统识别的身份证。 |
| **`scan-extensions`** | `process-classes` | **扫描扩展点实现**。<br>扫描编译后的 `.class` 文件，查找实现了 OneBase 扩展接口（如 `DataProcessor`、`EventListener`、`HttpHandler` 等）的类，并在 `META-INF/extensions.idx` 中生成索引。 |
| **`package-plugin`** | `package` | **打包插件**。<br>将生成的 JAR 包、`plugin.properties` 以及所有运行时依赖（`lib/` 目录）打包成一个 `.zip` 文件。这个 ZIP 包就是最终上传到 OneBase 平台的插件包。 |

---

## 3. 应用示例 (pom.xml)

在你的插件项目 `pom.xml` 中，添加以下配置即可启用完整功能：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.cmsr.onebase</groupId>
            <artifactId>onebase-plugin-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <executions>
                <!-- 1. 自动生成插件主类 -->
                <execution>
                    <id>generate-plugin-class</id>
                    <goals>
                        <goal>generate-plugin-class</goal>
                    </goals>
                    <configuration>
                        <!-- 指定生成的包名，通常与项目包名一致 -->
                        <generatedPackage>com.cmsr.onebase.plugin.demo</generatedPackage>
                    </configuration>
                </execution>

                <!-- 2. 自动生成插件配置文件 plugin.properties -->
                <execution>
                    <id>generate-properties</id>
                    <goals>
                        <goal>generate-properties</goal>
                    </goals>
                    <configuration>
                        <!-- 插件唯一标识 -->
                        <pluginId>demo-plugin</pluginId>
                        <!-- 插件版本，通常引用项目版本 -->
                        <pluginVersion>${project.version}</pluginVersion>
                        <!-- 插件描述 -->
                        <pluginDescription>这是一个示例插件</pluginDescription>
                        <!-- 插件提供者 -->
                        <pluginProvider>OneBase Team</pluginProvider>
                    </configuration>
                </execution>

                <!-- 3. 自动扫描扩展点 (无需额外配置) -->
                <execution>
                    <id>scan-extensions</id>
                    <goals>
                        <goal>scan-extensions</goal>
                    </goals>
                </execution>

                <!-- 4. 自动打包成 ZIP (无需额外配置) -->
                <execution>
                    <id>package-plugin</id>
                    <goals>
                        <goal>package-plugin</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 常用配置参数说明

*   **`<generatedPackage>`**: 指定自动生成的 `GeneratedPlugin` 类所在的包名。
*   **`<pluginId>`**: 插件的唯一 ID，安装到系统时以此 ID 区分。
*   **`<pluginVersion>`**: 插件版本号。
*   **`<pluginDescription>`**: 插件的功能描述，显示在插件管理界面。
*   **`<pluginProvider>`**: 插件的开发者或组织名称。
*   **`<pluginDependencies>`**: (可选) 依赖的其他插件 ID，用逗号分隔。
