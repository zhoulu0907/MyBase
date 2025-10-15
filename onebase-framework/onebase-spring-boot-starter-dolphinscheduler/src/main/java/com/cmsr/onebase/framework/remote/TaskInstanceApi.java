package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.model.HttpRestResult;
import com.cmsr.onebase.framework.remote.model.PageInfo;
import com.cmsr.onebase.framework.remote.model.taskinstance.TaskInstanceQueryResp;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 任务实例相关 API（DolphinScheduler 3.3.1）
 *
 * @author matianyu
 * @date 2025-10-15
 */
public interface TaskInstanceApi {

    /**
     * 分页查询任务实例
     *
     * GET /projects/{projectCode}/task-instance
     *
     * @param projectCode 项目编码
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param processInstanceId 流程实例ID（可选）
     * @return 分页结果
     */
    @GET("projects/{projectCode}/task-instance")
    Call<HttpRestResult<PageInfo<TaskInstanceQueryResp>>> listTaskInstances(
            @Path("projectCode") long projectCode,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("processInstanceId") Long processInstanceId
    );

    /**
     * 分页查询任务实例（复用 plural 路径）
     * GET /projects/{projectCode}/task-instances
     */
    @GET("projects/{projectCode}/task-instances")
    Call<HttpRestResult<PageInfo<TaskInstanceQueryResp>>> page(
            @Path("projectCode") long projectCode,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("processInstanceId") Long processInstanceId
    );

    /**
     * 查询任务实例日志
     * GET /log/{projectCode}/detail?taskInstanceId=&skipLineNum=&limit=
     */
    @GET("log/{projectCode}/detail")
    Call<HttpRestResult<Object>> queryLog(
            @Path("projectCode") long projectCode,
            @Query("taskInstanceId") long taskInstanceId,
            @Query("skipLineNum") int skipLineNum,
            @Query("limit") int limit
    );
}
