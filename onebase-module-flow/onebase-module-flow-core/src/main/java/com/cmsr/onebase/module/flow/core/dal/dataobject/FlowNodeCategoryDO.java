package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "flow_node_category")
public class FlowNodeCategoryDO extends BaseDO {

    @Column(name = "level1_code")
    private String level1Code;

    @Column(name = "level1_name")
    private String level1Name;

    @Column(name = "level2_code")
    private String level2Code;

    @Column(name = "level2_name")
    private String level2Name;

    @Column(name = "level3_code")
    private String level3Code;

    @Column(name = "level3_name")
    private String level3Name;

    @Column(name = "sort_order")
    private Integer sortOrder;

}
