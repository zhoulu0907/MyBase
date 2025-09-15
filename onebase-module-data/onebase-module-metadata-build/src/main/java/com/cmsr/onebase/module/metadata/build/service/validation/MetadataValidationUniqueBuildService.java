package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;

/**
 * 唯一性校验 Service 接口
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationUniqueBuildService {
    MetadataValidationUniqueDO getByFieldId(Long fieldId);
    ValidationUniqueRespVO getByFieldIdWithRgName(Long fieldId);
    Long create(ValidationUniqueSaveReqVO vo);
    void update(ValidationUniqueUpdateReqVO vo);
    void deleteByFieldId(Long fieldId);
}
