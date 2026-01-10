package com.cmsr.onebase.module.flow.component.external.connector.exception;

import lombok.Getter;

/**
 * 连接器异常基类
 * 统一的异常处理模式
 *
 * <p>所有连接器相关异常的基类，提供统一的异常信息格式
 *
 * @author zhoulu
 * @since 2026-01-10
 */
@Getter
public class ConnectorException extends RuntimeException {

    /**
     * 连接器类型
     */
    private final String connectorType;

    /**
     * 动作类型
     */
    private final String actionType;

    /**
     * 构造函数
     *
     * @param connectorType 连接器类型
     * @param actionType 动作类型
     * @param message 异常消息
     */
    public ConnectorException(String connectorType, String actionType, String message) {
        super(message);
        this.connectorType = connectorType;
        this.actionType = actionType;
    }

    /**
     * 构造函数（带原因）
     *
     * @param connectorType 连接器类型
     * @param actionType 动作类型
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ConnectorException(String connectorType, String actionType, String message, Throwable cause) {
        super(message, cause);
        this.connectorType = connectorType;
        this.actionType = actionType;
    }

    /**
     * 获取完整的错误信息
     * 包含连接器类型、动作类型和错误消息
     *
     * @return 完整错误信息
     */
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("连接器错误[");

        if (connectorType != null) {
            sb.append("type=").append(connectorType);
        }

        if (actionType != null) {
            if (connectorType != null) {
                sb.append(", ");
            }
            sb.append("action=").append(actionType);
        }

        sb.append("]: ");
        sb.append(getMessage());

        return sb.toString();
    }
}
