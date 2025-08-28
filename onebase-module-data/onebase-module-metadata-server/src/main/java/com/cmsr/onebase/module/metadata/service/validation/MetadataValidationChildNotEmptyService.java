package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;

/**
 * 子表非空校验 Service 接口
 */
public interface MetadataValidationChildNotEmptyService {
    MetadataValidationChildNotEmptyDO getByFieldId(Long fieldId);
    Long create(ValidationChildNotEmptySaveReqVO vo);
    void update(MetadataValidationChildNotEmptyDO data);
    void deleteByFieldId(Long fieldId);
}
