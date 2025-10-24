package com.cmsr.onebase.dolphins.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cmsr.onebase.dolphins.BaseTest;
import com.cmsr.onebase.dolphins.enums.HttpCheckCondition;
import com.cmsr.onebase.dolphins.enums.HttpMethod;
import com.cmsr.onebase.dolphins.task.HttpTask;
import com.cmsr.onebase.dolphins.task.ShellTask;
import com.cmsr.onebase.dolphins.util.TaskDefinitionUtils;
import com.cmsr.onebase.dolphins.util.TaskLocationUtils;
import com.cmsr.onebase.dolphins.util.TaskRelationUtils;
import com.cmsr.onebase.dolphins.util.TaskUtils;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
/** the test for workflow/process */
public class WorkflowHttpTest extends BaseTest {

  public static final String WORKFLOW_NAME = "test-httptask-dag";

  /**
   * create simple workflow like: shellTask -> httpTask
   *
   * <p>1.generate task code
   *
   * <p>2.create tasks
   *
   * <p>3.create task definitions
   *
   * <p>4.create task relations
   *
   * <p>5.create process create parm
   *
   * <p>
   */
  @Test
  @Order(1)
  public void testCreateProcessDefinition() {

    List<Long> taskCodes = getClient().opsForProcess().generateTaskCode(projectCode, 2);

    // build shell task
    ShellTask shellTask = new ShellTask();
    shellTask.setRawScript("echo 'hello dolphin scheduler java sdk'");
    TaskDefinition shellTaskDefinition =
        TaskDefinitionUtils.createDefaultTaskDefinition(taskCodes.get(0), shellTask);

    // build http task
    HttpTask httpTask = new HttpTask();
    httpTask
        .setUrl("http://www.baidu.com")
        .setHttpMethod(HttpMethod.GET.toString())
        .setHttpCheckCondition(HttpCheckCondition.STATUS_CODE_DEFAULT.toString())
        .setCondition("")
        .setConditionResult(TaskUtils.createEmptyConditionResult());
    TaskDefinition httpTaskDefinition =
        TaskDefinitionUtils.createDefaultTaskDefinition(taskCodes.get(1), httpTask);

    ProcessDefineParam pcr = new ProcessDefineParam();
    pcr.setName(WORKFLOW_NAME)
        .setLocations(TaskLocationUtils.horizontalLocation(taskCodes.toArray(new Long[0])))
        .setDescription("test-dag-description")
        .setTenantCode(tenantCode)
        .setTimeout("0")
        .setExecutionType(ProcessDefineParam.EXECUTION_TYPE_PARALLEL)
        .setTaskDefinitionJson(Arrays.asList(shellTaskDefinition, httpTaskDefinition))
        .setTaskRelationJson(TaskRelationUtils.oneLineRelation(taskCodes.toArray(new Long[0])))
        .setGlobalParams(null);

    System.out.println(getClient().opsForProcess().create(projectCode, pcr));
  }

  @Test
  @Order(2)
  public void testPage() {
    List<ProcessDefineResp> page =
        getClient().opsForProcess().page(projectCode, null, null, WORKFLOW_NAME);
    int expectedWorkflowNumber = 1;
    assertEquals(expectedWorkflowNumber, page.size());
  }

  @Test
  @Order(3)
  public void testOnlineWorkflow() {
    List<ProcessDefineResp> page =
        getClient().opsForProcess().page(projectCode, null, null, WORKFLOW_NAME);
    assertTrue(getClient().opsForProcess().online(projectCode, page.get(0).getCode()));
  }

  @Test
  @Order(4)
  public void testOfflineWorkflow() {
    List<ProcessDefineResp> page =
        getClient().opsForProcess().page(projectCode, null, null, WORKFLOW_NAME);
    assertTrue(getClient().opsForProcess().offline(projectCode, page.get(0).getCode()));
  }

  /** the workflow must in offline state */
  @Test
  @Order(5)
  public void testDeleteWorkflow() {
    List<ProcessDefineResp> page =
        getClient().opsForProcess().page(projectCode, null, null, WORKFLOW_NAME);
    getClient().opsForProcess().offline(projectCode, page.get(0).getCode()); // 确保下线之后才能删除
    assertTrue(getClient().opsForProcess().delete(projectCode, page.get(0).getCode()));
  }
}
