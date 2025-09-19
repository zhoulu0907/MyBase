package com.cmsr.onebase.module.system.service.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.tools.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataInsertReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataPageReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataUpdateReqVO;
import com.cmsr.onebase.module.system.dal.database.DictDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典数据 Service 实现类
 *
 * @author ma
 */
@Service
@Slf4j
public class DictDataServiceImpl implements DictDataService {

    /**
     * 排序 dictType > sort
     */
    private static final Comparator<DictDataDO> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictDataDO::getDictType)
            .thenComparingInt(DictDataDO::getSort);

    @Resource
    private DictTypeService dictTypeService;

    @Resource
    private DictDataRepository dictDataRepository;

    @Override
    @TenantIgnore
    public List<DictDataDO> getDictDataList(Integer status, String dictType) {
        return TenantUtils.executeIgnore(() -> {
            List<DictDataDO> list = dictDataRepository.findListByStatusAndDictType(status, dictType);
            // 创建可变列表的副本以支持排序
            List<DictDataDO> mutableList = new ArrayList<>(list);
            mutableList.sort(COMPARATOR_TYPE_AND_SORT);
            return mutableList;
        });
    }

    @Override
    @TenantIgnore
    public PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO pageReqVO) {
        return TenantUtils.executeIgnore(() -> dictDataRepository.findPage(pageReqVO));
    }

    @Override
    @TenantIgnore
    public DictDataDO getDictData(Long id) {
        return TenantUtils.executeIgnore(() -> dictDataRepository.findById(id));
    }

    @Override
    public Long createDictData(DictDataInsertReqVO createReqVO) {
        // 校验字典类型有效
        validateDictTypeExists(createReqVO.getDictType());
        // 校验字典数据的值的唯一性
        validateDictDataValueUnique(null, createReqVO.getDictType(), createReqVO.getValue());

        // 插入字典类型
        DictDataDO dictData = BeanUtils.toBean(createReqVO, DictDataDO.class);
        dictDataRepository.insert(dictData);
        return dictData.getId();
    }

    @Override
    public void updateDictData(DictDataUpdateReqVO updateReqVO) {
        // 校验自己存在
        validateDictDataExists(updateReqVO.getId());
        // 校验字典类型有效
        validateDictTypeExists(updateReqVO.getDictType());
        // 校验字典数据的值的唯一性
        validateDictDataValueUnique(updateReqVO.getId(), updateReqVO.getDictType(), updateReqVO.getValue());

        // 更新字典类型
        DictDataDO updateObj = BeanUtils.toBean(updateReqVO, DictDataDO.class);
        dictDataRepository.update(updateObj);
    }

    @Override
    public void updateDictDataStatus(Long id, Integer status) {
        // 校验字典数据存在
        validateDictDataExists(id);

        // 更新状态
        DictDataDO updateObj = new DictDataDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        dictDataRepository.update(updateObj);
    }

    @Override
    public void deleteDictData(Long id) {
        // 校验是否存在
        validateDictDataExists(id);

        // 删除字典数据
        dictDataRepository.deleteById(id);
    }

    @Override
    public long getDictDataCountByDictType(String dictType) {
        return dictDataRepository.countByDictType(dictType);
    }

    @VisibleForTesting
    public void validateDictDataValueUnique(Long id, String dictType, String value) {
        DictDataDO dictData = dictDataRepository.findOneByDictTypeAndValue(dictType, value);
        if (dictData == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典数据
        if (id == null) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
        if (!dictData.getId().equals(id)) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void validateDictDataExists(Long id) {
        if (id == null) {
            return;
        }
        
        // 由于 DictDataDO 有 @TenantIgnore 注解，需要忽略租户过滤
        DictDataDO dictData = TenantUtils.executeIgnore(() -> dictDataRepository.findById(id));
        if (dictData == null) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    public void validateDictTypeExists(String type) {
        DictTypeDO dictType = dictTypeService.getDictType(type);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dictType.getStatus())) {
            throw exception(DICT_TYPE_NOT_ENABLE);
        }
    }

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) {
            return;
        }
        List<DictDataDO> dictDataList = dictDataRepository.findListByDictTypeAndValues(dictType, values);
        Map<String, DictDataDO> dictDataMap = CollectionUtils.convertMap(dictDataList, DictDataDO::getValue);
        // 校验
        values.forEach(value -> {
            DictDataDO dictData = dictDataMap.get(value);
            if (dictData == null) {
                throw exception(DICT_DATA_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictData.getStatus())) {
                throw exception(DICT_DATA_NOT_ENABLE, dictData.getLabel());
            }
        });
    }

    @Override
    @TenantIgnore
    public DictDataDO getDictData(String dictType, String value) {
        return TenantUtils.executeIgnore(() -> dictDataRepository.findOneByDictTypeAndValue(dictType, value));
    }

    @Override
    @TenantIgnore
    public DictDataDO parseDictData(String dictType, String label) {
        return TenantUtils.executeIgnore(() -> dictDataRepository.findOneByDictTypeAndLabel(dictType, label));
    }

    @Override
    @TenantIgnore
    public List<DictDataDO> getDictDataListByDictType(String dictType) {
        return TenantUtils.executeIgnore(() -> {
            List<DictDataDO> list = dictDataRepository.findListByDictType(dictType);
            // 创建可变列表的副本以支持排序
            List<DictDataDO> mutableList = new ArrayList<>(list);
            mutableList.sort(Comparator.comparing(DictDataDO::getSort));
            return mutableList;
        });
    }

}
