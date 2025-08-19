package com.cmsr.onebase.module.metadata.service.number;

import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberRuleItemDO;

import java.util.List;

/**
 * 自动编号-配置 Service
 */
public interface AutoNumberConfigService {
    MetadataAutoNumberConfigDO getByFieldId(Long fieldId);

    MetadataAutoNumberConfigDO getByConfigId(Long configId);

    Long upsert(MetadataAutoNumberConfigDO config);

    void deleteByFieldId(Long fieldId);

    List<MetadataAutoNumberRuleItemDO> listRules(Long configId);
}


