package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataEntityFieldOptionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 字段选项 仓储
 *
 * @author bty418
 * @date 2025-08-18
 */
@Repository
@Slf4j
public class MetadataEntityFieldOptionRepository extends ServiceImpl<MetadataEntityFieldOptionMapper, MetadataEntityFieldOptionDO> {

    /**
     * 根据字段UUID获取字段选项列表
     *
     * @param fieldUuid 字段UUID
     * @return 字段选项列表
     */
    public List<MetadataEntityFieldOptionDO> findAllByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldOptionDO::getFieldUuid, fieldUuid)
                .orderBy(MetadataEntityFieldOptionDO::getOptionOrder, true)
                .orderBy(MetadataEntityFieldOptionDO::getCreateTime, true);
        return list(queryWrapper);
    }

    public List<MetadataEntityFieldOptionDO> findAllByFieldUuids(Collection<String> fieldUuids) {
        if (fieldUuids == null || fieldUuids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper queryWrapper = this.query()
                .in(MetadataEntityFieldOptionDO::getFieldUuid, fieldUuids)
                .orderBy(MetadataEntityFieldOptionDO::getCreateTime, true);
        return list(queryWrapper);
    }


    /**
     * 根据字段UUID删除字段选项
     *
     * @param fieldUuid 字段UUID
     */
    public void deleteByFieldUuid(String fieldUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldOptionDO::getFieldUuid, fieldUuid);
        remove(queryWrapper);
    }

    /**
     * 插入字段选项
     *
     * @param option 字段选项DO
     * @return 受影响行数
     */
    public boolean insert(MetadataEntityFieldOptionDO option) {
        return save(option);
    }

    /**
     * 更新字段选项
     *
     * @param option 字段选项DO
     * @return 受影响行数
     */
    public boolean update(MetadataEntityFieldOptionDO option) {
        return updateById(option);
    }

    /**
     * 根据ID删除字段选项
     *
     * @param id 字段选项ID
     * @return 受影响行数
     */
    public boolean deleteById(Long id) {
        return removeById(id);
    }

    // ==================== 向后兼容方法 ====================

    /**
     * 根据字段ID获取字段选项列表（兼容旧代码）
     * @deprecated 请使用 findAllByFieldUuid(String)
     * @param fieldId 字段ID
     * @return 字段选项列表
     */
    @Deprecated
    public List<MetadataEntityFieldOptionDO> findAllByFieldId(Long fieldId) {
        return findAllByFieldUuid(fieldId != null ? String.valueOf(fieldId) : null);
    }

    /**
     * 根据字段ID删除字段选项（兼容旧代码）
     * @deprecated 请使用 deleteByFieldUuid(String)
     * @param fieldId 字段ID
     */
    @Deprecated
    public void deleteByFieldId(Long fieldId) {
        deleteByFieldUuid(fieldId != null ? String.valueOf(fieldId) : null);
    }
}

