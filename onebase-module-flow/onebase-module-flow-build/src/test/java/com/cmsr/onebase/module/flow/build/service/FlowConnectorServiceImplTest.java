package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.flow.build.util.ConnectorConfigParser;
import com.cmsr.onebase.module.flow.build.vo.EnvConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.build.vo.UpdateFlowConnectorReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}
