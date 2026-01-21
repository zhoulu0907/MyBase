package com.cmsr.onebase.module.flow.component.external.connector.exception;

/**
 * 连接器配置异常
 * 当连接器配置无效或缺少必要配置时抛出
 *
 * @author zhoulu
 * @since 2026-01-10
 */
public class ConnectorConfigException extends ConnectorException {

    /**
     * 构造函数
     *
     * @param connectorType 连接器类型
     * @param message 异常消息
     */
    public ConnectorConfigException(String connectorType, String message) {
        super(connectorType, null, message);
    }

    /**
     * 构造函数（带原因）
     *
     * @param connectorType 连接器类型
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ConnectorConfigException(String connectorType, String message, Throwable cause) {
        super(connectorType, null, message, cause);
    }
}
