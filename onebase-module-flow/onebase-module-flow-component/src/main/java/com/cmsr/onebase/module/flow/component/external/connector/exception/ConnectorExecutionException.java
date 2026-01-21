package com.cmsr.onebase.module.flow.component.external.connector.exception;

/**
 * 连接器执行异常
 * 当连接器执行过程中发生错误时抛出
 *
 * @author zhoulu
 * @since 2026-01-10
 */
public class ConnectorExecutionException extends ConnectorException {

    /**
     * 构造函数
     *
     * @param connectorType 连接器类型
     * @param actionType 动作类型
     * @param message 异常消息
     */
    public ConnectorExecutionException(String connectorType, String actionType, String message) {
        super(connectorType, actionType, message);
    }

    /**
     * 构造函数（带原因）
     *
     * @param connectorType 连接器类型
     * @param actionType 动作类型
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ConnectorExecutionException(String connectorType, String actionType, String message, Throwable cause) {
        super(connectorType, actionType, message, cause);
    }
}
