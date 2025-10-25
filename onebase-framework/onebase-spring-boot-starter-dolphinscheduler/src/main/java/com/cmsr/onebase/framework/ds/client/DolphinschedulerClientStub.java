package com.cmsr.onebase.framework.ds.client;

import com.cmsr.onebase.framework.ds.model.common.Result;
import com.cmsr.onebase.framework.ds.model.schedule.ScheduleInfoReq;
import com.cmsr.onebase.framework.ds.model.schedule.ScheduleInfoResp;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionResp;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowReleaseReq;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface DolphinschedulerClientStub {

    // Workflow   out:: query/create/delete/online/offline\
    //            in::  verify-name
    @GET("projects/{projectCode}/workflow-definition/{workflowCode}")
    Call<Result<Map>> queryWorkflowByCode(@Path("projectCode") Long projectCode,
                                          @Path("workflowCode") Long workflowCode);

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

    @PUT("projects/{projectCode}/workflow-definition/{workflowCode}")
    Call<Result<WorkflowDefinitionResp>> updateWorkflow(@Path("projectCode") Long projectCode,
                                                        @Path("workflowCode") Long workflowCode,
                                                        @Field("taskDefinitionJson") String taskDefinitionJson,
                                                        @Field("taskRelationJson") String taskRelationJson,
                                                        @Field("locations") String locations,
                                                        @Field("name") String name,
                                                        @Field("executionType") String executionType,
                                                        @Field("description") String description,
                                                        @Field("globalParams") String globalParams,
                                                        @Field("timeout") String timeout,
                                                        @Field("releaseState") String releaseState);

    @DELETE("projects/{projectCode}/workflow-definition/{workflowCode}")
    Call<String> deleteWorkflow(@Path("projectCode") Long projectCode,
                                @Path("workflowCode") Long workflowCode);

    @GET("projects/{projectCode}/workflow-definition/verify-name")
    Call<Result<Object>> verifyNameUniqueInProject(@Path("projectCode") Long projectCode,
                                                   @Query("name") String name);

    // online or offline diff by param
    @POST("projects/{projectCode}/workflow-definition/{workflowCode}/release")
    Call<String> releaseWorkflow(@Path("projectCode") Long projectCode,
                                 @Path("workflowCode") Long workflowCode,
                                 @Body WorkflowReleaseReq releaseReq);

    @GET("projects/{projectCode}/task-definition/gen-task-codes")
    Call<Result<List<Long>>> generateTaskCodes(@Path("projectCode") Long projectCode,
                                               @Query("genNum") Integer codeNumber);

    // WorkflowInstance manualy start
    @POST("projects/{projectCode}/executors/start-workflow-instance")
    Call<Result<List<Long>>> startWorkflowInstance(@Path("projectCode") Long projectCode,
                                                   @Body Object startParam);

    // Schedule create/update/delete/online/offline
    @POST("projects/{projectCode}/schedules")
    Call<ScheduleInfoResp> createSchedule(@Path("projectCode") Long projectCode,
                                          @Body ScheduleInfoReq scheduleReq);

    @PUT("projects/{projectCode}/schedules/{scheduleId}")
    Call<ScheduleInfoResp> updateSchedule(@Path("projectCode") Long projectCode,
                                          @Path("scheduleId") Integer scheduleId,
                                          @Body ScheduleInfoReq scheduleReq);

    @POST("projects/{projectCode}/schedules/{scheduleId}/online")
    Call<Result<Boolean>> onlineSchedule(@Path("projectCode") Long projectCode,
                                         @Path("scheduleId") Integer scheduleId);

    @POST("projects/{projectCode}/schedules/{scheduleId}/offline")
    Call<Result<Boolean>> offlineSchedule(@Path("projectCode") Long projectCode,
                                          @Path("scheduleId") Integer scheduleId);

    @DELETE("projects/{projectCode}/schedules/{scheduleId}")
    Call<Result<Object>> deleteSchedule(@Path("projectCode") Long projectCode,
                                        @Path("scheduleId") Long pathScheduleId,
                                        @Query("scheduleId") Long queryScheduleId);

    // TaskInstance queryLog
    @GET()
    Call<?> queryTaskLog();
}
