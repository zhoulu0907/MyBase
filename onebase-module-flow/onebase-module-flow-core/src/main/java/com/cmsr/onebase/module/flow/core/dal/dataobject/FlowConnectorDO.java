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
     * 连接器配置（JSON格式）
     * <p>
     * 存储连接器的配置参数，如SMTP服务器、数据库连接等
     */
    @Column(value = "config")
    private String config;

}
