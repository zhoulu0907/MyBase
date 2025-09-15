package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;

import java.util.List;

/**
 * 校验规则定义 Service 接口
 *
 * @author bty418
 * @date 2025-01-27
 */
public interface MetadataValidationRuleDefinitionBuildService {

    /**
     * 根据分组ID删除校验规则定义
     *
     * @param groupId 分组ID
     */
    void deleteByGroupId(Long groupId);

    /**
     * 批量保存校验规则定义
     *
     * @param groupId 分组ID
     * @param valueRules 规则定义列表
     */
    void saveValueRules(Long groupId, List<Object> valueRules);

    /**
     * 保存单个校验规则定义
     *
     * @param ruleDefinition 规则定义对象
     */
    void saveRuleDefinition(MetadataValidationRuleDefinitionDO ruleDefinition);

    /**
     * 根据分组ID获取校验规则定义列表
     *
     * @param groupId 分组ID
     * @return 规则定义列表
     */
    List<MetadataValidationRuleDefinitionDO> getByGroupId(Long groupId);

}
