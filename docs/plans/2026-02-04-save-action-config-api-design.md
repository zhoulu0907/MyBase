# 保存连接器动作配置接口设计

## 需求概述

### 目标
新增"保存连接器实例动作配置"接口，用于将单个动作配置保存到连接器实例的 `action_config` 字段。

### 业务场景
- 用户为连接器实例配置动作（如 HTTP 请求动作）
- 每个动作包含：基本信息、请求头、请求体、查询参数、路径参数
- 动作配置保存到 `flow_connector.action_config` 字段
- 参考环境配置保存的实现模式

---

## 接口设计

### 接口定义

```java
@Operation(summary = "保存连接器动作配置",
          description = "保存新的动作配置到 connector.action_config，如果动作已存在则拒绝")
@Parameter(name = "id", description = "连接器实例ID", required = true, example = "1")
@PostMapping("/{id}/save-action")
public CommonResult<Boolean> saveActionConfig(
        @PathVariable("id") Long id,
        @RequestBody @Valid SaveActionConfigReqVO reqVO) {
    Boolean result = connectorService.saveActionConfig(id, reqVO);
    return CommonResult.success(result);
}
```

**与 saveEnvironmentConfig 的对比**：

| 方面 | saveEnvironmentConfig | saveActionConfig |
|------|----------------------|-------------------|
| 接口路径 | `/{id}/save-env` | `/{id}/save-action` |
| 存储字段 | `connector.config` | `connector.action_config` |
| 数据结构 | `properties.envName` | `properties.actionCode` |
| 提取路径 | `config.envConfig.basicInfo.envName` | `config.basic.actionName` |
| 重复错误 | `ENV_ALREADY_EXISTS` | `ACTION_ALREADY_EXISTS` |

---

## 数据结构设计

### action_config 字段格式

存储到 `flow_connector.action_config` 的标准 Formily Schema 格式：

```json
{
  "type": "object",
  "title": "HTTP类型连接器动作",
  "properties": {
    "hahaha1": {
      "basic": {
        "actionName": "hahaha1",
        "description": "desp"
      },
      "requestHeaders": [
        {
          "key": "header1",
          "fieldName": "请求头1",
          "fieldType": "string",
          "required": true,
          "defaultValue": "",
          "description": "",
          "id": "row-1770190157490-q3t3ndbsfg"
        }
      ],
      "requestBody": [...],
      "queryParams": [...],
      "pathParams": [...]
    },
    "hahaha2": {
      "basic": {
        "actionName": "hahaha2",
        "description": "desp"
      },
      ...
    }
  },
  "_metadata": {
    "version": 1,
    "lastModified": "2026-02-04T12:00:00"
  }
}
```

### SaveActionConfigReqVO 结构

```java
@Data
@Schema(description = "保存连接器动作配置请求")
public class SaveActionConfigReqVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "动作配置（包含 basic, requestHeaders, requestBody, queryParams, pathParams）",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "动作配置不能为空")
    @Valid
    private JsonNode actionConfig;
}
```

**请求示例**：
```json
{
  "actionConfig": {
    "basic": {
      "actionName": "hahaha1",
      "description": "desp"
    },
    "requestHeaders": [
      {
        "key": "header1",
        "fieldName": "请求头1",
        "fieldType": "string",
        "required": true,
        "defaultValue": "",
        "description": "",
        "id": "row-1770190157490-q3t3ndbsfg"
      }
    ],
    "requestBody": [...],
    "queryParams": [...],
    "pathParams": [...]
  }
}
```

---

## Service 层实现

### 核心方法

```java
@Override
@Transactional(rollbackFor = Exception.class)
public Boolean saveActionConfig(Long connectorId, SaveActionConfigReqVO reqVO) {
    log.info("saveActionConfig start, connectorId: {}", connectorId);

    // 1. 查询并验证连接器实例
    FlowConnectorDO connector = connectorRepository.getById(connectorId);
    if (connector == null) {
        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
    }

    // 2. 解析或创建 action_config 根配置
    ObjectNode rootActionConfig = parseOrCreateActionRootConfig(connector.getActionConfig());
    ObjectNode properties = rootActionConfig.withObject("properties");

    // 3. 从请求中提取动作编码（actionCode）
    String actionCode = extractActionCodeFromConfig(reqVO.getActionConfig());

    // 4. 检查动作是否已存在
    if (properties.has(actionCode)) {
        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_ALREADY_EXISTS, actionCode);
    }

    // 5. 添加新动作配置
    properties.set(actionCode, reqVO.getActionConfig());

    // 6. 更新元数据版本
    updateActionMetadataVersion(rootActionConfig);

    // 7. 保存到数据库
    connector.setActionConfig(toJsonString(rootActionConfig));
    connectorRepository.updateById(connector);

    log.info("saveActionConfig success, connectorId: {}, actionCode: {}", connectorId, actionCode);
    return Boolean.TRUE;
}
```

### 辅助方法

#### 1. 提取动作编码

```java
/**
 * 从动作配置中提取动作编码
 * <p>
 * 配置格式: {"basic": {"actionName": "hahaha1"}, ...}
 *
 * @param configNode 动作配置节点
 * @return 动作编码（actionName）
 */
private String extractActionCodeFromConfig(JsonNode configNode) {
    if (configNode == null || !configNode.isObject()) {
        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
    }

    JsonNode basicNode = configNode.get("basic");
    if (basicNode == null || !basicNode.isObject()) {
        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
    }

    JsonNode actionNameNode = basicNode.get("actionName");
    if (actionNameNode == null || actionNameNode.isNull()) {
        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
    }

    return actionNameNode.asText();
}
```

#### 2. 解析或创建根配置

```java
/**
 * 解析或创建 action_config 根配置对象
 *
 * @param actionConfigJson 现有动作配置JSON，可能为空
 * @return 根配置对象，包含 properties 和 _metadata 节点
 */
private ObjectNode parseOrCreateActionRootConfig(String actionConfigJson) {
    ObjectNode rootConfig;
    if (StringUtils.isBlank(actionConfigJson)) {
        // 首次创建：构建标准的 Formily Schema 格式
        rootConfig = objectMapper.createObjectNode();
        rootConfig.put("type", "object");
        rootConfig.put("title", "连接器动作配置");
        rootConfig.putObject("properties");
        rootConfig.putObject("_metadata");
    } else {
        try {
            rootConfig = (ObjectNode) objectMapper.readTree(actionConfigJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse action_config JSON", e);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }
    }
    return rootConfig;
}
```

#### 3. 更新元数据版本

```java
/**
 * 更新动作配置元数据版本号
 *
 * @param rootConfig 根配置对象
 */
private void updateActionMetadataVersion(ObjectNode rootConfig) {
    ObjectNode metadata = rootConfig.withObject("_metadata");

    long currentVersion = metadata.path("version").asLong(0);
    metadata.put("version", currentVersion + 1);
    metadata.put("lastModified", LocalDateTime.now().toString());
}
```

---

## 错误处理

### 新增错误码

在 `FlowErrorCodeConstants` 中添加：

```java
/**
 * 动作已存在
 */
ErrorCode ACTION_ALREADY_EXISTS = new ErrorCode(1123795, "动作已存在：actionCode={}");

/**
 * 动作配置格式无效
 */
ErrorCode INVALID_ACTION_CONFIG = new ErrorCode(1123796, "动作配置格式无效，缺少必填字段");
```

### 错误场景

| 场景 | 错误码 | 触发条件 |
|------|--------|---------|
| 连接器不存在 | `CONNECTOR_NOT_EXISTS` | `connectorId` 对应的记录不存在 |
| 动作已存在 | `ACTION_ALREADY_EXISTS` | `actionCode` 在 properties 中已存在 |
| 配置格式无效 | `INVALID_ACTION_CONFIG` | 缺少 basic 或 actionName 字段 |
| JSON 解析失败 | `INVALID_CONNECTOR_CONFIG` | 现有 action_config 不是有效 JSON |

---

## 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `SaveActionConfigReqVO.java` | 新增 | 请求 VO |
| `FlowConnectorController.java` | 修改 | 添加新接口 |
| `FlowConnectorService.java` | 修改 | 添加方法声明 |
| `FlowConnectorServiceImpl.java` | 修改 | 实现方法 + 辅助方法 |
| `FlowErrorCodeConstants.java` | 修改 | 添加新错误码 |

---

## 实现检查清单

- [ ] 创建 `SaveActionConfigReqVO` 类
- [ ] 添加 `ACTION_ALREADY_EXISTS` 错误码
- [ ] 添加 `INVALID_ACTION_CONFIG` 错误码
- [ ] 在 `FlowConnectorService` 添加方法声明
- [ ] 实现 `saveActionConfig` 方法
- [ ] 实现 `extractActionCodeFromConfig` 辅助方法
- [ ] 实现 `parseOrCreateActionRootConfig` 辅助方法
- [ ] 实现 `updateActionMetadataVersion` 辅助方法
- [ ] 在 `FlowConnectorController` 添加接口
- [ ] 编译验证通过
- [ ] 代码提交

---

## 测试场景

| 场景 | 输入 | 预期结果 |
|------|------|---------|
| 成功保存 | 新 actionCode | 返回 true，action_config 更新 |
| 动作已存在 | 重复的 actionCode | 抛出 ACTION_ALREADY_EXISTS |
| 连接器不存在 | 无效的 connectorId | 抛出 CONNECTOR_NOT_EXISTS |
| 配置格式无效 | 缺少 basic.actionName | 抛出 INVALID_ACTION_CONFIG |

---

**设计完成时间**: 2026-02-04
