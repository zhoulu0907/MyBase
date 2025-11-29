package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.mapper.MetadataEntityFieldOptionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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

    public List<MetadataEntityFieldOptionDO> findAllByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldOptionDO::getFieldId, fieldId)
                .orderBy(MetadataEntityFieldOptionDO::getOptionOrder, true)
                .orderBy(MetadataEntityFieldOptionDO::getCreateTime, true);
        return list(queryWrapper);
    }

    public void deleteByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldOptionDO::getFieldId, fieldId);
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
}


