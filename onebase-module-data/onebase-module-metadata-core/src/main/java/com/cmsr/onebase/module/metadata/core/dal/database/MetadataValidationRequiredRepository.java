package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRequiredMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 必填验证规则仓储类
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataValidationRequiredRepository extends ServiceImpl<MetadataValidationRequiredMapper, MetadataValidationRequiredDO> {

    /**
     * 根据字段ID查询单条必填验证规则
     *
     * @param fieldId 字段ID
     * @return 必填验证规则
     */
    public MetadataValidationRequiredDO findOneByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq("field_id", fieldId)
                .eq("deleted", 0);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段ID查询必填验证规则列表
     *
     * @param fieldId 字段ID
     * @return 必填验证规则列表
     */
    public List<MetadataValidationRequiredDO> findByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = query()
                .eq("field_id", fieldId)
                .eq("deleted", 0);
        return list(queryWrapper);
    }

    /**
     * 根据组ID查询必填验证规则列表
     *
     * @param groupId 组ID
     * @return 必填验证规则列表
     */
    public List<MetadataValidationRequiredDO> findByGroupId(Long groupId) {
        QueryWrapper queryWrapper = query()
                .eq("group_id", groupId)
                .eq("deleted", 0);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID删除必填验证规则
     *
     * @param fieldId 字段ID
     */
    public void deleteByFieldId(Long fieldId) {
        for (var item : findByFieldId(fieldId)) {
            removeById(item.getId());
        }
    }
}
