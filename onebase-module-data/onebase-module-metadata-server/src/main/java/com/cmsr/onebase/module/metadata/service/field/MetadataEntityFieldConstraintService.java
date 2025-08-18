package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO;

import java.util.List;

/**
 * 字段约束 Service
 *
 * @author bty418
 * @date 2025-08-18
 */
public interface MetadataEntityFieldConstraintService {

    List<MetadataEntityFieldConstraintDO> listByFieldId(Long fieldId);

    void upsert(MetadataEntityFieldConstraintDO constraint);

    void delete(Long fieldId, String constraintType);

    void deleteByFieldId(Long fieldId);
}


