package com.cmsr.onebase.module.metadata.api.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * RPC 服务 - 数据源管理
 *
 * @author matianyu
 * @date 2025-08-13
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 数据源管理")
public interface MetadataDatasourceApi {

    String PREFIX = ApiConstants.PREFIX + "/datasource";

    @PostMapping(PREFIX + "/create-default")
    @Operation(summary = "创建默认数据源")
    @Parameter(name = "appId", description = "应用ID", required = true, example = "1024")
    CommonResult<String> createDefaultDatasource(@RequestParam("appId") Long appId);

}
