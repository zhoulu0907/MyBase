package com.cmsr.onebase.module.bpm.framework.rpc.config;

import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.PostApi;
import com.cmsr.onebase.module.system.api.dict.DictDataApi;
import com.cmsr.onebase.module.system.api.permission.PermissionApi;
import com.cmsr.onebase.module.system.api.permission.RoleApi;
import com.cmsr.onebase.module.system.api.sms.SmsSendApi;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "bpmRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {RoleApi.class, DeptApi.class, PostApi.class, AdminUserApi.class, SmsSendApi.class, DictDataApi.class,
        PermissionApi.class})
public class RpcConfiguration {
}
