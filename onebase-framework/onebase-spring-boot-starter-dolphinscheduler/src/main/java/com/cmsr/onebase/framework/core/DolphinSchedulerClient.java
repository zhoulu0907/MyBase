package com.cmsr.onebase.framework.core;

import com.cmsr.onebase.framework.remote.InstanceApi;
import com.cmsr.onebase.framework.remote.ScheduleApi;
import com.cmsr.onebase.framework.remote.TaskApi;
import com.cmsr.onebase.framework.remote.TaskInstanceApi;
import com.cmsr.onebase.framework.remote.WorkflowApi;
import jakarta.annotation.Resource;

/**
 * DolphinScheduler 客户端门面，聚合各 OpenAPI 接口
 *
 * @author matianyu
 * @date 2025-10-15
 */
public class DolphinSchedulerClient {

    @Resource
    private WorkflowApi workflowApi;
    @Resource
    private InstanceApi instanceApi;
    @Resource
    private ScheduleApi scheduleApi;
    @Resource
    private TaskInstanceApi taskInstanceApi;
    @Resource
    private TaskApi taskApi;

    /**
     * 获取工作流 API
     *
     * @return WorkflowApi
     */
    public WorkflowApi workflow() {
        return workflowApi;
    }

    /**
     * 获取流程实例 API
     *
     * @return InstanceApi
     */
    public InstanceApi instance() {
        return instanceApi;
    }

    /**
     * 获取调度 API
     *
     * @return ScheduleApi
     */
    public ScheduleApi schedule() {
        return scheduleApi;
    }

    /**
     * 获取任务实例 API
     *
     * @return TaskInstanceApi
     */
    public TaskInstanceApi taskInstance() { return taskInstanceApi; }

    /**
     * 获取任务定义相关 API
     *
     * @return TaskApi
     */
    public TaskApi task() {
        return taskApi;
    }

}
