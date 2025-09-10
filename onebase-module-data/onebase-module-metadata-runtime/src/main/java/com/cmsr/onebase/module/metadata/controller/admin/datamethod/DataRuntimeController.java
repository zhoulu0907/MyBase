package com.cmsr.onebase.module.metadata.controller.admin.datamethod;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行时 - 系统级动态数据操作接口
 * 仅包含 /insert, /update, /delete, /data, /data/page 五个接口
 */
@Tag(name = "运行时 - 动态数据接口")
@RestController
@RequestMapping("/metadata/data-method")
@Validated
public class DataRuntimeController {

    @Resource
    private MetadataDataMethodService dataMethodService;

    @PostMapping("/insert")
    @Operation(summary = "新增单条数据")
    public CommonResult<DynamicDataRespVO> createData(@Valid @RequestBody DynamicDataCreateReqVO reqVO) {
        return success(dataMethodService.createData(reqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新单条数据")
    public CommonResult<DynamicDataRespVO> updateData(@Valid @RequestBody DynamicDataUpdateReqVO reqVO) {
        return success(dataMethodService.updateData(reqVO));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除单条数据")
    public CommonResult<Boolean> deleteData(@Valid @RequestBody DynamicDataDeleteReqVO reqVO) {
        return success(dataMethodService.deleteData(reqVO));
    }

    @PostMapping("/data")
    @Operation(summary = "根据ID查询数据详情")
    public CommonResult<DynamicDataRespVO> getData(@Valid @RequestBody DynamicDataGetReqVO reqVO) {
        return success(dataMethodService.getData(reqVO));
    }

    @PostMapping("/data/page")
    @Operation(summary = "分页查询数据列表")
    public CommonResult<PageResult<DynamicDataRespVO>> getDataPage(@Valid @RequestBody DynamicDataPageReqVO reqVO) {
        return success(dataMethodService.getDataPage(reqVO));
    }
}
