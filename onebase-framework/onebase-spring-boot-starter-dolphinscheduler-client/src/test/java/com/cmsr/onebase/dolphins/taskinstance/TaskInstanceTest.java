package com.cmsr.onebase.dolphins.taskinstance;

import com.cmsr.onebase.dolphins.BaseTest;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskInstanceTest extends BaseTest {

  @Test
  @Order(1)
  public void testPage() {
    Long processInstanceId = 1L;
    List<TaskInstanceQueryResp> taskInstanceQueryResps =
        getClient().opsForTaskInstance().page(projectCode, null, null, processInstanceId);

    taskInstanceQueryResps.forEach(System.out::println);
  }

  @Test
  @Order(2)
  public void testQueryLog() {
    Long taskInstanceId = 1L;
    String log = getClient().opsForTaskInstance().queryLog(projectCode, null, null, taskInstanceId);

    System.out.println(log);
  }
}
