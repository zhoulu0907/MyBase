package com.cmsr.onebase.module.system.api.dict;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.module.system.service.dict.DictDataService;
import com.cmsr.onebase.module.system.service.dict.DictTypeService;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
@Primary // 由于 DictDataCommonApi 的存在，必须声明为 @Primary Bean
public class DictDataApiImpl implements DictDataApi {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeService dictTypeService;

    @Override
    public CommonResult<Boolean> validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
        return success(true);
    }

    @Override
    public CommonResult<List<DictDataRespDTO>> getDictDataList(String dictType) {
        List<DictDataDO> list = dictDataService.getDictDataListByDictType(dictType);
        return success(BeanUtils.toBean(list, DictDataRespDTO.class));
    }

    @Override
    public CommonResult<List<DictDataRespDTO>> getDictDataListByTypeId(Long dictTypeId) {
        DictTypeDO dictTypeDO = dictTypeService.getDictType(dictTypeId);
        String dictType = dictTypeDO == null ? null : dictTypeDO.getType();
        List<DictDataDO> list = dictDataService.getDictDataList(CommonStatusEnum.ENABLE.getStatus(), dictType);
        return success(BeanUtils.toBean(list, DictDataRespDTO.class));
    }

    @Override
    public CommonResult<Map<Long, List<DictDataRespDTO>>> getDictDataListByTypeIds(Collection<Long> dictTypeIds) {
        if (dictTypeIds == null || dictTypeIds.isEmpty()) {
            return success(Collections.emptyMap());
        }
        Map<Long, List<DictDataRespDTO>> result = new HashMap<>();
        for (Long id : dictTypeIds) {
            DictTypeDO dictTypeDO = dictTypeService.getDictType(id);
            String dictType = dictTypeDO == null ? null : dictTypeDO.getType();
            List<DictDataDO> list = dictDataService.getDictDataList(CommonStatusEnum.ENABLE.getStatus(), dictType);
            result.put(id, BeanUtils.toBean(list, DictDataRespDTO.class));
        }
        return success(result);
    }

}
