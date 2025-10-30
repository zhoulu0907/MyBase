package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;

@Data
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
}
