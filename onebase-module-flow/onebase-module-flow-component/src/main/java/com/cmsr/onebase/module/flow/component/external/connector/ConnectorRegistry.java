package com.cmsr.onebase.module.flow.component.external.connector;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接器注册表
 * 实现连接器的动态发现和注册
 *
 * <p>核心特性：
 * <ul>
 *   <li>自动注册所有ConnectorExecutor实现类</li>
 *   <li>支持动态获取连接器</li>
 *   <li>线程安全（使用ConcurrentHashMap）</li>
 *   <li>启动时打印所有注册的连接器</li>
 * </ul>
 *
 * @author zhoulu
 * @since 2026-01-10
 */
@Slf4j
@Component
public class ConnectorRegistry {

    /**
     * 连接器注册表
     * Key: 连接器类型（如 EMAIL_163, SMS_ALI）
     * Value: 连接器实例
     */
    private final Map<String, ConnectorExecutor> connectors = new ConcurrentHashMap<>();

    /**
     * 自动注册所有连接器
     * Spring自动注入所有ConnectorExecutor实现类
     *
     * @param connectorList 所有连接器实现类列表
     */
    @Autowired
    public void registerConnectors(List<ConnectorExecutor> connectorList) {
        for (ConnectorExecutor connector : connectorList) {
            String connectorType = connector.getConnectorType();
            connectors.put(connectorType, connector);
            log.info("注册连接器: {} - {} - {}",
                    connectorType,
                    connector.getConnectorName(),
                    connector.getConnectorDescription());
        }
        log.info("连接器注册完成，共注册 {} 个连接器", connectors.size());
    }

    /**
     * 获取连接器
     *
     * @param connectorType 连接器类型
     * @return 连接器实例
     * @throws ConnectorNotFoundException 连接器不存在
     */
    public ConnectorExecutor getConnector(String connectorType) {
        ConnectorExecutor connector = connectors.get(connectorType);
        if (connector == null) {
            throw new ConnectorNotFoundException(connectorType);
        }
        return connector;
    }

    /**
     * 获取所有支持的连接器类型
     *
     * @return 连接器类型集合
     */
    public Set<String> getSupportedConnectorTypes() {
        return new HashSet<>(connectors.keySet());
    }

    /**
     * 检查连接器是否支持
     *
     * @param connectorType 连接器类型
     * @return true-支持，false-不支持
     */
    public boolean isSupported(String connectorType) {
        return connectors.containsKey(connectorType);
    }

    /**
     * 获取已注册连接器的数量
     *
     * @return 连接器数量
     */
    public int getConnectorCount() {
        return connectors.size();
    }
}
