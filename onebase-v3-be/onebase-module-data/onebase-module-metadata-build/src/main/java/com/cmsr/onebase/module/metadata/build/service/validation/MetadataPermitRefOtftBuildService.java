package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;

import java.util.List;
import java.util.Set;

/**
 * 字段类型与校验类型关联查询 Service
 * 表：metadata_permit_ref_otft
 *
 * @author GitHub Copilot
 * @date 2025-09-11
 */
public interface MetadataPermitRefOtftBuildService {

    /**
     * 根据字段类型ID集合查询关联配置（deleted=0），按 field_type_id, sort_order 升序
     *
     * @param fieldTypeIds 字段类型ID集合
     * @return 关联关系列表
     */
    List<MetadataPermitRefOtftDO> listByFieldTypeIds(Set<Long> fieldTypeIds);
}
