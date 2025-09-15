package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRangeUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO;

/**
 * 范围校验 Service 接口（数值/日期）
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationRangeBuildService {
    MetadataValidationRangeDO getByFieldId(Long fieldId);
    ValidationRangeRespVO getByFieldIdWithRgName(Long fieldId);
    Long create(ValidationRangeSaveReqVO vo);
    void update(ValidationRangeUpdateReqVO reqVO);
    void deleteByFieldId(Long fieldId);
}
