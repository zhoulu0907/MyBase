package com.cmsr.onebase.dolphins.core;

import com.cmsr.onebase.dolphins.datasource.DataSourceOperator;
import com.cmsr.onebase.dolphins.project.ProjectOperator;
import com.cmsr.onebase.dolphins.remote.DolphinsRestTemplate;
import com.cmsr.onebase.dolphins.resource.ResourceOperator;
import com.cmsr.onebase.dolphins.schedule.ScheduleOperator;
import com.cmsr.onebase.dolphins.taskinstance.TaskInstanceOperator;
import com.cmsr.onebase.dolphins.tenant.TenantOperator;
import com.cmsr.onebase.dolphins.workflow.ProcessOperator;
import com.cmsr.onebase.dolphins.workflowinstance.WorkflowInstanceOperator;
import lombok.extern.slf4j.Slf4j;

/** dolphin scheduler client to operate dolphin scheduler */
@Slf4j
public class DolphinClient {

  private final DolphinsRestTemplate dolphinsRestTemplate;
  private final String dolphinAddress;
  private final String token;

  private DataSourceOperator dataSourceOperator;
  private ResourceOperator resourceOperator;
  private ProcessOperator processOperator;
  private WorkflowInstanceOperator workflowInstanceOperator;
  private ScheduleOperator scheduleOperator;
  private ProjectOperator projectOperator;
  private TenantOperator tenantOperator;
  private TaskInstanceOperator taskInstanceOperator;

  public DolphinClient(
      String token, String dolphinAddress, DolphinsRestTemplate dolphinsRestTemplate) {
    this.token = token;
    this.dolphinAddress = dolphinAddress;
    this.dolphinsRestTemplate = dolphinsRestTemplate;
    this.initOperators();
  }

  public void initOperators() {
    this.dataSourceOperator =
        new DataSourceOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.resourceOperator =
        new ResourceOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.processOperator =
        new ProcessOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.workflowInstanceOperator =
        new WorkflowInstanceOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.scheduleOperator =
        new ScheduleOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.projectOperator =
        new ProjectOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.taskInstanceOperator =
        new TaskInstanceOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
    this.tenantOperator =
        new TenantOperator(this.dolphinAddress, this.token, this.dolphinsRestTemplate);
  }

  public DataSourceOperator opsForDataSource() {
    return this.dataSourceOperator;
  }

  public ResourceOperator opsForResource() {
    return this.resourceOperator;
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

  public ProjectOperator opsForProject() {
    return this.projectOperator;
  }

  public TaskInstanceOperator opsForTaskInstance() {
    return this.taskInstanceOperator;
  }

  public TenantOperator opsForTenant() {
    return this.tenantOperator;
  }
}
