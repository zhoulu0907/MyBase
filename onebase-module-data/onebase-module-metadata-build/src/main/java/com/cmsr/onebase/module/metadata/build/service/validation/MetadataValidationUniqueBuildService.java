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
    MetadataValidationUniqueDO getByFieldId(String fieldUuid);
    ValidationUniqueRespVO getByFieldIdWithRgName(String fieldUuid);
    Long create(ValidationUniqueSaveReqVO vo);
    void update(ValidationUniqueUpdateReqVO vo);
    void deleteByFieldId(String fieldUuid);

    /**
     * 按主键ID查询唯一性校验配置（包含规则组名称）
     *
     * @param id 唯一性校验规则主键ID
     * @return 唯一性校验VO，可能为null
     */
    ValidationUniqueRespVO getById(Long id);

    /**
     * 按主键ID删除唯一性校验配置
     *
     * @param id 唯一性校验规则主键ID
     */
    void deleteById(Long id);
}
