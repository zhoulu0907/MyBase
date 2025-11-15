package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskLocation {

    private Long taskCode;

    private int x;

    private int y;

    public static TaskLocation singleton(Long taskCode) {
        TaskLocation taskLocation = new TaskLocation();
        taskLocation.setTaskCode(taskCode);
        taskLocation.setX(100);
        taskLocation.setY(100);
        return taskLocation;
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
