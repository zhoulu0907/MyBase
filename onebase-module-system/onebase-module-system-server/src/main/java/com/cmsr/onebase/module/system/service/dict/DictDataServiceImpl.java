package com.cmsr.onebase.module.system.service.dict;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
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
 * @author ruoyi
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

    //@Resource
    //private DictDataMapper dictDataMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    @TenantIgnore
    public List<DictDataDO> getDictDataList(Integer status, String dictType) {
        return TenantUtils.executeIgnore(() -> {
            ConfigStore cs = new DefaultConfigStore();
            if (status != null) {
                cs.and(Compare.EQUAL, "status", status);
            }
            if (cn.hutool.core.util.StrUtil.isNotBlank(dictType)) {
                cs.and(Compare.EQUAL, "dict_type", dictType);
            }
            List<DictDataDO> list = dataRepository.findAll(DictDataDO.class, cs);
            // 创建可变列表的副本以支持排序
            List<DictDataDO> mutableList = new ArrayList<>(list);
            mutableList.sort(COMPARATOR_TYPE_AND_SORT);
            return mutableList;
        });
    }

    @Override
    @TenantIgnore
    public PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO pageReqVO) {
        return TenantUtils.executeIgnore(() -> {
            try {
                ConfigStore cs = new DefaultConfigStore();
                
                // 构建查询条件
                if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getLabel())) {
                    cs.and(Compare.LIKE, "label", pageReqVO.getLabel());
                }
                if (cn.hutool.core.util.StrUtil.isNotBlank(pageReqVO.getDictType())) {
                    cs.and(Compare.LIKE, "dict_type", pageReqVO.getDictType());
                }
                if (pageReqVO.getStatus() != null) {
                    cs.and(Compare.EQUAL, "status", pageReqVO.getStatus());
                }
                
                // 添加排序条件，按ID降序排列
                cs.order("id", "DESC");
                
                return dataRepository.findPageWithConditions(
                        DictDataDO.class, 
                        cs, 
                        pageReqVO.getPageNo(), 
                        pageReqVO.getPageSize()
                );
            } catch (Exception e) {
                log.error("分页查询字典数据失败", e);
                throw new RuntimeException("分页查询字典数据失败", e);
            }
        });
    }

    @Override
    @TenantIgnore
    public DictDataDO getDictData(Long id) {
        return TenantUtils.executeIgnore(() -> dataRepository.findById(DictDataDO.class, id));
    }

    @Override
    public Long createDictData(DictDataSaveReqVO createReqVO) {
        // 校验字典类型有效
        validateDictTypeExists(createReqVO.getDictType());
        // 校验字典数据的值的唯一性
        validateDictDataValueUnique(null, createReqVO.getDictType(), createReqVO.getValue());

        // 插入字典类型
        DictDataDO dictData = BeanUtils.toBean(createReqVO, DictDataDO.class);
        dataRepository.insert(dictData);
        return dictData.getId();
    }

    @Override
    public void updateDictData(DictDataSaveReqVO updateReqVO) {
        // 校验自己存在
        validateDictDataExists(updateReqVO.getId());
        // 校验字典类型有效
        validateDictTypeExists(updateReqVO.getDictType());
        // 校验字典数据的值的唯一性
        validateDictDataValueUnique(updateReqVO.getId(), updateReqVO.getDictType(), updateReqVO.getValue());

        // 更新字典类型
        DictDataDO updateObj = BeanUtils.toBean(updateReqVO, DictDataDO.class);
        dataRepository.update(updateObj);
    }

    @Override
    public void deleteDictData(Long id) {
        // 校验是否存在
        validateDictDataExists(id);

        // 删除字典数据
        dataRepository.deleteById(DictDataDO.class, id);
    }

    @Override
    public long getDictDataCountByDictType(String dictType) {
        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "dict_type", dictType);
        List<DictDataDO> list = dataRepository.findAll(DictDataDO.class, cs);
        return list.size();
    }

    @VisibleForTesting
    public void validateDictDataValueUnique(Long id, String dictType, String value) {
        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "dict_type", dictType)
                .and(Compare.EQUAL, "value", value);
        DictDataDO dictData = dataRepository.findOne(DictDataDO.class, cs);
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
        DictDataDO dictData = TenantUtils.executeIgnore(() -> dataRepository.findById(DictDataDO.class, id));
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
        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "dict_type", dictType)
                .in("value", values);
        List<DictDataDO> dictDataList = dataRepository.findAll(DictDataDO.class, cs);
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
        return TenantUtils.executeIgnore(() -> {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "dict_type", dictType)
                    .and(Compare.EQUAL, "value", value);
            return dataRepository.findOne(DictDataDO.class, cs);
        });
    }

    @Override
    @TenantIgnore
    public DictDataDO parseDictData(String dictType, String label) {
        return TenantUtils.executeIgnore(() -> {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "dict_type", dictType)
                    .and(Compare.EQUAL, "label", label);
            return dataRepository.findOne(DictDataDO.class, cs);
        });
    }

    @Override
    @TenantIgnore
    public List<DictDataDO> getDictDataListByDictType(String dictType) {
        return TenantUtils.executeIgnore(() -> {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "dict_type", dictType);
            List<DictDataDO> list = dataRepository.findAll(DictDataDO.class, cs);
            // 创建可变列表的副本以支持排序
            List<DictDataDO> mutableList = new ArrayList<>(list);
            mutableList.sort(Comparator.comparing(DictDataDO::getSort));
            return mutableList;
        });
    }

}
