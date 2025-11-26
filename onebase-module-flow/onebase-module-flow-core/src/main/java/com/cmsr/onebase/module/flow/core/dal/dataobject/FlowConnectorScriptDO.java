package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_connector_script")
public class FlowConnectorScriptDO extends BaseAppEntity {

    @Column(value = "connector_id")
    private Long connectorId;

    @Column(value = "script_name")
    private String scriptName;

    @Column(value = "script_type")
    private String scriptType;

    @Column(value = "description")
    private String description;

    @Column(value = "raw_script")
    private String rawScript;

    @Column(value = "input_parameter")
    private String inputParameter;

    @Column(value = "output_parameter")
    private String outputParameter;

}
