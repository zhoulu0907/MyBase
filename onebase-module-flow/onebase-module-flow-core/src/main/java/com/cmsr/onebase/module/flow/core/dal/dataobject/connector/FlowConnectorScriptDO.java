package com.cmsr.onebase.module.flow.core.dal.dataobject.connector;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "flow_connector_script")
public class FlowConnectorScriptDO extends BaseDO {

    @Column(name = "connector_id")
    private Long connectorId;

    @Column(name = "script_name")
    private String scriptName;

    @Column(name = "script_type")
    private String scriptType;

    @Column(name = "description")
    private String description;

    @Column(name = "raw_script")
    private String rawScript;

    @Column(name = "input_parameter")
    private String inputParameter;

    @Column(name = "output_parameter")
    private String outputParameter;

}
