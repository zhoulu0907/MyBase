package com.cmsr.onebase.module.metadata.service.number;

import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

public interface AutoNumberRuleService {
    List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId);
    Long add(MetadataAutoNumberRuleItemDO rule);
    void update(MetadataAutoNumberRuleItemDO rule);
    void delete(Long id);
    void batchSort(Long configId, List<MetadataAutoNumberRuleItemDO> rules);
}


