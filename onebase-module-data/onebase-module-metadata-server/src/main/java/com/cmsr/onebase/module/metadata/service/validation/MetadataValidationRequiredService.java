package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRequiredSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRequiredDO;

/**
 * 必填校验 Service 接口
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationRequiredService {
    MetadataValidationRequiredDO getByFieldId(Long fieldId);
    Long create(ValidationRequiredSaveReqVO vo);
    void update(MetadataValidationRequiredDO data);
    void deleteByFieldId(Long fieldId);
}
