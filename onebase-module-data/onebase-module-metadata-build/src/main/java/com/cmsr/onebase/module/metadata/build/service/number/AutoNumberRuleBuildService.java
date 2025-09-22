package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

/**
 * 自动编号规则项 Build Service 接口
 *
 * @author bty418
 * @date 2025-09-17
 */
public interface AutoNumberRuleBuildService {

    /**
     * 新增规则项
     *
     * @param ruleItem 规则项信息
     * @return 规则项ID
     */
    Long add(MetadataAutoNumberRuleItemDO ruleItem);

    /**
     * 更新规则项
     *
     * @param ruleItem 规则项信息
     */
    void update(MetadataAutoNumberRuleItemDO ruleItem);

    /**
     * 根据ID删除规则项
     *
     * @param id 规则项ID
     */
    void deleteById(Long id);

    /**
     * 根据配置ID获取规则项列表
     *
     * @param configId 配置ID
     * @return 规则项列表
     */
    List<MetadataAutoNumberRuleItemDO> listByConfigId(Long configId);

    /**
     * 根据配置ID删除所有规则项
     *
     * @param configId 配置ID
     */
    void deleteByConfigId(Long configId);

    /**
     * 根据配置ID获取规则项列表（Controller使用）
     *
     * @param configId 配置ID
     * @return 规则项列表
     */
    List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId);

    /**
     * 删除规则项（Controller使用）
     *
     * @param id 规则项ID
     */
    void delete(Long id);

    /**
     * 批量排序规则项
     *
     * @param configId 配置ID
     * @param items 规则项列表
     */
    void batchSort(Long configId, List<MetadataAutoNumberRuleItemDO> items);
}