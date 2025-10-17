package com.cmsr.onebase.framework.dolphins.api;

import com.cmsr.onebase.framework.dolphins.dto.project.request.ProjectCreateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.project.request.ProjectUpdateRequestDTO;
import com.cmsr.onebase.framework.dolphins.dto.project.response.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 项目相关操作 API
 *
 * @author matianyu
 * @date 2025-01-17
 */
public interface ProjectApi {

    /**
     * 创建项目
     *
     * @param body 创建请求参数
     * @return 创建结果
     */
    @POST("projects")
    Call<ProjectCreateResponseDTO> createProject(@Body ProjectCreateRequestDTO body);

    /**
     * 通过项目编码查询项目信息
     *
     * @param code 项目编码
     * @return 项目信息
     */
    @GET("projects/{code}")
    Call<ProjectQueryResponseDTO> queryProjectByCode(@Path("code") Long code);

    /**
     * 更新项目
     *
     * @param code 项目编码
     * @param body 更新请求参数
     * @return 更新结果
     */
    @PUT("projects/{code}")
    Call<ProjectUpdateResponseDTO> updateProject(@Path("code") Long code,
                                                 @Body ProjectUpdateRequestDTO body);

    /**
     * 通过编码删除项目
     *
     * @param code 项目编码
     * @return 删除结果
     */
    @DELETE("projects/{code}")
    Call<ProjectDeleteResponseDTO> deleteProject(@Path("code") Long code);

    /**
     * 分页查询项目列表
     *
     * @param searchVal 搜索值
     * @param pageNo 页码号
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GET("projects")
    Call<ProjectPageResponseDTO> queryProjectListPaging(
            @Query("searchVal") String searchVal,
            @Query("pageNo") Integer pageNo,
            @Query("pageSize") Integer pageSize);

    /**
     * 查询所有项目
     *
     * @return 项目列表
     */
    @GET("projects/list")
    Call<ProjectListResponseDTO> queryAllProjectList();

    /**
     * 查询 Dependent 节点所有项目
     *
     * @return 项目列表
     */
    @GET("projects/list-dependent")
    Call<ProjectListResponseDTO> queryAllProjectListForDependent();

    /**
     * 查询授权和用户创建的项目
     *
     * @return 项目列表
     */
    @GET("projects/created-and-authed")
    Call<ProjectListResponseDTO> queryProjectCreatedAndAuthorizedByUser();

    /**
     * 查询授权项目
     *
     * @param userId 用户 ID
     * @return 项目列表
     */
    @GET("projects/authed-project")
    Call<ProjectListResponseDTO> queryAuthorizedProject(@Query("userId") Integer userId);

    /**
     * 查询未授权的项目
     *
     * @param userId 用户 ID
     * @return 项目列表
     */
    @GET("projects/unauth-project")
    Call<ProjectListResponseDTO> queryUnauthorizedProject(@Query("userId") Integer userId);

    /**
     * 查询拥有项目授权的用户
     *
     * @param projectCode 项目编码
     * @return 用户列表
     */
    @GET("projects/authed-user")
    Call<UserListResponseDTO> queryAuthorizedUser(@Query("projectCode") Long projectCode);
}
