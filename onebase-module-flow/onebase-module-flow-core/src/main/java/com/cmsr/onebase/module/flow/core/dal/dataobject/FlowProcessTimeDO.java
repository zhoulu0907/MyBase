package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_time")
public class FlowProcessTimeDO extends BaseDO {

    @Column(name = "process_id", length = 19, nullable = false)
    private Long processId;

    @Column(name = "job_id", length = 64)
    private String jobId;

}