package com.cmsr.onebase.framework.ds.model.task;

import lombok.Data;

@Data
public class TaskRelation {

    private String name = "";

    private Long preTaskCode = 0L;

    private Integer preTaskVersion = 0;

    private Long postTaskCode;

    private Integer postTaskVersion = 0;

    private Integer conditionType = 0;

    private Integer conditionParams;

    public static TaskRelation singleTask(Long taskCode) {
        TaskRelation taskRelation = new TaskRelation();
        taskRelation.setPostTaskCode(taskCode);

        return taskRelation;
    }
}
