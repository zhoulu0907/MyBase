package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.data.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_node_category")
public class FlowNodeCategoryDO extends BaseEntity {

    @Column(value = "level1_code")
    private String level1Code;

    @Column(value = "level1_name")
    private String level1Name;

    @Column(value = "level2_code")
    private String level2Code;

    @Column(value = "level2_name")
    private String level2Name;

    @Column(value = "level3_code")
    private String level3Code;

    @Column(value = "level3_name")
    private String level3Name;

    @Column(value = "sort_order")
    private Integer sortOrder;

}
