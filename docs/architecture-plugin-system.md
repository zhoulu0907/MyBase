---
stepsCompleted: [1, 2, 3, 4]
inputDocuments:
  - docs/prd.md
workflowType: 'architecture'
lastStep: 4
project_name: 'onebase-v3-be'
user_name: 'Kanten'
date: '2025-12-07'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

## 项目上下文分析

### 需求概览

**功能需求:**
- 29个功能需求,组织为6个核心能力领域
- 核心能力:插件生命周期管理、流程扩展、开发工具链、管理API、元数据发现、隔离安全
- **MVP范围**:仅实现1个扩展点(流程扩展),其他功能延后到Phase 2

**非功能需求:**
- 31个非功能需求,覆盖7个质量类别
- 关键NFR:
  - 性能:插件加载<10秒、API P95<500ms、支持≥50插件
  - 安全:租户隔离100%、代码审核、权限控制
  - 可靠性:故障隔离100%、内存泄漏<50MB(MVP可接受)
  - 可维护性:测试覆盖>80%、代码质量标准
  - 可测试性:单元测试、集成测试支持

**规模与复杂度:**

- **主要技术域**: 后端开发者工具(Enterprise Low-Code Platform Plugin System)
- **复杂度级别**: Medium-High
- **预估架构组件**:
  - 核心框架:5-7个核心类(PluginClassLoader、PluginContainer、PluginRegistry等)
  - 扩展点:1个(FlowComponentExtension)
  - REST API:5个端点(上传、安装、卸载、列表、详情)
  - SDK模块:2个(onebase-plugin-sdk、Maven Archetype)
  - 测试工具:Mock环境和工具类

### 技术约束与依赖

**技术栈约束:**
- Spring Boot 3.4.5+ (与OneBase平台保持一致)
- MyBatis-Flex 1.9.7+ (数据访问,租户隔离在Phase 2)
- LiteFlow 2.12+ (流程编排引擎)
- JDK 17 (推荐)

**MVP阶段技术债(允许的简化):**
- 无热部署:插件安装/卸载需重启服务器
- 仅平台级插件:租户隔离延后到Phase 2
- 手动依赖冲突处理:开发者需手动标记`<scope>provided</scope>`
- 允许少量内存泄漏:<50MB可接受范围
- 基础安全审核:完整审核机制延后

**关键依赖:**
- OneBase平台核心:与主应用Spring容器共存
- LiteFlow流程引擎:动态组件注册API
- 文件系统:插件JAR存储和加载
- 数据库:插件元数据和状态存储

### 已识别的跨领域关注点

1. **隔离与安全**
   - ClassLoader隔离(每个插件独立)
   - Spring Bean命名空间隔离(`plugin.{pluginId}.{beanName}`)
   - 异常隔离(插件异常不传播到平台核心)
   - 资源限制(单插件内存<200MB)

2. **可观测性**
   - 结构化日志(JSON格式)
   - Micrometer指标采集(Phase 2完整监控)
   - 插件状态追踪(INSTALLED、UNINSTALLED、ERROR)
   - 错误码标准化和文档化

3. **开发者体验**
   - 完整SDK(Javadoc覆盖率100%)
   - Maven Archetype脚手架(一键生成项目骨架)
   - HelloWorld示例(30分钟上手目标)
   - 本地测试框架(Mock OneBase环境)

4. **性能优化**
   - 插件懒加载(按需加载,Phase 2)
   - 依赖注入优化(减少启动时间增加<20%)
   - API响应时间监控(P95<500ms)
   - 内存泄漏检测(VisualVM/JProfiler验证)

5. **质量保证**
   - 单元测试覆盖率>80%
   - 集成测试覆盖全生命周期
   - SonarQube静态分析(无Critical/Blocker)
   - 自动化CI/CD流程

## 现有技术栈与集成分析

### 现有技术栈确认

**核心框架（已确定）：**
- **Spring Boot**: 3.4.5（主应用框架）
- **Java**: 17（运行时环境）
- **Maven**: 多模块项目管理

**关键依赖（插件系统必须兼容）：**
- **LiteFlow**: 2.15.0.2（流程编排引擎 - 插件扩展点）
- **MyBatis-Flex**: 1.11.4（数据访问 - 插件元数据存储）
- **Spring Cloud**: 2024.0.1（模块化支持）
- **Redisson**: 3.52.0（分布式缓存和锁）

**项目结构约束：**
- **Monorepo 架构**：72 个 Maven 模块
- **分层模式**：framework → server → module (api/core/runtime/build)
- **多租户架构**：已有租户隔离机制

### 插件系统模块定位

**新增模块建议：**

```
onebase-module-plugin/
├── onebase-module-plugin-api      # 插件API接口定义
├── onebase-module-plugin-core     # 插件核心框架（ClassLoader、Registry）
├── onebase-module-plugin-platform # 插件管理API（上传、安装、监控）
└── onebase-plugin-sdk             # 插件开发SDK（独立发布）
```

**模块职责：**
- **plugin-api**: 定义插件扩展点接口，供其他模块依赖
- **plugin-core**: 实现插件加载、生命周期管理、隔离机制
- **plugin-platform**: 提供管理界面 REST API（租户级/平台级）
- **plugin-sdk**: 开发者工具包（独立 Maven artifact，外部开发者使用）

### 关键技术集成点

**1. Spring 容器集成**
- **父子容器模式**：每个插件独立的 `ApplicationContext`
- **Bean 命名空间隔离**：`plugin.{pluginId}.{beanName}` 格式
- **生命周期钩子**：`@PostConstruct`、`@PreDestroy` 支持

**2. LiteFlow 流程扩展**
- **动态组件注册**：`FlowExecutor.reloadRule()` API
- **组件隔离**：插件组件与平台组件分离
- **组件发现**：通过 `@Component` 注解自动发现

**3. 数据库集成**
- **插件元数据表**：
  - `plugin_registry`（插件注册信息）
  - `plugin_instance`（插件实例配置）
  - `plugin_tenant_binding`（租户-插件绑定）
- **租户数据隔离**：基于现有 MyBatis-Flex 租户拦截器

**4. 类加载隔离**
- **自定义 ClassLoader**：`PluginClassLoader extends URLClassLoader`
- **双亲委派打破**：插件依赖优先从插件 ClassLoader 加载
- **平台API共享**：`onebase-plugin-sdk` 由平台 ClassLoader 加载

**5. 安全与权限**
- **租户隔离**：基于现有的 `TenantContextHolder`
- **权限控制**：集成现有的 RBAC 权限体系
- **API 签名**：复用现有的 `@ApiSignCheck` 机制

### 架构约束与技术债

**MVP 阶段允许的简化：**
- ✅ **无热部署**：插件安装/卸载需要重启应用（降低复杂度）
- ✅ **仅平台级插件**：租户隔离延后到 Phase 2
- ✅ **手动依赖管理**：开发者需标记 `<scope>provided</scope>`
- ✅ **基础监控**：仅日志和状态追踪，完整监控 Phase 2

**必须保证的技术要求：**
- ❌ ClassLoader 隔离 100%（防止类冲突）
- ❌ 异常隔离 100%（插件崩溃不影响平台）
- ❌ 资源限制（单插件内存 < 200MB）
- ❌ 数据隔离（租户数据严格隔离，Phase 2）

### 开发工具链

**Maven Archetype 骨架：**
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.cmsr.onebase \
  -DarchetypeArtifactId=onebase-plugin-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.example.plugin \
  -DartifactId=my-plugin \
  -Dversion=1.0.0-SNAPSHOT
```

**SDK 依赖（插件开发者使用）：**
```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-plugin-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### 部署与运维集成

**与现有部署流程集成：**
- **构建**：插件 JAR 打包到 `plugins/` 目录
- **配置**：通过 `application.yaml` 配置插件路径
- **启动**：OneBase 启动时自动扫描和加载插件
- **监控**：集成现有的 Spring Boot Actuator

**插件仓库（Phase 2）：**
- 本地文件系统：`/var/onebase/plugins/`
- 远程仓库：Maven 私服集成
- 版本管理：基于 Maven 版本号

### 集成分析总结

**技术栈确认：**
- ✅ Spring Boot 3.4.5 + Java 17 为基础
- ✅ LiteFlow 作为主要扩展点
- ✅ MyBatis-Flex 管理插件元数据
- ✅ Maven 多模块架构扩展

**新增模块：**
- ✅ `onebase-module-plugin-*`（4个子模块）
- ✅ 集成到现有 monorepo 结构

**关键技术决策：**
- ✅ 父子容器模式实现 Spring 隔离
- ✅ 自定义 ClassLoader 实现类隔离
- ✅ LiteFlow 动态组件注册
- ✅ MVP 阶段简化（无热部署、仅平台级插件）
