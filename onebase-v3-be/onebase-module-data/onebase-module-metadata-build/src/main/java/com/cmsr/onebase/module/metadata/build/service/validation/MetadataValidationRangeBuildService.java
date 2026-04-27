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
    MetadataValidationRangeDO getByFieldId(String fieldUuid);
    ValidationRangeRespVO getByFieldIdWithRgName(String fieldUuid);
    Long create(ValidationRangeSaveReqVO vo);
    void update(ValidationRangeUpdateReqVO reqVO);
    void deleteByFieldId(String fieldUuid);

    /**
     * 按主键ID查询范围校验配置（包含规则组名称）
     *
     * @param id 范围校验规则主键ID
     * @return 范围校验VO，可能为null
     */
    ValidationRangeRespVO getById(Long id);

    /**
     * 按主键ID删除范围校验配置
     *
     * @param id 范围校验规则主键ID
     */
    void deleteById(Long id);
}
