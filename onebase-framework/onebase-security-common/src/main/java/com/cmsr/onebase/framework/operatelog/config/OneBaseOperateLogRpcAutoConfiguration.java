package com.cmsr.onebase.framework.operatelog.config;

import com.cmsr.onebase.framework.common.biz.system.logger.OperateLogCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * OperateLog 使用到 Feign 的配置项
 *
 */
@AutoConfiguration
// @EnableFeignClients(clients = {OperateLogCommonApi.class}) // 主要是引入相关的 API 服务
public class OneBaseOperateLogRpcAutoConfiguration {
}
