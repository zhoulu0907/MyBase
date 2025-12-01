package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "flow_process_entity")
public class FlowProcessEntityDO extends BaseTenantEntity {

    @Column(value = "process_id")
    private Long processId;

}