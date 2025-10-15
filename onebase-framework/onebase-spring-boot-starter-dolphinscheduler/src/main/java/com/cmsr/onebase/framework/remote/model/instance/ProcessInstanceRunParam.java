package com.cmsr.onebase.framework.remote.model.instance;

/**
 * 流程实例执行参数（重跑/暂停/停止等）
 */
public class ProcessInstanceRunParam {

    /** 流程实例ID */
    private Long processInstanceId;

    /** 执行类型：RE_RUN/STOP/PAUSE 等 */
    private String executeType;

    public Long getProcessInstanceId() { return processInstanceId; }
    public ProcessInstanceRunParam setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public String getExecuteType() { return executeType; }
    public ProcessInstanceRunParam setExecuteType(String executeType) {
        this.executeType = executeType;
        return this;
    }
}

