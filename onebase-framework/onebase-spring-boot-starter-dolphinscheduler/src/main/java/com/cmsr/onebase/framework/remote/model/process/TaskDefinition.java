package com.cmsr.onebase.framework.remote.model.process;

/**
 * 任务定义 DTO（仅数据传输，无业务逻辑）
 */
public class TaskDefinition {

    private Long code;
    private Integer version;
    /** 任务节点名称 */
    private String name;
    /** 任务节点描述 */
    private String description;
    /** 任务类型，如 SHELL/SQL/HTTP 等 */
    private String taskType;
    /** 任务参数（不同任务类型结构不同，保持为 Object 以兼容） */
    private Object taskParams;
    /** 节点是否执行：YES/NO */
    private String flag;
    private String taskPriority;
    private String workerGroup;
    private String failRetryTimes;
    private String failRetryInterval;
    private String timeoutFlag;
    private String timeoutNotifyStrategy;
    private Integer timeout = 0;
    private String delayTime = "0";
    private Integer environmentCode = -1;
    private String taskExecuteType;
    private Integer cpuQuota = -1;
    private Long memoryMax = -1L;
    /** 缓存：YES/NO */
    private String isCache = "NO";

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
    public Object getTaskParams() { return taskParams; }
    public void setTaskParams(Object taskParams) { this.taskParams = taskParams; }
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
    public String getTimeoutFlag() { return timeoutFlag; }
    public void setTimeoutFlag(String timeoutFlag) { this.timeoutFlag = timeoutFlag; }
    public String getTimeoutNotifyStrategy() { return timeoutNotifyStrategy; }
    public void setTimeoutNotifyStrategy(String timeoutNotifyStrategy) { this.timeoutNotifyStrategy = timeoutNotifyStrategy; }
    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
    public String getDelayTime() { return delayTime; }
    public void setDelayTime(String delayTime) { this.delayTime = delayTime; }
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

