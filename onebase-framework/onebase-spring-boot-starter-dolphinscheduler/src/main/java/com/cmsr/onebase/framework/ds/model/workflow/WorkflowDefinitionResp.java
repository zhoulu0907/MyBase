package com.cmsr.onebase.framework.ds.model.workflow;

import com.cmsr.onebase.framework.ds.model.common.Parameter;
import com.cmsr.onebase.framework.ds.model.schedule.ScheduleInfoResp;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowDefinitionResp {

    private int id;

    private long code;

    private String name;

    private int version = 0;

    private String releaseState;

    private long projectCode;

    private String description;

    private String globalParams;

    private List<Parameter> globalParamList;

    private Map<String, String> globalParamMap;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
