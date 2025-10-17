package com.cmsr.onebase.framework.dolphins.config;

import com.cmsr.onebase.framework.dolphins.api.ProjectApi;
import com.cmsr.onebase.framework.dolphins.api.ScheduleApi;
import com.cmsr.onebase.framework.dolphins.api.TaskApi;
import com.cmsr.onebase.framework.dolphins.api.TaskInstanceApi;
import com.cmsr.onebase.framework.dolphins.api.WorkflowApi;
import com.cmsr.onebase.framework.dolphins.api.WorkflowInstanceApi;
import com.cmsr.onebase.framework.dolphins.interceptor.AuthenticationInterceptor;
import com.cmsr.onebase.framework.dolphins.interceptor.ErrorHandlingInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Retrofit 配置类
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Configuration
public class RetrofitConfig {

    @Resource
    private DolphinSchedulerProperties properties;

    @Resource
    private AuthenticationInterceptor authenticationInterceptor;

    @Resource
    private ErrorHandlingInterceptor errorHandlingInterceptor;

    /**
     * 配置 ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 注册 Java 8 时间模块，并配置日期时间格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 配置 LocalDateTime 反序列化器，支持 DolphinScheduler 的日期时间格式（空格分隔）
        javaTimeModule.addDeserializer(
            java.time.LocalDateTime.class,
            new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            )
        );
        mapper.registerModule(javaTimeModule);
        
        // 禁用将日期序列化为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 允许空对象
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return mapper;
    }

    /**
     * 配置 OkHttpClient
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(properties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .addInterceptor(authenticationInterceptor)
                .addInterceptor(errorHandlingInterceptor);

        // 配置日志拦截器
        if (properties.getLogLevel() != DolphinSchedulerProperties.LogLevel.NONE) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(convertLogLevel(properties.getLogLevel()));
            builder.addInterceptor(loggingInterceptor);
        }

        // 配置重试（固定启用，最大重试3次）
        builder.addInterceptor(chain -> {
            okhttp3.Request request = chain.request();
            okhttp3.Response response = null;
            int tryCount = 0;
            final int maxRetries = 3;

            while (tryCount < maxRetries) {
                try {
                    response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                    // 响应不成功时，如果还有重试次数，关闭当前响应并重试
                    if (tryCount < maxRetries - 1) {
                        response.close();
                        response = null;
                    }
                    tryCount++;
                } catch (Exception e) {
                    // 异常时也要关闭响应
                    if (response != null) {
                        response.close();
                        response = null;
                    }
                    tryCount++;
                    if (tryCount >= maxRetries) {
                        throw e;
                    }
                }
            }

            // 所有重试失败后，如果有响应则返回最后一次的响应，否则抛出异常
            if (response != null) {
                return response;
            }
            throw new RuntimeException("请求失败且无可用响应");
        });

        return builder.build();
    }

    /**
     * 配置 Retrofit
     */
    @Bean
    public Retrofit retrofit(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl(properties.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    /**
     * 创建 ProjectApi Bean
     */
    @Bean
    public ProjectApi projectApi(Retrofit retrofit) {
        return retrofit.create(ProjectApi.class);
    }

    /**
     * 创建 TaskApi Bean
     */
    @Bean
    public TaskApi taskApi(Retrofit retrofit) {
        return retrofit.create(TaskApi.class);
    }

    /**
     * 创建 TaskInstanceApi Bean
     */
    @Bean
    public TaskInstanceApi taskInstanceApi(Retrofit retrofit) {
        return retrofit.create(TaskInstanceApi.class);
    }

    /**
     * 创建 WorkflowApi Bean
     */
    @Bean
    public WorkflowApi workflowApi(Retrofit retrofit) {
        return retrofit.create(WorkflowApi.class);
    }

    /**
     * 创建 ScheduleApi Bean
     */
    @Bean
    public ScheduleApi scheduleApi(Retrofit retrofit) {
        return retrofit.create(ScheduleApi.class);
    }

    /**
     * 创建 WorkflowInstanceApi Bean
     */
    @Bean
    public WorkflowInstanceApi workflowInstanceApi(Retrofit retrofit) {
        return retrofit.create(WorkflowInstanceApi.class);
    }

    /**
     * 转换日志级别
     */
    private HttpLoggingInterceptor.Level convertLogLevel(DolphinSchedulerProperties.LogLevel logLevel) {
        return switch (logLevel) {
            case BASIC -> HttpLoggingInterceptor.Level.BASIC;
            case HEADERS -> HttpLoggingInterceptor.Level.HEADERS;
            case BODY -> HttpLoggingInterceptor.Level.BODY;
            default -> HttpLoggingInterceptor.Level.NONE;
        };
    }
}
