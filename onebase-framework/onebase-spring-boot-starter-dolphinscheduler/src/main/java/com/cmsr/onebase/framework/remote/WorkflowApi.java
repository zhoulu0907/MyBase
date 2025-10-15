package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.model.HttpRestResult;
import com.cmsr.onebase.framework.remote.model.process.ProcessDefineResp;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * 工作流相关 API（DolphinScheduler 3.3.1）
 *
 * 仅封装最小常用子集，后续可按需扩展
 *
 * @author matianyu
 * @date 2025-10-15
 */
public interface WorkflowApi {

    /**
     * 查询流程定义列表
     *
     * GET /projects/{projectCode}/process-definition/list
     *
     * @param projectCode 项目编码
     * @return 流程定义列表
     */
    @GET("projects/{projectCode}/process-definition/list")
    Call<HttpRestResult<List<ProcessDefineResp>>> listDefinitions(@Path("projectCode") long projectCode);
}
