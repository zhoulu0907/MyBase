package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRangeDO;

/**
 * 范围校验 Service 接口（数值/日期）
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationRangeService {
    MetadataValidationRangeDO getByFieldId(Long fieldId);
    Long create(MetadataValidationRangeDO data);
    void update(MetadataValidationRangeDO data);
    void deleteByFieldId(Long fieldId);
}
