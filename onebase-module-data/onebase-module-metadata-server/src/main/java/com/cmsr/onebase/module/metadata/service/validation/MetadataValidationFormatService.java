package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationFormatRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationFormatUpdateReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationFormatDO;

/**
 * 格式校验 Service 接口（正则/枚举）
 */
public interface MetadataValidationFormatService {
    MetadataValidationFormatDO getRegexByFieldId(Long fieldId);
    ValidationFormatRespVO getRegexByFieldIdWithRgName(Long fieldId);
    Long create(ValidationFormatSaveReqVO vo);
    void update(ValidationFormatUpdateReqVO reqVO);
    void deleteByFieldId(Long fieldId);
}
