package com.cmsr.onebase.module.app.api.appresource;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageRespDTO;
import com.cmsr.onebase.module.app.enums.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 页面")
public interface PageApi {
    String PREFIX = ApiConstants.PREFIX + "/page";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过 code 查询page")
    @Parameter(name = "code", description = "page code", example = "xxx", required = true)
    CommonResult<PageRespDTO> getUser(@RequestParam("code") String code);

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建page")
    CommonResult<Long> createPage(@RequestBody CreatePageDTO createPageDTO);

    @PostMapping(PREFIX + "/delete")
    @Operation(summary = "删除page")
    @Parameter(name = "code", description = "page code", required = true)
    CommonResult<Boolean> deletePage(@RequestParam("code") String code);
}
