package com.cmsr.onebase.framework.dolphins.api;

import com.cmsr.onebase.framework.dolphins.dto.taskinstance.response.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 任务实例相关操作 API
 *
 * 提供任务实例的查询、停止、强制成功等操作能力。
 *
 * @author matianyu
 * @date 2025-10-17
 */
public interface TaskInstanceApi {

    /**
     * 分页查询任务实例列表
     *
     * @param projectCode 项目编码
     * @param workflowInstanceId 工作流实例ID
     * @param workflowInstanceName 流程实例名称
     * @param searchVal 搜索值
     * @param taskName 任务实例名
     * @param taskCode 任务代码
     * @param executorName 执行人名称
     * @param stateType 运行状态
     * @param host 运行主机IP
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param taskExecuteType 任务执行类型
     * @param pageNo 页码号
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GET("projects/{projectCode}/task-instances")
    Call<TaskInstancePageResponseDTO> queryTaskListPaging(
            @Path("projectCode") Long projectCode,
            @Query("workflowInstanceId") Integer workflowInstanceId,
            @Query("workflowInstanceName") String workflowInstanceName,
            @Query("searchVal") String searchVal,
            @Query("taskName") String taskName,
            @Query("taskCode") Long taskCode,
            @Query("executorName") String executorName,
            @Query("stateType") String stateType,
            @Query("host") String host,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("taskExecuteType") String taskExecuteType,
            @Query("pageNo") Integer pageNo,
            @Query("pageSize") Integer pageSize);

    /**
     * 查询单个任务实例
     *
     * @param projectCode 项目编码
     * @param taskInstanceId 任务实例ID
     * @return 任务实例详情
     */
    @POST("projects/{projectCode}/task-instances/{taskInstanceId}")
    Call<TaskInstanceQueryResponseDTO> queryTaskInstanceByCode(
            @Path("projectCode") Long projectCode,
            @Path("taskInstanceId") Long taskInstanceId);

    /**
     * 停止任务实例
     *
     * @param projectCode 项目编码
     * @param id 任务实例ID
     * @return 操作结果
     */
    @POST("projects/{projectCode}/task-instances/{id}/stop")
    Call<TaskInstanceStopResponseDTO> stopTask(
            @Path("projectCode") Long projectCode,
            @Path("id") Integer id);

    /**
     * 任务 SavePoint
     *
     * @param projectCode 项目编码
     * @param id 任务实例ID
     * @return 操作结果
     */
    @POST("projects/{projectCode}/task-instances/{id}/savepoint")
    Call<TaskInstanceSavePointResponseDTO> taskSavePoint(
            @Path("projectCode") Long projectCode,
            @Path("id") Integer id);

    /**
     * 强制任务成功
     *
     * @param projectCode 项目编码
     * @param id 任务实例ID
     * @return 操作结果
     */
    @POST("projects/{projectCode}/task-instances/{id}/force-success")
    Call<TaskInstanceForceSuccessResponseDTO> forceTaskSuccess(
            @Path("projectCode") Long projectCode,
            @Path("id") Integer id);
}
