package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据实体字段仓储类
 * <p>
 * 提供实体字段相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
@Slf4j
public class MetadataEntityFieldRepository extends DataRepository<MetadataEntityFieldDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataEntityFieldRepository() {
        super(MetadataEntityFieldDO.class);
    }

    /**
     * 获取实体字段列表
     *
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据实体ID获取实体字段列表
     *
     * @param entityId 实体ID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return findAllByConfig(configStore);
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.and(MetadataEntityFieldDO.FIELD_NAME, fieldName);
        return findOne(configStore);
    }

    /**
     * 根据实体ID获取未删除的字段列表
     *
     * @param entityId 实体ID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getActiveEntityFieldsByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.and("deleted", 0);
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据实体ID获取ID字段
     *
     * @param entityId 实体ID
     * @return ID字段对象
     */
    public MetadataEntityFieldDO getIdFieldByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.and(MetadataEntityFieldDO.FIELD_NAME, "id");
        return findOne(configStore);
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.DICT_TYPE_ID, dictTypeId);
        return countByConfig(configStore);
    }
}
