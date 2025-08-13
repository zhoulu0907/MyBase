package com.cmsr.onebase.module.app.api.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 应用")
public interface AppApplicationApi {

    @GetMapping(ApiConstants.PREFIX + "/count-application-by-tenantId")
    CommonResult<Long> countApplicationByTenantId(Long tenantId);

}
