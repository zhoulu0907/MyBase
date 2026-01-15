package com.cmsr.onebase.module.flow.component.external.connector;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConnectorRegistry单元测试
 * 测试连接器注册表的注册、查找功能
 */
class ConnectorRegistryTest extends ConnectorTestBase {

    private ConnectorRegistry connectorRegistry;

    @Mock
    private ConnectorExecutor mockConnector1;

    @Mock
    private ConnectorExecutor mockConnector2;

    @BeforeEach
    void setUp() {
        super.setUp();
        connectorRegistry = new ConnectorRegistry();

        // 设置mock连接器的基本信息
        setupMockConnector(mockConnector1, "EMAIL_163", "163邮箱连接器");
        setupMockConnector(mockConnector2, "SMS_ALI", "阿里云短信连接器");
    }

    private void setupMockConnector(ConnectorExecutor connector, String type, String name) {
        // 由于接口方法不是默认方法，我们需要使用when来配置返回值
        // 但这里我们先简化，使用真实的测试连接器
    }

    @Test
    void testRegisterConnectors() {
        // Given
        List<ConnectorExecutor> connectors = new ArrayList<>();
        connectors.add(new TestConnector("EMAIL_163", "163邮箱连接器"));
        connectors.add(new TestConnector("SMS_ALI", "阿里云短信连接器"));

        // When
        connectorRegistry.registerConnectors(connectors);

        // Then
        assertNotNull(connectorRegistry.getConnector("EMAIL_163"));
        assertNotNull(connectorRegistry.getConnector("SMS_ALI"));
    }

    @Test
    void testGetConnector_Success() {
        // Given
        List<ConnectorExecutor> connectors = new ArrayList<>();
        TestConnector testConnector = new TestConnector("EMAIL_163", "163邮箱连接器");
        connectors.add(testConnector);
        connectorRegistry.registerConnectors(connectors);

        // When
        ConnectorExecutor connector = connectorRegistry.getConnector("EMAIL_163");

        // Then
        assertNotNull(connector);
        assertEquals("EMAIL_163", connector.getConnectorType());
        assertEquals("163邮箱连接器", connector.getConnectorName());
    }

    @Test
    void testGetConnector_NotFound() {
        // Given
        List<ConnectorExecutor> connectors = new ArrayList<>();
        connectors.add(new TestConnector("EMAIL_163", "163邮箱连接器"));
        connectorRegistry.registerConnectors(connectors);

        // When & Then
        assertNotFoundException(() -> {
            connectorRegistry.getConnector("NOT_EXIST");
        });
    }

    @Test
    void testRegisterConnectors_EmptyList() {
        // Given
        List<ConnectorExecutor> connectors = new ArrayList<>();

        // When
        connectorRegistry.registerConnectors(connectors);

        // Then
        assertNotFoundException(() -> {
            connectorRegistry.getConnector("ANY_CONNECTOR");
        });
    }

    @Test
    void testRegisterConnectors_DuplicateType() {
        // Given
        List<ConnectorExecutor> connectors = new ArrayList<>();
        connectors.add(new TestConnector("EMAIL_163", "163邮箱连接器1"));
        connectors.add(new TestConnector("EMAIL_163", "163邮箱连接器2"));

        // When
        connectorRegistry.registerConnectors(connectors);

        // Then - 后注册的应该覆盖先注册的
        ConnectorExecutor connector = connectorRegistry.getConnector("EMAIL_163");
        assertEquals("163邮箱连接器2", connector.getConnectorName());
    }

    @Test
    void testGetConnector_AfterMultipleRegistrations() {
        // Given
        List<ConnectorExecutor> connectors1 = new ArrayList<>();
        connectors1.add(new TestConnector("EMAIL_163", "163邮箱连接器"));

        List<ConnectorExecutor> connectors2 = new ArrayList<>();
        connectors2.add(new TestConnector("SMS_ALI", "阿里云短信连接器"));

        // When
        connectorRegistry.registerConnectors(connectors1);
        connectorRegistry.registerConnectors(connectors2);

        // Then
        assertNotNull(connectorRegistry.getConnector("EMAIL_163"));
        assertNotNull(connectorRegistry.getConnector("SMS_ALI"));
    }

    /**
     * 测试用的简单连接器实现
     */
    private static class TestConnector implements ConnectorExecutor {
        private final String type;
        private final String name;

        TestConnector(String type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String getConnectorType() {
            return type;
        }

        @Override
        public String getConnectorName() {
            return name;
        }

        @Override
        public String getConnectorDescription() {
            return "测试连接器";
        }

        @Override
        public Map<String, Object> execute(String actionType, Map<String, Object> config) throws Exception {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "测试执行成功");
            return result;
        }

        @Override
        public boolean validateConfig(Map<String, Object> config) {
            return config != null;
        }
    }
}
