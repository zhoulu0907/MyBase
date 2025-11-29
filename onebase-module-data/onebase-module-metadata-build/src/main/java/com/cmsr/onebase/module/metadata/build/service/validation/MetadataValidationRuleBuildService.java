package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * 校验规则 Service 接口
 *
 * @author bty418
 * @date 2025-01-27
 */
public interface MetadataValidationRuleBuildService {

    /**
     * 根据条件查询校验规则列表
     *
     * @param queryWrapper 查询条件
     * @return 校验规则列表
     */
    List<MetadataValidationRuleDefinitionDO> findAllByConfig(QueryWrapper queryWrapper);

}
