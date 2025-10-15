package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.process.ProcessDefineParamDTO;
import com.cmsr.onebase.framework.remote.dto.process.ProcessDefineRespDTO;
import com.cmsr.onebase.framework.remote.dto.process.ProcessReleaseParamDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

/**
 * 工作流相关 API（DolphinScheduler 3.3.1）
 *
 * 适配 weaksloth 的 ProcessOperator 全量能力，并提供 JSON/FORM 两种提交方式。
 */
public interface WorkflowApi {

    /**
     * 分页查询流程定义
     * GET /projects/{projectCode}/workflow-definition
     */
    @GET("projects/{projectCode}/workflow-definition")
    Call<HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>>> page(
            @Path("projectCode") long projectCode,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("searchVal") String searchVal
    );

    /**
     * 创建流程定义（JSON 提交）
     * POST /projects/{projectCode}/workflow-definition
     */
    @POST("projects/{projectCode}/workflow-definition")
    Call<HttpRestResultDTO<ProcessDefineRespDTO>> create(@Path("projectCode") long projectCode,
                                                   @Body ProcessDefineParamDTO body);

    /**
     * 创建流程定义（x-www-form-urlencoded 提交）
     * POST /projects/{projectCode}/workflow-definition
     */
    @FormUrlEncoded
    @POST("projects/{projectCode}/workflow-definition")
    Call<HttpRestResultDTO<ProcessDefineRespDTO>> createForm(@Path("projectCode") long projectCode,
                                                       @FieldMap Map<String, String> form);

    /**
     * 更新流程定义（JSON 提交）
     * PUT /projects/{projectCode}/process-definition/{processDefinitionCode}
     */
    @PUT("projects/{projectCode}/process-definition/{processDefinitionCode}")
    Call<HttpRestResultDTO<ProcessDefineRespDTO>> update(@Path("projectCode") long projectCode,
                                                   @Path("processDefinitionCode") long processDefinitionCode,
                                                   @Body ProcessDefineParamDTO body);

    /**
     * 更新流程定义（x-www-form-urlencoded 提交）
     * PUT /projects/{projectCode}/process-definition/{processDefinitionCode}
     */
    @FormUrlEncoded
    @PUT("projects/{projectCode}/process-definition/{processDefinitionCode}")
    Call<HttpRestResultDTO<ProcessDefineRespDTO>> updateForm(@Path("projectCode") long projectCode,
                                                       @Path("processDefinitionCode") long processDefinitionCode,
                                                       @FieldMap Map<String, String> form);

    /**
     * 删除流程定义
     * DELETE /projects/{projectCode}/workflow-definition/{processDefinitionCode}
     */
    @DELETE("projects/{projectCode}/workflow-definition/{processDefinitionCode}")
    Call<HttpRestResultDTO<String>> delete(@Path("projectCode") long projectCode,
                                        @Path("processDefinitionCode") long processDefinitionCode);

    /**
     * 发布（上线/下线）流程定义
     * POST /projects/{projectCode}/workflow-definition/{code}/release
     */
    @POST("projects/{projectCode}/workflow-definition/{code}/release")
    Call<HttpRestResultDTO<String>> release(@Path("projectCode") long projectCode,
                                         @Path("code") long processCode,
                                         @Body ProcessReleaseParamDTO body);

    /**
     * 生成任务编码
     * GET /projects/{projectCode}/task-definition/gen-task-codes?genNum=
     */
    @GET("projects/{projectCode}/task-definition/gen-task-codes")
    Call<HttpRestResultDTO<List<Long>>> generateTaskCodes(@Path("projectCode") long projectCode,
                                                       @Query("genNum") int genNum);

    /**
     * 查询流程定义列表（兼容原有简易列表接口）
     * GET /projects/{projectCode}/process-definition/list
     */
    @GET("projects/{projectCode}/process-definition/list")
    Call<HttpRestResultDTO<List<ProcessDefineRespDTO>>> listDefinitions(@Path("projectCode") long projectCode);
}
