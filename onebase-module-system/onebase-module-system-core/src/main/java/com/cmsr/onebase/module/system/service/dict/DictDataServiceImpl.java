package com.cmsr.onebase.module.system.service.dict;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataBatchReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataBatchRespVO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            // 按 sort 字段排序
            mutableList.sort(Comparator.comparingInt(DictDataDO::getSort));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDataBatchRespVO batchOperateDictData(DictDataBatchReqVO batchReqVO) {
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<Long> deletedIds = new ArrayList<>();

        // 批量新增
        if (CollUtil.isNotEmpty(batchReqVO.getCreateList())) {
            for (DictDataInsertReqVO createReqVO : batchReqVO.getCreateList()) {
                Long id = createDictData(createReqVO);
                createdIds.add(id);
            }
            log.info("[batchOperateDictData] 批量新增字典数据成功，数量: {}", createdIds.size());
        }

        // 批量更新
        if (CollUtil.isNotEmpty(batchReqVO.getUpdateList())) {
            for (DictDataUpdateReqVO updateReqVO : batchReqVO.getUpdateList()) {
                updateDictData(updateReqVO);
                updatedIds.add(updateReqVO.getId());
            }
            log.info("[batchOperateDictData] 批量更新字典数据成功，数量: {}", updatedIds.size());
        }

        // 批量删除
        if (CollUtil.isNotEmpty(batchReqVO.getDeleteIds())) {
            for (Long id : batchReqVO.getDeleteIds()) {
                deleteDictData(id);
                deletedIds.add(id);
            }
            log.info("[batchOperateDictData] 批量删除字典数据成功，数量: {}", deletedIds.size());
        }

        // 构建返回结果
        DictDataBatchRespVO respVO = new DictDataBatchRespVO();
        respVO.setCreatedIds(createdIds);
        respVO.setUpdatedIds(updatedIds);
        respVO.setDeletedIds(deletedIds);
        respVO.setCreateCount(createdIds.size());
        respVO.setUpdateCount(updatedIds.size());
        respVO.setDeleteCount(deletedIds.size());

        return respVO;
    }

    @Override
    @TenantIgnore
    public Map<String, List<DictDataDO>> getDictDataMapByTypes(Collection<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return Collections.emptyMap();
        }
        // 批量查询所有字典类型的数据（一次性查询）
        List<DictDataDO> allDataList = dictDataRepository.findListByStatusAndDictTypes(
                CommonStatusEnum.ENABLE.getStatus(), dictTypes);
        // 按 dictType 分组
        return allDataList.stream()
                .collect(Collectors.groupingBy(DictDataDO::getDictType));
    }

    @Override
    @TenantIgnore
    public Map<String, List<DictDataDO>> getDictDataMapByTypesAndTypeIds(Collection<String> dictTypes, Collection<Long> dictTypeIds) {
        // 收集所有需要查询的字典类型
        Set<String> allDictTypes = new HashSet<>();
        
        // 添加直接传入的字典类型
        if (dictTypes != null && !dictTypes.isEmpty()) {
            allDictTypes.addAll(dictTypes);
        }
        
        // 如果提供了dictTypeIds，批量查询并转换为dictTypes
        if (dictTypeIds != null && !dictTypeIds.isEmpty()) {
            List<DictTypeDO> dictTypeList = dictTypeService.getDictTypesByIds(dictTypeIds);
            for (DictTypeDO dictTypeDO : dictTypeList) {
                if (dictTypeDO != null && dictTypeDO.getType() != null) {
                    allDictTypes.add(dictTypeDO.getType());
                }
            }
        }
        
        // 批量查询并按dictType分组
        return getDictDataMapByTypes(allDictTypes);
    }

}
