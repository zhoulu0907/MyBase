package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.FieldTypeMappingDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.FieldTypeMappingMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字段类型映射仓储类
 * <p>
 * 提供字段类型映射相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class FieldTypeMappingRepository extends ServiceImpl<FieldTypeMappingMapper, FieldTypeMappingDO> {

    /**
     * 根据数据库类型获取字段类型映射列表
     *
     * @param databaseType 数据库类型
     * @return 字段类型映射列表
     */
    public List<FieldTypeMappingDO> getFieldTypeMappingsByDatabaseType(String databaseType) {
        QueryWrapper queryWrapper = this.query()
                .eq(FieldTypeMappingDO::getDatabaseType, databaseType)
                .orderBy(FieldTypeMappingDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据Java类型获取字段类型映射列表
     *
     * @param javaType Java类型
     * @return 字段类型映射列表
     */
    public List<FieldTypeMappingDO> getFieldTypeMappingsByJavaType(String javaType) {
        QueryWrapper queryWrapper = this.query()
                .eq(FieldTypeMappingDO::getBusinessFieldType, javaType)
                .orderBy(FieldTypeMappingDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据数据库类型和数据库字段类型获取映射
     *
     * @param databaseType 数据库类型
     * @param dbFieldType 数据库字段类型
     * @return 字段类型映射对象
     */
    public FieldTypeMappingDO getFieldTypeMappingByDbType(String databaseType, String dbFieldType) {
        QueryWrapper queryWrapper = this.query()
                .eq(FieldTypeMappingDO::getDatabaseType, databaseType)
                .eq(FieldTypeMappingDO::getDatabaseField, dbFieldType);
        return getOne(queryWrapper);
    }

    /**
     * 获取所有字段类型映射列表
     *
     * @return 字段类型映射列表
     */
    public List<FieldTypeMappingDO> getAllFieldTypeMappings() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(FieldTypeMappingDO::getDatabaseType, true)
                .orderBy(FieldTypeMappingDO::getCreateTime, false);
        return list(queryWrapper);
    }
}
