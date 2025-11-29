package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 自动编号规则项 Build Service 实现类
 *
 * @author bty418
 * @date 2025-09-17
 */
@Slf4j
@Service
public class AutoNumberRuleBuildServiceImpl implements AutoNumberRuleBuildService {

    @Resource
    private MetadataAutoNumberRuleItemRepository ruleItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(MetadataAutoNumberRuleItemDO ruleItem) {
        ruleItemRepository.save(ruleItem);
        return ruleItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataAutoNumberRuleItemDO ruleItem) {
        ruleItemRepository.updateById(ruleItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        ruleItemRepository.removeById(id);
    }

    @Override
    public List<MetadataAutoNumberRuleItemDO> listByConfigId(Long configId) {
        return ruleItemRepository.listByConfig(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByConfigId(Long configId) {
        ruleItemRepository.deleteByConfigId(configId);
    }

    @Override
    public List<MetadataAutoNumberRuleItemDO> listByConfig(Long configId) {
        return listByConfigId(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSort(Long configId, List<MetadataAutoNumberRuleItemDO> items) {
        // 批量更新排序
        for (int i = 0; i < items.size(); i++) {
            MetadataAutoNumberRuleItemDO item = items.get(i);
            item.setItemOrder(i + 1);
            ruleItemRepository.updateById(item);
        }
    }
}