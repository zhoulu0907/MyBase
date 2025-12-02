package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationUniqueMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 唯一性验证规则仓储类
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationUniqueRepository extends ServiceImpl<MetadataValidationUniqueMapper, MetadataValidationUniqueDO> {

    /**
     * 根据字段UUID查询单条唯一性验证规则
     *
     * @param fieldUuid 字段UUID
     * @return 唯一性验证规则
     */
    public MetadataValidationUniqueDO findOneByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationUniqueDO::getFieldUuid, fieldUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段UUID查询唯一性验证规则列表
     *
     * @param fieldUuid 字段UUID
     * @return 唯一性验证规则列表
     */
    public List<MetadataValidationUniqueDO> findByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationUniqueDO::getFieldUuid, fieldUuid);
        return list(queryWrapper);
    }

    /**
     * 根据字段UUID删除唯一性验证规则
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        for (var item : findByFieldUuid(fieldUuid)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组UUID查询唯一性验证规则列表
     *
     * @param groupUuid 组UUID
     * @return 唯一性验证规则列表
     */
    public List<MetadataValidationUniqueDO> findByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationUniqueDO::getGroupUuid, groupUuid);
        return list(queryWrapper);
    }

    /**
     * 根据组ID查询唯一性验证规则列表（兼容旧代码）
     *
     * @deprecated 请使用 findByGroupUuid(String)
     * @param groupId 组ID
     * @return 唯一性验证规则列表
     */
    @Deprecated
    public List<MetadataValidationUniqueDO> findByGroupId(Long groupId) {
        return findByGroupUuid(groupId != null ? String.valueOf(groupId) : null);
    }
}
