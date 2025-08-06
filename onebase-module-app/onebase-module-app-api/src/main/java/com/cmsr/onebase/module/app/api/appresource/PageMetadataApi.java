package com.cmsr.onebase.module.app.api.appresource;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageMetadataDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageMetadataRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.UpdatePageMetadataDTO;
import com.cmsr.onebase.module.app.enums.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 页面元数据")
public interface PageMetadataApi {

    String PREFIX = ApiConstants.PREFIX + "/page_metadata";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过 code 查询pageMetadata")
    @Parameter(name = "id", description = "pageMetadata id", example = "xxx", required = true)
    CommonResult<PageMetadataRespDTO> getPageMetadata(@RequestParam("id") Long id);

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建pageMetadata")
    CommonResult<Long> createPageMetadata(@RequestBody CreatePageMetadataDTO createPageMetadataDTO);

    @PostMapping(PREFIX + "/delete")
    @Operation(summary = "删除pageMetadata")
    @Parameter(name = "id", description = "pageMetadata id", required = true)
    CommonResult<Boolean> deletePageMetadata(@RequestParam("id") Long id);

    @PostMapping(PREFIX + "/update")
    @Operation(summary = "更新pageMetadata")
    CommonResult<Boolean> updatePageMetadata(@RequestBody UpdatePageMetadataDTO updatePageMetadataDTO);

}
