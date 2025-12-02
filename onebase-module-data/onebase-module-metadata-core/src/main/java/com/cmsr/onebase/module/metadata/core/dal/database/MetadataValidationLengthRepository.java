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
     * 根据字段UUID查询单条长度验证规则
     *
     * @param fieldUuid 字段UUID
     * @return 长度验证规则
     */
    public MetadataValidationLengthDO findOneByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationLengthDO::getFieldUuid, fieldUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段UUID查询长度验证规则列表
     *
     * @param fieldUuid 字段UUID
     * @return 长度验证规则列表
     */
    public List<MetadataValidationLengthDO> findByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationLengthDO::getFieldUuid, fieldUuid);
        return list(queryWrapper);
    }

    /**
     * 根据字段UUID删除长度验证规则
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        for (var item : findByFieldUuid(fieldUuid)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组UUID查询长度验证规则列表
     *
     * @param groupUuid 组UUID
     * @return 长度验证规则列表
     */
    public List<MetadataValidationLengthDO> findByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationLengthDO::getGroupUuid, groupUuid);
        return list(queryWrapper);
    }

    /**
     * 根据组ID查询长度验证规则列表（兼容旧代码）
     *
     * @deprecated 请使用 findByGroupUuid(String)
     * @param groupId 组ID
     * @return 长度验证规则列表
     */
    @Deprecated
    public List<MetadataValidationLengthDO> findByGroupId(Long groupId) {
        return findByGroupUuid(groupId != null ? String.valueOf(groupId) : null);
    }
}
