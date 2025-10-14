package com.cmsr.onebase.dolphins.remote.model.taskinstance;

/**
 * 任务实例查询响应
 */
public class TaskInstanceQueryResp {
    private Long id;
    private Long processInstanceId;
    private String name;
    private String state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

