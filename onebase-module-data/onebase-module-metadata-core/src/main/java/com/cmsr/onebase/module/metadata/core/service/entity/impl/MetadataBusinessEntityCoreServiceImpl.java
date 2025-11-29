package com.cmsr.onebase.module.metadata.core.service.entity.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
        metadataBusinessEntityRepository.save(businessEntity);
        return businessEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBusinessEntity(@Valid MetadataBusinessEntityDO businessEntity) {
        validateBusinessEntityExists(businessEntity.getId());
        metadataBusinessEntityRepository.updateById(businessEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessEntity(Long id) {
        validateBusinessEntityExists(id);
        metadataBusinessEntityRepository.removeById(id);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntity(Long id) {
        if (id == null) {
            return null;
        }
        return metadataBusinessEntityRepository.getById(id);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        return metadataBusinessEntityRepository.list();
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                .eq(MetadataBusinessEntityDO::getCode, code.trim());
        return metadataBusinessEntityRepository.getOne(queryWrapper);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceId(Long datasourceId) {
        if (datasourceId == null) {
            return List.of();
        }
        QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                .eq(MetadataBusinessEntityDO::getDatasourceId, datasourceId);
        return metadataBusinessEntityRepository.list(queryWrapper);
    }

    @Override
    public List<MetadataBusinessEntityDO> findAllByConfig(QueryWrapper queryWrapper) {
        return metadataBusinessEntityRepository.list(queryWrapper);
    }

    @Override
    public long countByConfig(QueryWrapper queryWrapper) {
        return metadataBusinessEntityRepository.count(queryWrapper);
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
        QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                .eq(MetadataBusinessEntityDO::getTableName, tableName.trim());
        return metadataBusinessEntityRepository.getOne(queryWrapper);
    }
}
