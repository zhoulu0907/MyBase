package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRangeMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 范围验证规则仓储类
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationRangeRepository extends ServiceImpl<MetadataValidationRangeMapper, MetadataValidationRangeDO> {

    /**
     * 根据字段ID查询范围验证规则列表
     *
     * @param fieldId 字段ID
     * @return 范围验证规则列表
     */
    public List<MetadataValidationRangeDO> findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRangeDO::getFieldId, fieldId);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID删除范围验证规则
     *
     * @param fieldId 字段ID
     */
    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组ID查询范围验证规则列表
     *
     * @param groupId 组ID
     * @return 范围验证规则列表
     */
    public List<MetadataValidationRangeDO> findByGroupId(Long groupId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRangeDO::getGroupId, groupId);
        return list(queryWrapper);
    }

    public List<MetadataValidationRangeDO> findByFieldIds(java.util.Collection<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) { return java.util.Collections.emptyList(); }
        QueryWrapper queryWrapper = query()
                .in(MetadataValidationRangeDO::getFieldId, fieldIds);
        return list(queryWrapper);
    }
}
