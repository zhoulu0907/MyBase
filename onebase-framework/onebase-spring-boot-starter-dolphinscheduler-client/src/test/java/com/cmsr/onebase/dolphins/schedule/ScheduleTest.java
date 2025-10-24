package com.cmsr.onebase.dolphins.schedule;

import com.cmsr.onebase.dolphins.BaseTest;
import java.util.List;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScheduleTest extends BaseTest {

  public static final Long WORKFLOW_CODE = 155151039079744L;

  /** the workflow must in online state */
  @Test
  @Order(1)
  public void testCreate() {
    ScheduleDefineParam scheduleDefineParam = new ScheduleDefineParam();
    scheduleDefineParam
        .setWorkflowDefinitionCode(WORKFLOW_CODE)
        .setSchedule(
            new ScheduleDefineParam.Schedule()
                .setStartTime("2026-10-27 00:00:00")
                .setEndTime("2029-09-20 00:00:00")
                .setCrontab("0 0 * * * ? *"));
    ScheduleInfoResp scheduleInfoResp =
        getClient().opsForSchedule().create(projectCode, scheduleDefineParam);
    System.out.println(scheduleInfoResp);
  }

  @Test
  @Order(2)
  public void testGetByProject() {
    List<ScheduleInfoResp> resp =
        getClient().opsForSchedule().getByWorkflowCode(projectCode, WORKFLOW_CODE);
    System.out.println(resp);
    Assertions.assertEquals(1, resp.size());
  }

  @Test
  @Order(3)
  public void testOnline() {
    List<ScheduleInfoResp> resp =
        getClient().opsForSchedule().getByWorkflowCode(projectCode, WORKFLOW_CODE);
    long id = resp.get(0).getId();
    Assertions.assertTrue(getClient().opsForSchedule().online(projectCode, id));
  }

  @Test
  @Order(4)
  public void testOffline() {
    List<ScheduleInfoResp> resp =
        getClient().opsForSchedule().getByWorkflowCode(projectCode, WORKFLOW_CODE);
    long id = resp.get(0).getId();
    Assertions.assertTrue(getClient().opsForSchedule().offline(projectCode, id));
  }

  @Test
  @Order(5)
  public void testDelete() {
    List<ScheduleInfoResp> resp =
        getClient().opsForSchedule().getByWorkflowCode(projectCode, WORKFLOW_CODE);
    long id = resp.get(0).getId();
    Assertions.assertTrue(getClient().opsForSchedule().delete(projectCode, id));
  }
}
