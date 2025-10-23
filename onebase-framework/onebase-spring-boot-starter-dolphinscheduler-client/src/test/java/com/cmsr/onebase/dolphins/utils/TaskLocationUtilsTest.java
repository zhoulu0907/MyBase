package com.cmsr.onebase.dolphins.utils;

import com.cmsr.onebase.dolphins.util.TaskLocationUtils;
import com.cmsr.onebase.dolphins.workflow.TaskLocation;
import java.util.List;
import org.junit.Test;

public class TaskLocationUtilsTest {

  @Test
  public void testHorizontalLocation() {
    List<TaskLocation> taskLocations = TaskLocationUtils.horizontalLocation(1L, 2L, 3L, 4L);
    System.out.println(taskLocations);
  }

  @Test
  public void testVerticalLocation() {
    List<TaskLocation> taskLocations = TaskLocationUtils.verticalLocation(1L, 2L, 3L, 4L);
    System.out.println(taskLocations);
  }
}
