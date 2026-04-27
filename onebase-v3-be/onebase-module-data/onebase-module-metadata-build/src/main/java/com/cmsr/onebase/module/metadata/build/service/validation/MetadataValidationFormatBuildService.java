package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;

/**
 * 格式校验 Service 接口（正则/枚举）
 */
public interface MetadataValidationFormatBuildService {
    MetadataValidationFormatDO getRegexByFieldId(String fieldUuid);
    ValidationFormatRespVO getRegexByFieldIdWithRgName(String fieldUuid);
    
    // 新增：通过主键ID操作的方法
    ValidationFormatRespVO getById(Long id);
    void deleteById(Long id);
    
    Long create(ValidationFormatSaveReqVO vo);
    void update(ValidationFormatUpdateReqVO reqVO);
    void deleteByFieldId(String fieldUuid);
}
