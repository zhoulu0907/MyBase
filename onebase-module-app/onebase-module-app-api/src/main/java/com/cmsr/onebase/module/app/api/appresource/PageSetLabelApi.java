package com.cmsr.onebase.module.app.api.appresource;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelCreateDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.PageSetLabelUpdateDTO;
import com.cmsr.onebase.module.app.enums.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 页面集合标签")
public interface PageSetLabelApi {

    String PREFIX = ApiConstants.PREFIX + "/page_set_label";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过 pagesetCode 查询页面集标签")
    @Parameter(name = "pagesetCode", description = "页面集编码", example = "home_set", required = true)
    CommonResult<List<PageSetLabelRespDTO>> getLabelsByPageSetCode(@RequestParam("pagesetCode") String pagesetCode);

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建页面集标签")
    CommonResult<Long> createPageSetLabel(@RequestBody PageSetLabelCreateDTO createDTO);

    @PostMapping(PREFIX + "/update")
    @Operation(summary = "更新页面集标签")
    CommonResult<Boolean> updatePageSetLabel(@RequestBody PageSetLabelUpdateDTO updateDTO);

    @PostMapping(PREFIX + "/delete")
    @Operation(summary = "删除页面集标签")
    @Parameter(name = "id", description = "标签ID", required = true)
    CommonResult<Boolean> deletePageSetLabel(@RequestParam("id") Long id);
}
