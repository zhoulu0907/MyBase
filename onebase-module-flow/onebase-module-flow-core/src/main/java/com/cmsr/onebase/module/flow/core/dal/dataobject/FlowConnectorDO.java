package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "flow_connector")
public class FlowConnectorDO extends BaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "connector_name")
    private String connectorName;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "description")
    private String description;

    @Column(name = "config")
    private String config;

}
