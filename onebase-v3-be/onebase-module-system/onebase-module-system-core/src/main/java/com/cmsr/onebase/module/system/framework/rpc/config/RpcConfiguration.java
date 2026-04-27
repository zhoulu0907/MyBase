package com.cmsr.onebase.module.system.framework.rpc.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import com.cmsr.onebase.module.infra.api.config.ConfigApi;
import com.cmsr.onebase.module.infra.api.file.FileApi;

@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
// @EnableFeignClients(clients = {FileApi.class, ConfigApi.class})
public class RpcConfiguration {
}
