package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationUniqueDO;

/**
 * 唯一校验 Service 接口
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationUniqueService {
    MetadataValidationUniqueDO getByFieldId(Long fieldId);
    Long create(MetadataValidationUniqueDO data);
    void update(MetadataValidationUniqueDO data);
    void deleteByFieldId(Long fieldId);
}
