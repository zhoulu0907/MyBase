package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.model.HttpRestResult;
import com.cmsr.onebase.framework.remote.model.schedule.ScheduleInfoResp;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * 调度相关 API（DolphinScheduler 3.3.1）
 *
 * 提供项目下调度查询等能力。
 */
public interface ScheduleApi {

    /**
     * 查询项目下所有调度
     *
     * GET /projects/{projectCode}/schedules
     *
     * @param projectCode 项目编码（Long 型编码，非自增 ID）
     * @return HttpRestResult，data 字段为调度信息列表
     * @throws retrofit2.HttpException 当 HTTP 层请求失败时抛出
     */
    @GET("projects/{projectCode}/schedules")
    Call<HttpRestResult<List<ScheduleInfoResp>>> list(@Path("projectCode") long projectCode);
}
