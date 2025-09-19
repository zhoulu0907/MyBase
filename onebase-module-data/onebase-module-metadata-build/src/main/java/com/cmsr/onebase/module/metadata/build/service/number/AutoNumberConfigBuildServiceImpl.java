package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberConfigRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberStateRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberResetLogRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 自动编号配置 Build Service 实现类
 *
 * @author bty418
 * @date 2025-09-17
 */
@Slf4j
@Service
public class AutoNumberConfigBuildServiceImpl implements AutoNumberConfigBuildService {

    @Resource
    private MetadataAutoNumberConfigRepository configRepository;

    @Resource
    private MetadataAutoNumberRuleItemRepository ruleItemRepository;

    @Resource
    private MetadataAutoNumberStateRepository stateRepository;

    @Resource
    private MetadataAutoNumberResetLogRepository resetLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsert(MetadataAutoNumberConfigDO config) {
        if (config.getId() != null) {
            // 更新
            configRepository.update(config);
            return config.getId();
        } else {
            // 新增
            configRepository.insert(config);
            return config.getId();
        }
    }

    @Override
    public MetadataAutoNumberConfigDO getByFieldId(Long fieldId) {
        return configRepository.findByFieldId(fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        MetadataAutoNumberConfigDO config = configRepository.findByFieldId(fieldId);
        if (config != null) {
            // 删除规则项
            ruleItemRepository.deleteByConfigId(config.getId());
            // 删除状态
            stateRepository.deleteByConfigId(config.getId());
            // 删除重置日志
            resetLogRepository.deleteByConfigId(config.getId());
            // 删除配置
            configRepository.deleteByFieldId(fieldId);
            
            log.info("Deleted auto number config for fieldId: {}, configId: {}", fieldId, config.getId());
        }
    }

    @Override
    public List<MetadataAutoNumberRuleItemDO> listRules(Long configId) {
        return ruleItemRepository.listByConfig(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRulesByConfigId(Long configId) {
        ruleItemRepository.deleteByConfigId(configId);
    }

    @Override
    public Object getAutoNumberConfigWithRules(Long fieldId) {
        // 这里应该返回 AutoNumberConfigWithRulesRespVO，但为了简化暂时返回null
        // 实际项目中需要创建相应的VO并组装数据
        MetadataAutoNumberConfigDO config = getByFieldId(fieldId);
        if (config == null) {
            return null;
        }
        List<MetadataAutoNumberRuleItemDO> rules = listRules(config.getId());
        // 返回组装好的VO，这里简化返回
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAutoNumberConfig(MetadataAutoNumberConfigDO config) {
        return upsert(config);
    }
}