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
     * 根据字段UUID查询范围验证规则列表
     *
     * @param fieldUuid 字段UUID
     * @return 范围验证规则列表
     */
    public List<MetadataValidationRangeDO> findByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRangeDO::getFieldUuid, fieldUuid);
        return list(queryWrapper);
    }

    /**
     * 根据字段UUID删除范围验证规则
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        for (var item : findByFieldUuid(fieldUuid)) {
            removeById(item.getId());
        }
    }

    /**
     * 根据组UUID查询范围验证规则列表
     *
     * @param groupUuid 组UUID
     * @return 范围验证规则列表
     */
    public List<MetadataValidationRangeDO> findByGroupUuid(String groupUuid) {
        QueryWrapper queryWrapper = query()
                .eq(MetadataValidationRangeDO::getGroupUuid, groupUuid);
        return list(queryWrapper);
    }

    /**
     * 根据组ID查询范围验证规则列表（兼容旧代码）
     *
     * @param groupId 组ID
     * @return 范围验证规则列表
     * @deprecated 请使用 {@link #findByGroupUuid(String)} 代替
     */
    @Deprecated
    public List<MetadataValidationRangeDO> findByGroupId(Long groupId) {
        // 由于已经改用UUID，Long类型的groupId已不再适用
        // 返回空列表以保持兼容性
        return java.util.Collections.emptyList();
    }
}
