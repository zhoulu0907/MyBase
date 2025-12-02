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
    public MetadataAutoNumberConfigDO getByFieldId(String fieldUuid) {
        return configRepository.findByFieldUuid(fieldUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        if (config != null) {
            // 删除规则项
            ruleItemRepository.deleteByConfigUuid(config.getConfigUuid());
            // 删除状态
            stateRepository.deleteByConfigUuid(config.getConfigUuid());
            // 删除重置日志（暂时注释，需要Repository支持）
            // resetLogRepository.deleteByConfigUuid(config.getConfigUuid());
            // 删除配置
            configRepository.deleteByFieldUuid(fieldUuid);
            
            log.info("Deleted auto number config for fieldUuid: {}, configId: {}", fieldUuid, config.getId());
        }
    }

    @Override
    public List<MetadataAutoNumberRuleItemDO> listRules(Long configId) {
        MetadataAutoNumberConfigDO config = configRepository.getById(configId);
        if (config == null) {
            return List.of();
        }
        return ruleItemRepository.listByConfigUuid(config.getConfigUuid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRulesByConfigId(Long configId) {
        MetadataAutoNumberConfigDO config = configRepository.getById(configId);
        if (config != null) {
            ruleItemRepository.deleteByConfigUuid(config.getConfigUuid());
        }
    }

    /**
     * 获取自动编号配置及其规则项
     *
     * @param fieldUuid 字段UUID
     * @return 自动编号配置及规则项响应VO，如果配置不存在则返回null
     */
    @Override
    public AutoNumberConfigWithRulesRespVO getAutoNumberConfigWithRules(String fieldUuid) {
        // 根据字段UUID获取配置
        MetadataAutoNumberConfigDO config = getByFieldId(fieldUuid);
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