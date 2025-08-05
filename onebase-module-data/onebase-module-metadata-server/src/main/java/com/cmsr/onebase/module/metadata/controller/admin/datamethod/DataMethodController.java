package com.cmsr.onebase.module.metadata.controller.admin.datamethod;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.MetadataDataMethodService;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 数据方法管理
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 数据方法管理")
@RestController
@RequestMapping("/metadata/data-method")
@Validated
public class DataMethodController {

    @Resource
    private MetadataDataMethodService dataMethodService;

    @PostMapping("/list")
    @Operation(summary = "查询业务实体的数据方法列表")
    public CommonResult<List<DataMethodRespVO>> getDataMethodList(@Valid @RequestBody DataMethodQueryReqVO reqVO) {
        // 将Controller层的VO转换为Service层的VO
        DataMethodQueryVO queryVO = new DataMethodQueryVO(reqVO.getEntityId(), reqVO.getMethodType(), reqVO.getKeyword());
        List<DataMethodRespVO> methods = dataMethodService.getDataMethodList(queryVO);
        return success(methods);
    }

    @PostMapping("/detail")
    @Operation(summary = "获取指定数据方法的详细信息")
    public CommonResult<DataMethodDetailRespVO> getDataMethodDetail(@Valid @RequestBody DataMethodDetailQueryReqVO reqVO) {
        DataMethodDetailRespVO detail = dataMethodService.getDataMethodDetail(Long.valueOf(reqVO.getEntityId()), reqVO.getMethodCode());
        return success(detail);
    }

    // ========== 系统级别的动态数据操作接口 ==========

    @PostMapping("/insert")
    @Operation(summary = "新增单条数据")
    @PreAuthorize("@ss.hasPermission('metadata:data:create')")
    public CommonResult<DynamicDataRespVO> createData(@Valid @RequestBody DynamicDataCreateReqVO reqVO) {
        DynamicDataRespVO result = dataMethodService.createData(reqVO);
        return success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新单条数据")
    @PreAuthorize("@ss.hasPermission('metadata:data:update')")
    public CommonResult<DynamicDataRespVO> updateData(@Valid @RequestBody DynamicDataUpdateReqVO reqVO) {
        DynamicDataRespVO result = dataMethodService.updateData(reqVO);
        return success(result);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除单条数据")
    @PreAuthorize("@ss.hasPermission('metadata:data:delete')")
    public CommonResult<Boolean> deleteData(@Valid @RequestBody DynamicDataDeleteReqVO reqVO) {
        Boolean result = dataMethodService.deleteData(reqVO);
        return success(result);
    }

    @PostMapping("/data")
    @Operation(summary = "根据ID查询数据详情")
    @PreAuthorize("@ss.hasPermission('metadata:data:query')")
    public CommonResult<DynamicDataRespVO> getData(@Valid @RequestBody DynamicDataGetReqVO reqVO) {
        DynamicDataRespVO result = dataMethodService.getData(reqVO);
        return success(result);
    }

    @PostMapping("/data/page")
    @Operation(summary = "分页查询数据列表")
    @PreAuthorize("@ss.hasPermission('metadata:data:query')")
    public CommonResult<PageResult<DynamicDataRespVO>> getDataPage(@Valid @RequestBody DynamicDataPageReqVO reqVO) {
        PageResult<DynamicDataRespVO> result = dataMethodService.getDataPage(reqVO);
        return success(result);
    }

}
