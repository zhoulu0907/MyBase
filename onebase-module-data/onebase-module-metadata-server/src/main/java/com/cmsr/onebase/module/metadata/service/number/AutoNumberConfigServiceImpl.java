package com.cmsr.onebase.module.metadata.service.number;

import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataAutoNumberConfigRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataAutoNumberRuleItemRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoNumberConfigServiceImpl implements AutoNumberConfigService {

    @Resource
    private MetadataAutoNumberConfigRepository configRepository;
    @Resource
    private MetadataAutoNumberRuleItemRepository ruleRepository;

    @Override
    public MetadataAutoNumberConfigDO getByFieldId(Long fieldId) {
        return configRepository.findByFieldId(fieldId);
    }

    @Override
    public MetadataAutoNumberConfigDO getByConfigId(Long configId) {
        return configRepository.findById(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsert(MetadataAutoNumberConfigDO config) {
        MetadataAutoNumberConfigDO exist = configRepository.findByFieldId(config.getFieldId());
        if (exist == null) {
            configRepository.insert(config);
            return config.getId();
        } else {
            config.setId(exist.getId());
            configRepository.update(config);
            return exist.getId();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        MetadataAutoNumberConfigDO exist = configRepository.findByFieldId(fieldId);
        if (exist != null) {
            // 先删子表
            ruleRepository.deleteByConfigId(exist.getId());
            configRepository.deleteById(exist.getId());
        }
    }

    @Override
    public List<MetadataAutoNumberRuleItemDO> listRules(Long configId) {
        return ruleRepository.listByConfig(configId);
    }
}


