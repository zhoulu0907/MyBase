package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static List<TaskRelation> oneLineRelation(Long... taskCodes) {
        List<TaskRelation> list = new ArrayList<>();
        for (int i = 0; i < taskCodes.length; i++) {
            if (i == 0) {
                list.add(new TaskRelation().setPostTaskCode(taskCodes[i]));
            } else {
                list.add(new TaskRelation().setPreTaskCode(taskCodes[i - 1]).setPostTaskCode(taskCodes[i]));
            }
        }
        return list;
    }
}
