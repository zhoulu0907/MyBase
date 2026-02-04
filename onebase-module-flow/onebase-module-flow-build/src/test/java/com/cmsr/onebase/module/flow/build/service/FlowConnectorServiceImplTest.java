package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.flow.build.util.ConnectorConfigParser;
import com.cmsr.onebase.module.flow.build.vo.ActionConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.ConnectorActionVO;
import com.cmsr.onebase.module.flow.build.vo.EnvConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.SaveActionConfigReqVO;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.build.vo.SaveEnvironmentConfigReqVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * FlowConnectorServiceImpl 单元测试
 * <p>
 * 测试连接器服务的环境配置查询功能
 *
 * @author kanten
 * @since 2026-01-30
 */
class FlowConnectorServiceImplTest {

    @Mock
    private FlowConnectorRepository connectorRepository;

    @Mock
    private FlowNodeConfigRepository flowNodeConfigRepository;

    @Mock
    private ConnectorConfigParser connectorConfigParser;

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    private ActionConfigHelper actionConfigHelper = new ActionConfigHelper(new ObjectMapper());

    @InjectMocks
    private FlowConnectorServiceImpl connectorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 注入真实的 ObjectMapper（用于 JSON 解析）
        connectorService.setObjectMapper(new ObjectMapper());
    }

    // ==================== getEnvironmentConfig 测试用例 ====================

    @Test
    void testGetEnvironmentConfig_connectorNotExists() {
        // Given
        Long connectorId = 999L;
        String envCode = "DEV";
        when(connectorRepository.getById(connectorId)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.getEnvironmentConfig(connectorId, envCode);
        });
    }

    @Test
    void testGetEnvironmentConfig_configIsEmpty() {
        // Given
        Long connectorId = 1L;
        String envCode = "DEV";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig("");
        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.getEnvironmentConfig(connectorId, envCode);
        });
    }

    @Test
    void testGetEnvironmentConfig_success() {
        // Given
        Long connectorId = 1L;
        String envCode = "PROD";
        String config = "{" +
                "\"type\":\"HTTP\"," +
                "\"properties\":{" +
                "\"PROD\":{" +
                "\"type\":\"object\"," +
                "\"title\":\"PROD动作\"" +
                "}" +
                "}}";

        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setConfig(config);
        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        JsonNode mockSchema = mock(JsonNode.class);
        when(mockSchema.get("type")).thenReturn(mock(JsonNode.class));
        when(mockSchema.get("title")).thenReturn(mock(JsonNode.class));
        when(connectorConfigParser.parseEnvironmentSchema(config, envCode)).thenReturn(mockSchema);

        // When
        EnvironmentConfigVO result = connectorService.getEnvironmentConfig(connectorId, envCode);

        // Then
        assertNotNull(result);
        assertEquals("PROD", result.getEnvCode());
        assertEquals("HTTP", result.getTypeCode());
        assertNotNull(result.getSchema());
        verify(connectorRepository).getById(connectorId);
        verify(connectorConfigParser).parseEnvironmentSchema(config, envCode);
    }

    // ==================== updateBaseInfo 测试用例 ====================

    @Test
    void testUpdateBaseInfo_noChange() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setDescription("旧描述");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        UpdateFlowConnectorReqVO updateVO = new UpdateFlowConnectorReqVO();
        updateVO.setDescription("旧描述");

        // When
        Boolean result = connectorService.updateBaseInfo(connectorId, updateVO);

        // Then
        assertFalse(result);
        verify(connectorRepository, never()).updateById(any());
    }

    @Test
    void testUpdateBaseInfo_withChange() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setDescription("旧描述");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        UpdateFlowConnectorReqVO updateVO = new UpdateFlowConnectorReqVO();
        updateVO.setDescription("新描述");

        // When
        Boolean result = connectorService.updateBaseInfo(connectorId, updateVO);

        // Then
        assertTrue(result);
        verify(connectorRepository).updateById(any());
    }

    @Test
    void testUpdateBaseInfo_notExists() {
        // Given
        Long connectorId = 999L;
        when(connectorRepository.getById(connectorId)).thenReturn(null);

        UpdateFlowConnectorReqVO updateVO = new UpdateFlowConnectorReqVO();
        updateVO.setDescription("描述");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.updateBaseInfo(connectorId, updateVO);
        });
    }

    @Test
    void testUpdateBaseInfo_emptyStringToNull() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setDescription(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        UpdateFlowConnectorReqVO updateVO = new UpdateFlowConnectorReqVO();
        updateVO.setDescription("");

        // When
        Boolean result = connectorService.updateBaseInfo(connectorId, updateVO);

        // Then
        assertFalse(result); // 空字符串和 null 视为相等
        verify(connectorRepository, never()).updateById(any());
    }

    @Test
    void testUpdateBaseInfo_nullOverwritesValue() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setDescription("旧描述");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        UpdateFlowConnectorReqVO updateVO = new UpdateFlowConnectorReqVO();
        updateVO.setDescription(null);

        // When
        Boolean result = connectorService.updateBaseInfo(connectorId, updateVO);

        // Then
        assertTrue(result); // null 可以覆盖有值
        verify(connectorRepository).updateById(any());
    }

    @Test
    void testUpdateBaseInfo_valueOverwritesNull() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setDescription(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        UpdateFlowConnectorReqVO updateVO = new UpdateFlowConnectorReqVO();
        updateVO.setDescription("新描述");

        // When
        Boolean result = connectorService.updateBaseInfo(connectorId, updateVO);

        // Then
        assertTrue(result);
        verify(connectorRepository).updateById(any());
    }

    // ==================== getEnvConfigTemplate 测试用例 ====================

    @Test
    void testGetEnvConfigTemplate_success() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setConnConfig("{\"type\":\"object\",\"properties\":{\"envName\":{\"type\":\"string\"}}}");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        // When
        EnvConfigTemplateVO result = connectorService.getEnvConfigTemplate(connectorId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSchema());
    }

    @Test
    void testGetEnvConfigTemplate_connectorNotExists() {
        // Given
        Long connectorId = 999L;
        when(connectorRepository.getById(connectorId)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.getEnvConfigTemplate(connectorId);
        });
    }

    @Test
    void testGetEnvConfigTemplate_nodeConfigNotExists() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setTypeCode("UNKNOWN");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("UNKNOWN")).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.getEnvConfigTemplate(connectorId);
        });
    }

    @Test
    void testGetEnvConfigTemplate_connConfigIsEmpty() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setConnConfig("");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.getEnvConfigTemplate(connectorId);
        });
    }

    @Test
    void testGetEnvConfigTemplate_connConfigInvalid() {
        // Given
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setConnConfig("{invalid json}");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            connectorService.getEnvConfigTemplate(connectorId);
        });
    }

    // ==================== getActionInfos 测试用例 ====================

    @Test
    void testGetActionInfos_configIsNull() {
        // Given - 连接器存在但 config 为 null
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setConfig(null);  // config 为 null
        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        // When - 不应该抛出异常，返回空列表
        List<ConnectorActionVO> result = connectorService.getActionInfos(connectorId);

        // Then - 返回空列表而不是抛出异常
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(connectorRepository).getById(connectorId);
    }

    @Test
    void testGetActionInfos_configIsEmpty() {
        // Given - 连接器存在但 config 为空字符串
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setConfig("");  // config 为空字符串
        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        // When - 不应该抛出异常，返回空列表
        List<ConnectorActionVO> result = connectorService.getActionInfos(connectorId);

        // Then - 返回空列表而不是抛出异常
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(connectorRepository).getById(connectorId);
    }

    // ==================== saveEnvironmentConfig 测试用例 ====================

    @Test
    void testSaveEnvironmentConfig_Success() throws Exception {
        // 准备测试数据
        Long connectorId = 1L;
        String existingConfig = "{\"properties\":{\"DEV\":{\"type\":\"object\"}},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(existingConfig);

        // Mock repository 行为
        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        // 准备请求 - 使用 ObjectMapper 创建包含 properties 的 JsonNode
        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String uatConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        JsonNode uatConfig = new ObjectMapper().readTree(uatConfigJson);

        // 调试：打印 JsonNode 的内容
        System.out.println("uatConfig: " + uatConfig);
        System.out.println("uatConfig.isObject(): " + uatConfig.isObject());
        System.out.println("uatConfig.has('properties'): " + uatConfig.has("properties"));
        System.out.println("uatConfig.get('properties'): " + uatConfig.get("properties"));

        config.put("UAT", uatConfig);
        reqVO.setConfig(config);

        // 执行测试
        Boolean result = connectorService.saveEnvironmentConfig(connectorId, reqVO);

        // 验证结果
        assertTrue(result);
        verify(connectorRepository).updateById(any(FlowConnectorDO.class));
    }

    @Test
    void testSaveEnvironmentConfig_ConnectorNotFound() {
        Long connectorId = 999L;
        when(connectorRepository.getById(connectorId)).thenReturn(null);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        reqVO.setConfig(Map.of());

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveEnvironmentConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void testSaveEnvironmentConfig_EnvAlreadyExists() throws Exception {
        Long connectorId = 1L;
        String existingConfig = "{\"properties\":{\"DEV\":{\"type\":\"object\"}},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(existingConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String devConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        JsonNode devConfig = new ObjectMapper().readTree(devConfigJson);
        config.put("DEV", devConfig);
        reqVO.setConfig(config);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveEnvironmentConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.ENV_ALREADY_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void testSaveEnvironmentConfig_EmptyConfig() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String uatConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        JsonNode uatConfig = new ObjectMapper().readTree(uatConfigJson);
        config.put("UAT", uatConfig);
        reqVO.setConfig(config);

        // 执行测试
        Boolean result = connectorService.saveEnvironmentConfig(connectorId, reqVO);

        assertTrue(result);
    }

    @Test
    void testSaveEnvironmentConfig_InvalidConfigFormat() throws Exception {
        Long connectorId = 1L;
        String existingConfig = "{\"properties\":{},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(existingConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String invalidConfigJson = "{\"type\":\"string\"}";
        JsonNode invalidConfig = new ObjectMapper().readTree(invalidConfigJson);
        config.put("DEV", invalidConfig);
        reqVO.setConfig(config);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveEnvironmentConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_ENV_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveEnvironmentConfig_MultipleEnvironments() throws Exception {
        Long connectorId = 1L;
        String existingConfig = "{\"properties\":{},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(existingConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();

        String devConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        String testConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        String prodConfigJson = "{\"type\":\"object\",\"properties\":{}}";

        config.put("DEV", new ObjectMapper().readTree(devConfigJson));
        config.put("TEST", new ObjectMapper().readTree(testConfigJson));
        config.put("PROD", new ObjectMapper().readTree(prodConfigJson));
        reqVO.setConfig(config);

        Boolean result = connectorService.saveEnvironmentConfig(connectorId, reqVO);

        assertTrue(result);
        verify(connectorRepository).updateById(any(FlowConnectorDO.class));
    }

    @Test
    void testSaveEnvironmentConfig_ConfigParsingError() throws Exception {
        Long connectorId = 1L;
        String invalidJson = "{invalid json}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(invalidJson);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String uatConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        JsonNode uatConfig = new ObjectMapper().readTree(uatConfigJson);
        config.put("UAT", uatConfig);
        reqVO.setConfig(config);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveEnvironmentConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveEnvironmentConfig_WithExistingMetadata() throws Exception {
        Long connectorId = 1L;
        String existingConfig = "{\"properties\":{\"DEV\":{\"type\":\"object\"}},\"_metadata\":{\"version\":2,\"updatedBy\":\"admin\"}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(existingConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String uatConfigJson = "{\"type\":\"object\",\"properties\":{\"apiUrl\":{\"type\":\"string\"}}}";
        JsonNode uatConfig = new ObjectMapper().readTree(uatConfigJson);
        config.put("UAT", uatConfig);
        reqVO.setConfig(config);

        Boolean result = connectorService.saveEnvironmentConfig(connectorId, reqVO);

        assertTrue(result);
        verify(connectorRepository).updateById(argThat(doArg -> {
            try {
                String updatedConfig = doArg.getConfig();
                return updatedConfig != null && updatedConfig.contains("UAT");
            } catch (Exception e) {
                return false;
            }
        }));
    }

    @Test
    void testSaveEnvironmentConfig_UpdateFailure() throws Exception {
        Long connectorId = 1L;
        String existingConfig = "{\"properties\":{},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setConfig(existingConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(false);

        SaveEnvironmentConfigReqVO reqVO = new SaveEnvironmentConfigReqVO();
        Map<String, JsonNode> config = new HashMap<>();
        String uatConfigJson = "{\"type\":\"object\",\"properties\":{}}";
        JsonNode uatConfig = new ObjectMapper().readTree(uatConfigJson);
        config.put("UAT", uatConfig);
        reqVO.setConfig(config);

        Boolean result = connectorService.saveEnvironmentConfig(connectorId, reqVO);

        assertTrue(result);
    }

    // ==================== getActionConfigTemplate 测试用例 ====================

    @Test
    void testGetActionConfigTemplate_success() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setNodeCode("HTTP");
        String actionConfigJson = "{\"type\":\"object\",\"properties\":{\"actionName\":{\"type\":\"string\"}}}";
        nodeConfig.setActionConfig(actionConfigJson);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        ActionConfigTemplateVO result = connectorService.getActionConfigTemplate(connectorId);

        assertNotNull(result);
        assertNotNull(result.getSchema());
        assertEquals("object", result.getSchema().get("type").asText());
        verify(connectorRepository).getById(connectorId);
        verify(flowNodeConfigRepository).findByNodeCode("HTTP");
    }

    @Test
    void testGetActionConfigTemplate_connectorNotExists() {
        Long connectorId = 999L;
        when(connectorRepository.getById(connectorId)).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.getActionConfigTemplate(connectorId);
        });

        assertEquals(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS.getCode(), exception.getCode());
        verify(connectorRepository).getById(connectorId);
    }

    @Test
    void testGetActionConfigTemplate_nodeConfigNotExists() {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("UNKNOWN");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("UNKNOWN")).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.getActionConfigTemplate(connectorId);
        });

        assertEquals(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXISTS.getCode(), exception.getCode());
        verify(flowNodeConfigRepository).findByNodeCode("UNKNOWN");
    }

    @Test
    void testGetActionConfigTemplate_actionConfigEmpty() {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setNodeCode("HTTP");
        nodeConfig.setActionConfig("");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.getActionConfigTemplate(connectorId);
        });

        assertEquals(FlowErrorCodeConstants.ACTION_CONFIG_EMPTY.getCode(), exception.getCode());
    }

    @Test
    void testGetActionConfigTemplate_actionConfigNull() {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setNodeCode("HTTP");
        nodeConfig.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.getActionConfigTemplate(connectorId);
        });

        assertEquals(FlowErrorCodeConstants.ACTION_CONFIG_EMPTY.getCode(), exception.getCode());
    }

    @Test
    void testGetActionConfigTemplate_invalidActionConfigFormat() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setNodeCode("HTTP");
        nodeConfig.setActionConfig("{invalid json}");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.getActionConfigTemplate(connectorId);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testGetActionConfigTemplate_complexSchema() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");

        String complexSchema = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"actionName\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"title\": \"动作名称\"\n" +
                "    },\n" +
                "    \"method\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"enum\": [\"GET\", \"POST\", \"PUT\", \"DELETE\"],\n" +
                "      \"title\": \"请求方法\"\n" +
                "    },\n" +
                "    \"timeout\": {\n" +
                "      \"type\": \"number\",\n" +
                "      \"title\": \"超时时间(毫秒)\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\"actionName\", \"method\"]\n" +
                "}";

        FlowNodeConfigDO nodeConfig = new FlowNodeConfigDO();
        nodeConfig.setNodeCode("HTTP");
        nodeConfig.setActionConfig(complexSchema);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(flowNodeConfigRepository.findByNodeCode("HTTP")).thenReturn(nodeConfig);

        ActionConfigTemplateVO result = connectorService.getActionConfigTemplate(connectorId);

        assertNotNull(result);
        assertNotNull(result.getSchema());
        assertEquals("object", result.getSchema().get("type").asText());
        assertNotNull(result.getSchema().get("properties"));
        assertEquals("string", result.getSchema().get("properties").get("actionName").get("type").asText());
        assertEquals("GET", result.getSchema().get("properties").get("method").get("enum").get(0).asText());
    }

    // ==================== saveActionConfig 测试用例 ====================

    @Test
    void testSaveActionConfig_Success_FirstTime() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String actionConfigJson = "{\"basic\":{\"actionName\":\"testAction\",\"description\":\"测试动作\"},\"requestHeaders\":[],\"requestBody\":[],\"queryParams\":[],\"pathParams\":[]}";
        JsonNode actionConfig = objectMapper.readTree(actionConfigJson);
        reqVO.setActionConfig(actionConfig);

        Boolean result = connectorService.saveActionConfig(connectorId, reqVO);

        assertTrue(result);
        verify(connectorRepository).updateById(argThat(conn ->
            conn.getActionConfig() != null &&
            conn.getActionConfig().contains("testAction") &&
            conn.getActionConfig().contains("\"type\":\"object\"")
        ));
    }

    @Test
    void testSaveActionConfig_Success_AddToExisting() throws Exception {
        Long connectorId = 1L;
        String existingActionConfig = "{\"type\":\"object\",\"title\":\"连接器动作配置\",\"properties\":{\"existingAction\":{\"basic\":{\"actionName\":\"existingAction\",\"description\":\"已有动作\"}},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setActionConfig(existingActionConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String newActionConfigJson = "{\"basic\":{\"actionName\":\"newAction\",\"description\":\"新动作\"},\"requestHeaders\":[],\"requestBody\":[],\"queryParams\":[],\"pathParams\":[]}";
        JsonNode actionConfig = objectMapper.readTree(newActionConfigJson);
        reqVO.setActionConfig(actionConfig);

        Boolean result = connectorService.saveActionConfig(connectorId, reqVO);

        assertTrue(result);
        verify(connectorRepository).updateById(argThat(conn ->
            conn.getActionConfig().contains("newAction") &&
            conn.getActionConfig().contains("existingAction")
        ));
    }

    @Test
    void testSaveActionConfig_ConnectorNotFound() {
        Long connectorId = 999L;
        when(connectorRepository.getById(connectorId)).thenReturn(null);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String actionConfigJson = "{\"basic\":{\"actionName\":\"testAction\",\"description\":\"测试\"}}";
        try {
            JsonNode actionConfig = objectMapper.readTree(actionConfigJson);
            reqVO.setActionConfig(actionConfig);
        } catch (Exception e) {
            fail("Failed to parse JSON");
        }

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_ActionAlreadyExists() throws Exception {
        Long connectorId = 1L;
        String existingActionConfig = "{\"type\":\"object\",\"properties\":{\"duplicateAction\":{\"basic\":{\"actionName\":\"duplicateAction\"}},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setActionConfig(existingActionConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String duplicateActionConfigJson = "{\"basic\":{\"actionName\":\"duplicateAction\",\"description\":\"重复动作\"}}";
        JsonNode actionConfig = objectMapper.readTree(duplicateActionConfigJson);
        reqVO.setActionConfig(actionConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.ACTION_ALREADY_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_NullActionConfig() {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        reqVO.setActionConfig(null);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_ACTION_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_MissingBasic() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String invalidConfigJson = "{\"requestHeaders\":[]}";
        JsonNode actionConfig = objectMapper.readTree(invalidConfigJson);
        reqVO.setActionConfig(actionConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_ACTION_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_MissingActionName() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String invalidConfigJson = "{\"basic\":{\"description\":\"缺少actionName\"}}";
        JsonNode actionConfig = objectMapper.readTree(invalidConfigJson);
        reqVO.setActionConfig(actionConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_ACTION_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_NullActionName() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String invalidConfigJson = "{\"basic\":{\"actionName\":null}}";
        JsonNode actionConfig = objectMapper.readTree(invalidConfigJson);
        reqVO.setActionConfig(actionConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_ACTION_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_InvalidJsonFormat() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setActionConfig("{invalid json}");

        when(connectorRepository.getById(connectorId)).thenReturn(connector);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String validActionConfigJson = "{\"basic\":{\"actionName\":\"testAction\"}}";
        JsonNode actionConfig = objectMapper.readTree(validActionConfigJson);
        reqVO.setActionConfig(actionConfig);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            connectorService.saveActionConfig(connectorId, reqVO);
        });

        assertEquals(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG.getCode(), exception.getCode());
    }

    @Test
    void testSaveActionConfig_ComplexActionConfig() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String complexActionConfigJson = "{\n" +
                "  \"basic\": {\n" +
                "    \"actionName\": \"complexAction\",\n" +
                "    \"description\": \"复杂动作配置\"\n" +
                "  },\n" +
                "  \"requestHeaders\": [\n" +
                "    {\n" +
                "      \"name\": \"Content-Type\",\n" +
                "      \"type\": \"string\",\n" +
                "      \"required\": true\n" +
                "    }\n" +
                "  ],\n" +
                "  \"requestBody\": [\n" +
                "    {\n" +
                "      \"name\": \"bodyParam\",\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"queryParams\": [\n" +
                "    {\n" +
                "      \"name\": \"page\",\n" +
                "      \"type\": \"number\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"pathParams\": [\n" +
                "    {\n" +
                "      \"name\": \"id\",\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JsonNode actionConfig = objectMapper.readTree(complexActionConfigJson);
        reqVO.setActionConfig(actionConfig);

        Boolean result = connectorService.saveActionConfig(connectorId, reqVO);

        assertTrue(result);
        verify(connectorRepository).updateById(argThat(conn ->
            conn.getActionConfig().contains("complexAction") &&
            conn.getActionConfig().contains("requestHeaders") &&
            conn.getActionConfig().contains("queryParams")
        ));
    }

    @Test
    void testSaveActionConfig_UpdateFailure() throws Exception {
        Long connectorId = 1L;
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setActionConfig(null);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(false);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String actionConfigJson = "{\"basic\":{\"actionName\":\"testAction\",\"description\":\"测试\"}}";
        JsonNode actionConfig = objectMapper.readTree(actionConfigJson);
        reqVO.setActionConfig(actionConfig);

        Boolean result = connectorService.saveActionConfig(connectorId, reqVO);

        assertTrue(result);
    }

    @Test
    void testSaveActionConfig_MultipleActions() throws Exception {
        Long connectorId = 1L;
        String existingActionConfig = "{\"type\":\"object\",\"properties\":{\"action1\":{\"basic\":{\"actionName\":\"action1\"}},\"_metadata\":{\"version\":1}}";
        FlowConnectorDO connector = new FlowConnectorDO();
        connector.setId(connectorId);
        connector.setTypeCode("HTTP");
        connector.setActionConfig(existingActionConfig);

        when(connectorRepository.getById(connectorId)).thenReturn(connector);
        when(connectorRepository.updateById(any())).thenReturn(true);

        SaveActionConfigReqVO reqVO = new SaveActionConfigReqVO();
        String actionConfigJson = "{\"basic\":{\"actionName\":\"action2\",\"description\":\"第二个动作\"}}";
        JsonNode actionConfig = objectMapper.readTree(actionConfigJson);
        reqVO.setActionConfig(actionConfig);

        Boolean result = connectorService.saveActionConfig(connectorId, reqVO);

        assertTrue(result);
        verify(connectorRepository).updateById(argThat(conn ->
            conn.getActionConfig().contains("action1") &&
            conn.getActionConfig().contains("action2")
        ));
    }
}
