package com.cmsr.onebase.dolphins.remote;

import com.cmsr.onebase.dolphins.remote.model.HttpRestResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
     * @return 结果（为简化，此处返回字符串或后续补充模型）
     */
    @GET("projects/{projectCode}/task-definition/list-by-process-definition-code/{processDefinitionCode}")
    Call<HttpRestResult<Object>> listByProcessDefinitionCode(@Path("projectCode") long projectCode,
                                                             @Path("processDefinitionCode") long processDefinitionCode);
}

