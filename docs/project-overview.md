# OneBase v3 后端项目概述

## 项目简介

OneBase v3 是一个企业级低代码平台后端系统，基于 Spring Boot 3.4.5 构建，采用模块化架构设计，支持多租户、插件化扩展、流程编排、数据管理等企业级功能。

**项目信息：**
- **GroupId**: com.cmsr.onebase
- **ArtifactId**: onebase-cloud
- **Version**: 1.0.0-SNAPSHOT
- **Java Version**: JDK 17+
- **Spring Boot**: 3.4.5
- **Spring Cloud**: 2024.0.1

## 核心特性

### 1. 多租户架构
- **框架级租户隔离**: TenantContext 自动传播
- **行级数据隔离**: ORM 层自动添加租户过滤条件
- **租户感知的 ORM**: 数据访问自动注入租户ID
- **多租户配置**: 支持租户级别的独立配置管理

### 2. 插件化扩展
- 基于 **PF4J 3.14.0** 的插件框架
- **动态加载/卸载**: 支持热插拔，无需重启
- **插件隔离运行**: 独立的类加载器和生命周期
- **插件间通信**: 提供标准化的消息通信机制
- **Maven 插件**: 自动化插件打包和扫描

### 3. 低代码能力
- **应用构建 (App)**: 可视化页面设计器、组件拖拽、事件配置
- **数据管理 (Data)**:
  - **元数据**: 动态表结构、字段定义、语义建模
  - **ETL**: 数据抽取、转换、加载、多执行器支持
- **流程引擎 (Flow)**:
  - 基于组件的流程编排
  - 40+ 内置节点类型
  - 支持条件分支、循环、并行执行
  - 可插拔的连接器机制
- **公式引擎 (Formula)**:
  - 支持 GraalVM、MVEL、JEXL 多引擎
  - 内置丰富的函数库
  - 公式验证和执行
- **工作流 (BPM)**:
  - 基于 Flowable 7.0.1
  - BPMN 2.0 标准流程
  - 任务管理和流程监控

### 4. 企业级功能
- **多数据库支持**:
  - 主要: PostgreSQL、Oracle、MySQL
  - 国产: 达梦DM8、人大金仓KingBase、OpenGauss
  - 时序: TDengine
  - ORM: MyBatis-Flex 1.11.4 + Anyline 8.7.2
- **分布式能力**:
  - 缓存: Redisson 3.52.0 + JetCache 2.7.8
  - 锁: Lock4j 2.2.7
  - UID生成器: 分布式唯一ID
- **中间件集成**:
  - 消息队列: RocketMQ 5.0.8
  - 文件存储: 本地/OSS/COS/MinIO
  - 搜索引擎: (可选)
- **安全与监控**:
  - OAuth2 认证
  - 数据脱敏 (银行卡、身份证、邮箱、姓名)
  - API 访问日志
  - 错误日志追踪

### 5. 部署方案

**四种部署模式**:

| 服务器 | 端口 | 职责 | 适用场景 |
|--------|------|------|----------|
| **onebase-server** | 48080 | 单体应用 | 中小型项目、开发环境 |
| **onebase-server-platform** | 48081 | 构建态服务 | 应用设计、流程编辑 |
| **onebase-server-runtime** | - | 运行态服务 | 应用执行、用户访问 |
| **onebase-server-flink** | - | 流处理服务 | ETL、异步任务 |

**部署方式**:
- **单体模式**: 所有模块打包到单一 JAR (onebase-server)
- **分布式模式**: Platform + Runtime + Flink 独立部署和扩展
- **容器化**: 完整的 Docker Compose 编排方案
- **调度集成**: DolphinScheduler 任务调度

## 技术栈

### 核心框架
- **Spring Boot**: 3.4.5
- **Spring Cloud**: 2024.0.1
- **Java**: JDK 17+

### 数据库相关
- **ORM**: MyBatis-Flex 1.11.4 (主要) + MyBatis 3.5.19 (兼容)
- **数据库支持**:
  - PostgreSQL 42.7.4
  - MySQL
  - Oracle 23.26.0.0
  - 达梦 DM8 8.1.3.140
  - 人大金仓 8.6.0
  - OpenGauss 5.1.0
  - TDengine 3.3.3 (时序数据库)
- **缓存**: Redisson 3.52.0, JetCache 2.7.8

### 工作流引擎
- **Flowable**: 7.0.1 (BPM 工作流)
- **LiteFlow**: 2.15.2 (流程编排)

### 工具库
- **Lombok**: 1.18.36
- **MapStruct**: 1.6.3 (对象映射)
- **Hutool**: 5.8.35 (工具类)
- **EasyExcel**: 4.0.3 (Excel处理)
- **Guava**: 33.4.8-jre
- **Gson**: 2.13.2

### 插件框架
- **PF4J**: 3.14.0
- **PF4J-Spring**: 0.10.0

### 其他组件
- **验证码**: anji-plus-captcha 1.4.0
- **分布式锁**: lock4j 2.2.7
- **HTTP 客户端**: Retrofit 3.0.0, OkHttp 4.12.0
- **文档**: SpringDoc OpenAPI 2.8.3

## 模块架构

### 基础设施层
```
onebase-dependencies/         # 依赖版本统一管理
onebase-framework/            # 框架层和公共能力
onebase-plugin/               # 插件框架
```

### 服务器层
```
onebase-server/               # 主服务器 (单体应用入口)
onebase-server-platform/      # 构建态服务器
onebase-server-runtime/       # 运行态服务器
onebase-server-flink/         # Flink 流处理服务器
```

### 业务模块层

#### 系统模块 (onebase-module-system)
系统基础能力，包括：
- 用户管理
- 角色权限
- 部门组织
- 租户管理
- 字典管理
- 参数配置
- 通知公告
- 操作日志

#### 基础设施模块 (onebase-module-infra)
基础设施服务，包括：
- 文件存储 (本地/阿里云OSS/腾讯云COS等)
- API 访问日志
- 错误日志
- 数据库文档
- 代码生成

#### 应用模块 (onebase-module-app)
低代码应用构建，分为三个子模块：
- **app-api**: 模块间 API 定义
- **app-build**: 构建态服务 (应用管理、页面编辑器)
- **app-runtime**: 运行态服务 (面向C端用户)
- **app-core**: 核心业务逻辑

功能包括：
- 应用管理
- 页面设计
- 组件管理
- 菜单权限
- 版本管理

#### 数据模块 (onebase-module-data)
数据管理和ETL，包含：
- **metadata**: 元数据管理
  - metadata-api: API定义
  - metadata-build: 构建态服务
  - metadata-core: 核心逻辑
  - metadata-runtime: 运行态服务
  - metadata-common: 公共组件

- **etl**: ETL数据处理
  - etl-api: API定义
  - etl-build: 构建态服务
  - etl-core: 核心逻辑
  - etl-runtime: 运行态服务
  - etl-executor: 执行器
  - etl-common: 公共组件

#### 流程模块 (onebase-module-flow)
业务流程编排，包含：
- flow-api: API定义
- flow-build: 构建态服务
- flow-runtime: 运行态服务
- flow-core: 核心逻辑
- flow-component: 流程组件
- flow-context: 上下文管理
- flow-sched: 调度服务

#### 公式模块 (onebase-module-formula)
业务公式计算引擎

#### BPM模块 (onebase-module-bpm)
工作流引擎，基于 Flowable：
- BPM 流程定义
- 流程实例管理
- 任务管理
- 流程监控

#### 仪表盘模块 (onebase-module-dashboard)
数据可视化和大屏展示

#### AI模块 (onebase-module-ai) (可选)
AI能力集成：
- AI 对话
- AI 屏幕分析
- 图表 DSL 解析

## 项目结构约定

### 1. 包名约定
通用模块遵循现有规范，业务模块主包名：`com.cmsr.onebase.module.{module-name}`

### 2. 表名约定
统一使用模块名作为前缀：
- system 模块: `system_*`
- app 模块: `app_*`
- infra 模块: `infra_*`
- bpm 模块: `bpm_*`

### 3. 分层结构
每个业务模块按以下层次组织：
```
module-{name}/
├── module-{name}-api/          # API 定义，模块间解耦
├── module-{name}-build/        # 构建态服务 (管理端)
├── module-{name}-runtime/      # 运行态服务 (用户端)
└── module-{name}-core/         # 核心逻辑 (DO/Repository/Service/Util)
```

### 4. 模块内部结构
```
module-{name}-core/
├── api/                        # 对外 API 接口
├── controller/                 # 控制器
├── service/                    # 业务服务
│   ├── impl/                   # 服务实现
│   └── {Service}.java          # 服务接口
├── dal/                        # 数据访问层
│   ├── dataobject/             # 数据对象 (DO)
│   ├── mapper/                 # MyBatis Mapper
│   └── database/               # Repository
├── convert/                    # 对象转换
├── enums/                      # 枚举定义
├── vo/                         # 视图对象
│   ├── req/                    # 请求 VO
│   └── resp/                   # 响应 VO
└── util/                       # 工具类
```

## 开发规范

### Git 协作流程
1. **分支策略**:
   - `dev`: 研发主干 (开发用)
   - `sit`: 集成主干 (测试/体验用)
   - `master`: 生产主干
   - `feature/task-{JIRA-ID}-{描述}`: 功能开发分支

2. **协作约定**:
   - 从 `dev` 拉取功能分支
   - 每天至少从主干合并一次，避免冲突堆积
   - 必须通过 MR 提交到 `dev`
   - 至少一位架构师审核通过才能合并

3. **提交规范**:
   - 严禁直接推送到主干分支
   - MR 标题清晰描述改动内容
   - 关联对应的 JIRA 任务号

### 编码规范
1. **工具使用**:
   - 优先使用 AI 辅助编程 (Cursor/Copilot/通义灵码)
   - 不盲信 AI，人工严格审查代码
   - 关注代码可读性和性能

2. **代码质量**:
   - 遵循阿里巴巴 Java 开发规范
   - 必须编写单元测试
   - 关键路径必须有测试覆盖
   - 代码提交前通过所有检查

3. **注释规范**:
   - 代码注释使用英文
   - 复杂业务逻辑必须添加注释
   - API 接口添加 JavaDoc 注释

## 构建与部署

### 本地开发
```bash
# 安装依赖
mvn clean install -Dmaven.test.skip=true

# 运行主服务
cd onebase-server
mvn spring-boot:run

# 运行构建态服务
cd onebase-server-platform
mvn spring-boot:run

# 运行运行态服务
cd onebase-server-runtime
mvn spring-boot:run
```

### 打包部署
```bash
# 单体模式打包
mvn package -am -pl onebase-server -Dmaven.test.skip=true

# Cloud 模式打包
mvn clean package -Dmaven.test.skip=true

# 启动服务
sh deploy.sh start
```

### Docker 部署
项目包含完整的 Docker 部署方案：
- `docker/docker-compose.yaml`: 容器编排
- `docker/dockerfiles/`: 各服务的 Dockerfile
- `docker/onebase/`: 配置文件
- 支持 PostgreSQL, Redis, Flink 等服务编排

## 相关文档
- [技术栈详解](./technology-stack.md)
- [架构设计文档](./architecture-plugin-system.md)
- [数据模型文档](./data-models-backend.md)
- [API接口文档](./api-contracts-backend.md)
- [部署配置文档](./deployment-configuration-backend.md)

## 更新日志
- 2026-01-10: 重新分析项目，更新项目概述文档
- 2026-01-07: 创建项目概述文档
