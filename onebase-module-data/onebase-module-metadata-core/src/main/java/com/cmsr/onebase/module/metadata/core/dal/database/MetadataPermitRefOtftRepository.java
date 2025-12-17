package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataPermitRefOtftMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 数据权限-操作符号与字段类型关联 Repository
 *
 * @author matianyu
 * @date 2025-12-05
 */
@Repository
public class MetadataPermitRefOtftRepository extends ServiceImpl<MetadataPermitRefOtftMapper, MetadataPermitRefOtftDO> {

    /**
     * 根据字段类型ID集合查询关联配置
     *
     * @param fieldTypeIds 字段类型ID集合
     * @return 关联配置列表
     */
    public List<MetadataPermitRefOtftDO> findByFieldTypeIds(Set<Long> fieldTypeIds) {
        if (fieldTypeIds == null || fieldTypeIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper queryWrapper = this.query()
                .in(MetadataPermitRefOtftDO::getFieldTypeId, fieldTypeIds)
                .orderBy(MetadataPermitRefOtftDO::getFieldTypeId, true)
                .orderBy(MetadataPermitRefOtftDO::getSortOrder, true);
        return list(queryWrapper);
    }

    /**
     * 查询所有关联配置
     *
     * @return 关联配置列表
     */
    public List<MetadataPermitRefOtftDO> findAll() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataPermitRefOtftDO::getFieldTypeId, true)
                .orderBy(MetadataPermitRefOtftDO::getSortOrder, true);
        return list(queryWrapper);
    }
}
