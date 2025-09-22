package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;

/**
 * 必填校验 Service 接口
 *
 * @author bty418
 * @date 2025-08-27
 */
public interface MetadataValidationRequiredBuildService {
    MetadataValidationRequiredDO getByFieldId(Long fieldId);
    ValidationRequiredRespVO getByFieldIdWithRgName(Long fieldId);
    Long create(ValidationRequiredSaveReqVO vo);
    void update(ValidationRequiredUpdateReqVO reqVO);
    void deleteByFieldId(Long fieldId);

    /**
     * 按主键ID查询必填校验配置（包含规则组名称）
     *
     * @param id 必填校验规则主键ID
     * @return 必填校验VO，可能为null
     */
    ValidationRequiredRespVO getById(Long id);

    /**
     * 按主键ID删除必填校验配置
     *
     * @param id 必填校验规则主键ID
     */
    void deleteById(Long id);
}
