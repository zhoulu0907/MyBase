package com.cmsr.onebase.module.flow.component.external.connector;

import java.util.Map;

/**
 * 连接器执行器接口
 * 定义了所有连接器必须实现的核心方法
 *
 * @author zhoulu
 * @since 2025-01-10
 */
public interface ConnectorExecutor {

    /**
     * 执行连接器逻辑（简洁版本）
     *
     * @param actionType 动作类型（如 EMAIL_SEND、SMS_SEND、DATABASE_QUERY）
     * @param config 合并后的配置，包含 connectorConfig、actionConfig、inputData
     * @return 执行结果
     * @throws Exception 执行异常
     */
    Map<String, Object> execute(String actionType, Map<String, Object> config) throws Exception;

    /**
     * 获取连接器类型
     *
     * @return 连接器类型，对应 flow_connector.type_code
     */
    String getConnectorType();

    /**
     * 验证连接器配置是否有效
     *
     * @param config 连接器配置
     * @return 是否有效
     */
    boolean validateConfig(Map<String, Object> config);

    /**
     * 获取连接器名称
     *
     * @return 连接器名称
     */
    default String getConnectorName() {
        return getConnectorType();
    }

    /**
     * 获取连接器描述
     *
     * @return 连接器描述
     */
    default String getConnectorDescription() {
        return "通用连接器：" + getConnectorType();
    }

    /**
     * 获取连接器版本
     *
     * @return 连接器版本
     */
    default String getConnectorVersion() {
        return "1.0.0";
    }
}
