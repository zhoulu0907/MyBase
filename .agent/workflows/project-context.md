---
description: OneBase V3 项目上下文
---

# OneBase V3 Backend - 项目上下文

## 项目概述
- **项目名称**: OneBase V3 Backend
- **核心功能**: 企业级低代码平台后端系统
- **技术栈**: Spring Boot 3.x, Java 17, Maven, PostgreSQL, Redis
- **开发环境**: Windows, IntelliJ IDEA
- **项目定位**: 提供灵活、可扩展的低代码平台基础设施

## 项目架构

### 整体架构
```
onebase-v3-be/
├── onebase-dependencies/          # 依赖版本统一管理
├── onebase-framework/             # 核心框架层
│   ├── 通用工具类
│   ├── 基础设施组件
│   └── 框架扩展
├── onebase-plugin/                # 插件系统
│   └── 支持动态扩展和热加载
├── onebase-module-*/              # 业务模块层
│   ├── onebase-module-system      # 系统管理
│   ├── onebase-module-data        # 数据管理
│   ├── onebase-module-bpm         # 工作流引擎
│   ├── onebase-module-ai          # AI 能力
│   ├── onebase-module-app         # 应用管理
│   ├── onebase-module-flow        # 流程编排
│   ├── onebase-module-formula     # 公式引擎
│   ├── onebase-module-dashboard   # 仪表盘
│   └── onebase-module-infra       # 基础设施
└── onebase-server*/               # 服务器层
    ├── onebase-server             # 编辑态服务
    ├── onebase-server-platform    # 平台服务
    ├── onebase-server-runtime     # 运行态服务
    └── onebase-server-flink       # 流处理服务
```

### 模块职责

#### 框架层 (onebase-framework)
- 提供通用工具类和基础组件
- 统一异常处理和日志框架
- 数据库访问和缓存抽象
- 安全认证和权限控制

#### 业务模块层 (onebase-module-*)
- 各业务领域的独立模块
- 遵循领域驱动设计 (DDD)
- 模块间通过接口解耦
- 支持独立开发和测试

#### 服务器层 (onebase-server-*)
- 应用启动和配置
- 模块组装和编排
- 对外提供 REST API
- 处理跨模块协调

## 核心技术选型

### 数据库
- **关系型数据库**: PostgreSQL
- **缓存**: Redis
- **ORM 框架**: MyBatis-Flex

### 框架和库
- **Web 框架**: Spring Boot 3.x
- **构建工具**: Maven
- **日志框架**: SLF4J + Logback
- **工具库**: Lombok
