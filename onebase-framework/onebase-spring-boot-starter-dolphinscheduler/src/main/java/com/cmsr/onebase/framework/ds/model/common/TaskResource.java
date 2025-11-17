package com.cmsr.onebase.framework.ds.model.common;

import lombok.Data;

@Data
public class TaskResource {

    private Long id;

    private String resourceName;

    public static TaskResource of(String resourceName) {
        TaskResource taskResource = new TaskResource();
        taskResource.setResourceName(resourceName);
        return taskResource;
    }
}
