package com.cmsr.onebase.framework.ds.model.workflow;

import com.cmsr.onebase.framework.ds.model.common.Parameter;
import com.cmsr.onebase.framework.ds.model.schedule.ScheduleInfoResp;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowDefinitionResp {

    private int id;

    private long code;

    private String name;

    private int version;

    private String releaseState;

    private long projectCode;

    private String description;

    private String globalParams;

    private List<Parameter> globalParamList;

    private Map<String, String> globalParamMap;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String flag;

    private int userId;

    private String userName;

    private String projectName;

    private String locations;

    private String scheduleReleaseState;

    private int timeout;

    private int tenantId;

    private String tenantCode;

    private String modifyBy;

    private int warningGroupId;

    private String executionType;

    private ScheduleInfoResp schedule;

}
