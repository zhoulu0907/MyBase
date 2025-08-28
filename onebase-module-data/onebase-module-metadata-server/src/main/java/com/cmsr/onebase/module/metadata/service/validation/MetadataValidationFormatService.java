package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationFormatDO;

/**
 * 格式校验 Service 接口（含REGEX）
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationFormatService {
    MetadataValidationFormatDO getRegexByFieldId(Long fieldId);
    Long create(ValidationFormatSaveReqVO vo);
    void update(MetadataValidationFormatDO data);
    void deleteByFieldId(Long fieldId);
}
