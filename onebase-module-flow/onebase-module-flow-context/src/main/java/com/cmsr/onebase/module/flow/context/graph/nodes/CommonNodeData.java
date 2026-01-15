package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

/**
 * 通用节点数据模型
 * 基于双Map架构，支持动态配置的连接器节点
 *
 * @author zhoulu
 * @since 2025-01-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NodeType("common")
public class CommonNodeData extends NodeData implements Serializable {

    /**
     * 组件上下文配置（来自 flow_node_config.conn_config）
     * 包含节点特定的配置参数，如邮件收件人、短信内容等
     */
    private Map<String, Object> componentContext;

    /**
     * 连接器配置信息（来自 flow_connector.config）
     * 包含连接器的基础配置，如SMTP服务器、数据库连接等
     */
    private Map<String, Object> connectorConfig;

    /**
     * 动作配置信息（来自 flow_node_config.action_config）
     * 包含动作执行的相关配置，如模板类型、编码格式等
     */
    private Map<String, Object> actionConfig;

    /**
     * 连接器代码
     * 对应 flow_connector.code，如 EMAIL_163、SMS_ALI、DATABASE_MYSQL
     */
    private String connectorCode;

    /**
     * 动作类型
     * 对应 flow_node_config.action_config_type，如 EMAIL_SEND、SMS_SEND、DATABASE_QUERY
     */
    private String actionType;

    /**
     * 节点实例UUID
     * 用于唯一标识流程中的节点实例
     */
    private String instanceUuid;

    /**
     * 节点代码
     * 对应 flow_node_config.code，用于查询节点配置
     */
    private String nodeCode;
}