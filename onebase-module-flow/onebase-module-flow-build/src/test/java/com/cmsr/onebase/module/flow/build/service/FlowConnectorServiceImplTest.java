package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.module.flow.build.util.ConnectorConfigParser;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.fasterxml.jackson.databind.JsonNode;
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
    private ConnectorConfigParser connectorConfigParser;

    @InjectMocks
    private FlowConnectorServiceImpl connectorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}
