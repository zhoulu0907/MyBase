# 动作配置模板接口重构设计

## 需求概述

### 背景
现有的动作配置查询接口 `getActionSchema` 需要传入 `actionCode` 参数，且命名风格与 `getEnvConfigTemplate` 不一致。为统一接口命名规范，需要重构为 `getActionConfigTemplate`。

### 目标
1. 统一接口命名规范（使用 `-template` 后缀）
2. 简化接口参数（移除 actionCode）
3. 返回完整的动作配置模板

---

## 接口设计

### 新接口定义

**接口路径**: `GET /flow/connector/{id}/action-config-template`

**接口名称**: `getActionConfigTemplate`

**请求参数**:
| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| id | Long | path | 是 | 连接器实例ID |

**响应数据**: `CommonResult<ActionConfigTemplateVO>`

```java
@Operation(summary = "获取动作配置模板",
          description = "获取连接器类型对应的动作配置 Formily Schema 模板，用于创建动作信息")
@Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
@GetMapping("/{id}/action-config-template")
public CommonResult<ActionConfigTemplateVO> getActionConfigTemplate(@PathVariable Long id) {
    ActionConfigTemplateVO template = connectorService.getActionConfigTemplate(id);
    return CommonResult.success(template);
}
```

### 与旧接口对比

| 方面 | 旧接口 | 新接口 |
|------|-----------------------------|-----------------------------------|
| 路径 | `/{id}/action-schema/{actionCode}` | `/{id}/action-config-template` |
| 参数 | connectorId + actionCode | 仅 connectorId |
| 返回类型 | `JsonNode` | `ActionConfigTemplateVO` |
| 用途 | 查询特定动作配置 | 查询动作配置模板 |
| 命名风格 | -schema | -config-template |

---

## 数据结构设计

### ActionConfigTemplateVO

参考 `EnvConfigTemplateVO` 结构：

```java
package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 动作配置模板 VO
 *
 * @author kanten
 * @since 2026-02-04
 */
@Data
@Schema(description = "动作配置模板（Formily Schema）")
public class ActionConfigTemplateVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "动作配置的 Formily Schema（包含完整的表单结构）")
    private JsonNode schema;
}
```

---

## 业务逻辑设计

### 实现流程

```
1. 通过 flow_connector.id 查询连接器实例
   └─> 获取 typeCode (= nodeCode)

2. 通过 nodeCode 查询 flow_node_config
   └─> 获取 action_config (JsonNode)

3. 校验 action_config 非空

4. 构造 ActionConfigTemplateVO 返回
```

### Service 层实现

```java
@Override
public ActionConfigTemplateVO getActionConfigTemplate(Long connectorId) {
    // 1. 查询连接器实例
    FlowConnectorDO connector = connectorMapper.selectById(connectorId);
    if (connector == null) {
        throw exception(CONNECTOR_NOT_EXISTS);
    }

    // 2. 查询节点配置
    String nodeCode = connector.getTypeCode();
    FlowNodeConfigDO nodeConfig = nodeConfigService.getByNodeCode(nodeCode);

    // 3. 校验动作配置模板
    if (nodeConfig == null || nodeConfig.getActionConfig() == null) {
        throw exception(ACTION_CONFIG_TEMPLATE_NOT_EXISTS);
    }

    // 4. 构造返回结果
    ActionConfigTemplateVO vo = new ActionConfigTemplateVO();
    vo.setSchema(nodeConfig.getActionConfig());

    return vo;
}
```

---

## 错误处理

### 错误码定义

```java
ErrorCode ACTION_CONFIG_TEMPLATE_NOT_EXISTS =
    new ErrorCode(0_020_500_006, "动作配置模板不存在");
```

### 异常场景

| 场景 | 错误码 | 处理方式 |
|------|--------|---------|
| connectorId 为 null | CONNECTOR_ID_NOT_NULL | 参数校验 |
| 连接器实例不存在 | CONNECTOR_NOT_EXISTS | 查询校验 |
| 节点配置不存在 | ACTION_CONFIG_TEMPLATE_NOT_EXISTS | 配置校验 |
| action_config 为空 | ACTION_CONFIG_TEMPLATE_NOT_EXISTS | 配置校验 |

---

## 接口变更

### 删除旧接口

**删除接口 1**: `getActionSchema`
```java
// 删除 FlowConnectorController 第 190-197 行
@GetMapping("/{connectorId}/action-schema/{actionCode}")
public CommonResult<JsonNode> getActionSchema(...)
```

**删除接口 2**: `getActionValue`
```java
// 删除 FlowConnectorController 第 163-170 行
@GetMapping("/{id}/action-value")
public CommonResult<JsonNode> getActionValue(...)
```

### Service 层删除

```java
// 删除 FlowConnectorService
JsonNode getActionSchema(Long connectorId, String actionCode);
JsonNode getActionValueById(Long connectorId, String actionName);

// 删除 FlowConnectorServiceImpl 实现方法
```

---

## 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `ActionConfigTemplateVO.java` | 新增 | 动作配置模板 VO |
| `FlowConnectorController.java` | 修改 | 添加新接口，删除 2 个旧接口 |
| `FlowConnectorService.java` | 修改 | 添加新方法，删除 2 个旧方法 |
| `FlowConnectorServiceImpl.java` | 修改 | 实现新方法，删除旧实现 |
| 错误码常量类 | 修改 | 添加 ACTION_CONFIG_TEMPLATE_NOT_EXISTS |

---

## 测试用例

### 单元测试覆盖

| 测试场景 | 预期结果 | 覆盖范围 |
|---------|---------|---------|
| 正常获取模板 | 返回完整 schema | ✅ 成功路径 |
| 连接器不存在 | 抛出异常 | ✅ 异常路径 |
| 模板不存在 | 抛出异常 | ✅ 异常路径 |
| connectorId 为 null | 参数校验失败 | ✅ 边界条件 |

### API 集成测试

```java
// 正常获取
GET /flow/connector/1/action-config-template
→ 200 OK, {"data": {"schema": {...}}}

// 连接器不存在
GET /flow/connector/999/action-config-template
→ 200 OK, {"code": 0_020_500_001, "message": "连接器不存在"}

// 模板不存在
GET /flow/connector/2/action-config-template
→ 200 OK, {"code": 0_020_500_006, "message": "动作配置模板不存在"}
```

---

## 设计原则

1. **命名一致性**: 与 `getEnvConfigTemplate` 保持一致的命名风格
2. **接口简化**: 移除不必要的 actionCode 参数
3. **数据完整性**: 返回完整的动作配置模板，前端自行解析
4. **向后兼容**: 通过删除旧接口避免混淆

---

## 设计完成时间

2026-02-04