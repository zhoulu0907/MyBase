package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_connector")
public class FlowConnectorDO extends BaseAppEntity {

    @Column(value = "connector_uuid")
    private String connectorUuid;

    @Column(value = "connector_name")
    private String connectorName;

    /**
     * 连接器类型编号
     * <p>
     * 系统预定义的连接器类型，对应 ConnectorExecutor.getConnectorType() 的返回值。
     * 用于 ConnectorRegistry 动态查找和执行连接器实现。
     * <p>
     * 常见值：EMAIL_163、SMS_ALI、DATABASE_MYSQL、HTTP 等
     */
    @Column(value = "type_code")
    private String typeCode;

    @Column(value = "description")
    private String description;

    /**
     * 环境配置UUID
     * <p>
     * 关联到 flow_connector_env.env_uuid
     * 用于引用该类型下的环境配置（URL、认证信息等）
     */
    @Column(value = "env_uuid")
    private String envUuid;

    /**
     * 连接器配置（JSON格式）
     * <p>
     * 存储连接器实例的补充配置参数
     * 环境通用配置（URL、认证）已通过 env_uuid 引用
     */
    @Column(value = "config")
    private String config;

    /**
     * 启用状态（0-禁用，1-启用）
     */
    @Column(value = "active_status")
    private Integer activeStatus;

}
