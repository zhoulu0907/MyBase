# 自定义按钮后端设计方案

## 1. 文档信息

- 主题：自定义按钮一期后端设计
- 适用仓库：`onebase-v3-be`
- 设计日期：`2026-04-20`
- 设计范围：仅后端，不包含前端交互实现
- 设计目标：严格贴合当前 `onebase-v3-be` 的模块边界、接口风格、权限模型和运行态执行模式

## 2. 背景与目标

### 2.1 背景

根据《【US-20260209-029】自定义按钮》需求文档，应用开发者需要能够在表单视图、列表页行内操作、列表页批量操作中配置自定义按钮。用户在 runtime 侧点击按钮后，可以触发以下四类动作：

1. 修改当前表单
2. 新建关联表单
3. 执行自动化流
4. 打开页面

同时，该能力需要覆盖：

- 单条操作与批量操作
- 可用条件控制
- 操作权限接入
- 流程表单状态限制
- 自动化流绑定校验
- 按钮执行留痕

### 2.2 设计目标

本方案目标如下：

1. 在不引入新权限体系的前提下，完成自定义按钮的配置、查询、执行和审计闭环。
2. 严格复用现有 `app/build + app/runtime + app/core + flow + auth` 架构，不做跨模块职责侵入。
3. 支持需求文档的一期全量范围，不裁剪动作类型，不裁剪批量能力，不裁剪日志留痕。
4. 运行态执行采用“同步优先”策略，自动化流按现有引擎能力进行触发受理，不等待完整流程结束。

### 2.3 非目标

本方案不包含以下内容：

- 前端页面、弹窗、样式、交互细节实现
- 队列化任务调度重构
- 独立的新权限中心建设
- 多应用通用动作引擎平台化抽象

## 3. 现状分析与设计原则

### 3.1 现状分析

当前仓库已有如下可复用能力：

- 页面集与页面资源管理
  - [PageSetController](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-build/src/main/java/com/cmsr/onebase/module/app/build/controller/resource/PageSetController.java:1)
  - [AppResourcePagesetDO](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/dataobject/AppResourcePagesetDO.java:1)
  - [AppResourcePageDO](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/dataobject/AppResourcePageDO.java:1)
- 应用权限管理
  - [AppAuthPermissionController](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-build/src/main/java/com/cmsr/onebase/module/app/build/controller/auth/AppAuthPermissionController.java:1)
  - [AppAuthPermissionDO](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/dataobject/AppAuthPermissionDO.java:1)
- 字段权限能力
  - `app_auth_field`
  - `AppAuthSecurityFieldProvider`
- 自动化流运行时触发
  - [FlowProcessExecController](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-flow/onebase-module-flow-runtime/src/main/java/com/cmsr/onebase/module/flow/runtime/controller/FlowProcessExecController.java:1)
  - [FormTriggerReqVO](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-flow/onebase-module-flow-runtime/src/main/java/com/cmsr/onebase/module/flow/runtime/vo/FormTriggerReqVO.java:1)

### 3.2 设计原则

1. 自定义按钮归属 `onebase-module-app`，因为它本质是页面资源配置能力，不是流程引擎核心能力。
2. 配置和执行分离。`build` 负责配置，`runtime` 负责执行，`core` 负责领域模型与编排。
3. 权限体系复用现有 `operation_tags`，不新增角色按钮关系表。
4. 按钮配置独立建表，不继续塞入 `main_metadata` 或 `interactionRules`。
5. 单条执行与批量执行共享统一执行框架，只在事务边界和明细日志粒度上做区分。
6. 运行态必须做到“可见性”和“可执行性”分离，避免显示和真正执行行为不一致。

## 4. 总体方案

### 4.1 模块归属

推荐模块职责如下：

- `onebase-module-app-build`
  - 自定义按钮配置管理
  - 元数据下拉查询
  - 页面链接入参配置
  - 权限页中自定义按钮分组展示
- `onebase-module-app-runtime`
  - 查询当前上下文可用按钮
  - 单条执行
  - 批量执行
  - 执行日志查询
- `onebase-module-app-core`
  - 按钮定义 DO / Repository / Manager
  - 条件计算器
  - 权限校验器
  - 动作执行器
  - 执行编排服务
  - 执行日志服务
- `onebase-module-flow`
  - 提供可绑定流程列表
  - 提供自动化流触发能力
  - 删除流程前执行绑定校验

### 4.2 领域拆分

建议将领域拆为四个对象：

- 按钮定义：名称、描述、样式、显示位置、排序、状态、动作类型
- 按钮条件：按钮何时可见、何时可执行
- 按钮动作：四类动作的配置明细
- 执行日志：记录谁在什么时候对哪些数据执行了什么按钮以及结果

### 4.3 统一执行模型

运行态统一编排流程如下：

1. 加载按钮聚合配置
2. 校验按钮状态与上下文
3. 校验按钮操作权限
4. 校验当前记录与数据访问权限
5. 校验按钮可用条件
6. 按动作类型路由至对应执行器
7. 写入执行日志
8. 返回统一执行结果

## 5. 数据模型设计

### 5.1 主表：`app_custom_button`

作用：按钮配置主表

建议字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 主键 |
| `application_id` | 应用 ID |
| `button_uuid` | 按钮 UUID |
| `menu_uuid` | 菜单 UUID |
| `pageset_uuid` | 页面集 UUID |
| `page_uuid` | 页面 UUID，可空 |
| `button_code` | 按钮编码，作为操作权限编码 |
| `button_name` | 按钮名称 |
| `button_desc` | 按钮描述 |
| `show_desc` | 是否展示描述 |
| `style_type` | 样式，`primary` / `outline` |
| `color_hex` | 颜色值 |
| `color_alpha` | 透明度 |
| `icon_code` | 图标编码 |
| `operation_scope` | 操作类型，`single` / `batch` |
| `show_in_form` | 是否展示在表单视图 |
| `show_in_row_action` | 是否展示在列表页行内操作 |
| `show_in_batch_action` | 是否展示在批量操作 |
| `action_type` | 动作类型 |
| `sort_no` | 排序号 |
| `status` | 状态，`ENABLE` / `DISABLE` |
| `deleted` | 逻辑删除标记 |

建议约束：

- 唯一索引：`(application_id, button_code)`
- 唯一索引：`(pageset_uuid, button_name, deleted)`
- 普通索引：`(pageset_uuid, status, sort_no)`

### 5.2 条件表

#### 5.2.1 `app_custom_button_condition_group`

作用：按钮条件组

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `group_no`
- `logic_type`
- `sort_no`

#### 5.2.2 `app_custom_button_condition_item`

作用：按钮条件明细

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `group_id`
- `field_uuid`
- `field_code`
- `operator`
- `value_type`
- `compare_value`
- `sort_no`

### 5.3 动作子表

#### 5.3.1 修改当前表单

主表：`app_custom_button_action_update`

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `open_mode`
- `submit_success_text`

明细表一：`app_custom_button_update_edit_field`

- `field_uuid`
- `field_code`
- `required_flag`
- `sort_no`

明细表二：`app_custom_button_update_auto_field`

- `field_uuid`
- `field_code`
- `value_type`
- `value_config`
- `sort_no`

#### 5.3.2 新建关联表单

表：`app_custom_button_action_create_related`

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `target_pageset_uuid`
- `target_page_uuid`
- `target_entity_uuid`
- `target_relation_field_uuid`
- `target_relation_scope`
- `open_mode`

#### 5.3.3 执行自动化流

表：`app_custom_button_action_flow`

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `flow_process_id`
- `flow_process_uuid`
- `confirm_required`
- `confirm_text`
- `trigger_type`

其中 `trigger_type` 固定为 `CUSTOM_BUTTON`，用于删除联动和审计追踪。

#### 5.3.4 打开页面

主表：`app_custom_button_action_open_page`

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `target_type`
- `target_page_uuid`
- `target_pageset_uuid`
- `target_url`
- `open_mode`

参数表：`app_custom_button_open_param`

关键字段：

- `param_name`
- `param_source_type`
- `param_value_config`
- `sort_no`

### 5.4 页面链接入参配置表：`app_page_link_param_config`

作用：定义页面可接收的外部链接参数

关键字段：

- `id`
- `application_id`
- `pageset_uuid`
- `page_uuid`
- `field_uuid`
- `field_code`
- `param_name`
- `field_type`
- `sort_no`
- `status`

建议唯一索引：`(page_uuid, param_name, deleted)`

### 5.5 执行日志表

#### 5.5.1 主日志：`app_custom_button_exec_log`

关键字段：

- `id`
- `application_id`
- `button_uuid`
- `button_code`
- `button_name`
- `action_type`
- `operator_user_id`
- `operator_dept_id`
- `pageset_uuid`
- `page_uuid`
- `entity_uuid`
- `record_id`
- `batch_no`
- `operation_scope`
- `exec_status`
- `error_code`
- `error_message`
- `request_snapshot`
- `response_snapshot`
- `start_time`
- `end_time`
- `duration_ms`

#### 5.5.2 明细日志：`app_custom_button_exec_detail`

作用：批量执行明细

关键字段：

- `id`
- `application_id`
- `exec_log_id`
- `batch_no`
- `record_id`
- `exec_status`
- `error_code`
- `error_message`
- `result_snapshot`

### 5.6 权限设计结论

不新增独立的角色按钮权限表。  
`button_code` 直接作为 `operation_tags` 的一部分接入 [AppAuthPermissionDO](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/dataobject/AppAuthPermissionDO.java:1)。

## 6. 接口设计

### 6.1 build 侧接口

建议新增控制器：

- `AppCustomButtonController`
- 基础路径：`/app/custom-button`

建议接口：

1. `GET /page`
  - 查询按钮列表
2. `GET /get`
  - 查询按钮详情
3. `POST /create`
  - 新增按钮
4. `POST /update`
  - 编辑按钮
5. `POST /copy`
  - 复制按钮
6. `POST /delete`
  - 删除按钮
7. `POST /sort`
  - 排序按钮
8. `POST /update-status`
  - 启停按钮

### 6.2 build 侧辅助元数据接口

建议新增控制器：

- `AppCustomButtonMetaController`
- 基础路径：`/app/custom-button-meta`

建议接口：

1. `GET /action-options`
2. `GET /field-options`
3. `GET /related-page-options`
4. `GET /flow-options`
5. `GET /open-page-options`

### 6.3 页面链接入参配置接口

建议基于页面资源域新增：

- 控制器：`AppPageLinkParamConfigController`
- 基础路径：`/app/resource/page-link-param`

建议接口：

1. `GET /list`
2. `POST /save`

### 6.4 runtime 侧接口

建议新增控制器：

- `AppCustomButtonRuntimeController`
- 基础路径：`/app/custom-button-runtime`

建议接口：

1. `POST /list-available`
  - 查询当前上下文可见、可执行的按钮
2. `POST /get-exec-form`
  - 查询“修改当前表单”动作弹窗所需字段及默认值
3. `POST /execute`
  - 执行单条按钮
4. `POST /batch-execute`
  - 执行批量按钮
5. `GET /exec-log`
  - 查询单次执行详情
6. `POST /exec-log-page`
  - 查询执行日志分页列表

### 6.5 flow 协作接口

保持自动化流为能力提供方，建议补充：

1. `GET /flow/process/custom-button-options`
  - 查询当前页面可绑定的自定义按钮触发流程
2. 删除流程前校验当前流程是否被按钮绑定

### 6.6 权限接口调整

不新增权限控制器，继续复用 [AppAuthPermissionController](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-app/onebase-module-app-build/src/main/java/com/cmsr/onebase/module/app/build/controller/auth/AppAuthPermissionController.java:1)。

需要调整点：

- `get-function` 返回中新增“自定义按钮”分类
- `update-operation` 接受按钮 `buttonCode` 作为操作权限编码

## 7. 执行链路设计

### 7.1 统一执行编排

建议新增核心编排服务：

- `AppCustomButtonExecService`

统一执行步骤：

1. 加载按钮聚合配置
2. 校验按钮状态与显示位置
3. 校验用户按钮操作权限
4. 校验记录存在与数据访问权限
5. 校验按钮可用条件
6. 创建执行日志主记录
7. 按 `actionType` 分发到具体动作执行器
8. 写入成功或失败结果

### 7.2 单条执行链路

适用于 `POST /execute`

处理流程：

1. 校验按钮存在、启用、归属页面匹配
2. 校验当前请求位置是否允许展示
3. 校验当前用户是否拥有 `buttonCode`
4. 校验当前记录存在且可访问
5. 校验流程表单未办结限制
6. 校验按钮条件
7. 写主日志
8. 调用对应动作执行器
9. 更新执行日志状态

### 7.3 批量执行链路

适用于 `POST /batch-execute`

处理流程：

1. 校验按钮为批量按钮
2. 校验 `recordIds` 非空且未超上限
3. 创建主日志并生成 `batch_no`
4. 遍历记录逐条执行统一子流程
5. 每条写入 `exec_detail`
6. 汇总成功数与失败数返回

设计结论：

- 每条记录独立事务
- 整批不做全局回滚
- 返回汇总结果和批次号

### 7.4 修改当前表单

分两段处理：

第一段：查询可编辑字段

- 读取按钮配置的可编辑字段
- 加载当前记录值
- 根据字段权限过滤可编辑字段
- 若过滤后为空，返回“当前无可编辑字段”

第二段：提交执行

- 校验提交字段必须在按钮允许编辑范围内
- 校验用户字段写权限
- 校验字段类型与必填性
- 执行自动更新字段规则
- 合并成最终更新载荷
- 调用现有实体保存能力完成更新

设计约束：

- 手动编辑字段与自动更新字段不允许重复配置
- 自动更新字段由后端统一执行，不信任前端透传结果

### 7.5 新建关联表单

目标不是直接代替用户写最终数据，而是生成目标页预填充上下文。

处理流程：

1. 校验目标页面、目标实体、关联字段存在
2. 校验当前用户对目标页面有访问权限
3. 根据单条或批量场景生成预填参数
4. 返回目标页面信息、打开方式和预填数据

### 7.6 执行自动化流

复用 [FlowProcessExecController](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-flow/onebase-module-flow-runtime/src/main/java/com/cmsr/onebase/module/flow/runtime/controller/FlowProcessExecController.java:1) 能力。

处理流程：

1. 校验绑定流程存在、启用、已发布
2. 组装 [FormTriggerReqVO](/Users/bty418/Documents/ob3.0/onebase-v3-be/onebase-module-flow/onebase-module-flow-runtime/src/main/java/com/cmsr/onebase/module/flow/runtime/vo/FormTriggerReqVO.java:1)
3. 以当前记录上下文作为 `inputParams`
4. 调用 `flow` 触发 service
5. 根据受理结果写按钮执行结果

设计结论：

- 按钮执行成功的判定标准为“自动化流触发受理成功”
- 不等待完整流程结束后才返回
- 后续流程节点执行失败归流程执行日志体系处理

### 7.7 打开页面

处理流程：

1. 校验目标类型是应用内页面或外链
2. 若为应用内页面，校验目标页存在且用户有权限访问
3. 读取目标页允许接收的链接入参配置
4. 将按钮参数配置与页面接参定义做匹配
5. 拼接最终地址或页面参数
6. 返回 `targetType`、`openMode`、`resolvedParams`

设计约束：

- 最终 URL 长度不超过 2000
- 参数名不可重复
- 应用内页面仅允许传入目标页面已声明接收的参数

### 7.8 事务边界

建议策略：

- `UPDATE_FORM`
  - 业务更新与日志成功写入尽量同事务
- `CREATE_RELATED_RECORD`
  - 如仅返回预填参数，则无需大事务
- `TRIGGER_FLOW`
  - 按钮域不包裹 flow 内部长事务
- `OPEN_PAGE`
  - 纯计算，无需事务
- `BATCH_EXECUTE`
  - 每条记录独立事务

### 7.9 幂等策略

建议运行态执行接口接入轻量幂等能力。

推荐方案：

- 前端传入 `requestId`
- 后端按 `buttonCode + operatorUserId + recordId + requestId` 做短时防重
- 批量操作按 `batchNo` 或 `requestId` 去重

## 8. 权限、批量、日志与错误处理

### 8.1 权限模型

权限收口原则：

1. 按钮不拥有独立权限体系
2. `buttonCode` 作为 `operation_tags`
3. 权限页中新增“自定义按钮”分组
4. 运行态执行前统一校验当前用户是否持有 `buttonCode`

### 8.2 字段权限

规则如下：

- 手工编辑字段必须同时满足：
  - 被按钮配置选中
  - 当前用户对该字段具备写权限
- 自动更新字段不要求用户具备显式写权限，但必须由具备按钮权限的系统动作执行
- 若手工可编辑字段过滤后为空，则拒绝执行并提示联系管理员

### 8.3 页面与目标资源权限

建议规则：

- 打开应用内页面前校验目标页面访问权限
- 新建关联表单前校验目标表单视图访问权限
- 自动化流触发只看按钮权限和当前页面访问权限，不要求用户额外持有流程管理权限

### 8.4 批量执行边界

建议约束：

- 单次批量记录数上限：建议 `100` 或 `200`
- 批量按钮仅允许出现在批量操作区域
- 批量模式下不支持“修改当前表单”和“打开页面”
- 首期重点支持：
  - 新建关联表单
  - 执行自动化流

### 8.5 日志与审计

日志设计目标：

- 可回溯
- 可排障
- 可分页查询

建议保留以下维度：

- 操作人
- 按钮编码与按钮名称快照
- 页面集 / 页面
- 单条记录或批量记录集合
- 动作类型
- 入参与结果快照
- 错误码、错误信息
- 耗时

### 8.6 错误码建议

建议新增按钮域错误码常量：

- `CUSTOM_BUTTON_NOT_EXISTS`
- `CUSTOM_BUTTON_DISABLED`
- `CUSTOM_BUTTON_NAME_DUPLICATE`
- `CUSTOM_BUTTON_COUNT_EXCEED_LIMIT`
- `CUSTOM_BUTTON_ACTION_MISMATCH`
- `CUSTOM_BUTTON_PERMISSION_DENIED`
- `CUSTOM_BUTTON_CONDITION_NOT_MATCH`
- `CUSTOM_BUTTON_TARGET_PAGE_DENIED`
- `CUSTOM_BUTTON_FLOW_NOT_ENABLED`
- `CUSTOM_BUTTON_FLOW_BOUND_CANNOT_DELETE`
- `CUSTOM_BUTTON_NO_EDITABLE_FIELDS`
- `CUSTOM_BUTTON_BATCH_EMPTY`
- `CUSTOM_BUTTON_BATCH_EXCEED_LIMIT`
- `CUSTOM_BUTTON_PARAM_INVALID`
- `CUSTOM_BUTTON_TARGET_INVALID`

### 8.7 配置期前置校验

建议在保存按钮配置时完成大部分前置校验：

1. 同一页面集下按钮名称唯一
2. 页面集下按钮数量不超过 10
3. `single` 类型必须至少有一个单条显示位置
4. `batch` 类型只能出现在批量操作
5. 动作类型必须与操作类型匹配
6. 手工编辑字段与自动更新字段不可重复
7. 打开页面参数名不可重复
8. 目标流程必须属于当前应用且触发类型正确
9. 目标页面必须属于当前应用
10. 条件字段、更新字段、参数字段必须属于当前实体或合法目标页

## 9. 核心服务拆分建议

建议新增如下核心服务：

- `AppCustomButtonQueryService`
  - 配置查询和运行态按钮查询
- `AppCustomButtonExecService`
  - 统一执行编排
- `AppCustomButtonConditionEvaluator`
  - 可用条件计算
- `AppCustomButtonPermissionChecker`
  - 按钮权限、字段权限、页面权限校验
- `AppCustomButtonLogService`
  - 主日志与批量明细日志处理
- `CustomButtonActionHandler`
  - 动作执行器接口
- `UpdateFormActionHandler`
- `CreateRelatedActionHandler`
- `TriggerFlowActionHandler`
- `OpenPageActionHandler`

## 10. 对现有能力的影响

### 10.1 应用权限

需要在应用权限页中将操作权限按以下维度分组：

- 常用操作
- 批量操作
- 自定义按钮

### 10.2 页面设置

页面设置中新增：

- 自定义按钮配置
- 链接入参配置

### 10.3 自动化流

流程管理侧需新增或补齐：

- 自定义按钮触发类型的流程筛选能力
- 流程删除前绑定校验

### 10.4 删除联动

建议补充以下一致性校验：

- 删除流程前校验是否绑定按钮
- 删除页面前校验页面或页面集下是否仍有按钮
- 删除字段前校验是否被按钮条件、自动更新字段、参数映射引用

## 11. 实施建议

建议按以下顺序实施：

1. 表结构与 DO / Repository 建模
2. build 侧按钮配置接口
3. runtime 侧按钮列表查询
4. 单条执行链路
5. 批量执行链路
6. 权限页自定义按钮分组接入
7. 自动化流联动校验
8. 日志查询接口

## 12. 风险与取舍

### 12.1 风险

1. 表数量较多，首期建模成本高
2. 条件计算与字段权限组合会增加运行态复杂度
3. 批量执行如不限制数量，可能带来明显压力
4. 自动化流执行受理成功但流程内部失败时，前端语义需统一

### 12.2 取舍结论

本方案选择：

- 接受首期建模复杂度，换取后续扩展性与可维护性
- 保持同步优先，避免首期过早任务化
- 复用权限与流程现有体系，不做平台化重构
- 以“按钮执行受理成功”作为自动化流动作成功标准

## 13. 最终结论

本方案推荐采用“页面域主导的独立按钮中心”设计：

- 以 `onebase-module-app` 为自定义按钮主域
- 以独立物理表承载按钮定义、动作配置、条件配置与执行日志
- 以 `buttonCode` 复用现有 `operation_tags` 权限体系
- 以 `flow` 模块作为自动化流能力提供方
- 以统一执行编排服务完成四类动作与批量能力的后端闭环

该方案能够在不破坏现有仓库边界的前提下，完整覆盖需求文档的一期后端建设范围，并为后续按钮动作扩展、日志审计和权限管理保留足够空间。
