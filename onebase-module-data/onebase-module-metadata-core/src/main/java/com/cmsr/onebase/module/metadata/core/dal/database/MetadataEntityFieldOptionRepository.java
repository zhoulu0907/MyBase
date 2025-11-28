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
                .eq(MetadataEntityFieldOptionDO::getDeleted, 0)
                .orderBy(MetadataEntityFieldOptionDO::getOptionOrder, true)
                .orderBy(MetadataEntityFieldOptionDO::getCreateTime, true);
        return list(queryWrapper);
    }

    public void deleteByFieldId(Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MetadataEntityFieldOptionDO::getFieldId, fieldId);
        remove(queryWrapper);
    }
}


