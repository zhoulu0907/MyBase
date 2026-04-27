package com.cmsr.onebase.framework.ds.model.task.def;

import com.cmsr.onebase.framework.ds.model.common.TaskResource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlinkTask extends AbstractTask {

    private String initScript = "";
    private String rawScript = "";
    private String programType;
    private String mainClass;
    private TaskResource mainJar;
    private String deployMode;
    private String yarnQueue = "";
    private String flinkVersion = ">=1.12";
    private String jobManagerMemory = "1G";
    private String taskManagerMemory = "2G";
    private Integer slot;
    private Integer taskManager;
    private Integer parallelism;

    @Override
    public String grantTaskType() {
        return "FLINK";
    }

    public void setMainJar(String mainJar) {
        TaskResource resourceJar = new TaskResource();
        resourceJar.setResourceName(mainJar);
        this.mainJar = resourceJar;
    }
}
