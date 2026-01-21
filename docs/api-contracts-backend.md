# OneBase v3.0 Backend - API 合约文档

> **生成日期：** 2025-12-06
> **项目：** onebase-v3-be
> **架构：** Spring Boot 3.4.5 + MyBatis-Flex
> **API 总数：** 200+ REST 端点

---

## 📋 目录

1. [概述](#概述)
2. [API 模块分类](#api-模块分类)
3. [核心业务模块 API](#核心业务模块-api)
4. [认证与授权](#认证与授权)
5. [数据管理模块](#数据管理模块)
6. [工作流引擎](#工作流引擎)
7. [系统管理](#系统管理)

---

## 概述

OneBase v3.0 Backend 提供了一套完整的企业级 REST API，支持低代码平台的核心功能，包括：

- **应用构建模块**（App）：页面、组件、菜单管理
- **系统管理模块**（System）：用户、角色、权限、租户管理
- **元数据模块**（Metadata）：业务实体、字段、数据源管理
- **工作流模块**（Flow）：流程设计、执行、编排
- **BPM 引擎**（BPM）：流程实例、任务中心
- **公式引擎**（Formula）：公式函数、表达式计算
- **ETL 模块**（ETL）：数据抽取、转换、加载
- **基础设施**（Infra）：文件存储、日志、安全配置

---

## API 模块分类

### 1. **应用构建模块** (`/app/*`)

#### 1.1 页面资源管理
- **基础路径：** `/app/resource/page`
- **主要端点：**
  - `POST /form/app_id` - 根据应用ID获取表单页面列表
  - `POST /metadata` - 获取页面元数据
  - `POST /view/create` - 创建页面视图
  - `POST /view/list` - 获取页面视图列表
  - `POST /name/update` - 更新页面名称

#### 1.2 页面集管理
- **基础路径：** `/app/resource/page_set`
- **主要端点：**
  - `GET /id` - 根据ID获取页面集
  - `GET /app_id` - 根据应用ID获取页面集
  - `GET /main_metadata` - 获取主元数据
  - `POST /create` - 创建页面集
  - `POST /copy` - 复制页面集
  - `POST /delete` - 删除页面集
  - `POST /save` - 保存页面集
  - `POST /load` - 加载页面集
  - `GET /list` - 获取页面集列表

#### 1.3 组件管理
- **基础路径：** `/app/resource/component`
- **主要端点：**
  - `POST /list` - 获取组件列表

#### 1.4 应用菜单管理
- **基础路径：** `/app/menu`
- **主要端点：**
  - `GET /bpm-list` - 获取BPM相关菜单列表
  - `GET /list` - 获取菜单列表
  - `POST /create` - 创建菜单
  - `POST /update` - 更新菜单
  - `POST /permission` - 权限配置

#### 1.5 应用管理
- **基础路径：** `/app/application`
- **主要端点：**
  - `GET /get` - 获取应用信息

#### 1.6 应用标签管理
- **基础路径：** `/app/tag`
- **主要端点：**
  - `GET /list` - 获取标签列表
  - `GET /group-count` - 按组统计标签数量
  - `POST /update-tags` - 更新标签
  - `POST /create` - 创建标签
  - `POST /delete` - 删除标签

#### 1.7 应用权限管理
- **基础路径：** `/app/auth-permission`
- **主要端点：**
  - `GET /get-function` - 获取功能权限
  - `GET /get-data` - 获取数据权限
  - `GET /get-field` - 获取字段权限
  - `POST /update-page-allowed` - 更新页面访问权限
  - `POST /update-operation` - 更新操作权限
  - `POST /update-view` - 更新视图权限
  - `POST /update-data-group` - 更新数据组权限
  - `POST /delete-data-group` - 删除数据组
  - `POST /update-field` - 更新字段权限

#### 1.8 应用角色管理
- **基础路径：** `/app/auth-role`
- **主要端点：**
  - `GET /list` - 获取角色列表
  - `GET /page-role-members` - 分页获取角色成员
  - `GET /list-dept-users` - 获取部门用户列表
  - `POST /create` - 创建角色
  - `POST /rename` - 重命名角色
  - `POST /add-user` - 添加用户到角色
  - `POST /add-dept` - 添加部门到角色
  - `POST /delete-member` - 删除成员
  - `POST /delete` - 删除角色

---

### 2. **系统管理模块** (`/system/*`)

#### 2.1 认证服务

##### 2.1.1 平台管理员认证
- **基础路径：** `/system/auth`
- **主要端点：**
  - `POST /login` - 平台管理员登录
  - `POST /logout` - 平台管理员登出
  - `POST /refresh-token` - 刷新访问令牌
  - `POST /reset-password` - 重置密码

##### 2.1.2 租户构建模式认证
- **基础路径：** `TBD` (BuildAuthController)

##### 2.1.3 租户运行时认证
- **基础路径：** `TBD` (RuntimeAuthController)

#### 2.2 OAuth2 服务
- **基础路径：** `/system/oauth2/*`
- **控制器：**
  - **OAuth2TokenController** - OAuth2令牌管理
  - **OAuth2ClientController** - OAuth2客户端管理
  - **OAuth2OpenController** - OAuth2开放接口
  - **OAuth2UserController** - OAuth2用户信息

#### 2.3 用户管理

##### 2.3.1 租户用户管理
- **控制器：** TenantUserController, TenantUserProfileController
- **主要功能：**
  - 用户CRUD操作
  - 用户个人资料管理

##### 2.3.2 企业用户管理
- **基础路径：** `/system/corp/user/*`
- **控制器：** CorpUserController, CorpUserProfileController
- **主要功能：**
  - 企业用户管理
  - 用户个人资料管理

#### 2.4 组织架构管理

##### 2.4.1 租户部门管理
- **控制器：** TenantDeptController

##### 2.4.2 企业部门管理
- **基础路径：** `/system/corp/dept/*`
- **控制器：** CorpDeptController

##### 2.4.3 岗位管理
- **控制器：** PostController

#### 2.5 租户管理
- **基础路径：** `/system/tenant/*`
- **控制器：** TenantController, TenantPackageController
- **主要功能：**
  - 租户CRUD
  - 租户套餐管理

#### 2.6 企业管理
- **基础路径：** `/system/corp/*`
- **控制器：** CorpInfoController, SystemCorpController
- **主要功能：**
  - 企业信息管理
  - 企业应用关联管理（CorpAppRelationController, SystemCorpAppRelationController）

#### 2.7 平台信息管理
- **基础路径：** `/system/platform`
- **主要端点：**
  - `GET /get-platform-info` - 获取平台信息
  - `POST /admin/create` - 创建平台管理员
  - `GET /admin/page` - 分页获取平台管理员
  - `POST /admin/update-email` - 更新管理员邮箱
  - `POST /admin/update-password` - 更新管理员密码
  - `GET /admin/list` - 获取管理员列表
  - `POST /admin/delete` - 删除管理员
  - `GET /admin/get` - 获取管理员详情

#### 2.8 权限管理
- **控制器：** PermissionController, SystemRoleController, SystemMenuController
- **主要功能：**
  - 权限分配
  - 角色管理
  - 菜单管理

#### 2.9 字典管理
- **控制器：**
  - **DictTypeController** - 字典类型管理
  - **DictDataController** - 字典数据管理
  - **RuntimeDictDataController** - 运行时字典数据

#### 2.10 日志管理
- **控制器：**
  - **OperateLogController** - 操作日志
  - **LoginLogController** - 登录日志

#### 2.11 短信服务
- **控制器：**
  - **SmsChannelController** - 短信渠道管理
  - **SmsTemplateController** - 短信模板管理
  - **SmsLogController** - 短信发送日志

#### 2.12 邮件服务
- **控制器：**
  - **MailAccountController** - 邮件账号管理
  - **MailTemplateController** - 邮件模板管理
  - **MailLogController** - 邮件发送日志

#### 2.13 许可证管理
- **控制器：** LicenseController (Platform & Build 模式)

---

### 3. **元数据管理模块** (`/metadata/*`)

#### 3.1 数据源管理
- **基础路径：** `/metadata/admin/datasource/*`
- **控制器：**
  - **DatasourceController** - 数据源管理
  - **AppDatasourceController** - 应用数据源关联

#### 3.2 业务实体管理
- **基础路径：** `/metadata/admin/entity/*`
- **控制器：**
  - **BusinessEntityController** - 业务实体管理
  - **EntityFieldController** - 实体字段管理

#### 3.3 实体关系管理
- **基础路径：** `/metadata/admin/relationship/*`
- **控制器：** EntityRelationshipController

#### 3.4 备份管理
- **基础路径：** `/metadata/admin/backupmanage/*`
- **控制器：** MetadataBackupController

---

### 4. **工作流引擎模块** (`/flow/*`)

#### 4.1 流程设计
- **基础路径：** `/flow/*`
- **控制器：**
  - **FlowNodeTypeController** - 节点类型管理
  - **FlowNodeCategoryController** - 节点分类管理
  - **FlowConnectorController** - 连接器管理
  - **FlowConnectorScriptController** - 连接器脚本管理

#### 4.2 流程执行
- **基础路径：** `/flow/*`
- **控制器：** FlowProcessExecController

---

### 5. **BPM 引擎模块** (`/bpm/*`)

#### 5.1 流程设计
- **控制器：** BpmDesignController

#### 5.2 流程实例管理
- **控制器：** BpmInstanceController

#### 5.3 任务中心
- **控制器：** BpmFlowTaskCenterController

#### 5.4 代理管理
- **控制器：** BpmAgentController

---

### 6. **公式引擎模块** (`/formula/*`)

#### 6.1 公式引擎
- **基础路径：** `/formula/engine`
- **主要端点：**
  - `POST /debug-formula` - 调试公式
  - `POST /execute-formula` - 执行公式
  - `POST /validate` - 验证公式

##### 运行时引擎
- **基础路径：** `/formula/excute/engine`
- **主要端点：**
  - `POST /execute-formula1` - 运行时公式执行

#### 6.2 公式函数管理
- **基础路径：** `/formula/function`
- **主要端点：**
  - `POST /create` - 创建函数
  - `POST /update` - 更新函数
  - `POST /delete` - 删除函数
  - `GET /get` - 获取函数详情
  - `GET /list` - 获取函数列表
  - `GET /page` - 分页获取函数
  - `GET /simple-list` - 简单列表
  - `GET /list-group-by-type` - 按类型分组列表

---

### 7. **ETL 数据模块** (`/etl/*`)

#### 7.1 数据源管理
- **控制器：** EtlDatasourceController

#### 7.2 工作流管理
- **控制器：** EtlWorkflowController

#### 7.3 Flink 函数管理
- **控制器：** EtlFlinkFunctionController

---

### 8. **基础设施模块** (`/infra/*`)

#### 8.1 文件管理

##### 构建模式
- **控制器：** 无（File API 通过 FileApiImpl 提供）

##### 平台模式
- **控制器：** PlatformFileController

##### 运行时模式
- **控制器：** RuntimeFileController

#### 8.2 安全配置
- **控制器：** SecurityConfigController

#### 8.3 错误日志
- **控制器：** ApiErrorLogController

---

### 9. **服务器模块**

#### 9.1 默认控制器
- **服务器类型：**
  - **onebase-server** (DefaultController)
  - **onebase-server-runtime** (DefaultController)
  - **onebase-server-platform** (DefaultController)
- **主要端点：**
  - `GET /test` - 测试端点
  - `POST /default` - 默认处理端点

#### 9.2 Flink 执行器
- **控制器：** FlinkExecutorController

---

## 认证与授权

### 认证机制

OneBase v3.0 支持多种认证模式：

1. **平台管理员认证** - 用于平台级别管理
2. **租户认证** - SaaS 多租户模式
3. **企业认证** - 企业内部用户认证
4. **OAuth2** - 第三方集成和SSO

### 授权模型

- **RBAC（基于角色的访问控制）**
- **多层级权限**：
  - 功能权限（页面、操作）
  - 数据权限（数据组、字段级别）
  - 视图权限
- **租户隔离**
- **企业隔离**

---

## 数据模型概览

### 核心实体类型

OneBase v3.0 使用 **MyBatis-Flex** 作为 ORM 框架，数据对象遵循 `*DO` 命名约定。

#### 主要数据模型模块：

1. **System 模块**（100+ 实体）
   - `AdminUserDO` - 管理员用户
   - `OAuth2AccessTokenDO` - OAuth2访问令牌
   - `OAuth2RefreshTokenDO` - OAuth2刷新令牌
   - `CorpAppRelationDO` - 企业应用关联

2. **Metadata 模块**（50+ 实体）
   - `MetadataBusinessEntityDO` - 业务实体
   - `MetadataEntityFieldDO` - 实体字段
   - `MetadataEntityRelationshipDO` - 实体关系
   - `MetadataDatasourceDO` - 数据源
   - `MetadataValidation*DO` - 各种验证规则

3. **Flow 模块**（10+ 实体）
   - `FlowProcessDO` - 流程定义
   - `FlowProcessEntityDO` - 流程实体
   - `FlowConnectorDO` - 流程连接器
   - `FlowNodeTypeDO` - 节点类型

4. **BPM 模块**（8+ 实体）
   - `FlowDefinition` - 流程定义
   - `FlowInstance` - 流程实例
   - `FlowTask` - 任务
   - `FlowHisTask` - 历史任务

5. **Formula 模块**
   - `FunctionDO` - 公式函数

---

## API 设计规范

### RESTful 约定

- **命名风格：** 小写、连字符分隔（kebab-case）
- **HTTP 方法：**
  - `GET` - 查询操作
  - `POST` - 创建/复杂查询操作
  - `PUT` - 更新操作
  - `DELETE` - 删除操作

### 通用响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

### 分页请求

- **参数：** `pageNo`、`pageSize`
- **响应：** 包含 `total`、`list` 等字段

### 异常处理

- 统一异常处理通过 `GlobalExceptionHandler`
- 标准化的错误码和错误消息

---

## 安全考虑

1. **认证令牌：** JWT / OAuth2
2. **权限校验：** 方法级注解、拦截器
3. **租户隔离：** 自动租户过滤
4. **数据脱敏：** 敏感字段处理
5. **审计日志：** 操作日志、登录日志

---

## 技术架构

- **框架：** Spring Boot 3.4.5
- **ORM：** MyBatis-Flex
- **工作流：** Flowable + LiteFlow（自研流程引擎）
- **缓存：** Redis
- **数据库：** 支持多数据源（MySQL、PostgreSQL等）
- **文件存储：** 支持本地、OSS等多种方式

---

## 下一步

- **详细 API 文档：** 每个端点的请求/响应示例需通过 Swagger/OpenAPI 生成
- **测试覆盖：** API 集成测试
- **性能优化：** 缓存策略、数据库索引优化

---

**文档版本：** 1.0
**最后更新：** 2025-12-06
**维护者：** OneBase Team
