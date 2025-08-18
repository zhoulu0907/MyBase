package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO;

import java.util.List;

/**
 * 字段选项 Service
 *
 * @author bty418
 * @date 2025-08-18
 */
public interface MetadataEntityFieldOptionService {

    List<MetadataEntityFieldOptionDO> listByFieldId(Long fieldId);

    Long create(MetadataEntityFieldOptionDO option);

    void update(MetadataEntityFieldOptionDO option);

    void deleteById(Long id);

    void deleteByFieldId(Long fieldId);

    void batchSort(Long fieldId, List<MetadataEntityFieldOptionDO> optionsInOrder);
}


