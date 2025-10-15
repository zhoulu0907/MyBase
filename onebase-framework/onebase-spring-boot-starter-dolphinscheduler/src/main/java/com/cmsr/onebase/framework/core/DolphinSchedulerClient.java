package com.cmsr.onebase.framework.core;

import com.cmsr.onebase.framework.remote.InstanceApi;
import com.cmsr.onebase.framework.remote.ScheduleApi;
import com.cmsr.onebase.framework.remote.TaskApi;
import com.cmsr.onebase.framework.remote.TaskInstanceApi;
import com.cmsr.onebase.framework.remote.WorkflowApi;
import com.cmsr.onebase.framework.remote.dto.process.ProcessReleaseParamDTO;
import jakarta.annotation.Resource;
import retrofit2.Response;

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
    public TaskInstanceApi taskInstance() {
        return taskInstanceApi;
    }

    /**
     * 获取任务定义相关 API
     *
     * @return TaskApi
     */
    public TaskApi task() {
        return taskApi;
    }

    /**
     * 便捷：上线工作流（封装 release ONLINE）
     */
    public Response<?> onlineWorkflow(long projectCode, long workflowCode) throws java.io.IOException {
        return workflowApi.release(projectCode, workflowCode, ProcessReleaseParamDTO.online()).execute();
    }

    /**
     * 便捷：下线工作流（封装 release OFFLINE）
     */
    public Response<?> offlineWorkflow(long projectCode, long workflowCode) throws java.io.IOException {
        return workflowApi.release(projectCode, workflowCode, ProcessReleaseParamDTO.offline()).execute();
    }
}
