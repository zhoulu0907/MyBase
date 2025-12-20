package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_node_type")
public class FlowNodeTypeDO extends BaseEntity {

    @Column(value = "level1_code")
    private String level1Code;

    @Column(value = "level2_code")
    private String level2Code;

    @Column(value = "level3_code")
    private String level3Code;

    @Column(value = "type_name")
    private String typeName;

    @Column(value = "type_code")
    private String typeCode;

    @Column(value = "simple_remark")
    private String simpleRemark;

    @Column(value = "detail_description")
    private String detailDescription;

    @Column(value = "active_status")
    private Integer activeStatus;

    @Column(value = "default_properties")
    private String defaultProperties;

    @Column(value = "sort_order")
    private Integer sortOrder;

    @Column(value = "config_type")
    private String configType;

    @Column(value = "form_config")
    private String formConfig;

}
