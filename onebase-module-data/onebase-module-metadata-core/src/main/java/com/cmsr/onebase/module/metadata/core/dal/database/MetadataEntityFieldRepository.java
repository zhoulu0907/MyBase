package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
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
public class MetadataEntityFieldRepository extends BaseBizRepository<MetadataEntityFieldMapper, MetadataEntityFieldDO> {

    /**
     * 根据ID查询实体字段
     *
     * @param id 字段ID
     * @return 实体字段对象，不存在时返回null
     */
    public MetadataEntityFieldDO findById(Long id) {
        return getById(id);
    }

    /**
     * 根据ID（String）查询实体字段（兼容旧代码）
     * @deprecated 请使用 getByFieldUuid(String)
     * @param id 字段ID（可能是Long字符串或UUID）
     * @return 实体字段对象
     */
    @Deprecated
    public MetadataEntityFieldDO findById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        // 尝试按UUID查询
        return getByFieldUuid(id);
    }

    /**
     * 根据字段UUID查询实体字段
     *
     * @param fieldUuid 字段UUID
     * @return 实体字段对象，不存在时返回null
     */
    public MetadataEntityFieldDO getByFieldUuid(String fieldUuid) {
        if (fieldUuid == null || fieldUuid.isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getFieldUuid, fieldUuid);
        return getOne(queryWrapper);
    }

    /**
     * 根据UUID查询实体字段（兼容旧代码）
     * @deprecated 请使用 getByFieldUuid()
     */
    @Deprecated
    public MetadataEntityFieldDO getByUuid(String uuid) {
        return getByFieldUuid(uuid);
    }

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
     * 根据实体UUID获取实体字段列表
     *
     * @param entityUuid 实体UUID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityUuid(String entityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体ID获取实体字段列表（通过数据库ID查询）
     *
     * @param entityId 实体ID（字符串格式）
     * @return 实体字段列表
     * @deprecated 请使用 {@link #getEntityFieldListByEntityUuid(String)} 代替
     */
    @Deprecated
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
        return getEntityFieldListByEntityId(longEntityId);
    }

    /**
     * 根据实体ID获取实体字段列表（通过数据库ID查询）
     *
     * @param entityId 实体ID
     * @return 实体字段列表
     * @deprecated 请使用 {@link #getEntityFieldListByEntityUuid(String)} 代替
     */
    @Deprecated
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        return list(query().eq(MetadataEntityFieldDO::getId, entityId));
    }

    /**
     * 根据实体UUID和字段名获取字段
     *
     * @param entityUuid  实体UUID
     * @param fieldName 字段名
     * @return 实体字段对象
     */
    public MetadataEntityFieldDO getEntityFieldByName(String entityUuid, String fieldName) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .eq(MetadataEntityFieldDO::getFieldName, fieldName);
        return getOne(queryWrapper);
    }

    /**
     * 根据实体UUID获取未删除的字段列表
     *
     * @param entityUuid 实体UUID
     * @return 实体字段列表
     */
    public List<MetadataEntityFieldDO> getActiveEntityFieldsByEntityUuid(String entityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }

    /**
     * 根据实体UUID获取ID字段
     *
     * @param entityUuid 实体UUID
     * @return ID字段对象
     */
    public MetadataEntityFieldDO getIdFieldByEntityUuid(String entityUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
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
                .eq(MetadataEntityFieldDO::getApplicationId, appId)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return list(queryWrapper);
    }
}
