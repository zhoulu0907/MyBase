package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskLocation {

    private Long taskCode;

    private int x = 100;

    private int y = 100;

    public static TaskLocation singleton(Long taskCode) {
        TaskLocation taskLocation = new TaskLocation();
        taskLocation.setTaskCode(taskCode);
        taskLocation.setX(100);
        taskLocation.setY(100);
        return taskLocation;
    }

    public TaskLocation() {
    }

    public TaskLocation(Long taskCode, int x, int y) {
        this.taskCode = taskCode;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }

    public static List<TaskLocation> horizontalLocation(Long... taskCodes) {
        int beginX = 100;
        int y = 100;
        int xStep = 300;
        return horizontalLocation(beginX, y, xStep, taskCodes);
    }

    public static List<TaskLocation> horizontalLocation(
            int beginX, int y, int xStep, Long... taskCodes) {
        List<TaskLocation> list = new ArrayList<>();
        for (Long taskCode : taskCodes) {
            list.add(new TaskLocation(taskCode, beginX, y));
            beginX += xStep;
        }
        return list;
    }
}
