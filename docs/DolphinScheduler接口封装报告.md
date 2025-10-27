# DolphinScheduler Client SDK 接口封装指南

> 生成时间：2025-10-24  
> 项目模块：onebase-spring-boot-starter-dolphinscheduler-client  
> 基于版本：DolphinScheduler 3.3.1

---

## 📊 总体概览

`onebase-spring-boot-starter-dolphinscheduler-client` 是一个用于与 Apache DolphinScheduler 进行交互的 Java SDK 客户端。该模块提供了一套完整的操作接口，允许开发者通过编程方式管理 DolphinScheduler 中的项目、工作流、任务、调度计划以及实例等资源。

### 核心组件

| 组件 | 类名 | 功能描述 |
|------|------|----------|
| 工作流定义操作 | [WorkflowOperator](#1-工作流定义操作) | 管理工作流定义（创建、更新、删除、发布等） |
| 调度计划操作 | [ScheduleOperator](#2-调度计划操作) | 管理调度计划（创建、更新、上线、下线、删除等） |
| 任务实例操作 | [TaskInstanceOperator](#3-任务实例操作) | 查询任务实例及日志 |
| 工作流实例操作 | [WorkflowInstanceOperator](#4-工作流实例操作) | 管理工作流实例（启动、停止、暂停、删除等） |

---

## 1. 工作流定义操作

**操作类：** `com.cmsr.onebase.dolphins.workflow.WorkflowOperator`  
**功能描述：** 提供工作流定义的完整生命周期管理，包括创建、查询、更新、删除、发布等操作。

### 接口列表

| 方法名 | 功能说明 |
|--------|----------|
| `page(Long projectCode, Integer page, Integer size, String searchVal)` | 分页查询工作流定义列表 |
| `create(Long projectCode, WorkflowDefineParam workflowDefineParam)` | 创建工作流定义 |
| `update(Long projectCode, WorkflowDefineParam workflowDefineParam, Long WorkflowCode)` | 更新工作流定义 |
| `delete(Long projectCode, Long WorkflowCode)` | 删除工作流定义 |
| `release(Long projectCode, Long code, WorkflowReleaseParam workflowReleaseParam)` | 发布工作流定义 |
| `online(Long projectCode, Long code)` | 上线工作流定义 |
| `offline(Long projectCode, Long code)` | 下线工作流定义 |
| `generateTaskCode(Long projectCode, int codeNumber)` | 生成任务编码 |

---

## 2. 调度计划操作

**操作类：** `com.cmsr.onebase.dolphins.schedule.ScheduleOperator`  
**功能描述：** 提供调度计划的管理功能，包括创建、查询、更新、上线、下线、删除等操作。

### 接口列表

| 方法名 | 功能说明 |
|--------|----------|
| `create(Long projectCode, ScheduleDefineParam scheduleDefineParam)` | 创建调度计划 |
| `getByWorkflowCode(Long projectCode, Long WorkflowDefinitionCode)` | 根据工作流编码查询调度计划 |
| `update(Long projectCode, Long scheduleId, ScheduleDefineParam scheduleDefineParam)` | 更新调度计划 |
| `online(Long projectCode, Long scheduleId)` | 上线调度计划 |
| `offline(Long projectCode, Long scheduleId)` | 下线调度计划 |
| `delete(Long projectCode, Long scheduleId)` | 删除调度计划 |

## 3. 任务实例操作

**操作类：** `com.cmsr.onebase.dolphins.taskinstance.TaskInstanceOperator`  
**功能描述：** 提供任务实例的查询及相关操作。

### 接口列表

| 方法名 | 功能说明 |
|--------|----------|
| `page(Long projectCode, Integer page, Integer size, Long WorkflowInstanceId)` | 分页查询任务实例列表 |
| `queryLog(Long projectCode, Integer skipLineNum, Integer limit, Long taskInstanceId)` | 查询任务实例日志 |

---

## 4. 工作流实例操作

**操作类：** `com.cmsr.onebase.dolphins.workflowinstance.WorkflowInstanceOperator`  
**功能描述：** 提供工作流实例的管理功能，包括启动、查询、控制（停止、暂停等）、删除等操作。

### 接口列表

| 方法名 | 功能说明 |
|--------|----------|
| `start(Long projectCode, WorkflowInstanceCreateParam workflowInstanceCreateParam)` | 启动工作流实例 |
| `page(Integer page, Integer size, Long projectCode, Long workflowCode)` | 分页查询工作流实例列表 |
| `reRun(Long projectCode, Long WorkflowInstanceId)` | 重新运行工作流实例 |
| `stop(Long projectCode, Long WorkflowInstanceId)` | 停止工作流实例 |
| `pause(Long projectCode, Long WorkflowInstanceId)` | 暂停工作流实例 |
| `execute(Long projectCode, Long WorkflowInstanceId, String executeType)` | 执行指定类型的操作 |
| `delete(Long projectCode, Long WorkflowInstanceId)` | 删除工作流实例 |

---

## 🎯 使用指南

### 项目集成方式

```xml
<!-- 1. 在 pom.xml 中引入依赖 -->
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-spring-boot-starter-dolphinscheduler-client</artifactId>
</dependency>
```

```yaml
# 2. 在配置文件中配置 DolphinScheduler 连接信息（application.yml）
onebase:
  dolphinscheduler:
    baseUrl: http://your-dolphinscheduler-host:12345/dolphinscheduler
    token: your-access-token
    projectCodeFlow: 155148878289211
    projectCodeETL: 155148878289216
    tenantCode: default
```

```java
// 3. 注入并使用 DolphinClient、dolphinProperties
import com.cmsr.onebase.dolphins.core.DolphinClient;
import com.cmsr.onebase.framework.dolphins.config.DolphinSchedulerClientProperties;

@Resource
private DolphinClient dolphinClient;
@Resource
private DolphinSchedulerClientProperties dolphinProperties;

// 使用示例
List<WorkflowDefineResp> workflows = dolphinClient.opsForWorkflow().page(dolphinProperties.getProjectCodeETL(), 1, 10, "search-keyword");
```
---

## 📝 使用注意事项

1. 所有操作都需要提供有效的 `projectCode`，可以直接使用dolphinProperties.getProjectCodeETL()或者dolphinProperties.getProjectCodeFlow()获取。
2. 可以直接使用dolphinProperties.getTenantCode()获取租户编码。

---