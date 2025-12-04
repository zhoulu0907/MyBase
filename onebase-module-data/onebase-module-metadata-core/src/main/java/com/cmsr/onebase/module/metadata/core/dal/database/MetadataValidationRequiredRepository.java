package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationRequiredMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
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
     * 根据字段UUID查询单条必填验证规则
     *
     * @param fieldUuid 字段UUID
     * @return 必填验证规则
     */
    public MetadataValidationRequiredDO findOneByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRequiredDO::getFieldUuid, fieldUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段UUID查询必填验证规则列表
     *
     * @param fieldUuid 字段UUID
     * @return 必填验证规则列表
     */
    public List<MetadataValidationRequiredDO> findByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRequiredDO::getFieldUuid, fieldUuid);
        return list(queryWrapper);
    }

    /**
     * 根据组UUID查询必填验证规则列表
     *
     * @param groupUuid 组UUID
     * @return 必填验证规则列表
     */
    public List<MetadataValidationRequiredDO> findByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRequiredDO::getGroupUuid, groupUuid);
        return list(queryWrapper);
    }

    /**
     * 根据字段UUID删除必填验证规则
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        for (var item : findByFieldUuid(fieldUuid)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据字段UUID查询必填验证规则列表
     *
     * @param fieldUuids 字段UUID列表
     * @return 必填验证规则列表
     */
    public List<MetadataValidationRequiredDO> findByFieldUuids(Collection<String> fieldUuids) {
        if (fieldUuids == null || fieldUuids.isEmpty()) { return Collections.emptyList(); }
        QueryWrapper queryWrapper = query()
                .in(MetadataValidationRequiredDO::getFieldUuid, fieldUuids);
        return list(queryWrapper);
    }

    // ====== 兼容旧代码的方法 ======

    /**
     * 根据组ID查询必填验证规则列表（兼容旧代码）
     * @deprecated 请使用 findByGroupUuid()
     */
    @Deprecated
    public List<MetadataValidationRequiredDO> findByGroupId(Long groupId) {
        return findByGroupUuid(String.valueOf(groupId));
    }
}
