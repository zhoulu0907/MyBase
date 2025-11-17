package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据实体关系仓储类
 * <p>
 * 提供实体关系相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataEntityRelationshipRepository extends DataRepository<MetadataEntityRelationshipDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataEntityRelationshipRepository() {
        super(MetadataEntityRelationshipDO.class);
    }

    /**
     * 根据主表实体ID获取关系列表
     *
     * @param masterEntityId 主表实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByMasterEntityId(Long masterEntityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, masterEntityId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据从表实体ID获取关系列表
     *
     * @param slaveEntityId 从表实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsBySlaveEntityId(Long slaveEntityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, slaveEntityId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据实体ID获取所有相关的关系（包括主表和从表）
     *
     * @param entityId 实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.or(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityId);
        configStore.or(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, entityId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据关系类型获取关系列表
     *
     * @param relationshipType 关系类型
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByType(String relationshipType) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.RELATIONSHIP_TYPE, relationshipType);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 获取所有实体关系列表
     *
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getAllRelationships() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据字段ID获取所有相关的关系（包括主表和从表）
     *
     * @param fieldId 实体ID
     * @return 实体关系列表
     */
    public List<MetadataEntityRelationshipDO> getRelationshipsByFieldId(Long fieldId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.or(MetadataEntityRelationshipDO.SOURCE_FIELD_ID, fieldId);
        configStore.or(MetadataEntityRelationshipDO.TARGET_FIELD_ID, fieldId);
        configStore.order("create_time", org.anyline.entity.Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}
