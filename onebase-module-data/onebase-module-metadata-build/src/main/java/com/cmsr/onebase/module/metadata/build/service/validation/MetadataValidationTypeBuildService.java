package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;

import java.util.Map;
import java.util.Set;

/**
 * 校验类型查询 Service
 *
 * @author GitHub Copilot
 * @date 2025-09-11
 */
public interface MetadataValidationTypeBuildService {

    /**
     * 批量按ID查询校验类型（deleted=0）
     *
     * @param ids 校验类型ID集合
     * @return id -> 校验类型DO映射
     */
    Map<Long, MetadataValidationTypeDO> getByIds(Set<Long> ids);
}
