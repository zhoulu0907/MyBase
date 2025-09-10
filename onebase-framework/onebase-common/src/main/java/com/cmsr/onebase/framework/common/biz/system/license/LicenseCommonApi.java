package com.cmsr.onebase.framework.common.biz.system.license;

import com.cmsr.onebase.framework.common.biz.system.license.dto.LicenseRespDTO;
import com.cmsr.onebase.framework.common.enums.RpcConstants;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = RpcConstants.SYSTEM_NAME) // TODO 开发者：fallbackFactory =
@Tag(name = "RPC 服务 - License")
public interface LicenseCommonApi {

    String PREFIX = RpcConstants.SYSTEM_PREFIX + "/license";

    @GetMapping(PREFIX + "/get-by-status")
    @Operation(summary = "根据状态获取License")
    @Parameter(name = "status", description = "状态", required = true, example = "enable")
    CommonResult<LicenseRespDTO> getActiveLicense();

}