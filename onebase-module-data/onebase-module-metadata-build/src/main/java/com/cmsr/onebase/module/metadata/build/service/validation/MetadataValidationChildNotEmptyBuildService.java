package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;

/**
 * 子表非空校验 Service 接口
 */
public interface MetadataValidationChildNotEmptyBuildService {
    MetadataValidationChildNotEmptyDO getByFieldId(Long fieldId);
    ValidationChildNotEmptyRespVO getByFieldIdWithRgName(Long fieldId);
    Long create(ValidationChildNotEmptySaveReqVO vo);
    void update(ValidationChildNotEmptyUpdateReqVO vo);
    void deleteByFieldId(Long fieldId);
}
