# OneBase v3.0 学习日志 - Day 1

**学习日期**: 2025-12-30  
**学习主题**: 项目概览与环境搭建  
**学习者**: Kanten  
**学习时长**: 8小时

---

## 一、学习目标

### 今日目标
- [ ] 了解OneBase v3.0的整体架构和业务定位
- [ ] 搭建本地开发环境
- [ ] 理解项目目录结构和模块划分

### 预期成果
- 能口述OneBase v3.0的定位和核心功能
- 本地环境成功启动项目
- 能画出9大模块的关系图
- 能说出每个模块后缀的含义（api/core/build/runtime/platform）

---

## 二、上午学习 (9:00-12:00)

### 1. 项目概览 (9:00-10:00)

#### 学习内容
阅读了以下文档：
- [`README.md`](../README.md) - 项目说明、构建指令、工程结构
- [`docs/index.md`](index.md) - 项目文档总索引
- [`docs/project-analysis-summary.md`](project-analysis-summary.md) - 项目分析报告

#### 学习笔记

**项目基本信息**:
- **项目名称**: OneBase管理后台脚手架 (onebase-v3-be)
- **项目类型**: 企业级低代码平台后端
- **架构模式**: Monorepo (Maven多模块)
- **主要语言**: Java 17
- **核心框架**: Spring Boot 3.4.5
- **模块数量**: 72+ Maven子模块
- **代码规模**: 2,400+ Java文件

**核心价值主张**:
OneBase v3.0 是一个**企业级私有化部署低代码平台**，通过可视化组件和配置化方式帮助用户快速构建业务应用。当前重点开发**插件系统**，旨在将平台从封闭系统演进为开放生态。

**核心价值**:
让企业在使用低代码快速构建应用的同时，能够无缝集成自定义业务逻辑和外部系统，实现"标准化+个性化"的最佳平衡。

#### 理解程度
- ✅ 理解了项目的定位和核心价值
- ✅ 理解了项目的技术栈和规模
- ✅ 理解了插件系统的重要性

### 2. 技术栈学习 (10:00-11:00)

#### 学习内容
学习了以下技术：
- Spring Boot 3.4.5 基础
- MyBatis-Flex ORM框架
- LiteFlow 流程编排
- Flowable BPM工作流

#### 学习笔记

**核心技术栈**:
| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **框架** | Spring Boot | 3.4.5 | 应用框架 |
| **ORM** | MyBatis-Flex | 1.11.4 | 数据持久化 |
| **工作流** | Flowable | 7.0.1 | BPM工作流引擎 |
| **流程编排** | LiteFlow | 2.15.0.2 | 流程编排 |
| **缓存** | Redisson | 3.52.0 | 分布式缓存 |
| **消息队列** | RocketMQ | 5.0.8 | 异步消息 |

**数据库**:
- **主数据库**: PostgreSQL 10+ (支持 DM8、KingBase、OpenGauss)
- **表数量**: 100+ 张表
- **设计特点**: 逻辑删除、乐观锁、租户隔离

#### 理解程度
- ✅ 理解了Spring Boot的基础概念
- ✅ 理解了MyBatis-Flex的作用
- ✅ 理解了LiteFlow和Flowable的区别
- ⚠️ 需要深入学习MyBatis-Flex的具体用法

### 3. 模块架构理解 (11:00-12:00)

#### 学习内容
理解了9大核心模块组和分层架构

#### 学习笔记

**9大核心模块组**:
```
onebase-v3-be/
├── onebase-dependencies/       # 统一依赖管理 (BOM)
├── onebase-framework/          # 框架层(通用能力和中间件集成)
├── onebase-server/             # 服务层(应用入口和配置)
├── onebase-module-system/      # 系统模块(用户、角色、权限、租户)
├── onebase-module-infra/       # 基础设施(文件、日志、配置、安全)
├── onebase-module-data/        # 数据模块(元数据、ETL、数据源)
├── onebase-module-app/         # 应用模块(页面、组件、菜单)
├── onebase-module-flow/        # 流程模块(流程编排和执行)
├── onebase-module-formula/     # 公式模块(公式引擎和函数)
└── onebase-module-bpm/         # BPM模块(工作流设计和运行)
```

**分层架构**:
| 模块后缀 | 职责 | 示例 |
|---------|------|------|
| **-api** | 模块间接口定义,实现模块解耦 | 供其他模块调用的API |
| **-core** | 核心业务逻辑、DO/DR/Manager/Utils | 数据访问层、业务逻辑 |
| **-build** | 编辑态服务(设计时) | 配置管理、设计器 |
| **-runtime** | 运行态服务(运行时) | 业务执行、数据CRUD |
| **-platform** | 平台态服务(管理态) | 租户管理、平台配置 |

**模块依赖关系**:
```
onebase-server (主应用)
    ├── onebase-framework (框架层)
    │   ├── onebase-common (通用工具)
    │   ├── onebase-spring-boot-starter-orm (ORM框架)
    │   └── ...
    ├── onebase-module-system (系统模块)
    ├── onebase-module-infra (基础设施)
    ├── onebase-module-data (数据模块)
    ├── onebase-module-app (应用模块)
    ├── onebase-module-flow (流程模块)
    ├── onebase-module-formula (公式模块)
    └── onebase-module-bpm (BPM模块)
```

#### 理解程度
- ✅ 理解了9大模块组的作用
- ✅ 理解了分层架构的含义
- ✅ 能画出模块依赖关系图
- ✅ 能说出每个模块后缀的含义

---

## 三、下午学习 (14:00-17:00)

### 1. 环境搭建 (14:00-15:30)

#### 学习内容
搭建本地开发环境

#### 操作步骤

**步骤1: 检查环境**
```bash
# 检查Java版本
java -version
# 输出: java version "17.0.x"

# 检查Maven版本
mvn -version
# 输出: Apache Maven 3.8.x

# 检查PostgreSQL是否运行
psql --version
```

**步骤2: 安装依赖**
```bash
# 在项目根目录执行
mvn clean install -Dmaven.test.skip=true
```

**执行结果**:
- ✅ 依赖安装成功
- ⚠️ 耗时约15分钟
- ⚠️ 部分依赖下载较慢

**步骤3: 配置数据库**
修改 `onebase-server/src/main/resources/application-local.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/onebase
    username: postgres
    password: your_password
```

**步骤4: 启动项目**
```bash
cd onebase-server
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**启动结果**:
- ✅ 项目启动成功
- ✅ 端口: 8080
- ✅ 健康检查: http://localhost:8080/actuator/health

#### 学习笔记
- 环境搭建过程顺利，没有遇到问题
- 需要确保PostgreSQL服务已启动
- 首次启动需要初始化数据库表结构

### 2. 项目结构探索 (15:30-17:00)

#### 学习内容
使用IDEA打开项目，探索目录结构

#### 学习笔记

**主启动类**:
- 位置: `onebase-server/src/main/java/com/cmsr/onebase/server/OneBaseServerApplication.java`
- 注解: `@SpringBootApplication`
- 包扫描: `com.cmsr.onebase`

**配置文件**:
- 主配置: `onebase-server/src/main/resources/application.yaml`
- 本地配置: `onebase-server/src/main/resources/application-local.yaml`
- 开发配置: `onebase-server/src/main/resources/application-dev.yaml`

**关键配置项**:
```yaml
spring:
  application:
    name: onebase-v3
  profiles:
    active: local
```

**模块结构示例** (以flow模块为例):
```
onebase-module-flow/
├── onebase-module-flow-api/          # API定义
│   └── src/main/java/com/cmsr/onebase/module/flow/api/
├── onebase-module-flow-component/    # 节点组件
│   └── src/main/java/com/cmsr/onebase/module/flow/component/
├── onebase-module-flow-context/      # 上下文管理
│   └── src/main/java/com/cmsr/onebase/module/flow/context/
├── onebase-module-flow-core/         # 核心执行引擎
│   └── src/main/java/com/cmsr/onebase/module/flow/core/
└── onebase-module-flow-runtime/      # 运行时服务
    └── src/main/java/com/cmsr/onebase/module/flow/runtime/
```

#### 理解程度
- ✅ 理解了项目的目录结构
- ✅ 找到了主启动类
- ✅ 理解了配置文件的作用
- ✅ 理解了模块的内部结构

---

## 四、晚上学习 (19:00-21:00)

### 1. 笔记整理 (19:00-20:00)

#### 整理内容
整理今天的学习笔记，绘制模块关系图

#### 模块关系图

```
┌─────────────────────────────────────────────────────────────┐
│                    OneBase v3.0                        │
│              企业级低代码平台后端                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   onebase-server                        │
│                   主应用入口                             │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Framework  │   │   Modules    │   │  Dependencies │
│   框架层      │   │   业务模块     │   │  依赖管理     │
└──────────────┘   └──────────────┘   └──────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   System     │   │    App       │   │    Flow      │
│   系统模块    │   │   应用模块     │   │   流程模块    │
└──────────────┘   └──────────────┘   └──────────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Infra      │   │    Data      │   │    BPM       │
│   基础设施   │   │   数据模块     │   │   工作流     │
└──────────────┘   └──────────────┘   └──────────────┘
```

#### 关键配置项记录
```yaml
# 应用名称
spring.application.name: onebase-v3

# 激活的Profile
spring.profiles.active: local

# 数据库配置
spring.datasource.url: jdbc:postgresql://localhost:5432/onebase

# 文件上传限制
spring.servlet.multipart.max-file-size: 16MB
spring.servlet.multipart.max-request-size: 32MB

# 缓存配置
spring.cache.type: REDIS
spring.cache.redis.time-to-live: 1h
```

### 2. 预习准备 (20:00-21:00)

#### 预习内容
预习ORM框架基础和多租户概念

#### 预习笔记

**ORM框架基础**:
- ORM: Object-Relational Mapping，对象关系映射
- 作用: 将Java对象映射到数据库表
- 优势: 减少SQL编写，提高开发效率
- MyBatis-Flex: 基于MyBatis的增强框架

**多租户概念**:
- 多租户: 一个系统实例服务多个租户（客户）
- 隔离级别:
  - 数据库隔离: 每个租户独立数据库
  - Schema隔离: 每个租户独立Schema
  - 表隔离: 共享表，通过租户ID字段隔离
- OneBase采用: 表隔离（共享数据库、共享表、租户字段隔离）

**租户字段**:
- `tenant_id`: 租户ID
- `tenant_id = 0`: 平台级数据
- `tenant_id > 0`: 租户级数据

#### 理解程度
- ✅ 理解了ORM的基本概念
- ✅ 理解了多租户的隔离方式
- ✅ 理解了OneBase的租户隔离策略
- ⚠️ 需要深入学习MyBatis-Flex的具体用法

---

## 五、今日总结

### 完成情况
- [x] 了解OneBase v3.0的整体架构和业务定位
- [x] 搭建本地开发环境
- [x] 理解项目目录结构和模块划分

### 验收标准检查
- [x] 能口述OneBase v3.0的定位和核心功能
- [x] 本地环境成功启动项目
- [x] 能画出9大模块的关系图
- [x] 能说出每个模块后缀的含义（api/core/build/runtime/platform）

### 学习成果
1. **项目理解**: 清晰理解了OneBase v3.0的定位、技术栈和核心功能
2. **环境搭建**: 成功搭建了本地开发环境，项目可以正常启动
3. **架构理解**: 理解了9大模块组的作用和分层架构
4. **文档整理**: 整理了完整的学习笔记和模块关系图

### 遇到的问题
1. **依赖下载慢**: 首次安装依赖时，部分依赖下载较慢
   - 解决方案: 配置Maven镜像加速

2. **数据库初始化**: 首次启动需要初始化数据库表结构
   - 解决方案: 执行数据库初始化脚本

### 学习心得
1. **项目规模大**: 72个模块、2400+文件，需要系统化学习
2. **架构清晰**: 分层架构设计合理，模块职责明确
3. **文档完善**: 项目文档齐全，便于学习
4. **插件系统**: 当前重点开发插件系统，是项目的核心创新点

### 明日计划
- 学习数据模型与数据库设计
- 理解通用字段约定
- 理解多租户隔离机制
- 练习数据库查询操作

---

## 六、学习资源

### 今日阅读的文档
- [`README.md`](../README.md)
- [`docs/index.md`](index.md)
- [`docs/project-analysis-summary.md`](project-analysis-summary.md)

### 今日参考的代码
- `onebase-server/src/main/java/com/cmsr/onebase/server/OneBaseServerApplication.java`
- `onebase-server/src/main/resources/application.yaml`
- `onebase-server/src/main/resources/application-local.yaml`

### 今日使用的工具
- IntelliJ IDEA
- Maven 3.8.x
- PostgreSQL 10+
- psql命令行工具

---

**学习日志版本**: 1.0  
**最后更新**: 2025-12-30 21:00  
**学习者**: Kanten