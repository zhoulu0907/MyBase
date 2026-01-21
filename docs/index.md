# OneBase v3 后端项目文档索引

> 最后更新: 2026-01-11
> 扫描模式: 深入分析 (Deep Analysis)
> 分析工具: Claude Code Explore Agent

---

## 📋 项目概览

- **项目名称:** OneBase 企业级低代码开发平台 (onebase-v3-be)
- **仓库类型:** Monorepo (Maven多模块项目)
- **项目类型:** Backend (Java Spring Boot)
- **主要语言:** Java 17
- **模块数量:** 72+ Maven子模块
- **代码规模:** 3,300+ Java文件
- **架构模式:** 模块化单体 + Build/Runtime分离 + 插件化扩展
- **测试覆盖:** 64+ 单元测试 (连接器架构)

---

## 🚀 快速参考

### 核心技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **框架** | Spring Boot | 3.4.5 | 应用框架 |
| **ORM** | MyBatis-Flex | 1.11.4 | 数据持久化 |
| **工作流** | Flowable | 7.0.1 | BPM工作流引擎 |
| **规则引擎** | LiteFlow | 2.15.0.2 | 流程编排 |
| **缓存** | Redisson | 3.52.0 | 分布式缓存 |
| **消息队列** | RocketMQ | 5.0.8 | 异步消息 |
| **工具库** | Hutool | 5.8.35 | 通用工具 |
| **API文档** | SpringDoc | 2.8.3 | OpenAPI/Swagger |

### 架构组成

**10大核心模块组:**
1. **onebase-framework** - 框架层(通用能力和中间件集成)
2. **onebase-server** - 服务层(应用入口和配置)
3. **onebase-module-system** - 系统模块(用户、角色、权限、租户)
4. **onebase-module-infra** - 基础设施(文件、日志、配置、安全)
5. **onebase-module-data** - 数据模块(元数据、ETL、数据源)
6. **onebase-module-app** - 应用模块(页面、组件、菜单)
7. **onebase-module-flow** - 流程模块(流程编排和执行)
   - **通用连接器**: 数据库类(MySQL)、短信类(阿里云)、邮箱类(163邮箱)
   - **组件节点**: 40+ 内置节点类型
8. **onebase-module-formula** - 公式模块(公式引擎和函数)
9. **onebase-module-bpm** - BPM模块(工作流设计和运行)
10. **onebase-module-dashboard** - 数据可视化模块(仪表盘、图表)

### 项目入口

- **主应用:** `onebase-server/src/main/java/com/cmsr/onebase/server/OneBaseServerApplication.java`
- **平台应用:** `onebase-server-platform/src/main/java/com/cmsr/onebase/server/OneBaseServerPlatformApplication.java`
- **运行时应用:** `onebase-server-runtime/src/main/java/com/cmsr/onebase/server/runtime/OneBaseServerRuntimeApplication.java`
- **Flink应用:** `onebase-server-flink/src/main/java/com/cmsr/onebase/server/flink/FlinkExecutorApplication.java`

### 统计数据

- **Controllers:** 100+ (@RestController)
- **Services:** 100+ (@Service)
- **Mappers:** 90+ (MyBatis Mapper接口)
- **配置环境:** local, dev, sit, offline 等多环境配置

---

## 📚 已生成文档

### 核心架构文档
- [项目概述](./project-overview.md) - 项目介绍、特性、技术栈、模块架构
- [架构深入分析](./architecture-deep-dive.md) - 完整的架构设计、模块职责、技术实现（新增）
- [插件系统架构](./architecture-plugin-system.md) - 插件框架设计和扩展机制
- [技术栈详解](./technology-stack.md) - 完整技术选型和依赖管理
- [模块参考手册](./modules-reference.md) - 各业务模块详细说明
- **连接器优化文档**:
  - [通用连接器开发计划](./universal-connector-development-plan.md) - 连接器架构设计
  - [连接器优化计划](./connector-optimization-plan.md) - 连接器优化方案
  - [连接器设计合规报告](./connector-design-compliance-report.md) - 合规性验证
  - [Flow连接器扩展开发计划](./flow-connector-extension-development-plan.md) - 扩展指南
- **连接器优化经验** (docs/experience/):
  - [连接器优化完成](./experience/20260110-1830-connector-optimization-complete.md) - 优化总结
  - [连接器单元测试完成](./experience/20260110-2050-connector-unit-tests-complete.md) - 测试总结

### API和数据文档
- [API合约 - Backend](./api-contracts-backend.md) - 200+ REST API端点文档
- [数据模型 - Backend](./data-models-backend.md) - 100+ 数据库表结构

### 运维和配置文档
- [部署配置 - Backend](./deployment-configuration-backend.md) - 部署模式和环境配置
- [常用命令](./常用命令.md) - Maven构建和运行命令

### 产品和设计文档
- [产品需求文档](./prd.md) - 产品功能需求
- [产品需求文档目录](./产品需求文档目录.docx) - PRD组织结构

### 技术报告和指南
- [DolphinScheduler接口封装报告](./DolphinScheduler接口封装报告.md) - 调度系统集成方案
- [公式规则引擎技术选型建议](./公式规则引擎技术选型建议.md) - 引擎选型分析
- [安全配置简易指导手册](./安全配置简易指导手册.md) - 安全配置指南

### Sprint工作制品
- [插件系统技术规格](./sprint-artifacts/tech-spec-plugin-system.md) - 插件系统设计

---

## 📂 现有文档资源

### 根目录文档
- [README.md](../README.md) - 项目说明、构建指令、工程结构
- [onebase-module-flow/README.md](../onebase-module-flow/README.md) - Flow流程模块说明

### BMM工作流状态
- [bmm-workflow-status.yaml](./bmm-workflow-status.yaml) - BMM工作流进度追踪

---

## 🛠 开发指南

### 快速开始

**1. 环境要求**
```bash
# Java 17+
# Maven 3.6+
# PostgreSQL / DM8 / KingBase / OpenGauss (数据库)
```

**2. 依赖安装**
```bash
# 在项目根目录执行
mvn clean install -Dmaven.test.skip=true
```

**3. 本地运行**
```bash
# 方式1: Maven直接运行
cd onebase-server
mvn spring-boot:run

# 方式2: 指定profile运行
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**4. 打包部署**
```bash
# 单体模式打包
mvn package -am -pl onebase-server -Dmaven.test.skip=true

# Cloud模式打包
mvn clean package -Dmaven.test.skip=true
```

### 配置说明

**环境配置文件位置:**
- `onebase-server/src/main/resources/application.yaml` - 主配置
- `onebase-server/src/main/resources/application-local.yaml` - 本地环境
- `onebase-server/src/main/resources/application-dev.yaml` - 开发环境
- `onebase-server/src/main/resources/application-sit.yaml` - 测试环境

**关键配置项:**
- Spring应用名: `onebase-v3`
- 默认Profile: `local`
- 文件上传限制: 单文件16MB,总计32MB
- 缓存类型: REDIS (TTL: 1小时)
- 字符编码: UTF-8

---

## 🏗 项目结构

### 模块分层架构

```
onebase-v3-be/
├── onebase-dependencies/       # 统一依赖管理 (BOM)
├── onebase-framework/          # 框架层
│   ├── onebase-common/             # 通用工具和基础类
│   ├── onebase-security-*/         # 安全框架 (build/runtime/platform)
│   ├── onebase-spring-boot-starter-*/ # 自定义Starter
│   └── ...
├── onebase-server/             # 主服务打包模块
├── onebase-server-platform/    # 平台服务
├── onebase-server-runtime/     # 运行时服务
├── onebase-server-flink/       # Flink实时计算服务
├── onebase-module-system/      # 系统模块
│   ├── -api/      # API接口定义
│   ├── -core/     # 核心业务逻辑
│   ├── -build/    # 编辑态服务
│   ├── -runtime/  # 运行态服务
│   └── -platform/ # 平台态服务
├── onebase-module-infra/       # 基础设施模块
├── onebase-module-data/        # 数据模块
│   ├── onebase-module-metadata-*/ # 元数据管理
│   └── onebase-module-etl-*/      # ETL数据处理
├── onebase-module-app/         # 应用模块
├── onebase-module-flow/        # 流程编排模块
├── onebase-module-formula/     # 公式引擎模块
└── onebase-module-bpm/         # BPM工作流模块
```

### 模块职责划分

| 模块后缀 | 职责 | 示例 |
|---------|------|------|
| **-api** | 模块间接口定义,实现模块解耦 | 供其他模块调用的API |
| **-core** | 核心业务逻辑、DO/DR/Manager/Utils | 数据访问层、业务逻辑 |
| **-build** | 编辑态服务(设计时) | 配置管理、设计器 |
| **-runtime** | 运行态服务(运行时) | 业务执行、数据CRUD |
| **-platform** | 平台态服务(管理态) | 租户管理、平台配置 |

---

## 🔍 关键目录

### 源代码目录
- `*/src/main/java/` - Java源代码
- `*/src/main/resources/` - 配置文件和静态资源
- `*/src/test/java/` - 单元测试代码

### 核心业务包结构
- `com.cmsr.onebase.*.controller` - REST API控制器层
- `com.cmsr.onebase.*.service` - 业务服务层
- `com.cmsr.onebase.*.dal.mapper` - MyBatis数据访问层
- `com.cmsr.onebase.*.convert` - 数据转换(DTO/DO/VO)
- `com.cmsr.onebase.*.api` - 模块间API实现

---

## 📖 使用场景

### 1. 了解项目架构
- 阅读 [architecture.md](./architecture.md)
- 查看 [README.md](../README.md) 中的工程结构
- 浏览模块分层架构部分

### 2. 开发新功能
1. 查看 [API合约](./api-contracts-backend.md) 了解现有API
2. 查看 [数据模型](./data-models-backend.md) 了解数据库设计
3. 参考对应模块的代码实现
4. 遵循分层架构(API/Core/Build/Runtime)

### 3. 部署和运维
- 参考 [部署配置文档](./deployment-configuration-backend.md)
- 查看 [常用命令](./常用命令.md)
- 检查环境配置文件

### 4. 问题排查
- 查看日志配置(module-infra)
- 参考API文档定位接口问题
- 检查数据模型和数据库状态

---

## 🎯 后续步骤

### 为AI辅助开发准备

**创建Brownfield PRD时:**
- 使用本索引 `docs/index.md` 作为项目上下文
- 参考 [architecture.md](./architecture.md) 了解架构约束
- 参考 [api-contracts-backend.md](./api-contracts-backend.md) 了解现有API
- 参考 [data-models-backend.md](./data-models-backend.md) 了解数据结构

**功能开发场景:**
- **UI功能:** 参考 `onebase-module-app` 架构
- **API功能:** 参考 `onebase-module-system` 架构
- **流程功能:** 参考 `onebase-module-flow` + `onebase-module-bpm`
- **数据功能:** 参考 `onebase-module-data`

**运行BMM PRD工作流:**
```bash
# 在Claude Code中执行
/bmad:bmm:workflows:create-prd
```

---

## 📝 文档维护

**文档生成信息:**
- 生成时间: 2026-01-10 22:00 CST
- 扫描级别: Deep Analysis (深入分析，包含架构、模块、组件级别)
- 工作流: document-project v1.3.0
- 项目分类: Backend Monorepo (Low-Code Platform)

**重新扫描项目:**
```bash
# 运行文档化工作流
/bmad:bmm:workflows:document-project
```

**文档状态文件:**
- [project-scan-report.json](./project-scan-report.json) - 扫描状态和元数据

---

## 📞 相关资源

- **项目主页:** https://cmsr.com
- **Group ID:** com.cmsr.onebase
- **Artifact ID:** onebase-cloud
- **Version:** 1.0.0-SNAPSHOT

---

