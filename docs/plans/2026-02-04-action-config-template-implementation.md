# 动作配置模板接口重构实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**目标:** 重构动作配置查询接口，统一命名规范，简化接口参数

**架构:** 通过连接器实例ID查询其类型的动作配置模板（Formily Schema），返回统一的VO结构

**技术栈:** Spring Boot, MyBatis-Plus, Jackson JsonNode, JUnit 5

---

## 实现概述

本计划将实现以下内容：
1. 创建 `ActionConfigTemplateVO` 类
2. 在 Controller 中添加新接口 `getActionConfigTemplate`
3. 删除旧接口 `getActionSchema` 和 `getActionValue`
4. 实现 Service 层方法
5. 添加错误码
6. 编写单元测试和集成测试

---

## Task 1: 创建 ActionConfigTemplateVO

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/vo/ActionConfigTemplateVO.java`

**Step 1: 创建 VO 类文件**

```java
package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 动作配置模板 VO
 * <p>
 * 用于返回连接器类型的动作配置 Formily Schema 模板
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

**Step 2: 编译验证**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn compile -q
```

预期：编译成功，无错误

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/vo/ActionConfigTemplateVO.java
git commit -m "feat: 添加 ActionConfigTemplateVO

新增动作配置模板VO，用于返回连接器类型的动作配置Formily Schema

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 2: 添加错误码定义

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-api/src/main/java/com/cmsr/onebase/module/flow/api/enums/ErrorCodeConstants.java` (或对应的错误码文件)

**Step 1: 查找错误码常量文件**

```bash
find onebase-module-flow -name "*ErrorCode*.java" -type f | grep -E "constants|enum"
```

**Step 2: 添加错误码**

在错误码常量类中添加：

```java
// ========== 动作配置模板错误码 (0_020_500_006) ==========

ErrorCode ACTION_CONFIG_TEMPLATE_NOT_EXISTS =
    new ErrorCode(0_020_500_006, "动作配置模板不存在，连接器类型: {0}");
```

**Step 3: 编译验证**

```bash
mvn compile -pl onebase-module-flow/onebase-module-flow-api -q
```

**Step 4: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-api/src/main/java/com/cmsr/onebase/module/flow/api/enums/ErrorCodeConstants.java
git commit -m "feat: 添加动作配置模板不存在错误码

错误码: 0_020_500_006

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 3: 在 Service 接口添加方法声明

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java`

**Step 1: 添加方法声明**

在接口中添加：

```java
/**
 * 获取连接器类型的动作配置模板
 *
 * @param connectorId 连接器实例ID
 * @return 动作配置模板（Formily Schema）
 */
ActionConfigTemplateVO getActionConfigTemplate(Long connectorId);
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
git commit -m "feat: 添加 getActionConfigTemplate 方法声明

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 4: 实现 Service 方法

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/impl/FlowConnectorServiceImpl.java`

**Step 1: 编写实现方法**

在 FlowConnectorServiceImpl 类中添加：

```java
@Override
public ActionConfigTemplateVO getActionConfigTemplate(Long connectorId) {
    // 参数校验
    if (connectorId == null) {
        throw exception(CONNECTOR_ID_NOT_NULL);
    }

    // 1. 查询连接器实例
    FlowConnectorDO connector = connectorMapper.selectById(connectorId);
    if (connector == null) {
        throw exception(CONNECTOR_NOT_EXISTS);
    }

    // 2. 通过 typeCode (= nodeCode) 查询节点配置
    String nodeCode = connector.getTypeCode();
    FlowNodeConfigDO nodeConfig = nodeConfigService.getByNodeCode(nodeCode);

    // 3. 校验动作配置模板存在
    if (nodeConfig == null || nodeConfig.getActionConfig() == null) {
        throw exception(ACTION_CONFIG_TEMPLATE_NOT_EXISTS, connector.getTypeCode());
    }

    // 4. 构造返回结果
    ActionConfigTemplateVO vo = new ActionConfigTemplateVO();
    vo.setSchema(nodeConfig.getActionConfig());

    return vo;
}
```

**Step 2: 添加必要的 import**

```java
import com.cmsr.onebase.module.flow.build.vo.ActionConfigTemplateVO;
```

**Step 3: 编译验证**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn compile -q
```

预期：编译成功

**Step 4: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/impl/FlowConnectorServiceImpl.java
git commit -m "feat: 实现 getActionConfigTemplate 方法

通过连接器实例ID查询其类型的动作配置模板:
1. 查询连接器实例获取typeCode
2. 通过typeCode查询flow_node_config.action_config
3. 返回ActionConfigTemplateVO

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 5: 在 Controller 添加新接口

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java`

**Step 1: 在动作管理接口区域添加新接口**

在 `// ==================== 动作管理接口 ====================` 区域添加：

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

**Step 2: 编译验证**

```bash
mvn compile -pl onebase-module-flow/onebase-module-flow-build -q
```

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java
git commit -m "feat: 添加 getActionConfigTemplate 接口

新增GET /{id}/action-config-template接口
- 统一命名规范，与getEnvConfigTemplate保持一致
- 返回ActionConfigTemplateVO

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 6: 删除旧接口 - getActionSchema

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java`
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java`
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/impl/FlowConnectorServiceImpl.java`

**Step 1: 删除 Controller 接口**

删除 FlowConnectorController 中的以下代码（约第 190-197 行）：

```java
@Operation(summary = "获取动作的 Formily Schema")
@GetMapping("/{connectorId}/action-schema/{actionCode}")
public CommonResult<JsonNode> getActionSchema(
        @PathVariable Long connectorId,
        @PathVariable String actionCode) {
    JsonNode schema = connectorService.getActionSchema(connectorId, actionCode);
    return CommonResult.success(schema);
}
```

**Step 2: 删除 Service 接口声明**

在 FlowConnectorService 中删除：

```java
JsonNode getActionSchema(Long connectorId, String actionCode);
```

**Step 3: 删除 Service 实现**

在 FlowConnectorServiceImpl 中删除对应的实现方法

**Step 4: 编译验证**

```bash
mvn compile -pl onebase-module-flow/onebase-module-flow-build -q
```

**Step 5: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/impl/FlowConnectorServiceImpl.java
git commit -m "refactor: 删除 getActionSchema 接口

已被getActionConfigTemplate替代

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 7: 删除旧接口 - getActionValue

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java`
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java`
- Modify: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/impl/FlowConnectorServiceImpl.java`

**Step 1: 删除 Controller 接口**

删除 FlowConnectorController 中的以下代码（约第 163-170 行）：

```java
@Operation(summary = "查询指定动作配置内容")
@GetMapping("/{id}/action-value")
public CommonResult<JsonNode> getActionValue(
        @PathVariable Long id,
        @RequestParam("actionName") @NotBlank(message = "动作名称不能为空") String actionName) {
    JsonNode actionValue = connectorService.getActionValueById(id, actionName);
    return CommonResult.success(actionValue);
}
```

**Step 2: 删除 Service 接口声明**

在 FlowConnectorService 中删除：

```java
JsonNode getActionValueById(Long connectorId, String actionName);
```

**Step 3: 删除 Service 实现**

在 FlowConnectorServiceImpl 中删除对应的实现方法

**Step 4: 删除不再需要的 import**

删除以下 import（如果没有其他地方使用）：
```java
import com.fasterxml.jackson.databind.JsonNode;
```

**Step 5: 编译验证**

```bash
mvn compile -pl onebase-module-flow/onebase-module-flow-build -q
```

**Step 6: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorController.java
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorService.java
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/impl/FlowConnectorServiceImpl.java
git commit -m "refactor: 删除 getActionValue 接口

已被getActionConfigTemplate替代

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 8: 编写单元测试

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/test/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorServiceGetActionConfigTemplateTest.java`

**Step 1: 创建单元测试类**

```java
package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.test.core.ut.BaseDbUnitTest;
import com.cmsr.onebase.module.flow.build.vo.ActionConfigTemplateVO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link FlowConnectorService#getActionConfigTemplate} 单元测试
 */
@DisplayName("FlowConnectorService.getActionConfigTemplate 测试")
class FlowConnectorServiceGetActionConfigTemplateTest extends BaseDbUnitTest {

    @InjectMocks
    private FlowConnectorServiceImpl connectorService;

    @MockBean
    private FlowConnectorMapper connectorMapper;

    @MockBean
    private FlowNodeConfigService nodeConfigService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("成功获取动作配置模板")
    void testGetActionConfigTemplate_Success() throws Exception {
        // 准备测试数据
        Long connectorId = 1L;
        String typeCode = "HTTP";

        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode(typeCode);

        String schemaJson = """
            {
                "type": "object",
                "properties": {
                    "basicInfo": {"type": "object"},
                    "inputConfig": {"type": "object"}
                }
            }
            """;
        JsonNode actionConfig = objectMapper.readTree(schemaJson);

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setNodeCode(typeCode);
        nodeConfig.setActionConfig(actionConfig);

        // Mock 行为
        when(connectorMapper.selectById(connectorId)).thenReturn(connector);
        when(nodeConfigService.getByNodeCode(typeCode)).thenReturn(nodeConfig);

        // 执行测试
        ActionConfigTemplateVO result = connectorService.getActionConfigTemplate(connectorId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getSchema()).isNotNull();
        assertThat(result.getSchema().has("properties")).isTrue();

        // 验证调用
        verify(connectorMapper).selectById(connectorId);
        verify(nodeConfigService).getByNodeCode(typeCode);
    }

    @Test
    @DisplayName("连接器不存在时抛出异常")
    void testGetActionConfigTemplate_ConnectorNotExists() {
        Long connectorId = 999L;

        when(connectorMapper.selectById(connectorId)).thenReturn(null);

        assertThatThrownBy(() -> connectorService.getActionConfigTemplate(connectorId))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("连接器不存在");
    }

    @Test
    @DisplayName("动作配置模板不存在时抛出异常")
    void testGetActionConfigTemplate_ActionConfigNotExists() {
        Long connectorId = 1L;
        String typeCode = "HTTP";

        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode(typeCode);

        when(connectorMapper.selectById(connectorId)).thenReturn(connector);
        when(nodeConfigService.getByNodeCode(typeCode)).thenReturn(null);

        assertThatThrownBy(() -> connectorService.getActionConfigTemplate(connectorId))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("动作配置模板不存在");
    }

    @Test
    @DisplayName("connectorId为null时抛出异常")
    void testGetActionConfigTemplate_NullConnectorId() {
        assertThatThrownBy(() -> connectorService.getActionConfigTemplate(null))
            .isInstanceOf(ServiceException.class);
    }
}
```

**Step 2: 运行测试验证失败**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn test -Dtest=FlowConnectorServiceGetActionConfigTemplateTest
```

预期：测试通过（因为我们已经实现了方法）

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/test/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorServiceGetActionConfigTemplateTest.java
git commit -m "test: 添加 getActionConfigTemplate 单元测试

覆盖场景:
- 成功获取动作配置模板
- 连接器不存在
- 动作配置模板不存在
- 参数为null

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 9: 编写 API 集成测试

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/test/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorControllerGetActionConfigTemplateTest.java`

**Step 1: 创建集成测试类**

```java
package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.test.core.ut.BaseDbUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link FlowConnectorController#getActionConfigTemplate} API 测试
 */
@DisplayName("FlowConnectorController.getActionConfigTemplate API 测试")
@AutoConfigureMockMvc
class FlowConnectorControllerGetActionConfigTemplateTest extends BaseDbUnitTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    @DisplayName("成功获取动作配置模板")
    void testGetActionConfigTemplate_Success() throws Exception {
        mockMvc.perform(get("/flow/connector/1/action-config-template"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.schema").exists());
    }

    @Test
    @DisplayName("连接器不存在返回错误")
    void testGetActionConfigTemplate_ConnectorNotExists() throws Exception {
        mockMvc.perform(get("/flow/connector/999/action-config-template"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0_020_500_001)); // CONNECTOR_NOT_EXISTS
    }
}
```

**Step 2: 运行测试**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn test -Dtest=FlowConnectorControllerGetActionConfigTemplateTest
```

**Step 3: 提交**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/test/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorControllerGetActionConfigTemplateTest.java
git commit -m "test: 添加 getActionConfigTemplate API 集成测试

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## Task 10: 运行完整测试套件

**Step 1: 运行所有测试**

```bash
cd onebase-module-flow/onebase-module-flow-build
mvn test
```

预期：所有测试通过

**Step 2: 检查测试覆盖率**

```bash
mvn jacoco:report
```

**Step 3: 如果测试失败，修复并重新测试**

**Step 4: 最终提交（如果需要修复）**

```bash
git add -A
git commit -m "test: 修复测试用例并验证通过

Co-Authored-By: Claude (GLM-4.7) <noreply@anthropic.com>"
```

---

## 完成检查清单

- [ ] ActionConfigTemplateVO 创建完成
- [ ] 错误码添加完成
- [ ] Service 接口方法添加完成
- [ ] Service 实现完成
- [ ] Controller 接口添加完成
- [ ] 旧接口 getActionSchema 删除完成
- [ ] 旧接口 getActionValue 删除完成
- [ ] 单元测试编写完成并通过
- [ ] API 集成测试编写完成并通过
- [ ] 所有测试套件通过
- [ ] 代码已提交

---

## 验证命令

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 本地启动验证
mvn spring-boot:run

# API 测试
curl http://localhost:8080/flow/connector/1/action-config-template
```

---

**计划完成时间**: 2026-02-04