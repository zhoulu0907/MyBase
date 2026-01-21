# Flow 连接器动作内容查询接口设计

**日期**: 2026-01-21
**功能**: 查询指定连接器特定动作的配置内容
**接口编号**: 4

## 1. 需求概述

根据连接器 UUID 和动作名称，查询该动作对应的配置内容（formilyjs schema 格式）。

### 1.1 基本信息

| 项目 | 说明 |
|------|------|
| 功能描述 | 根据动作名称查询动作内容 |
| 请求方式 | GET |
| 响应类型 | JsonNode |

### 1.2 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| connectorUuid | String | 是 | 连接器实例 UUID |
| actionName | String | 是 | 动作名称（对应 properties 中的 key） |

### 1.3 业务规则

1. 从 `flow_connector.config` 中获取 JSON（formilyjs schema 格式）
2. 通过 `actionName` 匹配 `properties` 下的 Key，返回对应的 value
3. 保持 JSON 原始顺序

### 1.4 数据结构示例

```json
{
    "type": "Http",
    "title": "HTTP类型CRM接口服务",
    "properties": {
        "getCustomerList": { /* 返回此内容 */ },
        "getCustomerDetail": { ... },
        "getCustomerOrders": { ... }
    }
}
```

## 2. API 接口设计

### 2.1 接口定义

```
GET /flow/connector/action-value
```

### 2.2 响应示例

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {
    "type": "object",
    "title": "获取客户列表",
    "description": "查询CRM系统中的客户列表数据",
    "x-component": "FormDataGrid",
    "x-component-props": {...},
    "x-api-meta": {
      "method": "GET",
      "path": "/api/customers"
    }
  }
}
```

### 2.3 错误码

| 错误码 | 说明 |
|--------|------|
| CONNECTOR_NOT_EXISTS | 连接器不存在 |
| INVALID_CONNECTOR_CONFIG | 配置格式无效 |
| ACTION_NOT_EXISTS | 指定的 actionName 不存在 |

## 3. Service 层实现

### 3.1 接口方法

```java
/**
 * 根据连接器UUID和动作名称获取动作配置内容
 * @param connectorUuid 连接器UUID
 * @param actionName 动作名称
 * @return 动作配置内容（JsonNode格式）
 */
JsonNode getActionValueByConnectorUuid(String connectorUuid, String actionName);
```

### 3.2 实现逻辑

1. 根据 `connectorUuid` 查询连接器
2. 解析 `config` JSON 字符串
3. 获取 `properties` 对象
4. 通过 `actionName` 获取对应的 value
5. 返回 `JsonNode` 类型的配置内容

## 4. 测试用例

### 4.1 成功场景

- 连接器存在，动作存在 → 返回动作配置内容
- 验证返回的 JsonNode 包含正确的字段

### 4.2 失败场景

- 连接器不存在 → 抛出 CONNECTOR_NOT_EXISTS
- 配置为空或格式无效 → 抛出 INVALID_CONNECTOR_CONFIG
- 动作名称不存在 → 抛出 ACTION_NOT_EXISTS

## 5. 实现文件清单

| 文件路径 | 修改内容 |
|---------|---------|
| `FlowConnectorController.java` | 新增 `/action-value` 接口 |
| `FlowConnectorService.java` | 新增方法定义 |
| `FlowConnectorServiceImpl.java` | 实现业务逻辑 |
| `FlowErrorCodeConstants.java` | 新增错误码 |
| `FlowConnectorControllerTest.java` | 新增测试用例 |
| `FlowConnectorServiceImplTest.java` | 新增测试用例 |