package com.cmsr.onebase.framework.ds.model.workflow;

import lombok.Data;

@Data
public class WorkflowReleaseReq {

    public static final String ONLINE_STATE = "ONLINE";
    public static final String OFFLINE_STATE = "OFFLINE";

    private String name;

    private String releaseState;

    public static WorkflowReleaseReq newOnlineInstance() {
        WorkflowReleaseReq workflowReleaseReq = new WorkflowReleaseReq();
        workflowReleaseReq.setReleaseState(ONLINE_STATE);

        return workflowReleaseReq;
    }

    public static WorkflowReleaseReq newOfflineInstance() {
        WorkflowReleaseReq workflowReleaseReq = new WorkflowReleaseReq();
        workflowReleaseReq.setReleaseState(OFFLINE_STATE);

        return workflowReleaseReq;
    }
}
