package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleDefineParamDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleInfoRespDTO;
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

import java.util.Map;

/**
 * 调度相关 API（DolphinScheduler 3.3.1）
 *
 * 提供项目下调度的增删改查与上下线能力，支持 JSON 与 FORM 两种提交方式。
 */
public interface ScheduleApi {

    /** 创建调度（JSON） */
    @POST("projects/{projectCode}/schedules")
    Call<HttpRestResultDTO<ScheduleInfoRespDTO>> create(@Path("projectCode") long projectCode,
                                                  @Body ScheduleDefineParamDTO body);

    /** 创建调度（FORM） */
    @FormUrlEncoded
    @POST("projects/{projectCode}/schedules")
    Call<HttpRestResultDTO<ScheduleInfoRespDTO>> createForm(@Path("projectCode") long projectCode,
                                                      @FieldMap Map<String, String> form);

    /** 查询项目下所有调度（可按 workflow 过滤） */
    @GET("projects/{projectCode}/schedules")
    Call<HttpRestResultDTO<PageInfoDTO<ScheduleInfoRespDTO>>> getByWorkflowCode(@Path("projectCode") long projectCode,
                                                                       @Query("pageNo") int pageNo,
                                                                       @Query("pageSize") int pageSize,
                                                                       @Query("processDefinitionCode") Long processDefinitionCode);

    /** 更新调度（JSON） */
    @PUT("projects/{projectCode}/schedules/{scheduleId}")
    Call<HttpRestResultDTO<ScheduleInfoRespDTO>> update(@Path("projectCode") long projectCode,
                                                  @Path("scheduleId") long scheduleId,
                                                  @Body ScheduleDefineParamDTO body);

    /** 更新调度（FORM） */
    @FormUrlEncoded
    @PUT("projects/{projectCode}/schedules/{scheduleId}")
    Call<HttpRestResultDTO<ScheduleInfoRespDTO>> updateForm(@Path("projectCode") long projectCode,
                                                      @Path("scheduleId") long scheduleId,
                                                      @FieldMap Map<String, String> form);

    /** 上线调度 */
    @POST("projects/{projectCode}/schedules/{scheduleId}/online")
    Call<HttpRestResultDTO<String>> online(@Path("projectCode") long projectCode,
                                        @Path("scheduleId") long scheduleId);

    /** 下线调度 */
    @POST("projects/{projectCode}/schedules/{scheduleId}/offline")
    Call<HttpRestResultDTO<String>> offline(@Path("projectCode") long projectCode,
                                         @Path("scheduleId") long scheduleId);

    /** 删除调度 */
    @DELETE("projects/{projectCode}/schedules/{scheduleId}")
    Call<HttpRestResultDTO<String>> delete(@Path("projectCode") long projectCode,
                                        @Path("scheduleId") long scheduleId);
}
