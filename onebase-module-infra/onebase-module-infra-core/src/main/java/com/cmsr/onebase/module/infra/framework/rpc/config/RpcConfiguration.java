package com.cmsr.onebase.module.infra.framework.rpc.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "infraRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients("com.cmsr.onebase.module.infra.api")
public class RpcConfiguration {
}