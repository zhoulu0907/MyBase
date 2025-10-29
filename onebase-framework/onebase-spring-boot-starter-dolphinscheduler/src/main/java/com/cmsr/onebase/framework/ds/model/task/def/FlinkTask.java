package com.cmsr.onebase.framework.ds.model.task.def;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlinkTask extends AbstractTask {

    private String initScript;
    private String rawScript;
    private String programType;
    private String mainClass;
    private String deployMode;
    private String yarnQueue;
    private String flinkVersion;
    private String jobManagerMemory;
    private String taskManagerMemory;
    private Integer slot;
    private Integer taskManager;
    private Integer parallelism;

    @Override
    public String grantTaskType() {
        return "FLINK";
    }
}
