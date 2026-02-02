package com.cmsr.onebase.module.flow.build.service.action;

import com.cmsr.onebase.module.flow.build.vo.ConnectorActionLiteVO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 动作列表功能测试
 * <p>
 * 测试从 config 字段解析动作列表的逻辑
 *
 * @author zhoulu
 * @since 2026-01-27
 */
class ActionListServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testParseActionList_Success() throws Exception {
        // Given - 创建测试配置数据
        String configJson = createTestConfigJson();

        // When - 解析配置
        List<ConnectorActionLiteVO> actions = parseActionList(configJson);

        // Then
        assertThat(actions).isNotNull();
        assertThat(actions).hasSize(2);

        // 验证排序（按创建时间倒序）
        assertThat(actions.get(0).getCreateTime()).isAfterOrEqualTo(actions.get(1).getCreateTime());

        // 验证两个动作都存在（不依赖顺序）
        assertThat(actions.stream().anyMatch(a -> "action-001".equals(a.getActionId()))).isTrue();
        assertThat(actions.stream().anyMatch(a -> "action-002".equals(a.getActionId()))).isTrue();
    }

    @Test
    void testParseActionList_EmptyConfig() throws Exception {
        // Given - 空配置
        String configJson = null;

        // When
        List<ConnectorActionLiteVO> actions = parseActionList(configJson);

        // Then
        assertThat(actions).isNotNull();
        assertThat(actions).isEmpty();
    }

    @Test
    void testParseActionList_EmptyStringConfig() throws Exception {
        // Given - 空字符串配置
        String configJson = "";

        // When
        List<ConnectorActionLiteVO> actions = parseActionList(configJson);

        // Then
        assertThat(actions).isNotNull();
        assertThat(actions).isEmpty();
    }

    @Test
    void testParseActionList_NoActionsNode() throws Exception {
        // Given - 没有 actions 节点
        String configJson = "{\"otherField\": \"value\"}";

        // When
        List<ConnectorActionLiteVO> actions = parseActionList(configJson);

        // Then
        assertThat(actions).isNotNull();
        assertThat(actions).isEmpty();
    }

    @Test
    void testParseActionList_SingleAction() throws Exception {
        // Given - 只有一个动作的配置
        String singleActionConfig = """
                {
                    "actions": {
                        "action1": {
                            "actionId": "action-001",
                            "actionName": "单个动作",
                            "actionCode": "SINGLE_ACTION",
                            "status": "published",
                            "version": 1,
                            "createTime": "2026-01-27T10:00:00"
                        }
                    }
                }
                """;

        // When
        List<ConnectorActionLiteVO> actions = parseActionList(singleActionConfig);

        // Then
        assertThat(actions).isNotNull();
        assertThat(actions).hasSize(1);
        assertThat(actions.get(0).getActionId()).isEqualTo("action-001");
        assertThat(actions.get(0).getActionName()).isEqualTo("单个动作");
    }

    @Test
    void testParseActionList_MissingOptionalFields() throws Exception {
        // Given - 动作配置缺少可选字段
        String configWithMissingFields = """
                {
                    "actions": {
                        "action1": {
                            "actionId": "action-001",
                            "actionName": "测试动作"
                        }
                    }
                }
                """;

        // When
        List<ConnectorActionLiteVO> actions = parseActionList(configWithMissingFields);

        // Then
        assertThat(actions).isNotNull();
        assertThat(actions).hasSize(1);
        ConnectorActionLiteVO action = actions.get(0);
        assertThat(action.getActionId()).isEqualTo("action-001");
        assertThat(action.getActionName()).isEqualTo("测试动作");
        assertThat(action.getActionCode()).isNull(); // 可选字段为null
        assertThat(action.getStatus()).isNull();
        assertThat(action.getVersion()).isNull();
        assertThat(action.getCreateTime()).isNull();
    }

    @Test
    void testParseActionList_SortingByCreateTime() throws Exception {
        // Given - 三个动作，创建时间不同
        String configWithThreeActions = """
                {
                    "actions": {
                        "action1": {
                            "actionId": "action-001",
                            "actionName": "第一个动作",
                            "createTime": "2026-01-26T10:00:00"
                        },
                        "action2": {
                            "actionId": "action-002",
                            "actionName": "第二个动作",
                            "createTime": "2026-01-27T15:30:00"
                        },
                        "action3": {
                            "actionId": "action-003",
                            "actionName": "第三个动作",
                            "createTime": "2026-01-25T08:00:00"
                        }
                    }
                }
                """;

        // When
        List<ConnectorActionLiteVO> actions = parseActionList(configWithThreeActions);

        // Then - 验证按创建时间倒序排列
        assertThat(actions).hasSize(3);
        assertThat(actions.get(0).getCreateTime()).isAfterOrEqualTo(actions.get(1).getCreateTime());
        assertThat(actions.get(1).getCreateTime()).isAfterOrEqualTo(actions.get(2).getCreateTime());
    }

    @Test
    void testConnectorEnvDO_ConfigField() {
        // Given
        FlowConnectorEnvDO env = new FlowConnectorEnvDO();
        env.setId(1L);
        env.setEnvUuid("test-env-" + System.currentTimeMillis());
        env.setEnvName("测试环境");
        env.setEnvCode("TEST");
        env.setTypeCode("HTTP");

        String testConfig = "{\"actions\": {\"action1\": {\"actionId\": \"action-001\"}}}";
        env.setConfig(testConfig);

        // Then - 验证 config 字段设置成功
        assertThat(env.getConfig()).isNotNull();
        assertThat(env.getConfig()).isEqualTo(testConfig);
        assertThat(env.getConfig()).contains("actions");
        assertThat(env.getConfig()).contains("action-001");
    }

    /**
     * 解析动作列表（模拟 Service 方法逻辑）
     */
    private List<ConnectorActionLiteVO> parseActionList(String configJson) {
        if (configJson == null || configJson.isEmpty() || "null".equals(configJson)) {
            return new ArrayList<>();
        }

        try {
            // 提取 actions 对象
            JsonNode configNode = objectMapper.readTree(configJson);
            JsonNode actionsNode = configNode.get("actions");

            if (actionsNode == null || !actionsNode.isObject()) {
                return new ArrayList<>();
            }

            // 转换为 VO 列表
            List<ConnectorActionLiteVO> result = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = actionsNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode actionNode = entry.getValue();

                ConnectorActionLiteVO vo = new ConnectorActionLiteVO();
                vo.setActionId(getJsonText(actionNode, "actionId"));
                vo.setActionName(getJsonText(actionNode, "actionName"));
                vo.setActionCode(getJsonText(actionNode, "actionCode"));
                vo.setStatus(getJsonText(actionNode, "status"));

                JsonNode versionNode = actionNode.get("version");
                if (versionNode != null && !versionNode.isNull()) {
                    vo.setVersion(versionNode.asInt());
                }

                String createTimeStr = getJsonText(actionNode, "createTime");
                if (createTimeStr != null) {
                    try {
                        vo.setCreateTime(LocalDateTime.parse(createTimeStr));
                    } catch (Exception e) {
                        // 忽略日期解析错误
                    }
                }

                result.add(vo);
            }

            // 按创建时间倒序排列
            result.sort((a, b) -> {
                if (a.getCreateTime() == null && b.getCreateTime() == null) {
                    return 0;
                }
                if (a.getCreateTime() == null) {
                    return 1;
                }
                if (b.getCreateTime() == null) {
                    return -1;
                }
                return b.getCreateTime().compareTo(a.getCreateTime());
            });

            return result;

        } catch (Exception e) {
            throw new RuntimeException("解析动作配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 辅助方法：安全获取 JSON 字段文本值
     */
    private String getJsonText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }

    /**
     * 创建测试配置JSON
     */
    private String createTestConfigJson() throws Exception {
        return objectMapper.writeValueAsString(new TestConfig());
    }

    /**
     * 测试配置数据结构
     */
    static class TestConfig {
        public Actions actions = new Actions();
    }

    static class Actions {
        public Action1 action1 = new Action1();
        public Action2 action2 = new Action2();
    }

    static class Action1 {
        public String actionId = "action-001";
        public String actionName = "获取用户信息";
        public String actionCode = "GET_USER";
        public String status = "published";
        public int version = 2;
        public String createTime = "2026-01-26T10:00:00";
    }

    static class Action2 {
        public String actionId = "action-002";
        public String actionName = "获取客户订单";
        public String actionCode = "GET_ORDERS";
        public String status = "draft";
        public int version = 1;
        public String createTime = "2026-01-27T15:30:00";
    }
}
