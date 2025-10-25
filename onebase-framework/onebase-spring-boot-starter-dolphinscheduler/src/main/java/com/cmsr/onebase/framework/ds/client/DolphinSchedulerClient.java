package com.cmsr.onebase.framework.ds.client;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.exception.DolphinschedulerException;
import com.cmsr.onebase.framework.ds.model.common.Result;
import com.cmsr.onebase.framework.ds.model.task.TaskDefinition;
import com.cmsr.onebase.framework.ds.model.task.TaskLocation;
import com.cmsr.onebase.framework.ds.model.task.TaskRelation;
import com.cmsr.onebase.framework.ds.model.task.def.AbstractTask;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionResp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Lazy
@Setter
@Component
public class DolphinSchedulerClient {
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

    @Value("${onebase.scheduler.etl-project}")
    private Long etlProjectCode;

    @Value("${onebase.scheduler.flow-project}")
    private Long flowProjectCode;

    @Value("${onebase.scheduler.tenant}")
    private String tenantCode;

    @Value("${onebase.scheduler.env}")
    private Long environmentCode;

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
        OkHttpClient httpClient = httpClientBuilder
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    return chain.proceed(request);
                })
                .build();

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

    public Long createHttpWorkflow(String flowName, HttpTask httpTask, String description) {
        return createSingletonWorkflow(this.flowProjectCode, flowName, httpTask, description);
    }

    public Long createSingletonWorkflow(Long projectCode,
                                        String flowName,
                                        AbstractTask task,
                                        String description) {
        // 1. verify-name unique
        Result<Object> uniqueResp = execute(dsClientStub.verifyNameUniqueInProject(projectCode, flowName));
        if (uniqueResp.getFailed()) {
            throw DolphinschedulerException.of("名称%s已存在", flowName);
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