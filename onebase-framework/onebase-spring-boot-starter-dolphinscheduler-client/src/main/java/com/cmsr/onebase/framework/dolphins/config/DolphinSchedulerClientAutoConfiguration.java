package com.cmsr.onebase.framework.dolphins.config;

import com.cmsr.onebase.dolphins.core.DolphinClient;
import com.cmsr.onebase.dolphins.remote.DolphinsRestTemplate;
import com.cmsr.onebase.dolphins.remote.request.DefaultHttpClientRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.RequestContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DolphinScheduler Client 自动配置类
 *
 * @author matianyu
 * @date 2025-10-23
 */
@Configuration
@EnableConfigurationProperties(DolphinSchedulerClientProperties.class)
public class DolphinSchedulerClientAutoConfiguration {

  // 硬编码的连接池配置参数
  private static final int MAX_TOTAL_CONNECTIONS = 100;
  private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
  private static final int CONNECTION_REQUEST_TIMEOUT = 3000;

  /**
   * 配置 RequestConfig
   *
   * @param properties 配置属性
   * @return RequestConfig
   */
  @Bean
  @ConditionalOnMissingBean
  public RequestConfig requestConfig(DolphinSchedulerClientProperties properties) {
    return RequestConfig.custom()
        .setConnectTimeout((int) properties.getConnectTimeout().toMillis())
        .setSocketTimeout((int) properties.getReadTimeout().toMillis())
        .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
        .build();
  }

  /**
   * 配置 DolphinsRestTemplate
   *
   * @param requestConfig 请求配置
   * @return DolphinsRestTemplate
   */
  @Bean
  @ConditionalOnMissingBean
  public DolphinsRestTemplate dolphinsRestTemplate(RequestConfig requestConfig) {

    DefaultHttpClientRequest httpClientRequest =
        new DefaultHttpClientRequest(
            HttpClients.custom()
                .addInterceptorLast(new RequestContent(true))
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(MAX_TOTAL_CONNECTIONS)
                .setMaxConnPerRoute(MAX_CONNECTIONS_PER_ROUTE)
                .build(),
            requestConfig);

    return new DolphinsRestTemplate(httpClientRequest);
  }

  /**
   * 配置 DolphinClient
   *
   * @param restTemplate REST 模板
   * @param properties 配置属性
   * @return DolphinClient
   */
  @Bean
  @ConditionalOnMissingBean
  public DolphinClient dolphinClient(
      DolphinsRestTemplate restTemplate, DolphinSchedulerClientProperties properties) {
    return new DolphinClient(properties.getToken(), properties.getBaseUrl(), restTemplate);
  }
}
