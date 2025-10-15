package com.cmsr.onebase.framework.remote.model.instance;

/**
 * 流程实例查询响应
 */
public class ProcessInstanceQueryResp {
    private Long id;
    private Long processDefinitionCode;
    private String processDefinitionName;
    private String state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessDefinitionCode() {
        return processDefinitionCode;
    }

    public void setProcessDefinitionCode(Long processDefinitionCode) {
        this.processDefinitionCode = processDefinitionCode;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

