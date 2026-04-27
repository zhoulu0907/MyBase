package com.cmsr.onebase.framework.ds.model.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowDetailedResp {
    private WorkflowDefinitionResp workflowDefinition;
}
