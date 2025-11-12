package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_date_field")
public class FlowProcessDateFieldDO extends BaseDO {


    @Column(name = "process_id", length = 19, nullable = false)
    private Long processId;

    @Column(name = "entity_id", length = 19, nullable = false)
    private Long entityId;

    @Column(name = "job_id", length = 64)
    private String jobId;

    @Column(name = "job_status", length = 19)
    private String jobStatus;
}