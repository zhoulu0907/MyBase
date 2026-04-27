package com.cmsr.onebase.module.system.api.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME) // TODO 开发者：fallbackFactory =
@Tag(name = "RPC 服务 - 管理员用户")
public interface AdminUserRoleApi {
    String PREFIX = ApiConstants.PREFIX + "/user";

    List<String> getUserRoleByRoleIdAndTenantId(@RequestParam("id") Long id,Long tenantId);


}
