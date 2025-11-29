package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataEntityFieldMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据实体字段仓储类
 * <p>
 * 提供实体字段相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataEntityFieldRepository extends ServiceImpl<MetadataEntityFieldMapper, MetadataEntityFieldDO> {

    /**
     * 获取实体字段列表
     *
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldList() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体ID获取实体字段列表
     *
     * @param entityId 实体ID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityId, entityId)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体ID获取实体字段列表（字符串格式）
     *
     * @param entityId 实体ID（字符串格式）
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
        return getEntityFieldListByEntityId(longEntityId);
    }

    /**
     * 根据实体ID和字段名获取字段
     *
     * @param entityId  实体ID
     * @param fieldName 字段名
     * @return 实体字段对象
     */
    public MetadataEntityFieldDO getEntityFieldByName(Long entityId, String fieldName) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityId, entityId)
                .eq(MetadataEntityFieldDO::getFieldName, fieldName);
        return getOne(queryWrapper);
    }

    /**
     * 根据实体ID获取未删除的字段列表
     *
     * @param entityId 实体ID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getActiveEntityFieldsByEntityId(Long entityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityId, entityId)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体ID获取ID字段
     *
     * @param entityId 实体ID
     * @return ID字段对象
     */
    public MetadataEntityFieldDO getIdFieldByEntityId(Long entityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityId, entityId)
                .eq(MetadataEntityFieldDO::getFieldName, "id");
        return getOne(queryWrapper);
    }

    /**
     * 根据字典类型ID统计引用该字典的字段数量
     *
     * @param dictTypeId 字典类型ID
     * @return 引用该字典的字段数量
     */
    public long countByDictTypeId(Long dictTypeId) {
        if (dictTypeId == null) {
            return 0;
        }
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getDictTypeId, dictTypeId);
        return count(queryWrapper);
    }

    /**
     * 根据应用ID获取实体字段列表
     *
     * @param appId 应用ID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldListByAppId(Long appId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getAppId, appId)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }
}
