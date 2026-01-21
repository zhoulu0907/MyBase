# Flow模块启动失败修复指南

## 📋 问题描述

**错误信息**：
```
ERROR c.c.o.m.f.c.handler.FlowCacheHandler - 初始化flowProcessDO异常
java.lang.NullPointerException: Cannot invoke "JsonGraph.getNodes()" because "jsonGraph" is null
```

**问题流程**：
```
FlowProcessDO(id=162636513213218816, processName=测试一下数据计算, processDefinition=null)
```

**根本原因**：
- 数据库中存在 `process_definition` 字段为 `NULL` 的流程记录
- 该流程状态为启用 (`enable_status = 1`)
- 启动时加载流程失败导致 NPE

---

## ✅ 解决方案

### 方案一：数据修复（立即生效 - 推荐）

#### 步骤1：连接数据库

```bash
# 使用你的数据库客户端工具连接到数据库
# 例如：DBeaver, Navicat, MySQL Workbench等
```

#### 步骤2：执行修复SQL

```sql
-- 禁用问题流程（推荐）
UPDATE flow_process
SET enable_status = 0
WHERE id = 162636513213218816
  AND process_definition IS NULL;

-- 或者直接删除测试数据（如果确认是测试数据）
-- DELETE FROM flow_process WHERE id = 162636513213218816;
```

#### 步骤3：验证修复

```sql
-- 查看所有 processDefinition 为空的流程
SELECT id, process_name, process_uuid, enable_status, create_time, update_time
FROM flow_process
WHERE process_definition IS NULL;

-- 预防：查找所有启用但定义为空的流程
SELECT id, process_name, application_id, tenant_id, enable_status
FROM flow_process
WHERE enable_status = 1
  AND (process_definition IS NULL OR process_definition = '');
```

#### 步骤4：重启应用

```bash
# 使用你的启动方式重启
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 或者在IDE中重新启动应用
```

---

### 方案二：代码防御性修复（已完成）

#### 已修复的文件：

1. **FlowGraphBuilder.java**
   - ✅ 增加了空字符串检查
   - ✅ 增加了JSON解析结果检查
   - ✅ 添加了日志输出

2. **FieldTypeProviderImpl.java**
   - ✅ 增加了JsonGraph空值检查
   - ✅ 增加了nodes空值检查
   - ✅ 添加了日志输出

#### 修复效果：

- **修复前**：`process_definition = null` → NPE → 启动失败
- **修复后**：`process_definition = null` → 日志警告 → 跳过该流程 → 启动成功

---

## 🔍 验证修复

### 1. 编译检查

```bash
# 清理并编译
mvn clean compile

# 确保没有编译错误
```

### 2. 启动应用

```bash
# 启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 观察日志
tail -f logs/onebase.log | grep -E "flow|Flow"
```

### 3. 预期日志

如果数据已修复，应该看到：
```
✅ 加载flowProcess流程成功：[其他流程ID]
```

如果数据未修复但代码已修复，应该看到：
```
⚠️ 流程定义为空，无法构建JsonGraph
⚠️ 初始化flowProcessDO异常：FlowProcessDO(id=162636513213218816...)
✅ 加载flowProcess流程成功：[其他正常流程ID]
```

---

## 🛡️ 预防措施

### 1. 数据库约束（推荐）

```sql
-- 方案A：添加NOT NULL约束（强制）
-- 注意：执行前确保所有现有数据都有process_definition
ALTER TABLE flow_process
MODIFY COLUMN process_definition TEXT NOT NULL;

-- 方案B：添加CHECK约束（温和）
-- 确保启用的流程必须有定义
ALTER TABLE flow_process
ADD CONSTRAINT chk_process_definition_not_empty
CHECK (enable_status = 0 OR process_definition IS NOT NULL);
```

### 2. 业务层校验

在流程创建/更新时增加校验：

```java
// FlowProcessService.java
public void enableProcess(Long processId) {
    FlowProcessDO process = flowProcessRepository.getById(processId);

    // 校验流程定义不能为空
    if (StringUtils.isBlank(process.getProcessDefinition())) {
        throw new ServiceException("流程定义为空，无法启用");
    }

    process.setEnableStatus(FlowEnableStatusEnum.ENABLE.getStatus());
    flowProcessRepository.update(process);
}
```

### 3. 定期数据清理

```sql
-- 定期清理未完成的流程（根据实际情况调整时间）
DELETE FROM flow_process
WHERE process_definition IS NULL
  AND enable_status = 0
  AND create_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
```

---

## 📊 问题原因分析

此问题产生的可能原因：

1. **测试数据未清理** ✅
   - 流程名称 "测试一下数据计算" 明显是测试数据
   - 创建后未完成就退出

2. **前端流程设计器问题** ⚠️
   - 用户创建流程但未设计就关闭
   - 应该禁止保存空定义的流程

3. **数据迁移问题** ⚠️
   - 从旧版本迁移时数据不完整
   - 需要检查迁移脚本

4. **业务逻辑缺陷** ⚠️
   - 允许启用空定义的流程
   - 应该在启用前校验

---

## 🔧 后续优化建议

### 1. 前端优化

```javascript
// 流程设计器保存时校验
function saveProcess(processData) {
  if (!processData.processDefinition ||
      processData.processDefinition === '{}' ||
      !processData.processDefinition.nodes ||
      processData.processDefinition.nodes.length === 0) {
    showError('流程定义不能为空');
    return;
  }

  // 执行保存
  saveProcessApi(processData);
}
```

### 2. 后端校验增强

```java
@Service
public class FlowProcessService {

    public void saveProcess(FlowProcessSaveReqVO reqVO) {
        // 校验流程定义
        validateProcessDefinition(reqVO.getProcessDefinition());

        // 保存流程
        // ...
    }

    private void validateProcessDefinition(String definition) {
        if (StringUtils.isBlank(definition)) {
            throw new ServiceException("流程定义不能为空");
        }

        JsonGraph graph = JsonUtils.parseObject(definition, JsonGraph.class);
        if (graph == null || CollectionUtils.isEmpty(graph.getNodes())) {
            throw new ServiceException("流程定义无效：至少需要一个节点");
        }
    }
}
```

### 3. 单元测试补充

```java
@Test
public void testBuildWithNullDefinition() {
    // Given
    String nullDefinition = null;

    // When
    JsonGraph result = flowGraphBuilder.build(nullDefinition);

    // Then
    assertNull(result);
}

@Test
public void testBuildWithEmptyDefinition() {
    // Given
    String emptyDefinition = "";

    // When
    JsonGraph result = flowGraphBuilder.build(emptyDefinition);

    // Then
    assertNull(result);
}

@Test
public void testCompleteFieldTypeWithNullGraph() {
    // Given
    JsonGraph nullGraph = null;

    // When & Then - 不应抛出异常
    assertDoesNotThrow(() -> fieldTypeProvider.completeFieldType(nullGraph));
}
```

---

## 📞 联系支持

如果问题仍未解决，请联系开发团队：

- **Flow模块负责人**: huangjie@cmsr.chinamobile.com
- **问题追踪**: 创建Gitlab Issue并关联 `flow模块`

**提供信息**：
1. 完整错误堆栈
2. 数据库flow_process表快照
3. 应用启动日志
4. 应用配置信息

---

## ✅ 修复检查清单

- [ ] 执行SQL禁用/删除问题流程
- [ ] 验证数据库中无其他空定义的启用流程
- [ ] 代码修复已完成（FlowGraphBuilder + FieldTypeProviderImpl）
- [ ] 编译通过无错误
- [ ] 应用启动成功
- [ ] 查看启动日志确认Flow模块正常
- [ ] （可选）添加数据库约束
- [ ] （可选）补充单元测试

---

**最后更新时间**: 2025-12-02
**修复状态**: ✅ 已修复
**测试状态**: 待验证
