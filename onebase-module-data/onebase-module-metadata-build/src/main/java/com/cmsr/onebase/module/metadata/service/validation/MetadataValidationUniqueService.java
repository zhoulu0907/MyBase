package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationUniqueRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationUniqueSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationUniqueUpdateReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationUniqueDO;

/**
 * 唯一性校验 Service 接口
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationUniqueService {
    MetadataValidationUniqueDO getByFieldId(Long fieldId);
    ValidationUniqueRespVO getByFieldIdWithRgName(Long fieldId);
    Long create(ValidationUniqueSaveReqVO vo);
    void update(ValidationUniqueUpdateReqVO vo);
    void deleteByFieldId(Long fieldId);
}
