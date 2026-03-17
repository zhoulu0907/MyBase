package com.cmsr.onebase.module.system.api.config;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = ApiConstants.NAME) // TODO 开发者：fallbackFactory =
@Tag(name = "RPC 服务 - 系统配置")
public interface SystemConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/config";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "根据configKey与appId查询应用系统配置")
    @Parameter(name = "configKey", description = "系统配置key", example = "null", required = true)
    @Parameter(name = "appId", description = "应用id", example = "null", required = true)
    CommonResult<Boolean> getAppConfig(@RequestParam("configKey") String configKey, @RequestParam("appId") long appId);


}
