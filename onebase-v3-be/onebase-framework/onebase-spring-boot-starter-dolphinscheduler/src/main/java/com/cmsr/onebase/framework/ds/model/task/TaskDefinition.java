package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.model.common.Parameter;
import com.cmsr.onebase.framework.ds.model.task.def.AbstractTask;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class TaskDefinition {

    private Integer id;

    private long code;

    private String name;

    private int version;

    private String description;

    private long projectCode;

    private int userId;

    private String taskType;

    private AbstractTask taskParams;

    private List<Parameter> taskParamList;

    private Map<String, String> taskParamMap;

    private String flag = "YES";

    private String taskPriority = "MEDIUM";

    private String userName;

    private String projectName;

    private String workerGroup = "default";

    private long environmentCode;

    private int failRetryTimes = 0;

    private int failRetryInterval;

    private String timeoutFlag = "CLOSE";

    private String timeoutNotifyStrategy = "";

    private int timeout = 0;

    private int delayTime = 0;

    private String taskExecuteType = "BATCH";

    private Integer cpuQuota = -1;

    private Integer memoryMax = -1;

    public static TaskDefinition singleton(AbstractTask task) {
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setTaskType(task.grantTaskType())
                .setTaskParams(task);

        return taskDefinition;
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}
