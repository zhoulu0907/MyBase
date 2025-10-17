package com.cmsr.onebase.framework.dolphins.client;

import com.cmsr.onebase.framework.dolphins.api.ProjectApi;
import com.cmsr.onebase.framework.dolphins.api.ScheduleApi;
import com.cmsr.onebase.framework.dolphins.api.TaskApi;
import com.cmsr.onebase.framework.dolphins.api.TaskInstanceApi;
import com.cmsr.onebase.framework.dolphins.api.WorkflowApi;
import com.cmsr.onebase.framework.dolphins.api.WorkflowInstanceApi;

import jakarta.validation.constraints.NotNull;

/**
 * DolphinScheduler 聚合客户端
 * <p>
 * 该客户端封装并暴露 ProjectApi、WorkflowApi、ScheduleApi、TaskApi、
 * WorkflowInstanceApi、TaskInstanceApi 六类接口，
 * 方便业务方通过单一 Bean 进行访问与管理。
 *
 * 使用示例：
 * <pre>
 *  {@code
 *   @Resource
 *   private DolphinSchedulerClient dolphins;
 *
 *   // 创建项目
 *   dolphins.project().createProject(req);
 *
 *   // 发布工作流
 *   dolphins.workflow().releaseWorkflowDefinition(projectCode, wfCode, 1);
 *  }
 * </pre>
 *
 * 说明：本类为轻量门面，默认仅提供各 API 的访问器；
 * 如需编排型便捷方法，建议在业务模块内基于本客户端二次封装。
 *
 * @author matianyu
 * @date 2025-10-17
 */
public class DolphinSchedulerClient {

    private final ProjectApi projectApi;
    private final WorkflowApi workflowApi;
    private final ScheduleApi scheduleApi;
    private final TaskApi taskApi;
    private final TaskInstanceApi taskInstanceApi;
    private final WorkflowInstanceApi workflowInstanceApi;

    /**
     * 构造方法
     *
     * @param projectApi Project 相关 API
     * @param workflowApi Workflow 定义相关 API
     * @param scheduleApi 调度计划相关 API
     * @param taskApi 任务定义相关 API
     * @param taskInstanceApi 任务实例相关 API
     * @param workflowInstanceApi 工作流实例相关 API
     */
    public DolphinSchedulerClient(
            @NotNull ProjectApi projectApi,
            @NotNull WorkflowApi workflowApi,
            @NotNull ScheduleApi scheduleApi,
            @NotNull TaskApi taskApi,
            @NotNull TaskInstanceApi taskInstanceApi,
            @NotNull WorkflowInstanceApi workflowInstanceApi) {
        this.projectApi = projectApi;
        this.workflowApi = workflowApi;
        this.scheduleApi = scheduleApi;
        this.taskApi = taskApi;
        this.taskInstanceApi = taskInstanceApi;
        this.workflowInstanceApi = workflowInstanceApi;
    }

    /**
     * 获取 Project 相关 API
     *
     * @return ProjectApi 实例
     */
    public ProjectApi project() {
        return projectApi;
    }

    /**
     * 获取 Workflow 定义相关 API
     *
     * @return WorkflowApi 实例
     */
    public WorkflowApi workflow() {
        return workflowApi;
    }

    /**
     * 获取 Schedule 调度计划相关 API
     *
     * @return ScheduleApi 实例
     */
    public ScheduleApi schedule() {
        return scheduleApi;
    }

    /**
     * 获取 Task 定义相关 API
     *
     * @return TaskApi 实例
     */
    public TaskApi task() {
        return taskApi;
    }

    /**
     * 获取 Task 实例相关 API
     *
     * @return TaskInstanceApi 实例
     */
    public TaskInstanceApi taskInstance() {
        return taskInstanceApi;
    }

    /**
     * 获取 Workflow 实例相关 API
     *
     * @return WorkflowInstanceApi 实例
     */
    public WorkflowInstanceApi workflowInstance() {
        return workflowInstanceApi;
    }
}

