package com.cmsr.onebase.framework.remote.model.instance;

/**
 * 流程实例创建参数
 *
 * 对应 DolphinScheduler 3.3.1 启动流程实例接口的常用表单字段。
 */
public class ProcessInstanceCreateParam {

    /** 失败策略：CONTINUE/END */
    private String failureStrategy;

    /** 流程定义编码 */
    private Long processDefinitionCode;

    /** 实例优先级：HIGHEST/HIGH/MEDIUM/LOW/LOWEST */
    private String processInstancePriority;

    /** 调度时间：yyyy-MM-dd HH:mm:ss */
    private String scheduleTime;

    /** 报警组ID */
    private Long warningGroupId;

    /** 报警类型：NONE/SUCCESS/FAILURE/ALL */
    private String warningType;

    /** 0/1 是否空跑 */
    private Integer dryRun;

    /** 运行环境编码 */
    private String environmentCode;

    /** 执行类型：START_PROCESS、REPEAT_RUNNING 等 */
    private String execType;

    /** 期望并行度 */
    private String expectedParallelismNumber;

    /** 运行模式：RUN_MODE_SERIAL/RUN_MODE_PARALLEL */
    private String runMode;

    /** 启动节点列表（JSON 字符串） */
    private String startNodeList;

    /** 启动参数（JSON 字符串） */
    private String startParams;

    /** 任务依赖类型 */
    private String taskDependType;

    /** worker 分组 */
    private String workerGroup;

    public String getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(String failureStrategy) { this.failureStrategy = failureStrategy; }
    public Long getProcessDefinitionCode() { return processDefinitionCode; }
    public void setProcessDefinitionCode(Long processDefinitionCode) { this.processDefinitionCode = processDefinitionCode; }
    public String getProcessInstancePriority() { return processInstancePriority; }
    public void setProcessInstancePriority(String processInstancePriority) { this.processInstancePriority = processInstancePriority; }
    public String getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(String scheduleTime) { this.scheduleTime = scheduleTime; }
    public Long getWarningGroupId() { return warningGroupId; }
    public void setWarningGroupId(Long warningGroupId) { this.warningGroupId = warningGroupId; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public Integer getDryRun() { return dryRun; }
    public void setDryRun(Integer dryRun) { this.dryRun = dryRun; }
    public String getEnvironmentCode() { return environmentCode; }
    public void setEnvironmentCode(String environmentCode) { this.environmentCode = environmentCode; }
    public String getExecType() { return execType; }
    public void setExecType(String execType) { this.execType = execType; }
    public String getExpectedParallelismNumber() { return expectedParallelismNumber; }
    public void setExpectedParallelismNumber(String expectedParallelismNumber) { this.expectedParallelismNumber = expectedParallelismNumber; }
    public String getRunMode() { return runMode; }
    public void setRunMode(String runMode) { this.runMode = runMode; }
    public String getStartNodeList() { return startNodeList; }
    public void setStartNodeList(String startNodeList) { this.startNodeList = startNodeList; }
    public String getStartParams() { return startParams; }
    public void setStartParams(String startParams) { this.startParams = startParams; }
    public String getTaskDependType() { return taskDependType; }
    public void setTaskDependType(String taskDependType) { this.taskDependType = taskDependType; }
    public String getWorkerGroup() { return workerGroup; }
    public void setWorkerGroup(String workerGroup) { this.workerGroup = workerGroup; }
}

