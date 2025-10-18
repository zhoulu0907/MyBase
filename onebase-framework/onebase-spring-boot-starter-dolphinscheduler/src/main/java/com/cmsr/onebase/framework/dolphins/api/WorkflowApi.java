package com.cmsr.onebase.framework.dolphins.api;

import com.cmsr.onebase.framework.dolphins.dto.workflow.request.WorkflowCreateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.request.WorkflowQueryRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.request.WorkflowUpdateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.response.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 工作流定义相关操作 API (V2)
 *
 * 提供工作流定义的创建、查询、更新、删除等功能。
 * 对应 DolphinScheduler V2 的 Workflow API。
 * 
 * 注意: 此接口基于 DolphinScheduler 3.3.1 API v2 版本
 *
 * @author matianyu
 * @date 2025-01-17
 */
public interface WorkflowApi {

    /**
     * 创建工作流定义
     * 
     * 对应接口: POST /v2/workflows
     *
     * @param body 创建请求参数(包含 projectCode、name等字段)
     * @return 创建结果
     */
    @POST("workflows")
    Call<WorkflowCreateResponseDTO> createWorkflow(
            @Body WorkflowCreateRequestDTO body);

    /**
     * 通过编码查询工作流定义
     * 
     * 对应接口: GET /v2/workflows/{code}
     *
     * @param code 工作流定义编码
     * @return 工作流定义信息
     */
    @GET("workflows/{code}")
    Call<WorkflowQueryResponseDTO> getWorkflow(
            @Path("code") Long code);

    /**
     * 更新工作流定义
     * 
     * 对应接口: PUT /v2/workflows/{code}
     *
     * @param code 工作流定义编码
     * @param body 更新请求参数
     * @return 更新结果
     */
    @PUT("workflows/{code}")
    Call<WorkflowUpdateResponseDTO> updateWorkflow(
            @Path("code") Long code,
            @Body WorkflowUpdateRequestDTO body);

    /**
     * 删除工作流定义
     * 
     * 对应接口: DELETE /v2/workflows/{code}
     *
     * @param code 工作流定义编码
     * @return 删除结果
     */
    @DELETE("workflows/{code}")
    Call<WorkflowDeleteResponseDTO> deleteWorkflow(
            @Path("code") Long code);

    /**
     * 分页查询工作流定义列表
     * 
     * 对应接口: POST /v2/workflows/query
     * 
     * 注意: 此接口使用 POST 方法,参数通过请求体传递
     *
     * @param body 查询请求参数(包含 pageNo、pageSize、projectName、workflowName等)
     * @return 分页结果
     */
    @POST("workflows/query")
    @Headers("Content-Type: application/json")
    Call<WorkflowPageResponseDTO> filterWorkflows(
            @Body WorkflowQueryRequestDTO body);
}
