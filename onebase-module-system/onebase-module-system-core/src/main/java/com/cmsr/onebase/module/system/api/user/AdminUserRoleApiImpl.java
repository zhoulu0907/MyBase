package com.cmsr.onebase.module.system.api.user;


import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AdminUserRoleApiImpl implements AdminUserRoleApi {

    @Resource
    private AdminUserService userService;
    @Override
    @TenantIgnore
    public List<String> getUserRoleByRoleIdAndTenantId(Long id,Long tenantId) {
      return   userService.getUserRoleByRoleIdAndTenantId(id,tenantId);
    }
}
