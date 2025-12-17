package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataValidationTypeMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 元数据校验类型 Repository
 *
 * @author matianyu
 * @date 2025-12-05
 */
@Repository
public class MetadataValidationTypeRepository extends ServiceImpl<MetadataValidationTypeMapper, MetadataValidationTypeDO> {

    /**
     * 根据ID集合批量查询校验类型
     *
     * @param ids ID集合
     * @return 校验类型列表
     */
    public List<MetadataValidationTypeDO> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        QueryWrapper queryWrapper = this.query()
                .in(MetadataValidationTypeDO::getId, ids);
        return list(queryWrapper);
    }

    /**
     * 查询所有启用的校验类型
     *
     * @return 校验类型列表
     */
    public List<MetadataValidationTypeDO> findAllEnabled() {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataValidationTypeDO::getStatus, 1)
                .orderBy(MetadataValidationTypeDO::getSortOrder, true);
        return list(queryWrapper);
    }
}
