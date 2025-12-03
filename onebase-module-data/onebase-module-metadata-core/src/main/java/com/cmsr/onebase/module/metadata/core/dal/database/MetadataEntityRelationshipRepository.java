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
     * 根据源实体UUID查询关系列表
     *
     * @param sourceEntityUuid 源实体UUID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> findBySourceEntityUuid(String sourceEntityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityUuid, sourceEntityUuid)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据目标实体UUID查询关系列表
     *
     * @param targetEntityUuid 目标实体UUID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> findByTargetEntityUuid(String targetEntityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getTargetEntityUuid, targetEntityUuid)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据主表实体UUID获取关系列表
     *
     * @param masterEntityUuid 主表实体UUID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByMasterEntityUuid(String masterEntityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityUuid, masterEntityUuid)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据从表实体UUID获取关系列表
     *
     * @param slaveEntityUuid 从表实体UUID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsBySlaveEntityUuid(String slaveEntityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getTargetEntityUuid, slaveEntityUuid)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体UUID获取所有相关的关系（包括主表和从表）
     *
     * @param entityUuid 实体UUID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByEntityUuid(String entityUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(MetadataEntityRelationshipDO::getSourceEntityUuid).eq(entityUuid)
                .or(MetadataEntityRelationshipDO::getTargetEntityUuid).eq(entityUuid)
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
     * 根据关系UUID查询关系
     *
     * @param relationshipUuid 关系UUID
     * @return 实体关系对象
     */
    public MetadataEntityRelationshipDO findByRelationshipUuid(String relationshipUuid) {
        if (relationshipUuid == null || relationshipUuid.trim().isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getRelationshipUuid, relationshipUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据字段UUID获取所有相关的关系（包括主表和从表）
     *
     * @param fieldUuid 字段UUID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(MetadataEntityRelationshipDO::getSourceFieldUuid).eq(fieldUuid)
                .or(MetadataEntityRelationshipDO::getTargetFieldUuid).eq(fieldUuid)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据源字段UUID和源实体UUID查找关联关系
     *
     * @param sourceFieldUuid 源字段UUID
     * @param sourceEntityUuid 源实体UUID
     * @return 关联关系
     */
    public MetadataEntityRelationshipDO findBySourceFieldAndEntityUuid(String sourceFieldUuid, String sourceEntityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityRelationshipDO::getSourceFieldUuid, sourceFieldUuid)
                .eq(MetadataEntityRelationshipDO::getSourceEntityUuid, sourceEntityUuid);
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

    // ==================== 兼容方法（已弃用，建议使用UUID版本）====================

    /**
     * 根据主表实体ID获取关系列表（已弃用）
     * @deprecated 请使用 {@link #getRelationshipsByMasterEntityUuid(String)}
     */
    @Deprecated
    public List<MetadataEntityRelationshipDO> getRelationshipsByMasterEntityId(Long masterEntityId) {
        log.warn("使用已弃用的方法 getRelationshipsByMasterEntityId，建议迁移到 getRelationshipsByMasterEntityUuid");
        return List.of();
    }

    /**
     * 根据从表实体ID获取关系列表（已弃用）
     * @deprecated 请使用 {@link #getRelationshipsBySlaveEntityUuid(String)}
     */
    @Deprecated
    public List<MetadataEntityRelationshipDO> getRelationshipsBySlaveEntityId(Long slaveEntityId) {
        log.warn("使用已弃用的方法 getRelationshipsBySlaveEntityId，建议迁移到 getRelationshipsBySlaveEntityUuid");
        return List.of();
    }

    /**
     * 根据实体ID获取所有相关的关系（已弃用）
     * @deprecated 请使用 {@link #getRelationshipsByEntityUuid(String)}
     */
    @Deprecated
    public List<MetadataEntityRelationshipDO> getRelationshipsByEntityId(Long entityId) {
        log.warn("使用已弃用的方法 getRelationshipsByEntityId，建议迁移到 getRelationshipsByEntityUuid");
        return List.of();
    }

    /**
     * 根据字段ID获取关系列表（已弃用）
     * @deprecated 请使用 {@link #getRelationshipsByFieldUuid(String)}
     */
    @Deprecated
    public List<MetadataEntityRelationshipDO> getRelationshipsByFieldId(Long fieldId) {
        log.warn("使用已弃用的方法 getRelationshipsByFieldId，建议迁移到 getRelationshipsByFieldUuid");
        return List.of();
    }

    /**
     * 根据源实体ID查询关系列表（兼容旧代码）
     * @deprecated 请使用 {@link #findBySourceEntityUuid(String)}
     */
    @Deprecated
    public List<MetadataEntityRelationshipDO> findBySourceEntityId(Long sourceEntityId) {
        return findBySourceEntityUuid(sourceEntityId != null ? String.valueOf(sourceEntityId) : null);
    }

    /**
     * 根据目标实体ID查询关系列表（兼容旧代码）
     * @deprecated 请使用 {@link #findByTargetEntityUuid(String)}
     */
    @Deprecated
    public List<MetadataEntityRelationshipDO> findByTargetEntityId(Long targetEntityId) {
        return findByTargetEntityUuid(targetEntityId != null ? String.valueOf(targetEntityId) : null);
    }
}
