# WarmFlowDataRepositoryV2 设计文档

## 概述

`WarmFlowDataRepositoryV2` 是一个使用组合模式设计的改进版数据访问层，它通过组合 `DataRepository` 来提供 WarmFlow 特定的功能，同时保持与现有框架的兼容性。

## 设计原则

### 1. 组合优于继承
- 使用组合模式复用 `DataRepository` 的功能
- 避免继承带来的复杂性和约束
- 提供更灵活的功能扩展

### 2. 功能委托
- 将标准的 CRUD 操作委托给 `DataRepository`
- 专注于 WarmFlow 特定的扩展功能
- 保持 API 的一致性

### 3. 类型安全
- 支持泛型，确保类型安全
- 兼容 `BaseDO` 及其子类
- 提供编译时类型检查

## 核心特性

### 1. 标准 CRUD 操作
```java
// 插入
T insert(T entity);

// 批量插入
List<T> insertBatch(List<T> entities);

// 更新
T update(T entity);

// 批量更新
List<T> updateBatch(List<T> entities);

// 根据ID删除
int deleteById(Long id);

// 批量删除
int deleteByIds(Collection<Long> ids);
```

### 2. 查询操作
```java
// 根据ID查询
T findById(Long id);
Optional<T> findByIdOptional(Long id);

// 批量查询
List<T> findAllByIds(Collection<Long> ids);

// 条件查询
List<T> findAllByConfig(ConfigStore configs);
T findOne(ConfigStore configs);
Optional<T> findOneOptional(ConfigStore configs);

// 分页查询
PageResult<T> findPage(ConfigStore configs, Integer page, Integer size);
```

### 3. WarmFlow 特定功能
```java
// 条件更新
long updateByConfig(DataRow dataRow, ConfigStore configs);

// 条件删除
long deleteByConfig(ConfigStore configs);

// 分组统计
List<DataRow> countByConfigWithGroup(ConfigStore configs, String groupBy);

// 带排序的分页查询
PageResult<T> findPageWithOrder(ConfigStore configs, Integer page, Integer size, String orderBy, boolean asc);

// 存在性检查
boolean existsByConfig(ConfigStore configs);

// 批量插入（指定批次大小）
int insertBatchWithSize(List<T> entities, int batchSize);
```

## 使用示例

### 1. 基本使用
```java
// 创建 Repository
WarmFlowDataRepositoryV2<FlowInstanceDO> repository =
    new WarmFlowDataRepositoryV2<>(FlowInstanceDO.class);

// 插入
FlowInstanceDO instance = new FlowInstanceDO();
instance.setDefinitionId(1L);
instance.setBusinessId("BIZ001");
FlowInstanceDO saved = repository.insert(instance);

// 查询
FlowInstanceDO found = repository.findById(saved.getId());

// 更新
found.setFlowStatus("COMPLETED");
repository.update(found);

// 删除
repository.deleteById(found.getId());
```

### 2. 条件查询
```java
// 创建查询条件
DefaultConfigStore config = repository.createDefaultConfig();
config.eq("definition_id", 1L);
config.eq("flow_status", "RUNNING");

// 查询
List<FlowInstanceDO> instances = repository.findAllByConfig(config);

// 分页查询
PageResult<FlowInstanceDO> pageResult = repository.findPage(config, 1, 10);
```

### 3. WarmFlow 特定功能
```java
// 条件更新
DataRow updateData = new DataRow();
updateData.put("flow_status", "SUSPENDED");
updateData.put("update_time", new Date());

DefaultConfigStore updateConfig = repository.createDefaultConfig();
updateConfig.eq("flow_status", "RUNNING");

long updatedCount = repository.updateByConfig(updateData, updateConfig);

// 条件删除
DefaultConfigStore deleteConfig = repository.createDefaultConfig();
deleteConfig.eq("flow_status", "CANCELLED");
deleteConfig.lt("create_time", new Date(System.currentTimeMillis() - 86400000));

long deletedCount = repository.deleteByConfig(deleteConfig);
```

## 优势

### 1. 复用性
- 完全复用 `DataRepository` 的成熟功能
- 减少代码重复
- 保持 API 一致性

### 2. 扩展性
- 可以轻松添加 WarmFlow 特定的功能
- 不影响现有的 `DataRepository` 功能
- 支持功能组合和定制

### 3. 维护性
- 清晰的职责分离
- 易于测试和调试
- 降低维护成本

### 4. 兼容性
- 与现有的 `DataRepository` 完全兼容
- 支持所有 `BaseDO` 子类
- 保持类型安全

## 与 WarmFlowDataRepository 的对比

| 特性 | WarmFlowDataRepository | WarmFlowDataRepositoryV2 |
|------|------------------------|---------------------------|
| 继承方式 | 继承 WarmFlowBaseDO | 组合 DataRepository |
| 类型约束 | 限制为 WarmFlowBaseDO | 支持所有 BaseDO 子类 |
| 功能复用 | 重复实现 | 完全复用 DataRepository |
| 扩展性 | 受继承限制 | 灵活扩展 |
| 维护性 | 需要维护重复代码 | 维护成本低 |

## 最佳实践

### 1. Repository 创建
```java
// 推荐：在构造函数中创建
public class FlowInstanceService {
    private final WarmFlowDataRepositoryV2<FlowInstanceDO> repository;

    public FlowInstanceService() {
        this.repository = new WarmFlowDataRepositoryV2<>(FlowInstanceDO.class);
    }
}
```

### 2. 查询条件构建
```java
// 推荐：使用 createDefaultConfig() 方法
DefaultConfigStore config = repository.createDefaultConfig();
config.eq("definition_id", definitionId);
config.in("flow_status", statusList);
config.order("create_time", false);
```

### 3. 异常处理
```java
try {
    FlowInstanceDO instance = repository.findById(id);
    // 处理结果
} catch (DatabaseAccessException e) {
    log.error("查询流程实例失败: {}", e.getMessage(), e);
    // 处理异常
}
```

### 4. 批量操作
```java
// 推荐：使用批量方法提高性能
List<FlowInstanceDO> instances = getInstances();
repository.insertBatch(instances);

// 对于大量数据，使用指定批次大小
repository.insertBatchWithSize(instances, 1000);
```

## 总结

`WarmFlowDataRepositoryV2` 通过组合模式提供了一个更加灵活、可维护和可扩展的数据访问层解决方案。它不仅复用了 `DataRepository` 的成熟功能，还提供了 WarmFlow 特定的扩展功能，是一个优秀的架构设计实践。
