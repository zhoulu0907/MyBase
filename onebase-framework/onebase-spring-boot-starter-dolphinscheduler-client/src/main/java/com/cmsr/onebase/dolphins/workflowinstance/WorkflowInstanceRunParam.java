package com.cmsr.onebase.dolphins.workflowinstance;

import lombok.Data;
import lombok.experimental.Accessors;

/** re run/recover process instance */
@Data
@Accessors(chain = true)
public class WorkflowInstanceRunParam {

  private Long workflowInstanceId;

  private String executeType;
}
