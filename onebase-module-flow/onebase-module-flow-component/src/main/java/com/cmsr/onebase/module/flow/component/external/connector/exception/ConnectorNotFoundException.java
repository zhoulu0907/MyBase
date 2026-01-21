package com.cmsr.onebase.module.flow.component.external.connector.exception;

/**
 * 连接器未找到异常
 * 当请求的连接器类型不存在时抛出
 *
 * @author zhoulu
 * @since 2026-01-10
 */
public class ConnectorNotFoundException extends ConnectorException {

    /**
     * 构造函数
     *
     * @param connectorType 连接器类型
     */
    public ConnectorNotFoundException(String connectorType) {
        super(connectorType, null, String.format("未找到连接器: %s", connectorType));
    }
}
