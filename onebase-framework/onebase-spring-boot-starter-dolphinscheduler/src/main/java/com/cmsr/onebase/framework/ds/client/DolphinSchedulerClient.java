package com.cmsr.onebase.framework.ds.client;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.exception.DolphinschedulerException;
import com.cmsr.onebase.framework.ds.model.common.PageInfo;
import com.cmsr.onebase.framework.ds.model.common.Result;
import com.cmsr.onebase.framework.ds.model.schedule.ScheduleInfoResp;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.TaskDefinition;
import com.cmsr.onebase.framework.ds.model.task.TaskLocation;
import com.cmsr.onebase.framework.ds.model.task.TaskRelation;
import com.cmsr.onebase.framework.ds.model.task.def.AbstractTask;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionResp;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDetailedResp;
import com.cmsr.onebase.framework.ds.model.workflow.sub.ComplementTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Lazy
@Setter
@Component
@Slf4j
public class DolphinSchedulerClient {
    private static final String STATUS_ONLINE = "ONLINE";
    private static final String EXECUTION_TYPE_SERIAL_WAIT = "SERIAL_WAIT";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.findAndRegisterModules();
    }

    @Value("${onebase.scheduler.address}")
    private String address;

    @Value("${onebase.scheduler.token}")
    private String token;

    @Value("${onebase.scheduler.tenant}")
    private String tenantCode;

    @Value("${onebase.scheduler.env}")
    private Long environmentCode;

    @Value("${onebase.scheduler.worker-group:default}")
    private String workerGroup;

    private DolphinschedulerClientStub dsClientStub;

    @PostConstruct
    public void initClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("token", token)
                    .build();
            return chain.proceed(request);
        });
        OkHttpClient httpClient = httpClientBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.client(httpClient);
        if (!address.endsWith("/")) {
            address = address + "/";
        }
        Retrofit retrofitClient = retrofitBuilder
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .baseUrl(address)
                .build();

        this.dsClientStub = retrofitClient.create(DolphinschedulerClientStub.class);
    }

    public List<Long> queryWorkflowCodeListByName(Long projectCode, String flowName) {
        List<WorkflowDefinitionResp> result = new ArrayList<>();
        Result<PageInfo<WorkflowDefinitionResp>> pageResp =
                execute(dsClientStub.queryWorkflowPage(projectCode, flowName, 1, 1000));
        if (pageResp.getFailed()) {
            throw DolphinschedulerException.of("根据查询条件【%s】查询工作流异常, %s", flowName, pageResp.getMsg());
        }
        int totalPage = pageResp.getData().getTotalPage();
        result.addAll(pageResp.getData().getTotalList());
        for (int i = 2; i <= totalPage; i++) {
            pageResp = execute(dsClientStub.queryWorkflowPage(projectCode, flowName, i, 1000));
            if (pageResp.getFailed()) {
                throw DolphinschedulerException.of("根据查询条件【%s】查询工作流异常, %s", flowName, pageResp.getMsg());
            }
            result.addAll(pageResp.getData().getTotalList());
        }
        return result.stream().map(WorkflowDefinitionResp::getCode).toList();
    }

    /**
     * 创建工作流
     */
    public Long createSingletonWorkflow(Long projectCode,
                                        String flowName,
                                        AbstractTask task,
                                        String description) {
        // 1. verify-name unique
        Result<WorkflowDetailedResp> uniqueResp = execute(dsClientStub.queryWorkflowByName(projectCode, flowName));
        if (uniqueResp.getSuccess()) {
            // remove data if exists
            WorkflowDefinitionResp existingData = uniqueResp.getData().getWorkflowDefinition();
            purgeWorkflow(projectCode, existingData.getCode());
        }
        // 2. generate unique task code
        Result<List<Long>> genTaskCodes = execute(dsClientStub.generateTaskCodes(projectCode, 1));
        Long taskCode = genTaskCodes.getData().get(0);
        // 3. create
        String taskType = task.grantTaskType();
        TaskDefinition taskDefinition = TaskDefinition.singleton(task)
                .setCode(taskCode)
                .setName(flowName + "_" + taskCode)
                .setTaskType(taskType)
                .setEnvironmentCode(environmentCode);
        TaskRelation taskRelation = TaskRelation.singleton(taskCode);
        TaskLocation taskLocation = TaskLocation.singleton(taskCode);
        Result<WorkflowDefinitionResp> response = execute(
                dsClientStub.createWorkflow(projectCode,
                        wrapSingleton2ListedJsonString(taskDefinition),
                        wrapSingleton2ListedJsonString(taskRelation),
                        wrapSingleton2ListedJsonString(taskLocation),
                        flowName,
                        EXECUTION_TYPE_SERIAL_WAIT,
                        description, "[]", "0"
                ));
        if (response.getFailed()) {
            throw DolphinschedulerException.of("创建工作流【%s】失败,响应信息: %s", flowName, response.getMsg());
        }
        return response.getData().getCode();
    }

    public Long queryWorkflowByName(Long projectCode, String flowName) {
        Result<WorkflowDetailedResp> queryResp = execute(dsClientStub.queryWorkflowByName(projectCode, flowName));
        if (queryResp.getFailed() && queryResp.getMsg().contains("does not exist")) {
            //throw DolphinschedulerException.of("工作流【%s】查询失败！%s", flowName, queryResp.getMsg());
            return null;
        }
        WorkflowDefinitionResp workflowDef = queryResp.getData().getWorkflowDefinition();
        if (workflowDef == null) {
            //throw DolphinschedulerException.of("工作流【%s】不存在！", flowName, queryResp.getMsg());
            return null;
        }
        return workflowDef.getCode();
    }

    /**
     * 上线工作流
     */
    public void onlineWorkflow(Long projectCode, Long workflowCode) {
        offlineWorkflow(projectCode, workflowCode);
        Result<Boolean> releaseResult = execute(dsClientStub.releaseWorkflow(projectCode, workflowCode,
                // magic string: flowName, DS required for this, but no usage at all.
                "flowName", "ONLINE"));
        if (releaseResult.getFailed()) {
            throw DolphinschedulerException.of("工作流【%s】上线失败", workflowCode);
        }
    }

    public List<Long> runWorkflowManually(Long projectCode, Long workflowCode, LocalDateTime startTime, LocalDateTime endTime) {
        ComplementTime complementTime = null;
        String execType = "START_PROCESS";
        if (startTime != null && endTime != null) {
            complementTime = new ComplementTime();
            complementTime.setComplementStartDate(startTime);
            complementTime.setComplementEndDate(endTime);
            execType = "COMPLEMENT_DATA";
        }

        Result<List<Long>> executeResult = execute(dsClientStub.manuallyStartWorkflow(projectCode, workflowCode, environmentCode, tenantCode,
                complementTime,
                "CONTINUE", "NONE",
                "RUN_MODE_SERIAL", "MEDIUM", workerGroup,
                execType, "DESC_ORDER"
        ));

        if (executeResult.getFailed()) {
            log.error("工作流运行失败{}, {}", workflowCode, executeResult.getMsg());
            throw DolphinschedulerException.of("工作流【%s】运行提交失败, %s", workflowCode);
        }

        return executeResult.getData();
    }

    /**
     * 上线工作流，包括调度
     */
    public void onlineWorkflowWithSchedule(Long projectCode, Long workflowCode, Schedule schedule) {
        // 1. 上线工作流
        onlineWorkflow(projectCode, workflowCode);
        // 2. 创建调度信息
        Result<ScheduleInfoResp> createScheduleResp = execute(dsClientStub.createSchedule(projectCode, workflowCode, environmentCode, tenantCode, schedule,
                "CONTINUE", "NONE", "MEDIUM", 0L, "default"));
        if (createScheduleResp.getFailed()) {
            throw DolphinschedulerException.of("工作流【%s】上线失败，失败原因：创建调度失败，%s", workflowCode, createScheduleResp.getMsg());
        }
        Integer scheduleId = createScheduleResp.getData().getId();
        // 3. 上线调度
        Result<Boolean> onlineScheduleResult = execute(dsClientStub.onlineSchedule(projectCode, scheduleId));
        if (onlineScheduleResult.getFailed()) {
            throw DolphinschedulerException.of("工作流【%s】调度【%s】上线失败，%s", workflowCode, scheduleId, onlineScheduleResult.getMsg());
        }
    }

    /**
     * 清理工作流，实现下线即删除。
     */
    public void purgeWorkflow(Long projectCode, Long workflowCode) {
        // 1. 下线相关信息
        offlineWorkflow(projectCode, workflowCode);
        // 2. 删除工作流，若存在
        Result<Object> deleted = execute(dsClientStub.deleteWorkflow(projectCode, workflowCode));
        if (deleted.getFailed()) {
            log.error("删除工作流【{}】失败, {}", workflowCode, deleted.getMsg());
            throw DolphinschedulerException.of("删除工作流【%s】失败, %s", workflowCode, deleted.getMsg());
        }
    }

    /**
     * 根据工作流CODE下线其调度及本身。本方法忽略所有调用报错。
     *
     * @param projectCode  项目CODE
     * @param workflowCode 工作流CODE
     */
    private void offlineWorkflow(Long projectCode, Long workflowCode) {
        // 0. 查询工作流，保证工作流存在
        WorkflowDefinitionResp workflowDef = queryWorkflowByCode(projectCode, workflowCode);
        // 1. 若存在调度，则下线调度
        //  1.1. 获取调度信息，判断调度存在，是否需要下线
        Result<PageInfo<ScheduleInfoResp>> scheduleQueryResp = execute(dsClientStub.queryScheduleByWorkflow(projectCode, workflowCode, 1, 1));
        if (scheduleQueryResp.getFailed()) {
            log.warn("获取工作流【{}】调度信息失败, {}", workflowCode, scheduleQueryResp.getMsg());
            throw DolphinschedulerException.of("获取工作流【%s】调度信息失败, %s", workflowCode, scheduleQueryResp.getMsg());
        }
        PageInfo<ScheduleInfoResp> schedulePage = scheduleQueryResp.getData();
        List<ScheduleInfoResp> scheduleList = null;
        if (schedulePage != null) {
            scheduleList = schedulePage.getTotalList();
        }
        boolean isScheduleExists = false;
        boolean isScheduleOnline = false;
        Integer scheduleCode = null;
        if (CollectionUtils.isNotEmpty(scheduleList)) {
            ScheduleInfoResp scheduleResp = scheduleList.get(0);
            isScheduleExists = true;
            scheduleCode = scheduleResp.getId();
            isScheduleOnline = StringUtils.equals(STATUS_ONLINE, scheduleResp.getReleaseState());
        }
        // 1.2. 如果调度已上线，则下线
        if (isScheduleOnline) {
            Result<Boolean> offlineScheduleResp = execute(dsClientStub.offlineSchedule(projectCode, scheduleCode));
            if (offlineScheduleResp.getFailed()) {
                log.warn("下线工作流【{}】对应调度【{}】失败, {}", workflowCode, scheduleCode, offlineScheduleResp.getMsg());
                throw DolphinschedulerException.of("下线工作流【%s】对应调度【%s】失败, %s", workflowCode, offlineScheduleResp.getMsg());
            }
        }
        //  1.3. 如果调度存在，则删除
        if (isScheduleExists) {
            Result<Boolean> deleteScheduleResp = execute(dsClientStub.deleteSchedule(projectCode, scheduleCode, scheduleCode));
            if (deleteScheduleResp.getFailed()) {
                log.warn("删除工作流【{}】对应调度【{}】失败,{}", workflowCode, scheduleCode, deleteScheduleResp.getMsg());
                throw DolphinschedulerException.of("删除工作流【%s】对应调度【%s】失败, %s", workflowCode, deleteScheduleResp.getMsg());
            }
        }
        // 2. 下线工作流若存在
        if (StringUtils.equals(STATUS_ONLINE, workflowDef.getReleaseState())) {
            Result<Boolean> releaseResult = execute(dsClientStub.releaseWorkflow(projectCode, workflowCode,
                    // magic string: flowName, DS required for this, but no usage at all.
                    "flowName", "OFFLINE"));
            if (releaseResult.getFailed()) {
                log.warn("工作流【{}】下线失败，下线接口调用失败, {}", workflowCode, releaseResult.getMsg());
                throw DolphinschedulerException.of("工作流【%s】下线失败, %s", workflowCode, releaseResult.getMsg());
            }
        }
    }

    private WorkflowDefinitionResp queryWorkflowByCode(Long projectCode, Long workflowCode) {
        Result<WorkflowDetailedResp> queryResp = execute(dsClientStub.queryWorkflowByCode(projectCode, workflowCode));

        if (queryResp.getFailed()) {
            throw DolphinschedulerException.of("工作流【%s】查询失败！%s", workflowCode, queryResp.getMsg());
        }
        WorkflowDefinitionResp workflowDef = queryResp.getData().getWorkflowDefinition();
        if (workflowDef == null) {
            throw DolphinschedulerException.of("工作流【%s】不存在！", workflowCode, queryResp.getMsg());
        }
        return workflowDef;
    }

    private <T> String wrapSingleton2ListedJsonString(T args) {
        List<T> array = Collections.singletonList(args);
        return JsonUtils.toJsonString(array);
    }

    private <T> Result<T> execute(Call<Result<T>> call) {
        Response<Result<T>> resp;
        try {
            resp = call.execute();
        } catch (IOException e) {
            throw DolphinschedulerException.of("DolphinScheduler接口请求失败", e);
        }
        // 判断响应是否为成功
        if (!resp.isSuccessful()) {
            String errorBody = extractErrorBody(resp);
            throw DolphinschedulerException.of("DolphinScheduler接口请求失败, code: %s, 错误信息: %s, %s",
                    resp.code(), resp.message(), errorBody);
        }
        return resp.body();
    }

    private String extractErrorBody(Response<?> resp) {
        String errorBody = "";
        try (var responseError = resp.errorBody()) {
            if (responseError != null) {
                errorBody = responseError.string();
            }
        } catch (Exception e) {
            errorBody = "响应错误信息无法读取";
        }
        return errorBody;
    }
}