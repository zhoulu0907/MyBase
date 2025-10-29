package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TaskRelation {

    private String name = "";

    private Long preTaskCode = 0L;

    private Integer preTaskVersion = 0;

    private Long postTaskCode;

    private Integer postTaskVersion = 0;

    private String conditionType = "NONE";

    private Map<String, Object> conditionParams = new HashMap<>();

    public static TaskRelation singleton(Long taskCode) {
        TaskRelation taskRelation = new TaskRelation();
        taskRelation.setPostTaskCode(taskCode);

        return taskRelation;
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}
