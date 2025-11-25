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

    @Column(value = "connector_name")
    private String connectorName;

    @Column(value = "type_code")
    private String typeCode;

    @Column(value = "description")
    private String description;

    @Column(value = "config")
    private String config;

}
