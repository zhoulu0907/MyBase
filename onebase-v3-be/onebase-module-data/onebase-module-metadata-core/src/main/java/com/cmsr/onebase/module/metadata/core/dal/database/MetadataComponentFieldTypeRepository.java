package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataComponentFieldTypeMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据组件字段类型 Repository
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Repository
public class MetadataComponentFieldTypeRepository extends ServiceImpl<MetadataComponentFieldTypeMapper, MetadataComponentFieldTypeDO> {

    /**
     * 根据字段类型编码查询字段类型信息
     *
     * @param fieldTypeCode 字段类型编码
     * @return 字段类型DO
     */
    public MetadataComponentFieldTypeDO findByFieldTypeCode(String fieldTypeCode) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataComponentFieldTypeDO::getFieldTypeCode, fieldTypeCode)
                .eq(MetadataComponentFieldTypeDO::getStatus, 1); // 只查询启用状态的
        return getOne(queryWrapper);
    }

    /**
     * 查询所有启用的字段类型
     *
     * @return 字段类型列表
     */
    public List<MetadataComponentFieldTypeDO> findAllEnabled() {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataComponentFieldTypeDO::getStatus, 1) // 只查询启用状态的
                .orderBy(MetadataComponentFieldTypeDO::getSortOrder, true);
        return list(queryWrapper);
    }
}
