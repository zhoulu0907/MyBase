package com.cmsr.onebase.module.metadata.runtime.controller.app.number.vo;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

/**
 * 自动编号配置 + 规则项 响应 VO
 *
 * @author bty418
 * @date 2025-10-30
 */
public class AutoNumberConfigWithRulesRespVO {
    
    /**
     * 自动编号配置信息
     */
    private MetadataAutoNumberConfigDO config;
    
    /**
     * 规则项列表
     */
    private List<MetadataAutoNumberRuleItemDO> rules;

    public MetadataAutoNumberConfigDO getConfig() {
        return config;
    }

    public void setConfig(MetadataAutoNumberConfigDO config) {
        this.config = config;
    }

    public List<MetadataAutoNumberRuleItemDO> getRules() {
        return rules;
    }

    public void setRules(List<MetadataAutoNumberRuleItemDO> rules) {
        this.rules = rules;
    }
}

