package com.cmsr.onebase.dolphins.core;

import com.cmsr.onebase.dolphins.remote.DolphinsRestTemplate;
import com.cmsr.onebase.dolphins.schedule.ScheduleOperator;
import com.cmsr.onebase.dolphins.taskinstance.TaskInstanceOperator;
import com.cmsr.onebase.dolphins.workflow.ProcessOperator;
import com.cmsr.onebase.dolphins.workflowinstance.WorkflowInstanceOperator;
import lombok.extern.slf4j.Slf4j;

/** dolphin scheduler client to operate dolphin scheduler */
@Slf4j
public class DolphinClient {

  private final DolphinsRestTemplate dolphinsRestTemplate;
  private final String dolphinAddress;
  private final String token;

  private ProcessOperator processOperator;
  private WorkflowInstanceOperator workflowInstanceOperator;
  private ScheduleOperator scheduleOperator;
  private TaskInstanceOperator taskInstanceOperator;

  public DolphinClient(
      String token, String dolphinAddress, DolphinsRestTemplate dolphinsRestTemplate) {
    this.token = token;
    this.dolphinAddress = dolphinAddress;
    this.dolphinsRestTemplate = dolphinsRestTemplate;
    this.initOperators();
  }

  public void initOperators() {
    this.processOperator =
        new ProcessOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.workflowInstanceOperator =
        new WorkflowInstanceOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.scheduleOperator =
        new ScheduleOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.taskInstanceOperator =
        new TaskInstanceOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
  }

  public ProcessOperator opsForProcess() {
    return this.processOperator;
  }

  public WorkflowInstanceOperator opsForProcessInst() {
    return this.workflowInstanceOperator;
  }

  public ScheduleOperator opsForSchedule() {
    return this.scheduleOperator;
  }

  public TaskInstanceOperator opsForTaskInstance() {
    return this.taskInstanceOperator;
  }
}
