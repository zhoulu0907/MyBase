# DolphinScheduler 接口封装实现报告

> 生成时间：2025-10-17  
> 项目模块：onebase-spring-boot-starter-dolphinscheduler  
> 基于版本：DolphinScheduler 3.3.1 API v2

---

## 📊 总体概览

| 统计项 | 数量 |
|--------|------|
| **API 分类数** | 6 个 |
| **接口总数** | 32 个 |

---

## 📁 接口分类详情

### 1️⃣ 项目管理（ProjectApi）

**接口文件：** `ProjectApi.java`  
**接口数量：** 13 个  
**功能描述：** 提供项目的创建、查询、更新、删除及授权管理等功能

#### 接口列表

| 序号 | HTTP方法 | 接口路径 | 接口方法 | 功能说明 |
|------|---------|----------|----------|----------|
| 1 | POST | `/projects` | createProject | 创建项目 |
| 2 | GET | `/projects/{code}` | queryProjectByCode | 通过项目编码查询项目信息 |
| 3 | PUT | `/projects/{code}` | updateProject | 更新项目 |
| 4 | DELETE | `/projects/{code}` | deleteProject | 通过编码删除项目 |
| 5 | GET | `/projects` | queryProjectListPaging | 分页查询项目列表 |
| 6 | GET | `/projects/list` | queryAllProjectList | 查询所有项目 |
| 7 | GET | `/projects/list-dependent` | queryAllProjectListForDependent | 查询 Dependent 节点所有项目 |
| 8 | GET | `/projects/created-and-authed` | queryProjectCreatedAndAuthorizedByUser | 查询授权和用户创建的项目 |
| 9 | GET | `/projects/authed-project` | queryAuthorizedProject | 查询授权项目 |
| 10 | GET | `/projects/unauth-project` | queryUnauthorizedProject | 查询未授权的项目 |
| 11 | GET | `/projects/authed-user` | queryAuthorizedUser | 查询拥有项目授权的用户 |

---

### 2️⃣ 工作流定义管理（WorkflowApi）

**接口文件：** `WorkflowApi.java`  
**接口数量：** 5 个  
**功能描述：** 提供工作流定义的创建、查询、更新、删除等功能

#### 接口列表

| 序号 | HTTP方法 | 接口路径 | 接口方法 | 功能说明 |
|------|---------|----------|----------|----------|
| 1 | POST | `/workflows` | createWorkflow | 创建工作流定义 |
| 2 | GET | `/workflows/{code}` | queryWorkflowByCode | 通过编码查询工作流定义 |
| 3 | PUT | `/workflows/{code}` | updateWorkflow | 更新工作流定义 |
| 4 | DELETE | `/workflows/{code}` | deleteWorkflow | 删除工作流定义 |
| 5 | POST | `/workflows/query` | queryWorkflowListPaging | 分页查询工作流定义列表 |

---

### 3️⃣ 定时调度管理（ScheduleApi）

**接口文件：** `ScheduleApi.java`  
**接口数量：** 5 个  
**功能描述：** 提供定时调度的创建、查询、更新、删除等功能

#### 接口列表

| 序号 | HTTP方法 | 接口路径 | 接口方法 | 功能说明 |
|------|---------|----------|----------|----------|
| 1 | POST | `/schedules` | createSchedule | 创建定时调度 |
| 2 | GET | `/schedules/{id}` | queryScheduleById | 通过ID查询定时调度 |
| 3 | PUT | `/schedules/{id}` | updateSchedule | 更新定时调度 |
| 4 | DELETE | `/schedules/{id}` | deleteSchedule | 删除定时调度 |
| 5 | POST | `/schedules/filter` | queryScheduleListPaging | 分页查询定时调度列表 |

---

### 4️⃣ 任务定义管理（TaskApi）

**接口文件：** `TaskApi.java`  
**接口数量：** 2 个  
**功能描述：** 提供任务定义的查询与详情获取能力（不包含任务实例相关接口）

#### 接口列表

| 序号 | HTTP方法 | 接口路径 | 接口方法 | 功能说明 |
|------|---------|----------|----------|----------|
| 1 | POST | `/tasks/query` | filterTaskDefinition | 分页筛选任务定义 |
| 2 | GET | `/tasks/{code}` | getTaskDefinition | 获取任务定义详情 |

---

### 5️⃣ 任务实例管理（TaskInstanceApi）

**接口文件：** `TaskInstanceApi.java`  
**接口数量：** 5 个  
**功能描述：** 提供任务实例的查询、停止、强制成功等操作能力

#### 接口列表

| 序号 | HTTP方法 | 接口路径 | 接口方法 | 功能说明 |
|------|---------|----------|----------|----------|
| 1 | GET | `/projects/{projectCode}/task-instances` | queryTaskListPaging | 分页查询任务实例列表 |
| 2 | POST | `/projects/{projectCode}/task-instances/{taskInstanceId}` | queryTaskInstanceByCode | 查询单个任务实例 |
| 3 | POST | `/projects/{projectCode}/task-instances/{id}/stop` | stopTask | 停止任务实例 |
| 4 | POST | `/projects/{projectCode}/task-instances/{id}/savepoint` | taskSavePoint | 任务 SavePoint |
| 5 | POST | `/projects/{projectCode}/task-instances/{id}/force-success` | forceTaskSuccess | 强制任务成功 |

---

### 6️⃣ 工作流实例管理（WorkflowInstanceApi）

**接口文件：** `WorkflowInstanceApi.java`  
**接口数量：** 4 个  
**功能描述：** 提供工作流实例的查询、执行与删除能力

#### 接口列表

| 序号 | HTTP方法 | 接口路径 | 接口方法 | 功能说明 |
|------|---------|----------|----------|----------|
| 1 | POST | `/workflow-instances/{workflowInstanceId}/execute/{executeType}` | execute | 执行流程实例操作（暂停、停止、重跑、恢复等） |
| 2 | GET | `/workflow-instances` | queryWorkflowInstanceListPaging | 分页查询流程实例列表 |
| 3 | GET | `/workflow-instances/{workflowInstanceId}` | queryWorkflowInstanceById | 根据ID查询流程实例详情 |
| 4 | DELETE | `/workflow-instances/{workflowInstanceId}` | deleteWorkflowInstance | 删除流程实例 |

---

## 📈 功能覆盖度分析

### 已实现的核心功能模块

✅ **项目管理** - 完整支持项目的 CRUD 及授权管理  
✅ **工作流定义** - 支持工作流的完整生命周期管理  
✅ **定时调度** - 支持定时任务的创建和管理  
✅ **任务定义** - 支持任务定义的查询  
✅ **任务实例** - 支持任务实例的查询和控制  
✅ **工作流实例** - 支持实例的查询、执行与删除

### 接口设计特点

1. **统一采用 Retrofit2** 框架进行 HTTP 调用封装
2. **遵循 RESTful 规范**，使用标准 HTTP 方法（GET/POST/PUT/DELETE）
3. **完善的 DTO 体系**，请求和响应都有对应的数据传输对象
4. **清晰的注释文档**，每个接口都有详细的 JavaDoc 说明
5. **模块化设计**，按业务领域划分不同的 API 接口

---

## 🎯 使用建议

### 项目集成方式

```java
// 1. 在 pom.xml 中引入依赖
<dependency>
    <groupId>com.cmsr.onebase</groupId>
    <artifactId>onebase-spring-boot-starter-dolphinscheduler</artifactId>
</dependency>

// 2. 在配置文件中配置 DolphinScheduler 连接信息
// application.yml
onebase:
  dolphinscheduler:
    url: http://dolphinscheduler-api:12345/dolphinscheduler/v2/
    token: your-token-here

// 3. 直接注入使用
@Resource
private ProjectApi projectApi;

@Resource
private WorkflowApi workflowApi;

@Resource
private TaskApi taskApi;

@Resource
private TaskInstanceApi taskInstanceApi;

@Resource
private ScheduleApi scheduleApi;

@Resource
private WorkflowInstanceApi workflowInstanceApi;
```

### 典型使用场景

1. **创建调度任务**：ProjectApi → WorkflowApi → ScheduleApi
2. **查询任务执行状态**：TaskInstanceApi
3. **管理工作流**：WorkflowApi + TaskApi
4. **控制工作流实例**：WorkflowInstanceApi（暂停/停止/重跑/恢复/删除/查询）

---

## 📝 补充说明

- 所有接口均基于 DolphinScheduler 3.3.1 版本的 API v2
- 项目位置：`onebase-framework/onebase-spring-boot-starter-dolphinscheduler`
- 支持自动配置和依赖注入，开箱即用
- 包含完整的异常处理和认证拦截器

---

**报告结束**
