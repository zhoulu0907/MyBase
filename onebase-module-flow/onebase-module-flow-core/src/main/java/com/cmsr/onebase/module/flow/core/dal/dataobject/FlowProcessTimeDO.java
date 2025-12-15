package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "flow_process_time")
public class FlowProcessTimeDO extends BaseAppEntity {

    @Column(value = "process_id")
    private Long processId;

    @Column(value = "job_id")
    private String jobId;

    @Column(value = "job_status")
    private String jobStatus;
}