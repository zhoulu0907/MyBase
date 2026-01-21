# Huangjie 代码位置索引

**更新日期:** 2025-12-28
**用途:** 按功能快速定位代码文件

---

## 📁 模块结构总览

```
onebase-v3-be/
├── onebase-framework/              # 核心框架层
│   ├── onebase-common              # 公共工具类
│   └── onebase-spring-boot-starter-orm/    # ORM框架 ⭐⭐⭐⭐⭐
│
├── onebase-server/                 # 主应用服务器
│
├── onebase-module-flow/            # 流程编排模块 ⭐⭐⭐⭐⭐
│   ├── onebase-module-flow-api/            # API定义
│   ├── onebase-module-flow-component/      # 节点组件
│   ├── onebase-module-flow-context/        # 上下文管理
│   ├── onebase-module-flow-core/           # 核心执行引擎
│   └── onebase-module-flow-runtime/        # 运行时服务
│
└── onebase-module-app/             # 应用管理模块 ⭐⭐⭐⭐
    ├── onebase-module-app-api/             # API定义
    ├── onebase-module-app-core/            # 核心业务逻辑
    └── onebase-module-app-runtime/         # 运行时服务
```

---

## 🔥 Flow模块（流程引擎）

### 核心执行引擎
```
路径: onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/flow/

├── FlowProcessExecutor.java              ⭐⭐⭐⭐⭐
│   位置: flow/FlowProcessExecutor.java:44
│   修改: 44次
│   职责: 流程执行器核心，负责流程的执行、监控和日志记录
│
├── FlowProcessExecServiceImpl.java       ⭐⭐⭐⭐
│   修改: 36次
│   职责: 流程执行服务实现
│
└── FlowProcessExecApiImpl.java           ⭐⭐⭐
    修改: 26次
    职责: 流程执行API实现
```

### 执行上下文管理
```
路径: onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/

├── ExecuteContext.java                   ⭐⭐⭐⭐⭐
│   修改: 23次
│   职责: 流程执行上下文，管理变量、节点结果、日志
│
├── express/ExpressionExecutor.java       ⭐⭐⭐⭐⭐
│   修改: 16次
│   职责: 表达式执行器，支持MVEL和JEXL引擎
│
├── express/condition/                    # 条件表达式相关
│   ├── OrExpression.java
│   ├── AndExpression.java
│   └── ExpressionItem.java
│
└── VariableContext.java                  ⭐⭐⭐⭐
    职责: 变量上下文，管理流程变量
```

### 节点组件（数据节点）
```
路径: onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/data/

├── DataAddNodeComponent.java             ⭐⭐⭐⭐⭐
│   修改: 22次
│   职责: 数据新增节点
│   特点: 支持批处理和单处理，租户隔离
│
├── DataUpdateNodeComponent.java          ⭐⭐⭐⭐
│   修改: 20次
│   职责: 数据更新节点
│
├── DataQueryNodeComponent.java           ⭐⭐⭐⭐
│   修改: 19次
│   职责: 数据查询节点
│
└── DataDeleteNodeComponent.java          ⭐⭐⭐
    修改: 16次
    职责: 数据删除节点
```

### 节点组件（逻辑节点）
```
路径: onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/logic/

├── IfCaseNodeComponent.java              ⭐⭐⭐⭐
│   修改: 15次
│   职责: 条件判断节点（IF-ELSE）
│
├── SwitchCaseNodeComponent.java          ⭐⭐⭐
│   职责: Switch分支节点
│
└── LoopNodeComponent.java                ⭐⭐⭐
    职责: 循环节点
```

### 节点组件（交互节点）
```
路径: onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/interact/

├── ModalNodeComponent.java               ⭐⭐⭐⭐
│   修改: 14次
│   职责: 弹窗节点
│
├── NavigateNodeComponent.java            ⭐⭐⭐
│   职责: 页面跳转节点
│
└── RefreshNodeComponent.java             ⭐⭐⭐
    职责: 刷新页面节点
```

### 节点组件（外部节点）
```
路径: onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/external/

├── ScriptNodeComponent.java              ⭐⭐⭐
│   职责: 脚本执行节点
│
└── DataCalcNodeComponent.java            ⭐⭐⭐
    职责: 数据计算节点
```

### 节点基类
```
路径: onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/

└── SkippableNodeComponent.java           ⭐⭐⭐⭐⭐
    职责: 可跳过节点组件基类
    特点: 所有节点组件的父类
```

### 节点数据类
```
路径: onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/nodedata/

├── data/
│   ├── DataAddNodeData.java              ⭐⭐⭐⭐
│   ├── DataUpdateNodeData.java           ⭐⭐⭐⭐
│   ├── DataQueryNodeData.java            ⭐⭐⭐
│   └── DataDeleteNodeData.java           ⭐⭐⭐
│
└── logic/
    └── IfCaseNodeData.java               ⭐⭐⭐
```

### 流程日志
```
路径: onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/

└── ExecuteLog.java                       ⭐⭐⭐⭐⭐
    代码: 31行
    职责: 流程执行日志
    特点: 极简设计，使用Guava Stopwatch和CopyOnWriteArrayList
```

---

## 🔧 ORM框架（数据访问层）

### 基础仓储
```
路径: onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/

├── BaseBizRepository.java                ⭐⭐⭐⭐⭐
│   代码: 352行
│   修改: 59次
│   职责: 业务基础仓储，所有业务仓储的父类
│   特点:
│     - 自动注入租户条件（AOP思想）
│     - 版本管理（编辑态、运行态、历史版）
│     - UpdateChain链式更新
│
└── BaseAppRepository.java                ⭐⭐⭐⭐
    修改: 17次
    职责: 应用基础仓储
    特点: 类似BaseBizRepository，但用于应用层
```

### 查询工具
```
路径: onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/

└── QueryWrapperUtils.java                ⭐⭐⭐⭐⭐
    代码: 130行
    修改: 65次
    职责: 查询包装工具类
    特点:
      - 自动创建applicationId和versionTag列
      - 判断查询是否适合自动注入
      - 反射获取表名
```

### 工作流仓储
```
路径: onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/

└── WarmFlowBaseBizRepository.java        ⭐⭐⭐⭐
    修改: 43次
    职责: 工作流业务仓储
    特点: 集成WarmFlow工作流引擎
```

---

## 🏢 App模块（应用管理）

### 菜单管理
```
路径: onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/database/

└── AppMenuRepository.java                ⭐⭐⭐⭐⭐
    修改: 20次
    职责: 菜单仓储
    特点:
      - 菜单树形结构构建
      - 使用LinkedList提升性能
      - 支持排序查询
```

### 权限管理
```
路径: onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/

├── AppAuthPermissionServiceImpl.java     ⭐⭐⭐⭐⭐
│   修改: 26次
│   职责: 权限服务实现
│   特点: RBAC权限模型
│
└── AppAuthRoleServiceImpl.java           ⭐⭐⭐⭐
    修改: 20次
    职责: 角色服务实现
```

### 应用管理
```
路径: onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/service/impl/

├── AppApplicationServiceImpl.java        ⭐⭐⭐
│   修改: 17次
│   职责: 应用服务实现
│
└── AppVersionServiceImpl.java            ⭐⭐⭐
    修改: 17次
    职责: 应用版本服务实现
```

### 权限安全API
```
路径: onebase-module-app/onebase-module-app-runtime/src/main/java/com/cmsr/onebase/module/app/runtime/api/impl/

└── AppAuthSecurityApiImpl.java           ⭐⭐⭐
    职责: 权限安全API实现
    特点: 应用权限判断逻辑修复
```

---

## 🧪 测试代码

### Flow模块测试
```
路径: onebase-module-flow/onebase-module-flow-core/src/test/java/com/cmsr/onebase/module/flow/

└── FlowProcessTest.java                  ⭐⭐⭐⭐⭐
    修改: 31次
    职责: 流程执行测试
```

---

## 🗄️ 数据库实体

### Flow模块实体
```
路径: onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/database/

├── FlowProcessDO.java                    流程定义实体
├── FlowProcessExecDO.java                流程执行实体
└── FlowExecutionLogDO.java               流程执行日志实体
```

### App模块实体
```
路径: onebase-module-app/onebase-module-app-core/src/main/java/com/cmsr/onebase/module/app/core/dal/database/

├── AppApplicationDO.java                 应用实体
├── AppMenuDO.java                        菜单实体
├── AppAuthRoleDO.java                    角色实体
└── AppAuthPermissionDO.java              权限实体
```

---

## 📊 数据库表映射

### Flow模块表
```sql
-- 流程定义表
flow_process

-- 流程执行记录表
flow_process_exec

-- 流程执行日志表
flow_execution_log

-- 节点配置表（JSON）
flow_process.nodes (JSON字段)
```

### App模块表
```sql
-- 应用表
app_application

-- 应用版本表
app_application_version

-- 菜单表
app_menu

-- 角色表
app_auth_role

-- 权限表
app_auth_permission

-- 用户角色关联表
app_auth_user_role

-- 角色菜单关联表
app_auth_role_menu
```

---

## 🔍 按修改次数排序（Top 20）

| 排名 | 文件名 | 修改次数 | 模块 | 职责 |
|------|--------|----------|------|------|
| 1 | QueryWrapperUtils.java | 65次 | ORM | 查询包装工具 |
| 2 | BaseBizRepository.java | 59次 | ORM | 业务基础仓储 |
| 3 | WarmFlowBaseBizRepository.java | 43次 | ORM | 工作流仓储 |
| 4 | FlowProcessExecutor.java | 44次 | Flow | 流程执行器 |
| 5 | FlowProcessExecServiceImpl.java | 36次 | Flow | 流程执行服务 |
| 6 | FlowProcessTest.java | 31次 | Flow | 流程测试 |
| 7 | FlowProcessExecApiImpl.java | 26次 | Flow | 流程执行API |
| 8 | ExecuteContext.java | 23次 | Flow | 执行上下文 |
| 9 | AppAuthPermissionServiceImpl.java | 26次 | App | 权限服务 |
| 10 | DataAddNodeComponent.java | 22次 | Flow | 数据新增节点 |
| 11 | DataUpdateNodeComponent.java | 20次 | Flow | 数据更新节点 |
| 12 | AppMenuRepository.java | 20次 | App | 菜单仓储 |
| 13 | AppAuthRoleServiceImpl.java | 20次 | App | 角色服务 |
| 14 | BaseAppRepository.java | 17次 | ORM | 应用基础仓储 |
| 15 | DataQueryNodeComponent.java | 19次 | Flow | 数据查询节点 |
| 16 | AppApplicationServiceImpl.java | 17次 | App | 应用服务 |
| 17 | AppVersionServiceImpl.java | 17次 | App | 版本服务 |
| 18 | ExpressionExecutor.java | 16次 | Flow | 表达式执行器 |
| 19 | DataDeleteNodeComponent.java | 16次 | Flow | 数据删除节点 |
| 20 | IfCaseNodeComponent.java | 15次 | Flow | 条件判断节点 |

---

## 🎯 按学习优先级排序

### 第一优先级：必读 ⭐⭐⭐⭐⭐
1. FlowProcessExecutor.java - 流程执行核心
2. ExecuteContext.java - 执行上下文
3. BaseBizRepository.java - ORM基础
4. QueryWrapperUtils.java - 查询工具
5. ExpressionExecutor.java - 表达式引擎

### 第二优先级：重要 ⭐⭐⭐⭐
6. DataAddNodeComponent.java - 数据节点示例
7. AppMenuRepository.java - 菜单管理
8. AppAuthPermissionServiceImpl.java - 权限管理
9. IfCaseNodeComponent.java - 条件节点
10. ExecuteLog.java - 日志设计

### 第三优先级：参考 ⭐⭐⭐
11. DataUpdateNodeComponent.java - 更新节点
12. DataQueryNodeComponent.java - 查询节点
13. BaseAppRepository.java - 应用仓储
14. FlowProcessTest.java - 测试示例

---

## 🔧 按功能分类

### 流程执行
- FlowProcessExecutor.java
- FlowProcessExecServiceImpl.java
- FlowProcessExecApiImpl.java
- ExecuteContext.java
- ExecuteLog.java

### 表达式处理
- ExpressionExecutor.java
- OrExpression.java
- AndExpression.java
- ExpressionItem.java

### 数据节点
- DataAddNodeComponent.java
- DataUpdateNodeComponent.java
- DataQueryNodeComponent.java
- DataDeleteNodeComponent.java

### 逻辑节点
- IfCaseNodeComponent.java
- SwitchCaseNodeComponent.java
- LoopNodeComponent.java

### 交互节点
- ModalNodeComponent.java
- NavigateNodeComponent.java
- RefreshNodeComponent.java

### 数据访问
- BaseBizRepository.java
- BaseAppRepository.java
- QueryWrapperUtils.java
- AppMenuRepository.java

### 权限管理
- AppAuthPermissionServiceImpl.java
- AppAuthRoleServiceImpl.java
- AppAuthSecurityApiImpl.java

---

**更新日期:** 2025-12-28
**维护者:** Kanten (Claude Code)

---

> 💡 **使用技巧**:
> 1. 使用IDEA的"Navigate to File"快捷键（Cmd+Shift+O / Ctrl+Shift+N）快速跳转
> 2. 使用"Find Usages"（Alt+F7）查找方法调用
> 3. 使用Git Blame查看代码作者和提交记录
> 4. 按照学习优先级逐个阅读，不要一次看太多
