package com.cmsr.onebase.framework.ds.model.schedule;

import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import lombok.Data;

@Data
public class ScheduleInfoReq {

    private Long id;

    private Schedule schedule;

    private String failureStrategy;

    private String warningType;

    private String workflowInstancePriority;

    private String warningGroupId;

    private String workerGroup;

    private String tenantCode;

    private String environmentCode;

    private Long workflowDefinitionCode;
}
