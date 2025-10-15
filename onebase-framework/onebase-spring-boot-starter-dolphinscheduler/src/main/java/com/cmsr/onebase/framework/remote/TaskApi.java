package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.task.TaskDefinitionRespDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * 任务定义相关 API（DolphinScheduler 3.3.1）
 *
 * @author matianyu
 * @date 2025-10-15
 */
public interface TaskApi {

    /**
     * 查询任务定义列表（按流程定义）
     *
     * GET /projects/{projectCode}/task-definition/list-by-process-definition-code/{processDefinitionCode}
     *
     * @param projectCode 项目编码
     * @param processDefinitionCode 流程定义编码
     * @return 任务定义列表
     */
    @GET("projects/{projectCode}/task-definition/list-by-process-definition-code/{processDefinitionCode}")
    Call<HttpRestResultDTO<List<TaskDefinitionRespDTO>>> listByProcessDefinitionCode(@Path("projectCode") long projectCode,
                                                                              @Path("processDefinitionCode") long processDefinitionCode);
}
