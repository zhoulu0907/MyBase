package com.cmsr.onebase.framework.ds.model.workflow;

import com.cmsr.onebase.framework.ds.model.common.Parameter;
import com.cmsr.onebase.framework.ds.model.task.TaskDefinition;
import com.cmsr.onebase.framework.ds.model.task.TaskLocation;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowDefinitionReq {

    public static final String EXECUTION_TYPE_SERIAL_WAIT = "SERIAL_WAIT";

    private String name;

    private List<TaskLocation> locations;

    private List<TaskDefinition> taskDefinitionJson;

    private String tenantCode;

    private String description;

    private String executionType;

    private List<Parameter> globalParams;

    private String timeout;

    public static WorkflowDefinitionReq newSerialWaitInstance() {
        WorkflowDefinitionReq workflowDefinitionReq = new WorkflowDefinitionReq();
        workflowDefinitionReq.setExecutionType(EXECUTION_TYPE_SERIAL_WAIT);

        return workflowDefinitionReq;
    }
}
