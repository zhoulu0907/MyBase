# 保存连接器动作配置接口实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**目标:** 新增保存连接器动作配置接口，将单个动作配置保存到 flow_connector.action_config 字段

**架构:** 参考 saveEnvironmentConfig 实现，提取 actionName 作为唯一键，合并到 Formily Schema 格式的 action_config 中

**技术栈:** Spring Boot, MyBatis-Plus, Jackson JsonNode, JUnit 5

---

## 实现概述

本计划将实现以下内容：
1. 创建 `SaveActionConfigReqVO` 类
2. 添加新错误码
3. 在 Service 接口添加方法声明
4. 实现 Service 方法和辅助方法
5. 在 Controller 添加新接口

**注意**: 本实现不包含单元测试

---

## Task 1: 创建 SaveActionConfigReqVO

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/vo/SaveActionConfigReqVO.java`

**Step 1: 创建 VO 类文件**

```java
package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存连接器动作配置请求 VO
 *
 * @author kanten
 * @since 2026-02-04
 */
@Data
@Schema(description = "保存连接器动作配置请求")
public class SaveActionConfigReqVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "动作配置（包含 basic, requestHeaders, requestBody, queryParams, pathParams）",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "{\"basic\":{\"actionName\":\"hahaha1\",\"description\":\"描述\"},\"requestHeaders\":[...]}")
    @NotNull(message = "动作配置不能为空")
    @Valid
    private JsonNode actionConfig;
}
```

**Step 2: 编译验证**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn compile -q
```

预期：编译成功，无错误

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/vo/SaveActionConfigReqVO.java
git commit -m "feat: 添加 SaveActionConfigReqVO

新增保存连接器动作配置请求VO

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 2: 添加错误码定义

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/enums/FlowErrorCodeConstants.java`

**Step 1: 添加错误码**

在错误码常量类末尾添加：

```java
    /**
     * 环境配置格式无效
     */
    ErrorCode INVALID_ENV_CONFIG = new ErrorCode(1123794, "环境配置格式无效");

    /**
     * 动作已存在
     */
    ErrorCode ACTION_ALREADY_EXISTS = new ErrorCode(1123795, "动作已存在：actionCode={}");

    /**
     * 动作配置格式无效
     */
    ErrorCode INVALID_ACTION_CONFIG = new ErrorCode(1123796, "动作配置格式无效，缺少必填字段");
}
```

**Step 2: 编译验证**

```bash
mvn compile -pl onebase-module-flow/onebase-module-flow-core -q
```

预期：编译成功

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/enums/FlowErrorCodeConstants.java
git commit -m "feat: 添加动作配置相关错误码

- ACTION_ALREADY_EXISTS: 动作已存在
- INVALID_ACTION_CONFIG: 动作配置格式无效

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 3: 在 Service 接口添加方法声明

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java`

**Step 1: 添加方法声明**

在接口中添加（建议放在 `saveEnvironmentConfig` 方法之后）：

```java
/**
 * 保存连接器动作配置
 * <p>
 * 将新的动作配置添加到 flow_connector.action_config.properties 中
 * 如果动作已存在则拒绝保存
 *
 * @param connectorId 连接器实例ID
 * @param reqVO      动作配置请求
 * @return 保存是否成功
 */
Boolean saveActionConfig(Long connectorId, SaveActionConfigReqVO reqVO);
```

**Step 2: 编译验证（预期失败，因为还没实现）**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn compile -q
```

预期：编译失败，提示方法未实现

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java
git commit -m "feat: 添加 saveActionConfig 方法声明

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 4: 实现 Service 方法和辅助方法

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorServiceImpl.java`

**Step 1: 实现核心方法**

在 FlowConnectorServiceImpl 类中添加（建议放在 `saveEnvironmentConfig` 方法之后）：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public Boolean saveActionConfig(Long connectorId, SaveActionConfigReqVO reqVO) {
    log.info("saveActionConfig start, connectorId: {}", connectorId);

    // 1. 查询并验证连接器实例
    FlowConnectorDO connector = connectorRepository.getById(connectorId);
    if (connector == null) {
        log.warn("Connector not found, connectorId: {}", connectorId);
        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
    }

    // 2. 解析或创建 action_config 根配置
    ObjectNode rootActionConfig = parseOrCreateActionRootConfig(connector.getActionConfig());
    ObjectNode properties = rootActionConfig.withObject("properties");

    // 3. 从请求中提取动作编码（actionCode）
    String actionCode = extractActionCodeFromConfig(reqVO.getActionConfig());

    // 4. 检查动作是否已存在
    if (properties.has(actionCode)) {
        log.warn("Action already exists, connectorId: {}, actionCode: {}", connectorId, actionCode);
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

**Step 2: 实现辅助方法 - extractActionCodeFromConfig**

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

**Step 3: 实现辅助方法 - parseOrCreateActionRootConfig**

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

**Step 4: 实现辅助方法 - updateActionMetadataVersion**

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

**Step 5: 添加必要的 import**

```java
import com.cmsr.onebase.module.flow.build.vo.SaveActionConfigReqVO;
```

**Step 6: 编译验证**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn compile -q
```

预期：编译成功

**Step 7: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorServiceImpl.java
git commit -m "feat: 实现 saveActionConfig 方法

实现保存连接器动作配置:
1. 查询连接器实例
2. 解析/创建 action_config 根节点
3. 提取 actionCode 作为唯一键
4. 检查重复并保存
5. 更新元数据版本

新增辅助方法:
- extractActionCodeFromConfig: 提取动作编码
- parseOrCreateActionRootConfig: 解析或创建根配置
- updateActionMetadataVersion: 更新元数据

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 5: 在 Controller 添加新接口

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java`

**Step 1: 添加新接口**

在动作管理接口区域添加（建议放在 `getActionConfigTemplate` 接口之后）：

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

**Step 2: 编译验证**

```bash
mvn compile -pl onebase-module-flow/onebase-module-flow-build -q
```

预期：编译成功

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java
git commit -m "feat: 添加 saveActionConfig 接口

新增POST /{id}/save-action接口
- 参考 saveEnvironmentConfig 实现
- 存储到 flow_connector.action_config 字段
- 返回 Boolean 结果

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## 完成检查清单

- [ ] SaveActionConfigReqVO 创建完成
- [ ] 错误码添加完成（ACTION_ALREADY_EXISTS, INVALID_ACTION_CONFIG）
- [ ] Service 接口方法添加完成
- [ ] Service 核心方法实现完成
- [ ] extractActionCodeFromConfig 辅助方法实现完成
- [ ] parseOrCreateActionRootConfig 辅助方法实现完成
- [ ] updateActionMetadataVersion 辅助方法实现完成
- [ ] Controller 接口添加完成
- [ ] 所有代码已提交

---

## 验证命令

```bash
# 编译
mvn clean compile

# 本地启动验证
mvn spring-boot:run

# API 测试
curl -X POST http://localhost:8080/flow/connector/1/save-action \
  -H "Content-Type: application/json" \
  -d '{
    "actionConfig": {
      "basic": {
        "actionName": "testAction",
        "description": "测试动作"
      },
      "requestHeaders": [],
      "requestBody": [],
      "queryParams": [],
      "pathParams": []
    }
  }'
```

---

**计划完成时间**: 2026-02-04
