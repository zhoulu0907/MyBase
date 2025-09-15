package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_time")
public class FlowProcessTimeDO extends TenantBaseDO {

    @Column(name = "process_id", length = 19, nullable = false)
    private Long processId;

    @Column(name = "job_id", length = 64)
    private String jobId;

}