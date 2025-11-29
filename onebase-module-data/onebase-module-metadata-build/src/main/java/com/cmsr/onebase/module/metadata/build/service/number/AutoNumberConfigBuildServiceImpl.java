package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.build.controller.admin.number.vo.AutoNumberConfigWithRulesRespVO;
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
            configRepository.updateById(config);
            return config.getId();
        } else {
            // 新增
            configRepository.save(config);
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

    /**
     * 获取自动编号配置及其规则项
     *
     * @param fieldId 字段ID
     * @return 自动编号配置及规则项响应VO，如果配置不存在则返回null
     */
    @Override
    public AutoNumberConfigWithRulesRespVO getAutoNumberConfigWithRules(Long fieldId) {
        // 根据字段ID获取配置
        MetadataAutoNumberConfigDO config = getByFieldId(fieldId);
        if (config == null) {
            return null;
        }
        
        // 获取该配置下的所有规则项
        List<MetadataAutoNumberRuleItemDO> rules = listRules(config.getId());
        
        // 组装返回VO
        AutoNumberConfigWithRulesRespVO respVO = new AutoNumberConfigWithRulesRespVO();
        respVO.setConfig(config);
        respVO.setRules(rules);
        
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAutoNumberConfig(MetadataAutoNumberConfigDO config) {
        return upsert(config);
    }
}