package com.cmsr.onebase.framework.dolphins.api;

import com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums.ExecuteTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflowinstance.response.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 工作流实例相关操作 API (V2)
 *
 * 提供工作流实例的查询、执行操作、删除等功能。
 * 对应 DolphinScheduler V2 的 Workflow Instance API。
 * 
 * 注意: 此接口基于 DolphinScheduler 3.3.1 API v2 版本
 *
 * @author matianyu
 * @date 2025-10-17
 */
public interface WorkflowInstanceApi {

    /**
     * 执行流程实例的各种操作(暂停、停止、重跑、恢复等)
     * 
     * 对应接口: POST /v2/workflow-instances/{workflowInstanceId}/execute/{executeType}
     *
     * @param workflowInstanceId 工作流实例ID
     * @param executeType 执行类型
     * @return 执行结果
     */
    @POST("workflow-instances/{workflowInstanceId}/execute/{executeType}")
    Call<WorkflowInstanceExecuteResponseDTO> execute(
            @Path("workflowInstanceId") Integer workflowInstanceId,
            @Path("executeType") ExecuteTypeEnum executeType);

    /**
     * 查询流程实例列表
     * 
     * 对应接口: GET /v2/workflow-instances
     *
     * @param pageNo 页码号
     * @param pageSize 页大小
     * @param projectName 项目名称
     * @param workflowName 工作流名称
     * @param host 主机地址
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param state 状态
     * @return 分页查询结果
     */
    @GET("workflow-instances")
    Call<WorkflowInstancePageResponseDTO> queryWorkflowInstanceListPaging(
            @Query("pageNo") Integer pageNo,
            @Query("pageSize") Integer pageSize,
            @Query("projectName") String projectName,
            @Query("workflowName") String workflowName,
            @Query("host") String host,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("state") Integer state);

    /**
     * 通过ID查询流程实例详情
     * 
     * 对应接口: GET /v2/workflow-instances/{workflowInstanceId}
     *
     * @param workflowInstanceId 工作流实例ID
     * @return 流程实例详情
     */
    @GET("workflow-instances/{workflowInstanceId}")
    Call<WorkflowInstanceQueryResponseDTO> queryWorkflowInstanceById(
            @Path("workflowInstanceId") Integer workflowInstanceId);

    /**
     * 删除流程实例
     * 
     * 对应接口: DELETE /v2/workflow-instances/{workflowInstanceId}
     *
     * @param workflowInstanceId 工作流实例ID
     * @return 删除结果
     */
    @DELETE("workflow-instances/{workflowInstanceId}")
    Call<WorkflowInstanceDeleteResponseDTO> deleteWorkflowInstance(
            @Path("workflowInstanceId") Integer workflowInstanceId);
}
