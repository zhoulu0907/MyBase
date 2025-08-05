package com.cmsr.onebase.module.app.api.appresource;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetRespDTO;
import com.cmsr.onebase.module.app.enums.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 页面集合")

public interface PageSetApi{

    String PREFIX = ApiConstants.PREFIX + "/page_set";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过 code 查询pageSet")
    @Parameter(name = "code", description = "pageset code", example = "xxx", required = true)
    CommonResult<PageSetRespDTO> getUser(@RequestParam("code") String code);

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建pageSet")
    CommonResult<String> createPageSet(@RequestBody CreatePageSetDTO createPageSetDTO);

    @DeleteMapping(PREFIX + "/delete")
    @Operation(summary = "删除pageSet")
    @Parameter(name = "code", description = "pageSet code", required = true)
    CommonResult<Boolean> deletePageSet(@RequestParam("code") String code);

}
