package com.cmsr.onebase.framework.apilog.config;

import com.cmsr.onebase.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import com.cmsr.onebase.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * API 日志使用到 Feign 的配置项
 *
 */
@AutoConfiguration
@EnableFeignClients(clients = {ApiAccessLogCommonApi.class, ApiErrorLogCommonApi.class}) // 主要是引入相关的 API 服务
public class YudaoApiLogRpcAutoConfiguration {
}
