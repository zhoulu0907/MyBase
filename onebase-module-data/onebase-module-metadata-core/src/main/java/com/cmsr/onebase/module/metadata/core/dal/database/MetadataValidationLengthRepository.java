package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationLengthMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 长度验证规则仓储类
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationLengthRepository extends ServiceImpl<MetadataValidationLengthMapper, MetadataValidationLengthDO> {

    /**
     * 根据字段ID查询单条长度验证规则
     *
     * @param fieldId 字段ID
     * @return 长度验证规则
     */
    public MetadataValidationLengthDO findOneByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationLengthDO::getFieldId, fieldId);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段ID查询长度验证规则列表
     *
     * @param fieldId 字段ID
     * @return 长度验证规则列表
     */
    public List<MetadataValidationLengthDO> findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationLengthDO::getFieldId, fieldId);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID删除长度验证规则
     *
     * @param fieldId 字段ID
     */
    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组ID查询长度验证规则列表
     *
     * @param groupId 组ID
     * @return 长度验证规则列表
     */
    public List<MetadataValidationLengthDO> findByGroupId(Long groupId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationLengthDO::getGroupId, groupId);
        return list(queryWrapper);
    }

    public List<MetadataValidationLengthDO> findByFieldIds(java.util.Collection<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) { return java.util.Collections.emptyList(); }
        QueryWrapper queryWrapper = query()
                .in(MetadataValidationLengthDO::getFieldId, fieldIds);
        return list(queryWrapper);
    }
}
