/**
 * Flink 任务参数模型，参考 DolphinScheduler FlinkTask json结构
 *
 * @author matianyu
 * @date 2025-10-24
 */
package com.cmsr.onebase.dolphins.task;

import com.cmsr.onebase.dolphins.workflow.Parameter;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class FlinkTask extends AbstractTask {
    /** 主类名 */
    private String mainClass;
    /** 主 jar 包资源 */
    private TaskResource mainJar;
    /** 运行模式，可选值: cluster, client */
    private String deployMode;
    /** 程序类型，可选值: JAVA, SCALA, PYTHON */
    private String programType;
    /** Flink 版本，如 <1.10 */
    private String flinkVersion;
    /** JobManager 内存配置，如 1G */
    private String jobManagerMemory;
    /** TaskManager 内存配置，如 2G */
    private String taskManagerMemory;
    /** slot 数量 */
    private Integer slot;
    /** TaskManager 数量 */
    private Integer taskManager;
    /** 并行度 */
    private Integer parallelism;
    /** 初始化脚本 */
    private String initScript;
    /** Flink SQL 脚本 */
    private String rawScript;
    /** Yarn 队列 */
    private String yarnQueue;
    /** 本地参数列表 */
    private List<Parameter> localParams = Collections.emptyList();
    /** 资源列表 */
    private List<TaskResource> resourceList = Collections.emptyList();

    /**
     * 获取任务类型
     *
     * @return 任务类型 FLINK
     */
    @Override
    public String getTaskType() {
        return "FLINK";
    }
}

