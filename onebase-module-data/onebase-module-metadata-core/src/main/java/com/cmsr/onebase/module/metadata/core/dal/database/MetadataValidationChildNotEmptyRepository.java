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
     * 根据字段UUID查询单条子记录非空验证规则
     *
     * @param fieldUuid 字段UUID
     * @return 子记录非空验证规则
     */
    public MetadataValidationChildNotEmptyDO findOneByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationChildNotEmptyDO::getFieldUuid, fieldUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段UUID查询子记录非空验证规则列表
     *
     * @param fieldUuid 字段UUID
     * @return 子记录非空验证规则列表
     */
    public List<MetadataValidationChildNotEmptyDO> findByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationChildNotEmptyDO::getFieldUuid, fieldUuid);
        return list(queryWrapper);
    }

    /**
     * 根据字段UUID删除子记录非空验证规则
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        for (var item : findByFieldUuid(fieldUuid)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组UUID查询子记录非空验证规则列表
     *
     * @param groupUuid 组UUID
     * @return 子记录非空验证规则列表
     */
    public List<MetadataValidationChildNotEmptyDO> findByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationChildNotEmptyDO::getGroupUuid, groupUuid);
        return list(queryWrapper);
    }

    /**
     * 根据组ID查询子记录非空验证规则列表（兼容旧代码）
     *
     * @deprecated 请使用 findByGroupUuid(String)
     * @param groupId 组ID
     * @return 子记录非空验证规则列表
     */
    @Deprecated
    public List<MetadataValidationChildNotEmptyDO> findByGroupId(Long groupId) {
        return findByGroupUuid(groupId != null ? String.valueOf(groupId) : null);
    }
}
