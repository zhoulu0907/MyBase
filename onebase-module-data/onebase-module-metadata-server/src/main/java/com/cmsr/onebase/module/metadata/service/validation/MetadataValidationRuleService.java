package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import org.anyline.data.param.init.DefaultConfigStore;

import java.util.List;

/**
 * 校验规则 Service 接口
 *
 * @author bty418
 * @date 2025-01-27
 */
public interface MetadataValidationRuleService {

    /**
     * 根据条件查询校验规则列表
     *
     * @param configStore 查询条件
     * @return 校验规则列表
     */
    List<MetadataValidationRuleDefinitionDO> findAllByConfig(DefaultConfigStore configStore);

}
