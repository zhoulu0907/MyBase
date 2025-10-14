package com.cmsr.onebase.dolphins.remote;

import com.cmsr.onebase.dolphins.remote.model.HttpRestResult;
import com.cmsr.onebase.dolphins.remote.model.PageInfo;
import com.cmsr.onebase.dolphins.remote.model.instance.ProcessInstanceQueryResp;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 流程实例相关 API（DolphinScheduler 3.3.1）
 *
 * @author matianyu
 * @date 2025-10-15
 */
public interface InstanceApi {

    /**
     * 分页查询流程实例
     *
     * GET /projects/{projectCode}/process-instance
     *
     * @param projectCode 项目编码
     * @param pageNo 页码，从1开始
     * @param pageSize 每页大小
     * @param searchVal 搜索关键字（可为空）
     * @return 分页结果
     */
    @GET("projects/{projectCode}/process-instance")
    Call<HttpRestResult<PageInfo<ProcessInstanceQueryResp>>> listInstances(
            @Path("projectCode") long projectCode,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("searchVal") String searchVal
    );
}
