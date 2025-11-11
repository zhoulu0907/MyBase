package com.cmsr.onebase.module.system.build.controller.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.excel.core.util.ExcelUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.module.system.service.dict.DictDataService;
import com.cmsr.onebase.module.system.service.dict.DictTypeService;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataBatchReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataBatchRespVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataInsertReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataPageReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataRespVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataSimpleRespVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataUpdateReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataUpdateStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class DictDataController {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeService dictTypeService;

    @PostMapping("/create")
    @Operation(summary = "新增字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictData(@Valid @RequestBody DictDataInsertReqVO createReqVO) {
        Long dictDataId = dictDataService.createDictData(createReqVO);
        return success(dictDataId);
    }

    @PostMapping("/update")
    @Operation(summary = "修改字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictData(@Valid @RequestBody DictDataUpdateReqVO updateReqVO) {
        dictDataService.updateDictData(updateReqVO);
        return success(true);
    }

    @PostMapping("/update-status")
    @Operation(summary = "修改字典数据状态")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictDataStatus(@Valid @RequestBody DictDataUpdateStatusVO updateStatusVO) {
        dictDataService.updateDictDataStatus(updateStatusVO.getId(), updateStatusVO.getStatus());
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictData(Long id) {
        dictDataService.deleteDictData(id);
        return success(true);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得全部字典数据列表", description = "一般用于管理后台缓存字典数据在本地")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictDataSimpleRespVO>> getSimpleDictDataList() {
        List<DictDataDO> list = dictDataService.getDictDataList(CommonStatusEnum.ENABLE.getStatus(), null);
        return success(BeanUtils.toBean(list, DictDataSimpleRespVO.class));
    }

    @GetMapping("/simple-list-by-type")
    @Operation(summary = "根据dict type获得字典数据列表", description = "一般用于前段获取")
    @Parameter(name = "dictType", description = "字典类型", required = false)
    @Parameter(name = "dictTypeId", description = "字典类型ID", required = false)
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictDataSimpleRespVO>> getSimpleDictDataListByType(
            @RequestParam(value = "dictType", required = false) String dictType,
            @RequestParam(value = "dictTypeId", required = false) Long dictTypeId) {
        // 如果提供了dictTypeId，则通过dictTypeId查询字典类型
        if (dictTypeId != null) {
            DictTypeDO dictTypeDO = dictTypeService.getDictType(dictTypeId);
            if (dictTypeDO != null) {
                dictType = dictTypeDO.getType();
            }
        }
        List<DictDataDO> list = dictDataService.getDictDataList(CommonStatusEnum.ENABLE.getStatus(), dictType);
        return success(BeanUtils.toBean(list, DictDataSimpleRespVO.class));
    }

    /**
     * 根据字典类型ID获得字典数据列表
     *
     * @param dictTypeId 字典类型ID
     * @return 字典数据列表
     */
    @GetMapping("/simple-list-by-type-id")
    @Operation(summary = "根据字典类型ID获得字典数据列表", description = "一般用于前段获取")
    @Parameter(name = "dictTypeId", description = "字典类型ID", required = true, example = "1")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictDataSimpleRespVO>> getSimpleDictDataListByTypeId(
            @RequestParam("dictTypeId") Long dictTypeId) {
        // 通过dictTypeId查询字典类型
        DictTypeDO dictTypeDO = dictTypeService.getDictType(dictTypeId);
        String dictType = null;
        if (dictTypeDO != null) {
            dictType = dictTypeDO.getType();
        }
        // 查询字典数据列表
        List<DictDataDO> list = dictDataService.getDictDataList(CommonStatusEnum.ENABLE.getStatus(), dictType);
        return success(BeanUtils.toBean(list, DictDataSimpleRespVO.class));
    }
    @GetMapping("/page")
    @Operation(summary = "/获得字典类型的分页列表")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictDataRespVO>> getDictTypePage(@Valid DictDataPageReqVO pageReqVO) {
        PageResult<DictDataDO> pageResult = dictDataService.getDictDataPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DictDataRespVO.class));
    }

    @GetMapping(value = "/get")
    @Operation(summary = "/查询字典数据详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictDataRespVO> getDictData(@RequestParam("id") Long id) {
        DictDataDO dictData = dictDataService.getDictData(id);
        return success(BeanUtils.toBean(dictData, DictDataRespVO.class));
    }

    @GetMapping("/export")
    @Operation(summary = "导出字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:export')")
    public void export(HttpServletResponse response, @Valid DictDataPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictDataDO> list = dictDataService.getDictDataPage(exportReqVO).getList();
        // 输出
        ExcelUtils.write(response, "字典数据.xls", "数据", DictDataRespVO.class,
                BeanUtils.toBean(list, DictDataRespVO.class));
    }

    @PostMapping("/batch-operate")
    @Operation(summary = "批量操作字典数据（批量新增、更新、删除）")
    @PreAuthorize("@ss.hasPermission('system:dict:write')")
    public CommonResult<DictDataBatchRespVO> batchOperateDictData(@Valid @RequestBody DictDataBatchReqVO batchReqVO) {
        DictDataBatchRespVO result = dictDataService.batchOperateDictData(batchReqVO);
        return success(result);
    }
}
