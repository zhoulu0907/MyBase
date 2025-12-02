package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataEntityRelationshipMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据实体关系仓储类
 * <p>
 * 提供实体关系相关的数据库操作接口，继承自ServiceImpl获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataEntityRelationshipRepository extends ServiceImpl<MetadataEntityRelationshipMapper, MetadataEntityRelationshipDO> {

    /**
     * 根据源实体ID查询关系列表
     *
     * @param sourceEntityId 源实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> findBySourceEntityId(Long sourceEntityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityId, sourceEntityId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据目标实体ID查询关系列表
     *
     * @param targetEntityId 目标实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> findByTargetEntityId(Long targetEntityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getTargetEntityId, targetEntityId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据主表实体ID获取关系列表
     *
     * @param masterEntityId 主表实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByMasterEntityId(Long masterEntityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityId, masterEntityId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据从表实体ID获取关系列表
     *
     * @param slaveEntityId 从表实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsBySlaveEntityId(Long slaveEntityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getTargetEntityId, slaveEntityId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体ID获取所有相关的关系（包括主表和从表）
     *
     * @param entityId 实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByEntityId(Long entityId) {
        QueryWrapper queryWrapper = this.query()
                .where(MetadataEntityRelationshipDO::getSourceEntityId).eq(entityId)
                .or(MetadataEntityRelationshipDO::getTargetEntityId).eq(entityId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据关系类型获取关系列表
     *
     * @param relationshipType 关系类型
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByType(String relationshipType) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getRelationshipType, relationshipType)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 获取所有实体关系列表
     *
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getAllRelationships() {
        QueryWrapper queryWrapper = this.query()
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据字段ID获取所有相关的关系（包括主表和从表）
     *
     * @param fieldId 实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .where(MetadataEntityRelationshipDO::getSourceFieldId).eq(fieldId)
                .or(MetadataEntityRelationshipDO::getTargetFieldId).eq(fieldId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据源字段ID和源实体ID查找关联关系
     *
     * @param sourceFieldId 源字段ID
     * @param sourceEntityId 源实体ID
     * @return 关联关系
     */
    public MetadataEntityRelationshipDO findBySourceFieldAndEntity(Long sourceFieldId, Long sourceEntityId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getSourceFieldId, sourceFieldId)
                .eq(MetadataEntityRelationshipDO::getSourceEntityId, sourceEntityId);
        return getOne(queryWrapper);
    }

    /**
     * 根据应用ID获取实体关系列表
     *
     * @param appId 应用ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByAppId(Long appId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getApplicationId, appId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }
}
