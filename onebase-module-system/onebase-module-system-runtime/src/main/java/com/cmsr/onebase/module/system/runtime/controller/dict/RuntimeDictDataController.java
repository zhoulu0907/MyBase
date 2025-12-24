package com.cmsr.onebase.module.system.runtime.controller.dict;

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
import com.cmsr.onebase.module.system.vo.dictdata.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "企业服务 - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class RuntimeDictDataController {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeService dictTypeService;

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

    @GetMapping("/simple-list-by-types")
    @Operation(summary = "根据多个dict type获得字典数据列表", description = "批量获取多个字典类型的数据，按dictType分组返回")
    @Parameter(name = "dictTypes", description = "字典类型列表", required = false)
    @Parameter(name = "dictTypeIds", description = "字典类型ID列表", required = false)
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<Map<String, List<DictDataSimpleRespVO>>> getSimpleDictDataListByTypes(
            @RequestParam(value = "dictTypes", required = false) List<String> dictTypes,
            @RequestParam(value = "dictTypeIds", required = false) List<Long> dictTypeIds) {
        // 调用 Service 层批量查询（封装了所有业务逻辑）
        Map<String, List<DictDataDO>> dictDataMap = dictDataService.getDictDataMapByTypesAndTypeIds(dictTypes, dictTypeIds);
        
        // 转换DO到VO
        Map<String, List<DictDataSimpleRespVO>> result = new HashMap<>();
        for (Map.Entry<String, List<DictDataDO>> entry : dictDataMap.entrySet()) {
            result.put(entry.getKey(), BeanUtils.toBean(entry.getValue(), DictDataSimpleRespVO.class));
        }
        
        return success(result);
    }


    @GetMapping(value = "/get")
    @Operation(summary = "/查询字典数据详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:dict:query')")
    public CommonResult<DictDataRespVO> getDictData(@RequestParam("id") Long id) {
        DictDataDO dictData = dictDataService.getDictData(id);
        return success(BeanUtils.toBean(dictData, DictDataRespVO.class));
    }

}
