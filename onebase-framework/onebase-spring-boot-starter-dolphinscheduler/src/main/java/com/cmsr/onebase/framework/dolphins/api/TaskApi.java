package com.cmsr.onebase.framework.dolphins.api;

import com.cmsr.onebase.framework.dolphins.dto.task.request.TaskQueryRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.response.TaskGetResponseDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.response.TaskPageResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 任务定义相关操作 API
 *
 * 提供任务定义的查询与详情获取能力。
 * 注意：不包含任务实例相关接口。
 *
 * @author matianyu
 * @date 2025-10-17
 */
public interface TaskApi {

    /**
     * 分页筛选任务定义
     *
     * @param body 查询条件
     * @return 分页结果
     */
    @POST("tasks/query")
    Call<TaskPageResponseDTO> filterTaskDefinition(@Body TaskQueryRequestDTO body);

    /**
     * 获取任务定义详情
     *
     * @param code 任务定义编码
     * @return 任务定义详情
     */
    @GET("tasks/{code}")
    Call<TaskGetResponseDTO> getTaskDefinition(@Path("code") Long code);
}
