package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.model.HttpRestResult;
import com.cmsr.onebase.framework.remote.model.PageInfo;
import com.cmsr.onebase.framework.remote.model.instance.ProcessInstanceCreateParam;
import com.cmsr.onebase.framework.remote.model.instance.ProcessInstanceRunParam;
import com.cmsr.onebase.framework.remote.model.instance.ProcessInstanceQueryResp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 流程实例相关 API（DolphinScheduler 3.3.1）
 */
public interface InstanceApi {

    /**
     * 启动流程实例
     * POST /projects/{projectCode}/executors/start-process-instance
     *
     * @param projectCode 项目编码
     * @param body 创建参数
     * @return 执行结果
     */
    @POST("projects/{projectCode}/executors/start-process-instance")
    Call<HttpRestResult<Object>> start(@Path("projectCode") long projectCode,
                                       @Body ProcessInstanceCreateParam body);

    /**
     * 分页查询流程实例（weaksloth: /process-instances）
     * GET /projects/{projectCode}/process-instances?pageNo=&pageSize=&processDefineCode=
     *
     * @param projectCode 项目编码
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param processDefineCode 流程定义编码
     * @return 分页结果
     */
    @GET("projects/{projectCode}/process-instances")
    Call<HttpRestResult<PageInfo<ProcessInstanceQueryResp>>> page(
            @Path("projectCode") long projectCode,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("processDefineCode") Long processDefineCode
    );

    /**
     * 执行流程实例操作（重跑/停止/暂停等）
     * POST /projects/{projectCode}/executors/execute
     *
     * @param projectCode 项目编码
     * @param body 执行参数（包含 processInstanceId 与 executeType）
     * @return 结果
     */
    @POST("projects/{projectCode}/executors/execute")
    Call<HttpRestResult<String>> execute(@Path("projectCode") long projectCode,
                                         @Body ProcessInstanceRunParam body);

    /**
     * 删除流程实例
     * DELETE /projects/{projectCode}/process-instances/{processInstanceId}
     *
     * @param projectCode 项目编码
     * @param processInstanceId 实例ID
     * @return 结果
     */
    @DELETE("projects/{projectCode}/process-instances/{processInstanceId}")
    Call<HttpRestResult<String>> delete(@Path("projectCode") long projectCode,
                                        @Path("processInstanceId") long processInstanceId);
}
