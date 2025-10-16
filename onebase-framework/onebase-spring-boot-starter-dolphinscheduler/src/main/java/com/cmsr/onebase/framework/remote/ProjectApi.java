package com.cmsr.onebase.framework.remote;

import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.project.ProjectRespDTO;
import com.cmsr.onebase.framework.remote.dto.project.ProjectUpdateReqDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 项目相关 API（DolphinScheduler 3.3.1）
 *
 * 提供项目的查询、更新、删除能力
 *
 * @author matianyu
 * @date 2025-10-16
 */
public interface ProjectApi {

    /**
     * 通过项目Code查询项目信息
     * GET projects/{code}
     *
     * @param code 项目Code
     * @return 项目详细信息
     */
    @GET("projects/{code}")
    Call<HttpRestResultDTO<ProjectRespDTO>> queryProjectByCode(@Path("code") Long code);

    /**
     * 更新项目
     * PUT /projects/{code}
     *
     * @param code 项目Code
     * @param body 更新请求参数
     * @return 更新后的项目信息
     */
    @PUT("projects/{code}")
    Call<HttpRestResultDTO<ProjectRespDTO>> updateProject(@Path("code") Long code,
                                                           @Body ProjectUpdateReqDTO body);

    /**
     * 通过Code删除项目
     * DELETE /projects/{code}
     *
     * @param code 项目Code
     * @return 删除结果
     */
    @DELETE("projects/{code}")
    Call<HttpRestResultDTO<Boolean>> deleteProject(@Path("code") Long code);
}
