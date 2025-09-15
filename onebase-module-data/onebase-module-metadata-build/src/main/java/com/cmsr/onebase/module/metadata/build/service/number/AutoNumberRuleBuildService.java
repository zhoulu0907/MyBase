package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

public interface AutoNumberRuleBuildService {
    List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId);
    Long add(MetadataAutoNumberRuleItemDO rule);
    void update(MetadataAutoNumberRuleItemDO rule);
    void delete(Long id);
    void batchSort(Long configId, List<MetadataAutoNumberRuleItemDO> rules);
}


