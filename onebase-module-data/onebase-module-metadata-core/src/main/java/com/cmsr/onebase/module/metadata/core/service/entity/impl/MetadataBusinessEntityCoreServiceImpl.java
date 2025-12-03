package com.cmsr.onebase.module.metadata.core.service.entity.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
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
        // 生成 UUID
        if (businessEntity.getEntityUuid() == null || businessEntity.getEntityUuid().isEmpty()) {
            businessEntity.setEntityUuid(UuidUtils.getUuid());
        }
        metadataBusinessEntityRepository.save(businessEntity);
        log.info("创建业务实体成功，ID: {}，UUID: {}", businessEntity.getId(), businessEntity.getEntityUuid());
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

    /**
     * 根据实体ID（String）获取业务实体（兼容旧代码）
     * @deprecated 请使用 getBusinessEntityByUuid(String)
     * @param id 实体ID（可能是Long字符串或UUID）
     * @return 业务实体DO
     */
    @Deprecated
    public MetadataBusinessEntityDO getBusinessEntity(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        // 尝试按UUID查询
        return getBusinessEntityByUuid(id);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByUuid(String entityUuid) {
        if (entityUuid == null || entityUuid.trim().isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                .eq(MetadataBusinessEntityDO::getEntityUuid, entityUuid.trim());
        return metadataBusinessEntityRepository.getOne(queryWrapper);
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
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceUuid(String datasourceUuid) {
        if (datasourceUuid == null || datasourceUuid.isEmpty()) {
            return List.of();
        }
        QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                .eq(MetadataBusinessEntityDO::getDatasourceUuid, datasourceUuid);
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
}
