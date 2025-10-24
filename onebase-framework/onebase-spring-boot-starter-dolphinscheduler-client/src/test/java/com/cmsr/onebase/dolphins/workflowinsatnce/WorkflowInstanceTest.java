package com.cmsr.onebase.dolphins.workflowinsatnce;

import com.cmsr.onebase.dolphins.BaseTest;
import com.cmsr.onebase.dolphins.enums.*;
import com.cmsr.onebase.dolphins.workflowinstance.WorkflowInstanceCreateParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowInstanceTest extends BaseTest {

  public static final Long WORK_FLOW_DEFINITION_CODE = 155488152079744L;
  public static final Long WORK_FLOW_INSTANCEID = 163L;

  /** the workflow must in online state,otherwise will cause error */
  @Test
  public void testStartInstance() {

    WorkflowInstanceCreateParam startParam = new WorkflowInstanceCreateParam();
    startParam
        .setWorkflowDefinitionCode(WORK_FLOW_DEFINITION_CODE)
        .setScheduleTime("")
        .setFailureStrategy(FailureStrategy.CONTINUE.toString())
        .setWarningType(WarningType.NONE.toString())
        .setWarningGroupId(0L)
        .setExecType("")
        .setStartNodeList("")
        .setTaskDependType(TaskDependType.TASK_POST.toString())
        .setRunMode(RunMode.RUN_MODE_SERIAL.toString())
        .setWorkflowInstancePriority(Priority.MEDIUM.toString())
        .setWorkerGroup("default")
        .setEnvironmentCode("")
        .setStartParams("")
        .setExpectedParallelismNumber("")
        .setDryRun(0);
    Assertions.assertTrue(getClient().opsForProcessInst().start(projectCode, startParam));
  }

  @Test
  public void testReRun() {
    Assertions.assertTrue(getClient().opsForProcessInst().reRun(projectCode, WORK_FLOW_INSTANCEID));
  }

  @Test
  public void testPage() {
    getClient()
        .opsForProcessInst()
        .page(null, null, projectCode, WORK_FLOW_DEFINITION_CODE)
        .forEach(System.out::println);
  }

  @Test
  public void testDelete() {
    Assertions.assertTrue(getClient().opsForProcessInst().delete(projectCode, WORK_FLOW_INSTANCEID));
  }
}
