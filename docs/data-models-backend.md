# OneBase v3.0 Backend - 数据模型文档

> **生成日期：** 2025-12-06
> **项目：** onebase-v3-be
> **数据库：** PostgreSQL 10+
> **ORM框架：** MyBatis-Flex
> **表总数：** 100+ 张表

---

## 📋 目录

1. [概述](#概述)
2. [数据库设计原则](#数据库设计原则)
3. [核心模块数据模型](#核心模块数据模型)
4. [通用字段说明](#通用字段说明)
5. [多租户架构](#多租户架构)
6. [索引策略](#索引策略)

---

## 概述

OneBase v3.0 Backend 采用多模块化数据模型设计，支持 SaaS 多租户架构。数据模型分为以下主要模块：

- **App 模块**：应用构建、页面资源、权限管理
- **System 模块**：系统管理、用户、角色、租户
- **Metadata 模块**：业务实体、字段定义、数据源
- **Flow 模块**：流程编排、节点管理、连接器
- **BPM 模块**：业务流程管理、流程实例、任务
- **Formula 模块**：公式引擎、函数管理
- **ETL 模块**：数据抽取、转换、加载
- **Infra 模块**：基础设施、日志、文件存储

---

## 数据库设计原则

### 1. 命名约定

- **表名：** 模块前缀 + 下划线 + 实体名（例如：`app_menu`、`system_user`）
- **序列：** 表名 + `_seq` 或 `_id_seq`
- **主键字段：** 统一使用 `id`（int8/bigint）
- **外键字段：** 关联表名 + `_id`

### 2. 通用字段

所有业务表包含以下标准字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | int8 | 主键ID |
| `creator` | int8 | 创建人ID |
| `create_time` | timestamp(6) | 创建时间 |
| `updater` | int8 | 更新人ID |
| `update_time` | timestamp(6) | 更新时间 |
| `deleted` / `is_deleted` | int2/int8/bool | 逻辑删除标记 |
| `tenant_id` | int8 | 租户ID（多租户隔离） |
| `lock_version` | int8 | 乐观锁版本号 |

### 3. 设计特点

- **逻辑删除**：所有业务表采用逻辑删除而非物理删除
- **乐观锁**：通过 `lock_version` 字段防止并发冲突
- **租户隔离**：`tenant_id` 字段实现多租户数据隔离
- **审计跟踪**：`creator`、`create_time`、`updater`、`update_time` 实现完整审计

---

## 核心模块数据模型

### 1. App 应用模块

#### 1.1 核心应用表

##### `app_application` - 应用主表
存储应用的基本信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 应用ID |
| app_code | varchar(64) | 应用编码 |
| app_name | varchar(128) | 应用名称 |
| app_type | int2 | 应用类型 |
| description | text | 应用描述 |
| + 通用字段 | - | - |

##### `app_version` - 应用版本表
管理应用的不同版本。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 版本ID |
| application_id | int8 | 应用ID |
| version_name | varchar(128) | 版本名称 |
| version_number | varchar(64) | 版本号 |
| + 通用字段 | - | - |

#### 1.2 菜单与导航

##### `app_menu` - 应用菜单表
应用的菜单导航结构。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 菜单ID |
| application_id | int8 | 应用ID |
| parent_uuid | varchar(64) | 父菜单UUID |
| menu_uuid | varchar(64) | 菜单UUID |
| menu_sort | int4 | 菜单排序 |
| menu_type | int2 | 菜单类型 |
| menu_name | varchar(64) | 菜单名称 |
| menu_icon | varchar(64) | 菜单图标 |
| action_target | varchar(256) | 菜单动作目标 |
| is_visible | int2 | 是否可见 |
| + 通用字段 | - | - |

##### `app_version_menu` - 版本菜单表
特定版本的菜单配置。

#### 1.3 页面资源

##### `app_resource_page` - 页面资源表
低代码页面的配置信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 页面ID |
| page_code | varchar(255) | 页面编码 |
| page_name | varchar(255) | 页面名称 |
| title | varchar(255) | 页面标题 |
| layout | varchar(255) | 布局类型 |
| width | varchar(255) | 页面宽度 |
| margin | varchar(255) | 页面边距 |
| background_color | varchar(255) | 背景颜色 |
| main_metadata | varchar(255) | 主元数据ID |
| bpm_enabled | bool | 是否启用BPM |
| router_path | varchar(255) | 路由路径 |
| router_name | varchar(255) | 路由名称 |
| router_meta_auth_required | bool | 是否需要认证 |
| router_meta_title | varchar(255) | 路由标题 |
| + 通用字段 | - | - |

##### `app_resource_component` - 组件资源表
页面中的组件配置。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 组件ID |
| component_code | varchar(255) | 组件编码 |
| page_id | int8 | 所属页面ID |
| in_table | bool | 是否在表格中 |
| component_type | varchar(64) | 组件类型 |
| label | varchar(255) | 组件标签 |
| width | int4 | 组件宽度 |
| hidden | bool | 是否隐藏 |
| read_only | varchar(255) | 是否只读 |
| required | bool | 是否必填 |
| config | text | 组件配置（JSON） |
| edit_data | text | 编辑数据 |
| + 通用字段 | - | - |

##### `app_resource_pageset` - 页面集表
管理一组相关页面。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 页面集ID |
| pageset_code | varchar(255) | 页面集编码 |
| menu_id | int8 | 关联菜单ID |
| pageset_name | varchar(255) | 页面集名称 |
| display_name | varchar(255) | 显示名称 |
| description | text | 描述 |
| + 通用字段 | - | - |

#### 1.4 权限管理

##### `app_auth_role` - 应用角色表
应用级别的角色定义。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 角色ID |
| application_id | int8 | 应用ID |
| role_code | varchar(64) | 角色编码 |
| role_name | varchar(64) | 角色名称 |
| description | varchar(256) | 角色描述 |
| + 通用字段 | - | - |

##### `app_auth_user_role` - 用户角色关联表
用户与应用角色的关联。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 关联ID |
| application_id | int8 | 应用ID |
| user_id | int8 | 用户ID |
| role_id | int8 | 角色ID |
| + 通用字段 | - | - |

##### `app_auth_operation` - 操作权限表
控制页面上的操作权限（增删改查等）。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 权限ID |
| application_id | int8 | 应用ID |
| menu_id | int8 | 菜单ID |
| operation_type | varchar(64) | 操作类型（create/update/delete等） |
| is_allowed | int2 | 是否允许 |
| + 通用字段 | - | - |

##### `app_auth_field` - 字段权限表
控制字段级别的权限。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int8 | 权限ID |
| application_id | int8 | 应用ID |
| menu_id | int8 | 菜单ID |
| field_name | varchar(100) | 字段名称 |
| is_can_read | int2 | 是否可读 |
| is_can_edit | int2 | 是否可编辑 |
| is_can_download | int2 | 是否可下载 |
| + 通用字段 | - | - |

##### `app_auth_data_group` - 数据权限组表
数据范围权限控制。

##### `app_auth_view` - 视图权限表
控制数据视图的访问权限。

#### 1.5 其他应用表

##### `app_tag` - 应用标签表
应用的分类标签。

##### `app_resource` - 通用资源表
存储各种协议类型的资源数据。

---

### 2. System 系统模块

#### 2.1 用户管理

##### `system_users` - 系统用户表
平台用户基础信息。

##### `system_corp_user` - 企业用户表
企业内部用户信息。

##### `system_user_role` - 用户角色关联表
用户与系统角色的关联。

#### 2.2 角色与权限

##### `system_role` - 角色表
系统角色定义。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 角色ID |
| role_name | varchar(30) | 角色名称 |
| role_code | varchar(100) | 角色编码 |
| role_sort | int4 | 角色排序 |
| data_scope | int2 | 数据范围权限 |
| status | int2 | 角色状态 |

##### `system_menu` - 菜单表
系统级菜单和权限资源。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 菜单ID |
| parent_id | int8 | 父菜单ID |
| menu_name | varchar(50) | 菜单名称 |
| menu_type | int2 | 菜单类型（目录/菜单/按钮） |
| permission | varchar(100) | 权限标识 |
| path | varchar(200) | 路由地址 |
| component | varchar(255) | 组件路径 |
| status | int2 | 菜单状态 |

##### `system_role_menu` - 角色菜单关联表
角色与菜单权限的关联。

#### 2.3 组织架构

##### `system_dept` - 部门表
组织架构的部门信息。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 部门ID |
| parent_id | int8 | 父部门ID |
| dept_name | varchar(30) | 部门名称 |
| dept_sort | int4 | 部门排序 |
| leader_user_id | int8 | 部门负责人 |
| status | int2 | 部门状态 |

##### `system_post` - 岗位表
岗位职位定义。

##### `system_user_post` - 用户岗位关联表

#### 2.4 租户管理

##### `system_tenant` - 租户表
SaaS 多租户主表。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 租户ID |
| tenant_name | varchar(30) | 租户名称 |
| contact_user_id | int8 | 联系人ID |
| contact_name | varchar(30) | 联系人姓名 |
| contact_mobile | varchar(20) | 联系手机 |
| status | int2 | 租户状态 |
| package_id | int8 | 租户套餐ID |
| expire_time | timestamp(6) | 过期时间 |

##### `system_tenant_package` - 租户套餐表
定义不同的租户套餐和权限。

##### `system_corp` - 企业信息表
企业组织的基本信息。

##### `system_corp_app_relation` - 企业应用关联表
企业与应用的关联关系。

#### 2.5 OAuth2 认证

##### `system_oauth2_client` - OAuth2客户端表
OAuth2 客户端配置。

##### `system_oauth2_access_token` - 访问令牌表
用户访问令牌存储。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 令牌ID |
| access_token | varchar(255) | 访问令牌 |
| user_id | int8 | 用户ID |
| user_type | int2 | 用户类型 |
| client_id | varchar(255) | 客户端ID |
| expires_time | timestamp(6) | 过期时间 |

##### `system_oauth2_refresh_token` - 刷新令牌表
用于刷新访问令牌。

##### `system_oauth2_code` - 授权码表
OAuth2 授权码流程。

##### `system_oauth2_approve` - 授权批准表

#### 2.6 字典管理

##### `system_dict_type` - 字典类型表
数据字典的分类。

##### `system_dict_data` - 字典数据表
字典的具体数据项。

#### 2.7 日志管理

##### `system_login_log` - 登录日志表
用户登录记录。

##### `system_operate_log` - 操作日志表
用户操作行为日志。

#### 2.8 通知与消息

##### `system_notice` - 通知公告表

##### `system_notify_template` - 通知模板表

##### `system_notify_message` - 通知消息表

#### 2.9 短信服务

##### `system_sms_channel` - 短信渠道表
短信供应商配置。

##### `system_sms_template` - 短信模板表

##### `system_sms_log` - 短信发送日志表

##### `system_sms_code` - 短信验证码表

#### 2.10 邮件服务

##### `system_mail_account` - 邮件账号表

##### `system_mail_template` - 邮件模板表

##### `system_mail_log` - 邮件发送日志表

#### 2.11 社交登录

##### `system_social_client` - 社交客户端表
第三方登录配置（微信、钉钉等）。

##### `system_social_user` - 社交用户表

##### `system_social_user_bind` - 社交用户绑定表

---

### 3. Metadata 元数据模块

#### 3.1 业务实体

##### `metadata_business_entity` - 业务实体表
低代码平台的核心：业务对象定义。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 实体ID |
| entity_code | varchar(64) | 实体编码 |
| entity_name | varchar(128) | 实体名称 |
| table_name | varchar(128) | 对应表名 |
| description | text | 实体描述 |
| entity_type | int2 | 实体类型（系统/自定义） |

##### `metadata_entity_field` - 实体字段表
业务实体的字段定义。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 字段ID |
| entity_id | int8 | 所属实体ID |
| field_code | varchar(64) | 字段编码 |
| field_name | varchar(128) | 字段名称 |
| field_type | varchar(64) | 字段类型 |
| db_column_name | varchar(128) | 数据库列名 |
| db_column_type | varchar(64) | 数据库列类型 |
| is_required | bool | 是否必填 |
| is_unique | bool | 是否唯一 |
| default_value | text | 默认值 |

##### `metadata_system_fields` - 系统字段表
预定义的系统字段（创建人、创建时间等）。

##### `metadata_component_field_type` - 组件字段类型表
字段类型与UI组件的映射。

##### `field_type_mapping` - 字段类型映射表
数据库类型与业务类型的映射关系。

#### 3.2 字段选项

##### `metadata_entity_field_option` - 字段选项表
下拉选择、单选、多选等字段的选项值。

#### 3.3 实体关系

##### `metadata_entity_relationship` - 实体关系表
实体间的关联关系（一对一、一对多、多对多）。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 关系ID |
| source_entity_id | int8 | 源实体ID |
| target_entity_id | int8 | 目标实体ID |
| relationship_type | varchar(64) | 关系类型（1:1, 1:N, M:N） |
| source_field_id | int8 | 源字段ID |
| target_field_id | int8 | 目标字段ID |

#### 3.4 数据源管理

##### `metadata_datasource` - 数据源表
多数据源配置。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 数据源ID |
| datasource_name | varchar(128) | 数据源名称 |
| datasource_type | varchar(64) | 数据源类型（MySQL/PostgreSQL等） |
| host | varchar(255) | 主机地址 |
| port | int4 | 端口 |
| database_name | varchar(128) | 数据库名 |
| username | varchar(128) | 用户名 |
| password | varchar(256) | 密码（加密） |

##### `metadata_app_and_datasource` - 应用数据源关联表

#### 3.5 字段验证规则

##### `metadata_validation_rule_definition` - 验证规则定义表

##### `metadata_validation_rule_group` - 验证规则组表

##### `metadata_validation_required` - 必填验证表

##### `metadata_validation_unique` - 唯一性验证表

##### `metadata_validation_length` - 长度验证表

##### `metadata_validation_range` - 范围验证表

##### `metadata_validation_format` - 格式验证表

##### `metadata_validation_child_not_empty` - 子表非空验证表

##### `metadata_permit_ref_otft` - 引用权限表

#### 3.6 自动编号

##### `metadata_auto_number_config` - 自动编号配置表
流水号生成规则配置。

##### `metadata_auto_number_rule_item` - 编号规则项表

##### `metadata_auto_number_state` - 编号状态表

##### `metadata_auto_number_reset_log` - 编号重置日志表

#### 3.7 数据方法

##### `metadata_data_system_method` - 系统数据方法表
预定义的数据处理方法。

---

### 4. Flow 流程编排模块

#### 4.1 流程定义

##### `flow_process` - 流程定义表
LiteFlow 流程编排的核心表。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 流程ID |
| process_code | varchar(64) | 流程编码 |
| process_name | varchar(128) | 流程名称 |
| process_type | varchar(64) | 流程类型 |
| graph_data | text | 流程图数据（JSON） |
| is_enabled | bool | 是否启用 |

##### `flow_process_entity` - 流程实体表
流程关联的业务实体。

##### `flow_process_form` - 流程表单表

##### `flow_process_time` - 流程时间配置表

##### `flow_process_date_field` - 流程日期字段表

#### 4.2 节点管理

##### `flow_node_type` - 流程节点类型表
定义不同类型的流程节点（开始、结束、条件、动作等）。

##### `flow_node_category` - 节点分类表

#### 4.3 连接器

##### `flow_connector` - 流程连接器表
节点间的连接定义。

##### `flow_connector_script` - 连接器脚本表
连接器的脚本逻辑。

#### 4.4 执行日志

##### `flow_execution_log` - 流程执行日志表
记录流程的执行历史。

---

### 5. BPM 业务流程模块

#### 5.1 流程引擎（基于 Warm-Flow）

##### `flow_definition` - 流程定义表（BPM引擎）

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 流程定义ID |
| flow_code | varchar(64) | 流程编码 |
| flow_name | varchar(128) | 流程名称 |
| version | int4 | 版本号 |
| is_publish | bool | 是否已发布 |
| xml_string | text | 流程定义XML |

##### `flow_instance` - 流程实例表

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 实例ID |
| definition_id | int8 | 流程定义ID |
| business_id | varchar(64) | 业务ID |
| node_code | varchar(64) | 当前节点编码 |
| node_name | varchar(128) | 当前节点名称 |
| flow_status | int2 | 流程状态（进行中/已完成/已终止） |

##### `flow_task` - 流程任务表（待办任务）

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 任务ID |
| instance_id | int8 | 流程实例ID |
| node_code | varchar(64) | 节点编码 |
| node_name | varchar(128) | 节点名称 |
| approver | varchar(64) | 审批人 |
| task_status | int2 | 任务状态（待审批/已审批/已拒绝） |

##### `flow_his_task` - 历史任务表
已完成的任务记录。

##### `flow_node` - 流程节点表
流程定义中的节点信息。

##### `flow_skip` - 流程跳转表
流程的条件跳转规则。

##### `flow_user` - 流程用户表
流程参与人员信息。

---

### 6. Formula 公式引擎模块

##### `formula_function` - 公式函数表
自定义公式函数定义。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 函数ID |
| function_code | varchar(64) | 函数编码 |
| function_name | varchar(128) | 函数名称 |
| function_type | varchar(64) | 函数类型 |
| script | text | 函数脚本（JavaScript/Groovy） |
| return_type | varchar(64) | 返回值类型 |
| parameters | text | 参数定义（JSON） |

---

### 7. ETL 数据模块

##### `etl_datasource` - ETL数据源表

##### `etl_workflow` - ETL工作流表

##### `etl_flink_function` - Flink函数表

---

### 8. Infra 基础设施模块

#### 8.1 文件管理

##### `infra_file` - 文件表
文件上传记录。

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 文件ID |
| config_id | int8 | 存储配置ID |
| file_name | varchar(256) | 文件名 |
| file_path | varchar(512) | 文件路径 |
| file_url | varchar(1024) | 访问URL |
| file_type | varchar(128) | 文件类型 |
| file_size | int8 | 文件大小（字节） |

##### `infra_file_config` - 文件存储配置表
支持本地、OSS、MinIO等多种存储方式。

##### `infra_file_content` - 文件内容表
文件的二进制内容（可选）。

#### 8.2 日志管理

##### `infra_api_access_log` - API访问日志表

| 核心字段 | 类型 | 说明 |
|---------|------|------|
| id | int8 | 日志ID |
| trace_id | varchar(64) | 链路追踪ID |
| user_id | int8 | 用户ID |
| user_type | int2 | 用户类型 |
| request_method | varchar(16) | 请求方法（GET/POST） |
| request_url | varchar(255) | 请求URL |
| request_params | text | 请求参数 |
| response_body | text | 响应内容 |
| user_ip | varchar(50) | 用户IP |
| user_agent | varchar(512) | 浏览器UA |
| begin_time | timestamp(6) | 开始时间 |
| end_time | timestamp(6) | 结束时间 |
| duration | int4 | 执行时长（毫秒） |
| result_code | int4 | 结果码 |

##### `infra_api_error_log` - API错误日志表

#### 8.3 配置管理

##### `infra_config` - 系统配置表
键值对形式的系统配置。

##### `infra_data_source_config` - 数据源配置表

#### 8.4 定时任务

##### `infra_job` - 定时任务表

##### `infra_job_log` - 任务执行日志表

---

## 通用字段说明

### 1. 主键 (id)

- **类型：** `int8` (bigint)
- **生成策略：** PostgreSQL 序列自增
- **说明：** 全局唯一，使用雪花算法或序列生成

### 2. 创建信息

- **creator：** 创建人ID（关联 system_users.id）
- **create_time：** 创建时间，默认 `CURRENT_TIMESTAMP`

### 3. 更新信息

- **updater：** 最后更新人ID
- **update_time：** 最后更新时间，默认 `CURRENT_TIMESTAMP`

### 4. 逻辑删除

- **deleted / is_deleted：**
  - 类型可能是 `int2`、`int8` 或 `bool`
  - `0` / `false` = 未删除
  - `非0` / `true` = 已删除
  - 索引建议：`WHERE deleted = 0` 或 `WHERE NOT deleted`

### 5. 租户隔离

- **tenant_id：** 租户ID
  - `0` = 平台级数据
  - `>0` = 租户数据
  - 所有查询必须加上 `WHERE tenant_id = #{tenantId}`

### 6. 乐观锁

- **lock_version：** 版本号
  - 更新时：`UPDATE ... SET ..., lock_version = lock_version + 1 WHERE id = #{id} AND lock_version = #{oldVersion}`
  - 防止并发更新冲突

---

## 多租户架构

### 1. 租户隔离策略

OneBase v3.0 采用 **共享数据库、共享表、租户字段隔离** 的多租户模式：

- 所有业务表包含 `tenant_id` 字段
- 通过 MyBatis-Flex 的租户插件自动注入租户条件
- SQL 示例：
  ```sql
  SELECT * FROM app_menu WHERE tenant_id = 123 AND deleted = 0;
  ```

### 2. 平台级数据

某些表的数据属于平台级别（`tenant_id = 0`）：

- `system_tenant` - 租户主表
- `system_tenant_package` - 租户套餐
- `system_platform_info` - 平台信息
- `metadata_system_fields` - 系统字段
- `metadata_component_field_type` - 组件字段类型

### 3. 租户数据隔离

应用数据、用户数据、业务数据等都按租户隔离：

- `app_*` 表 - 应用和页面
- `system_users` - 用户
- `system_role` - 角色
- `metadata_business_entity` - 业务实体
- `flow_*` - 流程数据

---

## 索引策略

### 1. 主键索引

所有表的 `id` 字段自动创建主键索引（`PRIMARY KEY`）。

### 2. 租户索引

建议为所有带 `tenant_id` 的表创建复合索引：

```sql
CREATE INDEX idx_table_tenant_deleted ON table_name(tenant_id, deleted);
```

### 3. 外键索引

外键字段建议创建索引以提升查询性能：

```sql
CREATE INDEX idx_app_menu_application_id ON app_menu(application_id);
CREATE INDEX idx_app_auth_user_role_user_id ON app_auth_user_role(user_id);
CREATE INDEX idx_app_auth_user_role_role_id ON app_auth_user_role(role_id);
```

### 4. 查询优化索引

根据常用查询条件创建索引：

```sql
-- 按编码查询
CREATE INDEX idx_app_application_app_code ON app_application(app_code);
CREATE INDEX idx_metadata_business_entity_code ON metadata_business_entity(entity_code);

-- 按状态查询
CREATE INDEX idx_system_user_status ON system_users(status) WHERE deleted = 0;

-- 按时间范围查询
CREATE INDEX idx_infra_api_access_log_time ON infra_api_access_log(begin_time DESC);
```

---

## ER 图建议

由于表数量众多（100+张），建议按模块拆分 ER 图：

1. **App 模块 ER 图**
2. **System 模块 ER 图**
3. **Metadata 模块 ER 图**
4. **Flow + BPM 模块 ER 图**
5. **Infra 模块 ER 图**

可使用工具：

- **DBeaver** - 数据库ER图生成
- **DbDiagram.io** - 在线ER图工具
- **PlantUML** - 代码生成ER图

---

## 数据迁移与备份

### 1. 版本管理

建议使用 Flyway 或 Liquibase 管理数据库版本：

```
sql/
├── postgresql/
│   └── 1010/
│       └── ddl/
│           ├── system_ddl.sql
│           ├── metadata_ddl.sql
│           ├── flow_ddl.sql
│           ├── bpm_ddl.sql
│           ├── app_ddl.sql
│           ├── formula_ddl.sql
│           ├── infra_ddl.sql
│           └── job_ddl.sql
```

### 2. 备份策略

- **全量备份：** 每日凌晨
- **增量备份：** 每小时
- **日志归档：** 每周
- **租户数据导出：** 按需

---

## 性能优化建议

1. **分区表**：
   - 日志表按时间分区（`infra_api_access_log`、`system_login_log` 等）
   - 大数据表按租户分区

2. **读写分离**：
   - 主库：写操作
   - 从库：读操作、报表查询

3. **缓存策略**：
   - Redis 缓存：字典数据、菜单数据、权限数据
   - 应用级缓存：元数据定义、实体字段

4. **归档策略**：
   - 历史日志归档到归档库
   - 已完成流程实例归档

---

## 下一步

- **ER 图生成**：使用 DBeaver 或 DbDiagram.io 生成各模块的 ER 图
- **数据字典导出**：完整的字段级别数据字典文档
- **示例数据**：提供测试数据脚本

---

**文档版本：** 1.0
**最后更新：** 2025-12-06
**维护者：** OneBase Team
