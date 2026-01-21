# OneBase v3 模块参考手册

## 模块概览

OneBase v3 采用模块化架构设计，各模块职责清晰、相互独立，通过 API 层实现模块间解耦。

```
onebase-v3-be/
├── onebase-dependencies/         # 依赖管理
├── onebase-framework/            # 框架层
├── onebase-plugin/               # 插件框架
├── onebase-server/               # 主服务器
├── onebase-server-platform/      # 构建态服务器
├── onebase-server-runtime/       # 运行态服务器
├── onebase-server-flink/         # Flink 服务器
├── onebase-module-system/        # 系统模块
├── onebase-module-infra/         # 基础设施模块
├── onebase-module-app/           # 应用模块
├── onebase-module-data/          # 数据模块
├── onebase-module-flow/          # 流程模块
├── onebase-module-formula/       # 公式模块
├── onebase-module-bpm/           # 工作流模块
└── onebase-module-dashboard/     # 仪表盘模块
```

---

## 基础层模块

### onebase-dependencies
**职责**: 统一管理项目所有依赖版本

**关键内容**:
- Spring Boot 版本: 3.4.5
- Spring Cloud 版本: 2024.0.1
- MyBatis-Flex: 1.11.4
- Redisson: 3.52.0
- Flowable: 7.0.1
- 各类数据库驱动版本

**依赖项**: 无

---

### onebase-framework
**职责**: 提供框架基础能力和通用组件

**核心能力**:
1. **Web Starter**
   - 统一异常处理
   - 统一返回格式
   - 请求日志记录
   - 跨域配置

2. **安全认证**
   - OAuth2 认证
   - Token 管理
   - 权限验证
   - API 签名

3. **ORM 层**
   - MyBatis-Flex 集成
   - 多数据源支持
   - 分页插件
   - 数据库初始化

4. **租户管理**
   - 租户隔离
   - 租户上下文
   - 数据权限

5. **缓存**
   - Redisson 集成
   - JetCache 集成
   - 本地缓存

6. **其他**
   - 文件存储
   - 任务调度
   - 消息队列
   - 日志管理

**子模块**:
```
onebase-framework/
├── onebase-spring-boot-starter-web/           # Web能力
├── onebase-spring-boot-starter-security/      # 安全认证
├── onebase-spring-boot-starter-orm/           # ORM框架
├── onebase-spring-boot-starter-tenant/        # 多租户
├── onebase-spring-boot-starter-biz-tenant/    # 租户业务
├── onebase-spring-boot-starter-biz-data/      # 数据权限
├── onebase-spring-boot-starter-biz-operatelog/ # 操作日志
├── onebase-spring-boot-starter-biz-dict/      # 字典
├── onebase-spring-boot-starter-biz-error-code/ # 错误码
├── onebase-spring-boot-starter-biz-social/    # 社交登录
├── onebase-spring-boot-starter-biz-wechat/    # 微信
├── onebase-spring-boot-starter-bay/           # 字节码增强
├── onebase-spring-boot-starter-mpi/           # 多数据库
└── onebase-spring-boot-starter-mq/            # 消息队列
```

---

### onebase-plugin
**职责**: 插件框架，支持动态扩展

**核心功能**:
- 基于 PF4J 3.14.0
- 插件生命周期管理
- 插件热加载/卸载
- 插件间通信
- 插件依赖管理

**使用场景**:
- 功能模块扩展
- 定制化开发
- 第三方集成

---

## 服务器层模块

### onebase-server
**职责**: 主服务器，单体应用入口

**端口**: 48080 (管理端)

**功能**:
- 系统管理 API
- 用户权限
- 应用管理
- 数据管理
- 流程管理
- 所有业务模块集成

**启动方式**:
```bash
cd onebase-server
mvn spring-boot:run
```

**配置文件**: `application.yaml`, `application-dev.yaml`

---

### onebase-server-platform
**职责**: 构建态服务器，面向应用开发者

**端口**: 48081

**核心功能**:
- 应用构建 API
- 页面设计器
- 组件管理
- 流程设计
- 数据建模
- 权限配置

**目标用户**:
- 低代码平台管理员
- 应用开发者
- 系统架构师

---

### onebase-server-runtime
**职责**: 运行态服务器，面向最终用户

**端口**: 48082

**核心功能**:
- 应用运行时 API
- 页面渲染
- 组件加载
- 流程执行
- 数据查询
- 租户隔离

**目标用户**:
- C 端用户
- 租户用户

---

### onebase-server-flink
**职责**: Flink 流处理服务器

**核心功能**:
- 实时数据处理
- ETL 任务执行
- 数据流转换
- 实时计算

---

## 业务模块

### onebase-module-system
**职责**: 系统基础能力模块

**核心功能**:
1. **用户管理**
   - 用户 CRUD
   - 用户导入导出
   - 用户状态管理
   - 密码重置

2. **角色管理**
   - 角色 CRUD
   - 角色权限分配
   - 数据角色
   - 角色成员管理

3. **菜单管理**
   - 菜单树结构
   - 菜单权限
   - 动态路由

4. **部门管理**
   - 部门树结构
   - 部门人员

5. **岗位管理**
   - 岗位定义
   - 岗位人员

6. **字典管理**
   - 字典类型
   - 字典数据

7. **参数配置**
   - 系统参数
   - 租户参数

8. **通知公告**
   - 公告发布
   - 通知记录

9. **操作日志**
   - 操作记录
   - 登录日志

10. **租户管理**
    - 租户 CRUD
    - 租户套餐
    - 租户过期管理

**子模块**:
```
onebase-module-system/
├── onebase-module-system-api/      # API定义
├── onebase-module-system-build/    # 构建态
├── onebase-module-system-core/     # 核心逻辑
└── onebase-module-system-runtime/  # 运行态 (如需要)
```

**数据库前缀**: `system_`

**API 路径**: `/admin-api/system/*`

---

### onebase-module-infra
**职责**: 基础设施服务模块

**核心功能**:
1. **文件存储**
   - 本地存储
   - 阿里云 OSS
   - 腾讯云 COS
   - 七牛云
   - MinIO

2. **API 日志**
   - 访问日志
   - 错误日志

3. **代码生成**
   - 表结构导入
   - 代码生成
   - 模板管理

4. **数据库文档**
   - 表结构文档
   - 自动生成

**子模块**:
```
onebase-module-infra/
├── onebase-module-infra-api/
├── onebase-module-infra-build/
└── onebase-module-infra-core/
```

**数据库前缀**: `infra_`

**API 路径**: `/admin-api/infra/*`

---

### onebase-module-app
**职责**: 低代码应用构建模块

**核心功能**:

#### 构建态 (build)
1. **应用管理**
   - 应用创建
   - 应用配置
   - 应用发布

2. **页面设计**
   - 可视化页面设计器
   - 组件拖拽
   - 事件配置
   - 数据绑定

3. **组件管理**
   - 系统组件库
   - 自定义组件
   - 组件分类

4. **菜单管理**
   - 应用菜单树
   - 菜单权限
   - 页面路由

5. **权限管理**
   - 应用角色
   - 权限配置
   - 数据权限

6. **版本管理**
   - 版本创建
   - 版本对比
   - 版本回滚

#### 运行态 (runtime)
1. **应用运行时**
   - 应用加载
   - 页面渲染
   - 组件实例化

2. **页面数据**
   - 数据查询
   - 数据提交
   - 数据校验

3. **权限控制**
   - 菜单权限
   - 按钮权限
   - 数据权限

**子模块**:
```
onebase-module-app/
├── onebase-module-app-api/         # API定义
│   ├── app/                        # 应用DTO
│   ├── auth/                       # 认证DTO
│   ├── menu/                       # 菜单DTO
│   └── resource/                   # 资源DTO
├── onebase-module-app-build/       # 构建态
│   ├── controller/                 # 控制器
│   │   ├── app/                    # 应用管理
│   │   ├── auth/                   # 权限管理
│   │   ├── menu/                   # 菜单管理
│   │   └── resource/               # 资源管理
│   └── service/                    # 业务服务
├── onebase-module-app-runtime/     # 运行态
│   ├── controller/                 # 运行态API
│   └── service/                    # 运行时服务
└── onebase-module-app-core/        # 核心逻辑
    ├── dal/                        # 数据访问
    ├── service/                    # 核心服务
    ├── provider/                   # SPI提供者
    └── vo/                         # 视图对象
```

**数据库前缀**: `app_`

**API 路径**:
- 构建态: `/admin-api/app/*`
- 运行态: `/app-api/app/*`

---

### onebase-module-data
**职责**: 数据管理和 ETL 模块

**核心功能**:

#### 元数据管理 (metadata)
1. **数据源管理**
   - 多数据源配置
   - 数据源测试
   - 连接池管理

2. **表管理**
   - 表结构导入
   - 表结构编辑
   - 表关系管理

3. **字段管理**
   - 字段定义
   - 字段类型
   - 验证规则

4. **视图管理**
   - 视图创建
   - 多表关联

5. **业务数据**
   - CRUD 操作
   - 数据导入导出
   - 数据校验

#### ETL (etl)
1. **数据抽取**
   - 定时抽取
   - 增量抽取
   - 全量抽取

2. **数据转换**
   - 数据清洗
   - 数据转换
   - 数据校验

3. **数据加载**
   - 目标表加载
   - 批量加载
   - 实时加载

4. **任务管理**
   - 任务设计
   - 任务调度
   - 任务监控

**子模块**:
```
onebase-module-data/
├── onebase-module-metadata-api/        # 元数据API
├── onebase-module-metadata-build/      # 元数据构建态
├── onebase-module-metadata-core/       # 元数据核心
├── onebase-module-metadata-runtime/    # 元数据运行态
├── onebase-module-metadata-common/     # 元数据公共
├── onebase-module-etl-api/             # ETL API
├── onebase-module-etl-build/           # ETL 构建态
├── onebase-module-etl-core/            # ETL 核心
├── onebase-module-etl-runtime/         # ETL 运行态
├── onebase-module-etl-executor/        # ETL 执行器
└── onebase-module-etl-common/          # ETL 公共
```

**数据库前缀**:
- 元数据: `metadata_`
- ETL: `etl_`

**API 路径**:
- 元数据: `/admin-api/metadata/*`
- ETL: `/admin-api/etl/*`

---

### onebase-module-flow
**职责**: 业务流程编排模块

**核心功能**:
1. **流程设计**
   - 可视化流程设计器
   - 流程节点配置
   - 流程连线

2. **流程组件**
   - 开始/结束节点
   - 任务节点
   - 条件分支
   - 并行网关
   - 子流程

3. **流程实例**
   - 流程启动
   - 流程执行
   - 流程监控

4. **流程上下文**
   - 变量管理
   - 数据传递
   - 状态管理

5. **流程调度**
   - 定时执行
   - 事件触发
   - 手动执行

**子模块**:
```
onebase-module-flow/
├── onebase-module-flow-api/            # 流程API
├── onebase-module-flow-build/          # 流程构建态
├── onebase-module-flow-runtime/        # 流程运行态
├── onebase-module-flow-core/           # 流程核心
├── onebase-module-flow-component/      # 流程组件
├── onebase-module-flow-context/        # 流程上下文
└── onebase-module-flow-sched/          # 流程调度
```

**数据库前缀**: `flow_`

**API 路径**: `/admin-api/flow/*`

**技术栈**: LiteFlow 2.15.2

---

### onebase-module-formula
**职责**: 公式计算引擎模块

**核心功能**:
1. **公式定义**
   - 公式编辑器
   - 公式语法
   - 公式验证

2. **公式执行**
   - 公式解析
   - 公式计算
   - 结果缓存

3. **内置函数**
   - 数学函数
   - 字符串函数
   - 日期函数
   - 聚合函数

**技术栈**:
- JEXL3 3.5.0
- MVEL2 2.4.14.Final
- GraalVM 23.0.9

**数据库前缀**: `formula_`

---

### onebase-module-bpm
**职责**: 工作流引擎模块

**核心功能**:
1. **流程定义**
   - BPMN 建模
   - 流程发布

2. **流程实例**
   - 流程启动
   - 流程流转
   - 流程撤回

3. **任务管理**
   - 待办任务
   - 已办任务
   - 任务委派
   - 任务转办

4. **表单管理**
   - 表单设计
   - 表单数据

5. **流程监控**
   - 流程跟踪
   - 流程统计

**技术栈**: Flowable 7.0.1

**子模块**:
```
onebase-module-bpm/
├── onebase-module-bpm-api/             # BPM API
├── onebase-module-bpm-build/           # BPM 构建态
├── onebase-module-bpm-core/            # BPM 核心
└── onebase-module-bpm-engine-orm/      # ORM 实现
    ├── onebase-module-bpm-engine-orm-anyline/  # Anyline
    └── onebase-module-bpm-engine-orm-mybatiflex/  # MyBatis-Flex
```

**数据库前缀**: `bpm_`

**API 路径**: `/admin-api/bpm/*`

**表**: Flowable 自动创建 (ACT_*)

---

### onebase-module-dashboard
**职责**: 数据可视化和大屏模块

**核心功能**:
1. **仪表盘管理**
   - 仪表盘创建
   - 仪表盘配置
   - 仪表盘权限

2. **图表组件**
   - 柱状图
   - 折线图
   - 饼图
   - 仪表盘
   - 数据表格

3. **数据源**
   - SQL 数据源
   - API 数据源
   - 静态数据

4. **大屏展示**
   - 全屏展示
   - 自动刷新
   - 主题切换

**数据库前缀**: `dashboard_`

**API 路径**: `/admin-api/dashboard/*`

---

### onebase-module-ai (可选)
**职责**: AI 能力集成模块

**核心功能**:
1. **AI 对话**
   - 对话管理
   - 上下文保持
   - 意图识别

2. **AI 屏幕分析**
   - 图表识别
   - 数据分析

3. **图表 DSL 解析**
   - 自然语言转图表
   - DSL 生成

**技术栈**: 通义千问、其他 AI 服务

---

## 模块间依赖关系

```
                    ┌─────────────────┐
                    │ onebase-server  │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌────────▼────────┐  ┌──────▼──────┐
│  module-system │  │   module-app    │  │ module-data │
└────────┬───────┘  └────────┬────────┘  └──────┬──────┘
         │                   │                   │
         └───────────────────┼───────────────────┘
                             │
                    ┌────────▼────────┐
                    │ onebase-framework│
                    └─────────────────┘
```

**依赖原则**:
1. 上层模块可以依赖下层模块
2. 同层模块通过 API 接口调用
3. 禁止下层依赖上层
4. 避免循环依赖

---

## 模块扩展指南

### 创建新模块

1. **创建模块结构**
```bash
mkdir -p onebase-module-{name}/{name}-api,{name}-build,{name}-core,{name}-runtime
```

2. **定义 pom.xml**
```xml
<parent>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-module-{name}</artifactId>
    <version>${revision}</version>
</parent>
```

3. **在根 pom.xml 注册模块**
```xml
<modules>
    <module>onebase-module-{name}</module>
</modules>
```

4. **实现模块功能**
   - API 层定义接口
   - Core 层实现逻辑
   - Build 层提供管理 API
   - Runtime 层提供用户 API

5. **在服务器模块集成**
```xml
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-module-{name}-build</artifactId>
</dependency>
```

---

## 最佳实践

1. **模块职责单一**: 每个模块只负责一个业务领域
2. **接口隔离**: 模块间通过 API 接口通信
3. **数据隔离**: 每个模块使用独立的表前缀
4. **版本管理**: 模块版本与项目版本统一
5. **文档完善**: 维护模块文档和 API 文档

---

## 参考文档
- [项目概述](./project-overview.md)
- [开发指南](./development-guide.md)
- [架构设计](./architecture-plugin-system.md)
