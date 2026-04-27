package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;

/**
 * 子表非空校验 Service 接口
 */
public interface MetadataValidationChildNotEmptyBuildService {
    MetadataValidationChildNotEmptyDO getByFieldId(String fieldUuid);
    ValidationChildNotEmptyRespVO getByFieldIdWithRgName(String fieldUuid);
    
    // 新增：通过主键ID操作的方法
    ValidationChildNotEmptyRespVO getById(Long id);
    void deleteById(Long id);
    
    Long create(ValidationChildNotEmptySaveReqVO vo);
    void update(ValidationChildNotEmptyUpdateReqVO vo);
    void deleteByFieldId(String fieldUuid);
}
