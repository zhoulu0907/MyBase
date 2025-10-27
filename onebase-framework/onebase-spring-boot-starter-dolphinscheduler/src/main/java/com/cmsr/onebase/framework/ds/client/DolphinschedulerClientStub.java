package com.cmsr.onebase.framework.ds.client;

import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionReq;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionResp;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowReleaseReq;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DolphinschedulerClientStub {

    // Workflow create/update/delete/online/offline
    @POST("/projects/{projectCode}/workflow-definition")
    Call<WorkflowDefinitionResp> createWorkflow(@Path("projectCode") Long projectCode,
                                                @Body WorkflowDefinitionReq workflowReq);

    @POST("/projects/{projectCode}/workflow-definition/{workflowCode}")
    Call<WorkflowDefinitionResp> updateWorkflow(@Path("projectCode") Long projectCode,
                                                @Path("workflowCode") Long workflowCode,
                                                @Body WorkflowDefinitionReq workflowReq);

    @DELETE("/projects/{projectCode}/workflow-definition/{workflowCode}")
    Call<String> deleteWorkflow(@Path("projectCode") Long projectCode,
                                @Path("workflowCode") Long workflowCode);

    // online or offline diff by param
    @POST("/projects/{projectCode}/workflow-definition/{workflowCode}/release")
    Call<String> releaseWorkflow(@Path("projectCode") Long projectCode,
                                 @Path("workflowCode") Long workflowCode,
                                 @Body WorkflowReleaseReq releaseReq);

    @GET("/projects/{projectCode}/task-definition/gen-task-codes")
    Call<List<Long>> generateTaskCodes(@Path("projectCode") Long projectCode,
                                       @Query("genNum") Integer codeNumber);

    // WorkflowInstance start
    Call<?> startWorkflowInstance();

    // TaskInstance queryLog
    Call<?> queryTaskLog();
}
