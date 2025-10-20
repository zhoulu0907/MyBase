package com.cmsr.onebase.framework.dolphins.api;

import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleCreateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleQueryRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleUpdateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.response.ScheduleCreateResponseDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.response.ScheduleDeleteResponseDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.response.SchedulePageResponseDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.response.ScheduleQueryResponseDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.response.ScheduleUpdateResponseDTO;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 定时调度相关操作 API (V2)
 *
 * 提供定时调度的创建、查询、更新、删除等功能。
 * 对应 DolphinScheduler V2 的 Schedule API。
 * 
 * 注意: 此接口基于 DolphinScheduler 3.3.1 API v2 版本
 *
 * @author matianyu
 * @date 2025-10-17
 */
public interface ScheduleApi {

    /**
     * 创建定时调度
     * 
     * 对应接口: POST /v2/schedules
     *
     * @param body 创建请求参数(包含 workflowDefinitionCode、crontab等字段)
     * @return 创建结果
     */
    @POST("schedules")
    Call<ScheduleCreateResponseDTO> createSchedule(
            @Body ScheduleCreateRequestDTO body);

    /**
     * 通过ID查询定时调度
     * 
     * 对应接口: GET /v2/schedules/{id}
     *
     * @param id 定时调度ID
     * @return 定时调度信息
     */
    @GET("schedules/{id}")
    Call<ScheduleQueryResponseDTO> getSchedule(
            @Path("id") Integer id);

    /**
     * 更新定时调度
     * 
     * 对应接口: PUT /v2/schedules/{id}
     *
     * @param id 定时调度ID
     * @param body 更新请求参数
     * @return 更新结果
     */
    @PUT("schedules/{id}")
    Call<ScheduleUpdateResponseDTO> updateSchedule(
            @Path("id") Integer id,
            @Body ScheduleUpdateRequestDTO body);

    /**
     * 删除定时调度
     * 
     * 对应接口: DELETE /v2/schedules/{id}
     *
     * @param id 定时调度ID
     * @return 删除结果
     */
    @DELETE("schedules/{id}")
    Call<ScheduleDeleteResponseDTO> deleteSchedule(
            @Path("id") Integer id);

    /**
     * 分页查询定时调度列表
     * 
     * 对应接口: POST /v2/schedules/filter
     * 
     * 注意: 此接口使用 POST 方法,参数通过请求体传递
     *
     * @param body 查询请求参数(包含 pageNo、pageSize、projectName、workflowDefinitionName等)
     * @return 分页结果
     */
    @POST("schedules/filter")
    @Headers("Content-Type: application/json")
    Call<SchedulePageResponseDTO> filterSchedule(
            @Body ScheduleQueryRequestDTO body);
}
