package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoNumberRuleBuildServiceImpl implements AutoNumberRuleBuildService {

    @Resource
    private MetadataAutoNumberRuleItemRepository ruleRepository;

    @Override
    public List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId) {
        return ruleRepository.listByConfig(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(MetadataAutoNumberRuleItemDO rule) {
        ruleRepository.insert(rule);
        return rule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataAutoNumberRuleItemDO rule) {
        ruleRepository.update(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ruleRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSort(Long configId, List<MetadataAutoNumberRuleItemDO> rules) {
        if (rules == null) return;
        for (var r : rules) {
            MetadataAutoNumberRuleItemDO upd = new MetadataAutoNumberRuleItemDO();
            upd.setId(r.getId());
            upd.setItemOrder(r.getItemOrder());
            ruleRepository.update(upd);
        }
    }
}


