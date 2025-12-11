package com.cmsr.onebase.module.system.api.dict;

import com.cmsr.onebase.framework.common.biz.system.dict.DictDataCommonApi;
import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = ApiConstants.NAME) // TODO 开发者：fallbackFactory =
@Tag(name = "RPC 服务 - 字典数据")
public interface DictDataApi extends DictDataCommonApi {

    String PREFIX = ApiConstants.PREFIX + "/dict-data";

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验字典数据们是否有效")
    @Parameters({
        @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true),
        @Parameter(name = "descriptions", description = "字典数据值的数组", example = "1,2", required = true)
    })
    CommonResult<Boolean> validateDictDataList(@RequestParam("dictType") String dictType,
                                               @RequestParam("values") Collection<String> values);

    @GetMapping(PREFIX + "/list-by-type-id")
    @Operation(summary = "根据字典类型ID获得字典数据列表")
    @Parameter(name = "dictTypeId", description = "字典类型ID", example = "1", required = true)
    CommonResult<List<DictDataRespDTO>> getDictDataListByTypeId(@RequestParam("dictTypeId") Long dictTypeId);

    @GetMapping(PREFIX + "/list-by-type-ids")
    @Operation(summary = "根据多个字典类型ID获得字典数据列表")
    @Parameter(name = "dictTypeIds", description = "字典类型ID数组", required = true)
    CommonResult<Map<Long, List<DictDataRespDTO>>> getDictDataListByTypeIds(@RequestParam("dictTypeIds") Collection<Long> dictTypeIds);

}
