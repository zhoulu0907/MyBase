package com.cmsr.onebase.module.app.build.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.custombutton.AppCustomButtonService;
import com.cmsr.onebase.module.app.build.vo.custombutton.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "应用资源管理-自定义按钮")
@RestController
@RequestMapping("/app/custom-button")
@Validated
public class AppCustomButtonController {

    @Resource
    private AppCustomButtonService customButtonService;

    @PostMapping("/page")
    @Operation(summary = "查询自定义按钮列表")
    public CommonResult<CustomButtonPageRespVO> page(@Valid @RequestBody CustomButtonPageReqVO reqVO) {
        return CommonResult.success(customButtonService.page(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "查询自定义按钮详情")
    public CommonResult<CustomButtonDetailRespVO> get(@RequestParam("id") Long id) {
        return CommonResult.success(customButtonService.get(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建自定义按钮")
    public CommonResult<Long> create(@Valid @RequestBody CustomButtonSaveReqVO reqVO) {
        return CommonResult.success(customButtonService.create(reqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新自定义按钮")
    public CommonResult<Boolean> update(@Valid @RequestBody CustomButtonSaveReqVO reqVO) {
        return CommonResult.success(customButtonService.update(reqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除自定义按钮")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        return CommonResult.success(customButtonService.delete(id));
    }

    @PostMapping("/update-status")
    @Operation(summary = "更新自定义按钮状态")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody CustomButtonStatusReqVO reqVO) {
        return CommonResult.success(customButtonService.updateStatus(reqVO));
    }

    @PostMapping("/sort")
    @Operation(summary = "更新自定义按钮排序")
    public CommonResult<Boolean> sort(@Valid @RequestBody CustomButtonSortReqVO reqVO) {
        return CommonResult.success(customButtonService.sort(reqVO));
    }
}
