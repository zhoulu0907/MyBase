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
     * 根据字段ID查询单条唯一性验证规则
     *
     * @param fieldId 字段ID
     * @return 唯一性验证规则
     */
    public MetadataValidationUniqueDO findOneByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationUniqueDO::getFieldId, fieldId);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段ID查询唯一性验证规则列表
     *
     * @param fieldId 字段ID
     * @return 唯一性验证规则列表
     */
    public List<MetadataValidationUniqueDO> findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationUniqueDO::getFieldId, fieldId);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID删除唯一性验证规则
     *
     * @param fieldId 字段ID
     */
    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组ID查询唯一性验证规则列表
     *
     * @param groupId 组ID
     * @return 唯一性验证规则列表
     */
    public List<MetadataValidationUniqueDO> findByGroupId(Long groupId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationUniqueDO::getGroupId, groupId);
        return list(queryWrapper);
    }

    public List<MetadataValidationUniqueDO> findByFieldIds(java.util.Collection<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) { return java.util.Collections.emptyList(); }
        QueryWrapper queryWrapper = query()
                .in(MetadataValidationUniqueDO::getFieldId, fieldIds);
        return list(queryWrapper);
    }
}
