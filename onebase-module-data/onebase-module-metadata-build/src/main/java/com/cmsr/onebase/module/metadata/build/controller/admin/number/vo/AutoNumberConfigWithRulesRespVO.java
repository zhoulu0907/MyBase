package com.cmsr.onebase.module.metadata.build.controller.admin.number.vo;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

/**
 * 自动编号配置 + 规则项 响应 VO
 */
public class AutoNumberConfigWithRulesRespVO {
    private MetadataAutoNumberConfigDO config;
    private List<MetadataAutoNumberRuleItemDO> rules;

    public MetadataAutoNumberConfigDO getConfig() { return config; }
    public void setConfig(MetadataAutoNumberConfigDO config) { this.config = config; }
    public List<MetadataAutoNumberRuleItemDO> getRules() { return rules; }
    public void setRules(List<MetadataAutoNumberRuleItemDO> rules) { this.rules = rules; }
}


