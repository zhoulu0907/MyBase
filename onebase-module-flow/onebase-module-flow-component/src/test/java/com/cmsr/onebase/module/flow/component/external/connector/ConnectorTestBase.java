package com.cmsr.onebase.module.flow.component.external.connector;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorExecutionException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 连接器测试基类
 * 提供通用的测试辅助方法
 */
public abstract class ConnectorTestBase {

    @Mock
    protected ConnectorExecutor mockConnector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 构建最小有效配置
     */
    protected Map<String, Object> buildMinimalConfig() {
        return new HashMap<>();
    }

    /**
     * 构建包含inputData的配置
     */
    protected Map<String, Object> buildConfigWithInputData(Map<String, Object> inputData) {
        Map<String, Object> config = new HashMap<>();
        config.put("inputData", inputData);
        return config;
    }

    /**
     * 断言连接器执行成功
     */
    protected void assertExecutionSuccess(Map<String, Object> result) {
        assertNotNull(result, "执行结果不应为null");
        assertTrue(result.containsKey("success"), "结果应包含success字段");
        assertTrue((Boolean) result.get("success"), "执行应该成功");
        assertTrue(result.containsKey("message"), "结果应包含message字段");
    }

    /**
     * 断言连接器执行失败
     */
    protected void assertExecutionFailure(Map<String, Object> result) {
        assertNotNull(result, "执行结果不应为null");
        assertTrue(result.containsKey("success"), "结果应包含success字段");
        assertFalse((Boolean) result.get("success"), "执行应该失败");
    }

    /**
     * 断言抛出配置异常
     */
    protected void assertConfigException(Runnable execution) {
        ConnectorConfigException exception = assertThrows(ConnectorConfigException.class, execution::run);
        assertNotNull(exception);
    }

    /**
     * 断言抛出执行异常
     */
    protected void assertExecutionException(Runnable execution) {
        ConnectorExecutionException exception = assertThrows(ConnectorExecutionException.class, execution::run);
        assertNotNull(exception);
    }

    /**
     * 断言抛出连接器未找到异常
     */
    protected void assertNotFoundException(Runnable execution) {
        ConnectorNotFoundException exception = assertThrows(ConnectorNotFoundException.class, execution::run);
        assertNotNull(exception);
    }

    /**
     * 验证异常消息包含指定文本
     */
    protected void assertExceptionMessageContains(Exception exception, String text) {
        assertTrue(exception.getMessage().contains(text),
                "异常消息应包含: " + text + ", 实际: " + exception.getMessage());
    }

    /**
     * 构建测试用的inputData
     */
    protected Map<String, Object> buildInputData(String key, Object value) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(key, value);
        return inputData;
    }

    /**
     * 构建测试用的inputData（多个键值对）
     */
    @SafeVarargs
    protected final Map<String, Object> buildInputData(Map.Entry<String, Object>... entries) {
        Map<String, Object> inputData = new HashMap<>();
        for (Map.Entry<String, Object> entry : entries) {
            inputData.put(entry.getKey(), entry.getValue());
        }
        return inputData;
    }
}
