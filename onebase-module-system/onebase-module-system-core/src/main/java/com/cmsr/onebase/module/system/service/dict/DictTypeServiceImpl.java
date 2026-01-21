package com.cmsr.onebase.module.system.service.dict;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.system.dal.database.DictTypeRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.module.system.enums.dict.DictOwnerTypeEnum;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypeListReqVO;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypePageReqVO;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypeSaveReqVO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典类型 Service 实现类
 */
@Service
@Slf4j
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeRepository dictTypeRepository;

    @Resource
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Override
    public PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO pageReqVO) {
        return dictTypeRepository.findPage(pageReqVO);
    }

    @Override
    public DictTypeDO getDictType(Long id) {
        return dictTypeRepository.findById(id);
    }

    @Override
    public DictTypeDO getDictType(String type) {
        return dictTypeRepository.findOneByType(type);
    }

    @Override
    public Long createDictType(DictTypeSaveReqVO createReqVO) {
        // 插入字典类型
        DictTypeDO dictType = BeanUtils.toBean(createReqVO, DictTypeDO.class);

        // 如果未指定字典所有者类型，默认为租户类型
        if (StrUtil.isEmpty(dictType.getDictOwnerType())) {
            dictType.setDictOwnerType(DictOwnerTypeEnum.TENANT.getType());
        }

        // 校验字典类型的名字的唯一性（同一所有者范围内）
        validateDictTypeNameUnique(null, createReqVO.getName(), dictType.getDictOwnerType(), dictType.getDictOwnerId());
        // 校验字典类型的类型的唯一性（同一所有者范围内）
        validateDictTypeUnique(null, createReqVO.getType(), dictType.getDictOwnerType(), dictType.getDictOwnerId());

        dictType.setDeletedTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())); // 唯一索引，避免
                                                                                                           // null 值
        dictTypeRepository.insert(dictType);
        return dictType.getId();
    }

    @Override
    public void updateDictType(DictTypeSaveReqVO updateReqVO) {
        // 校验自己存在
        DictTypeDO existingDictType = validateDictTypeExists(updateReqVO.getId());
        
        // 更新字典类型
        DictTypeDO updateObj = BeanUtils.toBean(updateReqVO, DictTypeDO.class);
        
        // 校验字典类型的名字的唯一性（使用现有的所有者信息）
        validateDictTypeNameUnique(updateReqVO.getId(), updateReqVO.getName(), 
                existingDictType.getDictOwnerType(), existingDictType.getDictOwnerId());
        // 校验字典类型的类型的唯一性（使用现有的所有者信息）
        validateDictTypeUnique(updateReqVO.getId(), updateReqVO.getType(), 
                existingDictType.getDictOwnerType(), existingDictType.getDictOwnerId());

        dictTypeRepository.update(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // 校验是否存在
        DictTypeDO dictType = validateDictTypeExists(id);
        // 校验是否有字典数据
        if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        // 校验是否有实体字段引用
        long fieldCount = metadataEntityFieldApi.countByDictTypeId(id);
        if (fieldCount > 0) {
            throw exception(DICT_TYPE_HAS_ENTITY_FIELD_REFERENCE);
        }
        // 删除字典类型（软删除）
        dictTypeRepository.deleteById(id);
    }

    @Override
    public List<DictTypeDO> getDictTypeList() {
        return dictTypeRepository.findAllList();
    }

    @Override
    public List<DictTypeDO> getDictTypeList(DictTypeListReqVO reqVO) {
        return dictTypeRepository.findList(reqVO);
    }

    @Override
    public List<DictTypeDO> getDictTypesByIds(Collection<Long> ids) {
        return dictTypeRepository.findByIds(ids);
    }

    @Override
    public List<DictTypeDO> getDictTypesByTypes(Collection<String> types) {
        return dictTypeRepository.findListByTypes(types);
    }

    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name) {
        DictTypeDO dictType = dictTypeRepository.findOneByName(name);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    /**
     * 校验字典类型名称的唯一性（同一所有者范围内）
     *
     * @param id 字典类型ID（更新时使用，新建时传null）
     * @param name 字典类型名称
     * @param dictOwnerType 字典所有者类型
     * @param dictOwnerId 字典所有者ID
     */
    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name, String dictOwnerType, Long dictOwnerId) {
        DictTypeDO dictType = dictTypeRepository.findOneByNameAndOwner(name, dictOwnerType, dictOwnerId);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictTypeDO dictType = dictTypeRepository.findOneByType(type);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    /**
     * 校验字典类型的唯一性（同一所有者范围内）
     *
     * @param id 字典类型ID（更新时使用，新建时传null）
     * @param type 字典类型
     * @param dictOwnerType 字典所有者类型
     * @param dictOwnerId 字典所有者ID
     */
    @VisibleForTesting
    void validateDictTypeUnique(Long id, String type, String dictOwnerType, Long dictOwnerId) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictTypeDO dictType = dictTypeRepository.findOneByTypeAndOwner(type, dictOwnerType, dictOwnerId);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    DictTypeDO validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictTypeDO dictType = dictTypeRepository.findById(id);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

}
