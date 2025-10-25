package com.cmsr.onebase.framework.ds.model.task;

import lombok.Data;

@Data
public class TaskLocation {

    private Long taskCode;

    private int x;

    private int y;

    public static TaskLocation singleTask(Long taskCode) {
        TaskLocation taskLocation = new TaskLocation();
        taskLocation.setTaskCode(taskCode);
        taskLocation.setX(100);
        taskLocation.setY(100);
        return taskLocation;
    }
}
