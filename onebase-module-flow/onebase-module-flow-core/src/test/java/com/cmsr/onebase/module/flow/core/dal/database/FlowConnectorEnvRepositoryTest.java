package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FlowConnectorEnvRepository 单元测试
 * <p>
 * 测试 config 字段的读写功能
 *
 * @author zhoulu
 * @since 2026-01-27
 */
class FlowConnectorEnvRepositoryTest {

    private FlowConnectorEnvDO testEnv;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testEnv = new FlowConnectorEnvDO();
        testEnv.setId(1L);
        testEnv.setEnvUuid("test-config-" + System.currentTimeMillis());
        testEnv.setEnvName("测试配置环境");
        testEnv.setEnvCode("TEST");
        testEnv.setTypeCode("HTTP");
    }

    @Test
    void testConfigField_ReadWrite() {
        // Given
        String testConfig = "{\"actions\": {\"action1\": {\"actionId\": \"action-001\"}}}";
        testEnv.setConfig(testConfig);

        // Then - 验证 config 字段设置成功
        assertThat(testEnv.getConfig()).isNotNull();
        assertThat(testEnv.getConfig()).isEqualTo(testConfig);

        // 验证 config 字段的 JSON 格式
        assertThat(testEnv.getConfig()).contains("actions");
        assertThat(testEnv.getConfig()).contains("action1");
        assertThat(testEnv.getConfig()).contains("action-001");
    }

    @Test
    void testConfigField_NullValue() {
        // Given - config 为 null
        testEnv.setConfig(null);

        // Then
        assertThat(testEnv.getConfig()).isNull();
    }

    @Test
    void testConfigField_EmptyValue() {
        // Given - config 为空字符串
        String emptyConfig = "";
        testEnv.setConfig(emptyConfig);

        // Then
        assertThat(testEnv.getConfig()).isEmpty();
    }

    @Test
    void testConfigField_ComplexJson() {
        // Given - 复杂的 JSON 配置
        String complexConfig = """
                {
                    "actions": {
                        "action1": {
                            "actionId": "action-001",
                            "actionName": "获取用户信息",
                            "actionCode": "GET_USER",
                            "status": "published",
                            "version": 2
                        },
                        "action2": {
                            "actionId": "action-002",
                            "actionName": "创建订单",
                            "actionCode": "CREATE_ORDER",
                            "status": "draft",
                            "version": 1
                        }
                    },
                    "actionDetails": {
                        "action1": {
                            "basicInfo": {
                                "description": "获取用户详细信息"
                            }
                        }
                    }
                }
                """;
        testEnv.setConfig(complexConfig);

        // Then
        assertThat(testEnv.getConfig()).isNotNull();
        assertThat(testEnv.getConfig()).contains("actions");
        assertThat(testEnv.getConfig()).contains("actionDetails");
        assertThat(testEnv.getConfig()).contains("GET_USER");
        assertThat(testEnv.getConfig()).contains("CREATE_ORDER");
    }

    @Test
    void testConfigField_JsonWithSpecialCharacters() {
        // Given - 包含特殊字符的 JSON
        String configWithSpecialChars = "{\"actions\": {\"action1\": {\"description\": \"测试中文&特殊字符<>\"}}}";
        testEnv.setConfig(configWithSpecialChars);

        // Then
        assertThat(testEnv.getConfig()).isNotNull();
        assertThat(testEnv.getConfig()).contains("测试中文");
        assertThat(testEnv.getConfig()).contains("&");
        assertThat(testEnv.getConfig()).contains("<>");
    }

    @Test
    void testConfigField_LargeJson() {
        // Given - 大型 JSON 配置
        StringBuilder largeJson = new StringBuilder();
        largeJson.append("{\"actions\": {");
        for (int i = 1; i <= 100; i++) {
            if (i > 1) largeJson.append(",");
            largeJson.append(String.format("\"action%d\": {\"actionId\": \"action-%d\", \"actionName\": \"动作%d\"}", i, i, i));
        }
        largeJson.append("}}");

        testEnv.setConfig(largeJson.toString());

        // Then
        assertThat(testEnv.getConfig()).isNotNull();
        assertThat(testEnv.getConfig()).startsWith("{\"actions\":");
        assertThat(testEnv.getConfig()).endsWith("}}");
        // 验证包含100个动作
        assertThat(testEnv.getConfig()).contains("\"action100\":");
    }
}
