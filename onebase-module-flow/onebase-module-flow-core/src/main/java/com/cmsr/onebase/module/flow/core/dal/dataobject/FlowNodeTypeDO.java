package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "flow_node_type")
public class FlowNodeTypeDO extends BaseDO {

    @Column(name = "level1_code")
    private String level1Code;

    @Column(name = "level2_code")
    private String level2Code;

    @Column(name = "level3_code")
    private String level3Code;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "active_status")
    private Integer activeStatus;

    @Column(name = "default_properties")
    private String defaultProperties;

    @Column(name = "sort_order")
    private Integer sortOrder;

}
