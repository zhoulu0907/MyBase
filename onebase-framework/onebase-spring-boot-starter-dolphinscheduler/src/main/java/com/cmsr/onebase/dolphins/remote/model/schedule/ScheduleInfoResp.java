package com.cmsr.onebase.dolphins.remote.model.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * 调度信息响应
 *
 * 参考 DolphinScheduler 3.3.1 字段含义。
 *
 * @author matianyu
 * @date 2025-10-15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleInfoResp {

    private int id;
    private long workflowDefinitionCode;
    private String workflowDefinitionName;
    private String projectName;
    private String definitionDescription;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private String timezoneId;
    private String crontab;
    private String failureStrategy;
    private String warningType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private int userId;
    private String userName;
    private String releaseState;
    private int warningGroupId;
    private String workflowInstancePriority;
    private String tenantCode;
    private String workerGroup;
    private Long environmentCode;
    private String environmentName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getWorkflowDefinitionCode() { return workflowDefinitionCode; }
    public void setWorkflowDefinitionCode(long workflowDefinitionCode) { this.workflowDefinitionCode = workflowDefinitionCode; }
    public String getWorkflowDefinitionName() { return workflowDefinitionName; }
    public void setWorkflowDefinitionName(String workflowDefinitionName) { this.workflowDefinitionName = workflowDefinitionName; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getDefinitionDescription() { return definitionDescription; }
    public void setDefinitionDescription(String definitionDescription) { this.definitionDescription = definitionDescription; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getTimezoneId() { return timezoneId; }
    public void setTimezoneId(String timezoneId) { this.timezoneId = timezoneId; }
    public String getCrontab() { return crontab; }
    public void setCrontab(String crontab) { this.crontab = crontab; }
    public String getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(String failureStrategy) { this.failureStrategy = failureStrategy; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getReleaseState() { return releaseState; }
    public void setReleaseState(String releaseState) { this.releaseState = releaseState; }
    public int getWarningGroupId() { return warningGroupId; }
    public void setWarningGroupId(int warningGroupId) { this.warningGroupId = warningGroupId; }
    public String getWorkflowInstancePriority() { return workflowInstancePriority; }
    public void setWorkflowInstancePriority(String workflowInstancePriority) { this.workflowInstancePriority = workflowInstancePriority; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public String getWorkerGroup() { return workerGroup; }
    public void setWorkerGroup(String workerGroup) { this.workerGroup = workerGroup; }
    public Long getEnvironmentCode() { return environmentCode; }
    public void setEnvironmentCode(Long environmentCode) { this.environmentCode = environmentCode; }
    public String getEnvironmentName() { return environmentName; }
    public void setEnvironmentName(String environmentName) { this.environmentName = environmentName; }
}

