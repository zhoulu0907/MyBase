package com.cmsr.onebase.framework.remote.model.schedule;

/**
 * 调度定义创建/更新参数
 *
 * 对应 DolphinScheduler 3.3.1 的 /schedules 接口请求体。
 */
public class ScheduleDefineParam {

    private Schedule schedule;
    private String failureStrategy = "END";
    private String warningType = "NONE";
    private String processInstancePriority = "MEDIUM";
    private String warningGroupId = "0";
    private String workerGroup = "default";
    private String environmentCode = "";
    private Long workflowDefinitionCode;

    public static class Schedule {
        private String startTime;
        private String endTime;
        private String crontab;
        private String timezoneId = "Asia/Shanghai";

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public String getCrontab() { return crontab; }
        public void setCrontab(String crontab) { this.crontab = crontab; }
        public String getTimezoneId() { return timezoneId; }
        public void setTimezoneId(String timezoneId) { this.timezoneId = timezoneId; }
    }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    public String getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(String failureStrategy) { this.failureStrategy = failureStrategy; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public String getProcessInstancePriority() { return processInstancePriority; }
    public void setProcessInstancePriority(String processInstancePriority) { this.processInstancePriority = processInstancePriority; }
    public String getWarningGroupId() { return warningGroupId; }
    public void setWarningGroupId(String warningGroupId) { this.warningGroupId = warningGroupId; }
    public String getWorkerGroup() { return workerGroup; }
    public void setWorkerGroup(String workerGroup) { this.workerGroup = workerGroup; }
    public String getEnvironmentCode() { return environmentCode; }
    public void setEnvironmentCode(String environmentCode) { this.environmentCode = environmentCode; }
    public Long getWorkflowDefinitionCode() { return workflowDefinitionCode; }
    public void setWorkflowDefinitionCode(Long workflowDefinitionCode) { this.workflowDefinitionCode = workflowDefinitionCode; }
}

