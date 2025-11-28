package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationChildNotEmptyMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 子记录非空验证规则仓储类
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationChildNotEmptyRepository extends ServiceImpl<MetadataValidationChildNotEmptyMapper, MetadataValidationChildNotEmptyDO> {

    /**
     * 根据字段ID查询单条子记录非空验证规则
     *
     * @param fieldId 字段ID
     * @return 子记录非空验证规则
     */
    public MetadataValidationChildNotEmptyDO findOneByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationChildNotEmptyDO::getFieldId, fieldId);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段ID查询子记录非空验证规则列表
     *
     * @param fieldId 字段ID
     * @return 子记录非空验证规则列表
     */
    public List<MetadataValidationChildNotEmptyDO> findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationChildNotEmptyDO::getFieldId, fieldId);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID删除子记录非空验证规则
     *
     * @param fieldId 字段ID
     */
    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组ID查询子记录非空验证规则列表
     *
     * @param groupId 组ID
     * @return 子记录非空验证规则列表
     */
    public List<MetadataValidationChildNotEmptyDO> findByGroupId(Long groupId) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationChildNotEmptyDO::getGroupId, groupId);
        return list(queryWrapper);
    }
}
