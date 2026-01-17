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
     * 连接器业务编号
     * <p>
     * 用户自定义的连接器编号，用于业务层识别和查找。
     * 与 type_code 的区别：
     * <ul>
     *   <li>code：用户自定义的业务编号，如 "email-notification"、"sms-marketing"</li>
     *   <li>type_code：系统连接器类型，对应具体实现类，如 "EMAIL_163"、"SMS_ALI"、"HTTP"</li>
     * </ul>
     * <p>
     * 业务场景：用户可以为同一个连接器类型创建多个实例，每个实例有独立的业务编号。
     * 例如：创建多个163邮件连接器，分别用于通知、营销等不同场景。
     */
    @Column(value = "code")
    private String code;

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

    @Column(value = "config")
    private String config;

    @Column(value = "config_json")
    private String configJson;

}
