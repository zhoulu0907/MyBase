# TaskInstance API 使用说明

本文档说明如何使用新生成的 TaskInstance 相关接口。

## 目录结构

```
onebase-spring-boot-starter-dolphinscheduler/
├── src/main/java/com/cmsr/onebase/framework/dolphins/
│   ├── api/
│   │   └── TaskInstanceApi.java                    # 任务实例 API 接口
│   ├── dto/taskinstance/
│   │   ├── enums/                                   # 枚举类
│   │   │   ├── AlertFlagEnum.java                   # 告警标识枚举
│   │   │   ├── TaskInstanceStateEnum.java           # 任务实例状态枚举
│   │   │   └── WorkflowInstanceStateEnum.java       # 流程实例状态枚举
│   │   ├── model/                                   # 模型类
│   │   │   ├── PageInfoTaskInstanceDTO.java         # 分页信息
│   │   │   ├── StateDescDTO.java                    # 状态描述
│   │   │   ├── TaskInstanceDTO.java                 # 任务实例
│   │   │   └── WorkflowInstanceDTO.java             # 流程实例
│   │   ├── request/                                 # 请求类
│   │   │   └── TaskInstanceQueryRequestDTO.java     # 查询请求
│   │   └── response/                                # 响应类
│   │       ├── TaskInstanceForceSuccessResponseDTO.java  # 强制成功响应
│   │       ├── TaskInstancePageResponseDTO.java          # 分页查询响应
│   │       ├── TaskInstanceQueryResponseDTO.java         # 单个查询响应
│   │       ├── TaskInstanceSavePointResponseDTO.java     # SavePoint响应
│   │       └── TaskInstanceStopResponseDTO.java          # 停止响应
│   └── config/
│       └── RetrofitConfig.java                      # 已添加 TaskInstanceApi Bean
└── src/test/java/com/cmsr/onebase/framework/dolphins/
    └── TaskInstanceApiTest.java                     # 集成测试

```

## API 接口说明

### 1. 分页查询任务实例列表

```java
@Resource
private TaskInstanceApi taskInstanceApi;

// 分页查询
Response<TaskInstancePageResponseDTO> response = taskInstanceApi.queryTaskListPaging(
    projectCode,          // 项目编码
    workflowInstanceId,   // 工作流实例ID（可选）
    workflowInstanceName, // 流程实例名称（可选）
    searchVal,            // 搜索值（可选）
    taskName,             // 任务实例名（可选）
    taskCode,             // 任务代码（可选）
    executorName,         // 执行人名称（可选）
    stateType,            // 运行状态（可选，如："SUCCESS", "FAILURE", "RUNNING_EXECUTION"）
    host,                 // 运行主机IP（可选）
    startDate,            // 开始时间（可选）
    endDate,              // 结束时间（可选）
    taskExecuteType,      // 任务执行类型（可选，如："BATCH", "STREAM"）
    pageNo,               // 页码号
    pageSize              // 页大小
).execute();
```

### 2. 查询单个任务实例

```java
Response<TaskInstanceQueryResponseDTO> response = taskInstanceApi.queryTaskInstanceByCode(
    projectCode,      // 项目编码
    taskInstanceId    // 任务实例ID
).execute();
```

### 3. 停止任务实例

```java
Response<TaskInstanceStopResponseDTO> response = taskInstanceApi.stopTask(
    projectCode,      // 项目编码
    taskInstanceId    // 任务实例ID
).execute();
```

### 4. 任务 SavePoint

```java
Response<TaskInstanceSavePointResponseDTO> response = taskInstanceApi.taskSavePoint(
    projectCode,      // 项目编码
    taskInstanceId    // 任务实例ID
).execute();
```

### 5. 强制任务成功

```java
Response<TaskInstanceForceSuccessResponseDTO> response = taskInstanceApi.forceTaskSuccess(
    projectCode,      // 项目编码
    taskInstanceId    // 任务实例ID
).execute();
```

## 枚举说明

### TaskInstanceStateEnum（任务实例状态）
- `SUBMITTED_SUCCESS` - 提交成功
- `RUNNING_EXECUTION` - 运行中
- `PAUSE` - 暂停
- `FAILURE` - 失败
- `SUCCESS` - 成功
- `NEED_FAULT_TOLERANCE` - 需要容错
- `KILL` - 终止
- `DELAY_EXECUTION` - 延迟执行
- `FORCED_SUCCESS` - 强制成功
- `DISPATCH` - 已分发

### WorkflowInstanceStateEnum（流程实例状态）
- `SUBMITTED_SUCCESS` - 提交成功
- `RUNNING_EXECUTION` - 运行中
- `READY_PAUSE` - 准备暂停
- `PAUSE` - 暂停
- `READY_STOP` - 准备停止
- `STOP` - 停止
- `FAILURE` - 失败
- `SUCCESS` - 成功
- `SERIAL_WAIT` - 串行等待
- `FAILOVER` - 容错

### AlertFlagEnum（告警标识）
- `NO` - 否
- `YES` - 是

## 测试说明

### 运行测试

```bash
cd onebase-framework/onebase-spring-boot-starter-dolphinscheduler
mvn test -Dtest=TaskInstanceApiTest
```

### 测试配置

测试配置位于 `src/test/resources/application.yml`：

```yaml
onebase:
  dolphinscheduler:
    baseUrl: http://10.0.104.33:12345/dolphinscheduler/v2/
    token: your-token-here
    connectTimeout: 5s
    readTimeout: 30s
    writeTimeout: 30s
    logLevel: BODY
test:
  user-id: 1
```

### 测试注意事项

1. **测试数据准备**：
   - 需要系统中有已运行或已完成的任务实例数据
   - 修改测试类中的 `testProjectCode` 为实际项目编码

2. **危险操作测试**：
   - 停止任务（`test_03_stopTask`）
   - SavePoint（`test_04_taskSavePoint`）
   - 强制成功（`test_05_forceTaskSuccess`）
   
   这些测试默认被 `@Disabled` 禁用，需要在安全环境下手动启用。

3. **测试顺序**：
   - 测试按照 `@Order` 注解顺序执行
   - 先查询列表获取任务实例ID
   - 再进行单个查询和操作测试

## 响应处理

所有响应都遵循统一的结构：

```java
{
    "code": 0,           // 状态码，0表示成功
    "msg": "success",    // 消息
    "data": {...},       // 数据
    "success": true,     // 是否成功
    "failed": false      // 是否失败
}
```

判断成功的标准（优先级从高到低）：
1. `code == 0`
2. `success == true`
3. `msg` 包含 "success"

## 常见问题

### Q1: 为什么查询不到任务实例？

**A**: 可能的原因：
- 项目编码不正确
- 系统中没有运行过的任务实例
- 筛选条件过于严格

**解决方案**：
- 使用正确的项目编码
- 尝试不带任何过滤条件的查询
- 检查 DolphinScheduler 系统中是否有任务实例数据

### Q2: 停止/强制成功操作失败？

**A**: 可能的原因：
- 任务实例已完成或已停止
- 任务实例状态不允许该操作
- 权限不足

**解决方案**：
- 检查任务实例当前状态
- 确认 token 有足够的权限
- 查看返回的错误消息

### Q3: 测试连接超时？

**A**: 可能的原因：
- DolphinScheduler 服务未启动
- 网络不可达
- 配置的地址或端口错误

**解决方案**：
- 检查 DolphinScheduler 服务状态
- 验证网络连接
- 确认配置文件中的 baseUrl 正确

## 相关文档

- [DolphinScheduler API 文档](https://dolphinscheduler.apache.org/zh-cn/docs/latest/user_doc/guide/api/introduction.html)
- [Retrofit 官方文档](https://square.github.io/retrofit/)
- [ProjectApi 使用说明](./README_PROJECT_API.md)
- [TaskApi 使用说明](./README_TASK_API.md)
