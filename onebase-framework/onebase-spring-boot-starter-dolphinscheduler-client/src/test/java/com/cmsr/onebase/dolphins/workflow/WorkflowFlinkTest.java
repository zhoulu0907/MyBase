/**
 * Flink 工作流测试类，参考 WorkflowTest 和 FlinkTask json结构
 *
 * @author matianyu
 * @date 2025-10-24
 */
package com.cmsr.onebase.dolphins.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cmsr.onebase.dolphins.BaseTest;
import com.cmsr.onebase.dolphins.task.FlinkTask;
import com.cmsr.onebase.dolphins.task.TaskResource;
import com.cmsr.onebase.dolphins.util.TaskDefinitionUtils;
import com.cmsr.onebase.dolphins.util.TaskLocationUtils;
import com.cmsr.onebase.dolphins.util.TaskRelationUtils;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/** Flink 工作流测试 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowFlinkTest extends BaseTest {

  public static final String WORKFLOW_NAME = "test-flink-dag";

  /**
   * 创建 Flink 工作流
   *
   * <p>1. 生成任务 code
   *
   * <p>2. 创建 FlinkTask
   *
   * <p>3. 创建任务定义
   *
   * <p>4. 创建任务关系
   *
   * <p>5. 创建流程参数
   */
  @Test
  @Order(1)
  public void testCreateFlinkProcessDefinition() {
    List<Long> taskCodes = getClient().opsForProcess().generateTaskCode(projectCode, 1);

    // 构建 Flink 任务
    FlinkTask flinkTask = new FlinkTask();
    flinkTask
        .setMainClass("abc.de")
        .setMainJar(
            new TaskResource()
                .setResourceName(
                    "onebase-ds/default/resources/JavaProject.Flink-1.0.0-SNAPSHOT.jar"))
        .setDeployMode("cluster")
        .setProgramType("JAVA")
        .setFlinkVersion(">=1.12")
        .setJobManagerMemory("1G")
        .setTaskManagerMemory("2G")
        .setSlot(1)
        .setTaskManager(2)
        .setParallelism(1)
        .setInitScript("")
        .setRawScript("")
        .setYarnQueue("")
        .setLocalParams(Collections.emptyList())
        .setResourceList(Collections.emptyList());
    TaskDefinition flinkTaskDefinition =
        TaskDefinitionUtils.createDefaultTaskDefinition(taskCodes.get(0), flinkTask);

    ProcessDefineParam pcr = new ProcessDefineParam();
    pcr.setName(WORKFLOW_NAME)
        .setLocations(TaskLocationUtils.horizontalLocation(taskCodes.toArray(new Long[0])))
        .setDescription("test-flink-dag-description")
        .setTenantCode(tenantCode)
        .setTimeout("0")
        .setExecutionType(ProcessDefineParam.EXECUTION_TYPE_PARALLEL)
        .setTaskDefinitionJson(Collections.singletonList(flinkTaskDefinition))
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
    assertTrue(page.size()>=expectedWorkflowNumber);
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

  /** 工作流必须处于下线状态才能删除 */
  @Test
  @Order(5)
  public void testDeleteWorkflow() {
    List<ProcessDefineResp> page =
        getClient().opsForProcess().page(projectCode, null, null, WORKFLOW_NAME);
    getClient().opsForProcess().offline(projectCode, page.get(0).getCode());
    assertTrue(getClient().opsForProcess().delete(projectCode, page.get(0).getCode()));
  }
}
