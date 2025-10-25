package com.cmsr.onebase.framework.ds.client;

import com.cmsr.onebase.framework.ds.exception.DolphinschedulerException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Service
public class DolphinSchedulerClient {

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
    private String etlProjectCode;

    @Value("${onebase.scheduler.flow-project}")
    private String flowProjectCode;

    @Value("${onebase.scheduler.tenant}")
    private String tenantCode;

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
        // optimize: add interceptors if needed
        OkHttpClient httpClient = httpClientBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.client(httpClient);

        Retrofit retrofitClient = retrofitBuilder
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .baseUrl(address)
                .build();

        this.dsClientStub = retrofitClient.create(DolphinschedulerClientStub.class);
    }

    private Response<?> doHttpCall(Call<?> call) {
        Response<?> resp;
        try {
            resp = call.execute();
        } catch (Exception e) {
            throw DolphinschedulerException.of("接口请求失败", e);
        }
        return resp;
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

    private Response<?> execute(Call<?> call) {
        Response<?> resp = doHttpCall(call);
        if (resp.isSuccessful()) {
            return resp;
        }
        String errorBody = extractErrorBody(resp);
        throw DolphinschedulerException.of("请求DS失败, code: %s, 错误信息: %s, %s",
                resp.code(), resp.message(), errorBody);
    }
}