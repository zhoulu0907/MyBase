package com.cmsr.onebase.framework.config;

import com.cmsr.onebase.framework.core.DolphinSchedulerClient;
import com.cmsr.onebase.framework.core.DolphinSchedulerClientException;
import com.cmsr.onebase.framework.remote.InstanceApi;
import com.cmsr.onebase.framework.remote.ScheduleApi;
import com.cmsr.onebase.framework.remote.TaskApi;
import com.cmsr.onebase.framework.remote.TaskInstanceApi;
import com.cmsr.onebase.framework.remote.WorkflowApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * DolphinScheduler 客户端自动装配
 *
 * @author matianyu
 * @date 2025-10-15
 */
@Configuration
@ConditionalOnClass(Retrofit.class)
@EnableConfigurationProperties(DolphinSchedulerClientProperties.class)
@ConditionalOnProperty(prefix = "onebase.dolphinscheduler.client", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DolphinSchedulerClientAutoConfiguration {

    @Resource
    private DolphinSchedulerClientProperties props;

    /**
     * 缺省 ObjectMapper
     *
     * @return ObjectMapper
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper dolphinsObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * OkHttpClient Bean
     *
     * @return OkHttpClient
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "onebase.dolphinscheduler.client", name = {"baseUrl", "token"})
    public OkHttpClient dolphinsOkHttpClient() {
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BASIC;
        try {
            level = HttpLoggingInterceptor.Level.valueOf(props.getLogLevel());
        } catch (IllegalArgumentException ignored) {
            // 无效日志级别时使用默认 BASIC
        }
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(level);

        return new OkHttpClient.Builder()
                .connectTimeout(props.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(props.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(props.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .addInterceptor(new TokenInterceptor(props.getToken()))
                .addInterceptor(logging)
                .build();
    }

    /**
     * Retrofit Bean
     *
     * @param okHttpClient OkHttpClient
     * @param objectMapper ObjectMapper
     * @return Retrofit 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "onebase.dolphinscheduler.client", name = {"baseUrl", "token"})
    public Retrofit dolphinsRetrofit(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        if (props.getBaseUrl() == null) {
            throw new DolphinSchedulerClientException("baseUrl 未配置");
        }
        if (!props.getBaseUrl().endsWith("/")) {
            throw new DolphinSchedulerClientException("baseUrl 必须以 / 结尾，例如：http://host:port/dolphinscheduler/");
        }
        return new Retrofit.Builder()
                .baseUrl(props.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Retrofit.class)
    public WorkflowApi workflowApi(Retrofit retrofit) {
        return retrofit.create(WorkflowApi.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Retrofit.class)
    public InstanceApi instanceApi(Retrofit retrofit) {
        return retrofit.create(InstanceApi.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Retrofit.class)
    public ScheduleApi scheduleApi(Retrofit retrofit) {
        return retrofit.create(ScheduleApi.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Retrofit.class)
    public TaskInstanceApi taskInstanceApi(Retrofit retrofit) {
        return retrofit.create(TaskInstanceApi.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Retrofit.class)
    public TaskApi taskApi(Retrofit retrofit) {
        return retrofit.create(TaskApi.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Retrofit.class)
    public DolphinSchedulerClient dolphinSchedulerClient() {
        return new DolphinSchedulerClient();
    }
}
