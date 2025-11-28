package com.cmsr.onebase.module.metadata.core.service.entity.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

/**
 * 业务实体 Service 核心实现类 - 只处理基础数据操作，不依赖VO
 *
 * @author matianyu  
 * @date 2025-09-15
 */
@Service
@Slf4j
public class MetadataBusinessEntityCoreServiceImpl implements MetadataBusinessEntityCoreService {

    @Resource
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBusinessEntity(@Valid MetadataBusinessEntityDO businessEntity) {
        metadataBusinessEntityRepository.insert(businessEntity);
        return businessEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBusinessEntity(@Valid MetadataBusinessEntityDO businessEntity) {
        validateBusinessEntityExists(businessEntity.getId());
        metadataBusinessEntityRepository.update(businessEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessEntity(Long id) {
        validateBusinessEntityExists(id);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        metadataBusinessEntityRepository.deleteByConfig(configStore);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntity(Long id) {
        if (id == null) {
            return null;
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        return metadataBusinessEntityRepository.findOne(configStore);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        return metadataBusinessEntityRepository.findAllByConfig(configStore);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code.trim());
        return metadataBusinessEntityRepository.findOne(configStore);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceId(Long datasourceId) {
        if (datasourceId == null) {
            return List.of();
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("datasource_id", datasourceId);
        return metadataBusinessEntityRepository.findAllByConfig(configStore);
    }

    @Override
    public List<MetadataBusinessEntityDO> findAllByConfig(DefaultConfigStore configStore) {
        return metadataBusinessEntityRepository.findAllByConfig(configStore);
    }

    @Override
    public long countByConfig(DefaultConfigStore configStore) {
        return metadataBusinessEntityRepository.countByConfig(configStore);
    }

    /**
     * 校验业务实体存在
     */
    private void validateBusinessEntityExists(Long id) {
        if (getBusinessEntity(id) == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return null;
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("table_name", tableName.trim());
        return metadataBusinessEntityRepository.findOne(configStore);
    }
}
