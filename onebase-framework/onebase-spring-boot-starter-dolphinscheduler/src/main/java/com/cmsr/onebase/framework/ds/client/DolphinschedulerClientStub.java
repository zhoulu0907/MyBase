package com.cmsr.onebase.framework.ds.client;

import com.cmsr.onebase.framework.ds.model.common.PageInfo;
import com.cmsr.onebase.framework.ds.model.common.Result;
import com.cmsr.onebase.framework.ds.model.schedule.ScheduleInfoResp;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionResp;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDetailedResp;
import com.cmsr.onebase.framework.ds.model.workflow.sub.ComplementTime;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DolphinschedulerClientStub {

    // Workflow   out:: create/delete/online/offline
    //            in::  verify-name
    @POST("projects/{projectCode}/workflow-definition")
    @FormUrlEncoded
    Call<Result<WorkflowDefinitionResp>> createWorkflow(@Path("projectCode") Long projectCode,
                                                        @Field("taskDefinitionJson") String taskDefinitionJson,
                                                        @Field("taskRelationJson") String taskRelationJson,
                                                        @Field("locations") String locations,
                                                        @Field("name") String name,
                                                        @Field("executionType") String executionType,
                                                        @Field("description") String description,
                                                        @Field("globalParams") String globalParams,
                                                        @Field("timeout") String timeout);

    @DELETE("projects/{projectCode}/workflow-definition/{workflowCode}")
    Call<Result<Object>> deleteWorkflow(@Path("projectCode") Long projectCode,
                                        @Path("workflowCode") Long workflowCode);

    @GET("projects/{projectCode}/workflow-definition/{code}")
    Call<Result<WorkflowDetailedResp>> queryWorkflowByCode(@Path("projectCode") Long projectCode,
                                                           @Path("code") Long workflowCode);

    @GET("projects/{projectCode}/workflow-definition/query-by-name")
    Call<Result<WorkflowDetailedResp>> queryWorkflowByName(@Path("projectCode") Long projectCode,
                                                           @Query("name") String name);

    @GET("projects/{projectCode}/workflow-definition")
    Call<Result<PageInfo<WorkflowDefinitionResp>>> queryWorkflowPage(@Path("projectCode") Long projectCode,
                                                                     @Query("searchVal") String searchVal,
                                                                     @Query("pageNo") Integer pageNo,
                                                                     @Query("pageSize") Integer pageSize);

    // online or offline diff by param
    @POST("projects/{projectCode}/workflow-definition/{workflowCode}/release")
    @FormUrlEncoded
    Call<Result<Boolean>> releaseWorkflow(@Path("projectCode") Long projectCode,
                                          @Path("workflowCode") Long workflowCode,
                                          @Field("name") String flowName,
                                          @Field("releaseState") String releaseState);

    @GET("projects/{projectCode}/task-definition/gen-task-codes")
    Call<Result<List<Long>>> generateTaskCodes(@Path("projectCode") Long projectCode,
                                               @Query("genNum") Integer codeNumber);

    // Schedule create/update/delete/online/offline
    @POST("projects/{projectCode}/schedules")
    @FormUrlEncoded
    Call<Result<ScheduleInfoResp>> createSchedule(@Path("projectCode") Long projectCode,
                                                  @Field("workflowDefinitionCode") Long workflowCode,
                                                  @Field("environmentCode") Long environmentCode,
                                                  @Field("tenantCode") String tenantCode,
                                                  @Field("schedule") Schedule schedule,
                                                  @Field("failureStrategy") String failureStrategy,
                                                  @Field("warningType") String warningType,
                                                  @Field("workflowInstancePriority") String workflowInstancePriority,
                                                  @Field("warningGroupId") Long warningGroupId,
                                                  @Field("workerGroup") String workerGroup);

    @POST("projects/{projectCode}/schedules/{scheduleId}/online")
    Call<Result<Boolean>> onlineSchedule(@Path("projectCode") Long projectCode,
                                         @Path("scheduleId") Integer scheduleId);

    @POST("projects/{projectCode}/schedules/{scheduleId}/offline")
    Call<Result<Boolean>> offlineSchedule(@Path("projectCode") Long projectCode,
                                          @Path("scheduleId") Integer scheduleId);

    @DELETE("projects/{projectCode}/schedules/{scheduleId}")
    Call<Result<Boolean>> deleteSchedule(@Path("projectCode") Long projectCode,
                                         @Path("scheduleId") Integer pathScheduleId,
                                         @Query("scheduleId") Integer queryScheduleId);

    @GET("projects/{projectCode}/schedules")
    Call<Result<PageInfo<ScheduleInfoResp>>> queryScheduleByWorkflow(@Path("projectCode") Long projectCode,
                                                                     @Query("workflowDefinitionCode") Long workflowCode,
                                                                     @Query("pageNo") Integer pageNo,
                                                                     @Query("pageSize") Integer pageSize);

    @POST("projects/{projectCode}/executors/start-workflow-instance")
    @FormUrlEncoded
    Call<Result<List<Long>>> manuallyStartWorkflow(@Path("projectCode") Long projectCode,
                                                   @Field("workflowDefinitionCode") Long workflowCode,
                                                   @Field("environmentCode") Long environmentCode,
                                                   @Field("tenantCode") String tenantCode,
                                                   @Field("scheduleTime") String scheduleTime, // {xxx}
                                                   @Field("failureStrategy") String failureStrategy, // CONTINUE
                                                   @Field("warningType") String warningType, // NONE
                                                   @Field("runMode") String runMode, // RUN_MODE_SERIAL
                                                   @Field("workflowInstancePriority") String workflowInstancePriority, // MEDIUM
                                                   @Field("workerGroup") String workerGroup, // default
                                                   @Field("execType") String execType,
                                                   @Field("executionOrder") String executionOrder // DESC_ORDER
    );

    // WorkflowInstance manualy start, no usage for now
    @POST("projects/{projectCode}/executors/start-workflow-instance")
    Call<Result<List<Long>>> startWorkflowInstance(@Path("projectCode") Long projectCode,
                                                   @Body Object startParam);
}
