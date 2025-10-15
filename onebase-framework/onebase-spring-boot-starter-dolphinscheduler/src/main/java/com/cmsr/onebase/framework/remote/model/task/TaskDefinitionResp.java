package com.cmsr.onebase.framework.remote.model.task;

/**
 * 任务定义信息（简要）
 *
 * 对齐 DolphinScheduler 3.3.1 常见字段。
 *
 * @author matianyu
 * @date 2025-10-15
 */
public class TaskDefinitionResp {

    private Long code;
    private Integer version;
    private String name;
    private String description;
    private String taskType;
    private String flag;
    private String taskPriority;
    private String workerGroup;
    private String failRetryTimes;
    private String failRetryInterval;
    private Integer timeout;
    private Integer environmentCode;
    private String taskExecuteType;
    private Integer cpuQuota;
    private Long memoryMax;
    private String isCache;

    public Long getCode() { return code; }
    public void setCode(Long code) { this.code = code; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
    public String getTaskPriority() { return taskPriority; }
    public void setTaskPriority(String taskPriority) { this.taskPriority = taskPriority; }
    public String getWorkerGroup() { return workerGroup; }
    public void setWorkerGroup(String workerGroup) { this.workerGroup = workerGroup; }
    public String getFailRetryTimes() { return failRetryTimes; }
    public void setFailRetryTimes(String failRetryTimes) { this.failRetryTimes = failRetryTimes; }
    public String getFailRetryInterval() { return failRetryInterval; }
    public void setFailRetryInterval(String failRetryInterval) { this.failRetryInterval = failRetryInterval; }
    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
    public Integer getEnvironmentCode() { return environmentCode; }
    public void setEnvironmentCode(Integer environmentCode) { this.environmentCode = environmentCode; }
    public String getTaskExecuteType() { return taskExecuteType; }
    public void setTaskExecuteType(String taskExecuteType) { this.taskExecuteType = taskExecuteType; }
    public Integer getCpuQuota() { return cpuQuota; }
    public void setCpuQuota(Integer cpuQuota) { this.cpuQuota = cpuQuota; }
    public Long getMemoryMax() { return memoryMax; }
    public void setMemoryMax(Long memoryMax) { this.memoryMax = memoryMax; }
    public String getIsCache() { return isCache; }
    public void setIsCache(String isCache) { this.isCache = isCache; }
}

