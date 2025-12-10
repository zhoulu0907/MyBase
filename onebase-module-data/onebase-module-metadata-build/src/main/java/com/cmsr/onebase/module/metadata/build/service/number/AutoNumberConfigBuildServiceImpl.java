package com.cmsr.onebase.module.metadata.build.service.number;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.AutoNumberConfigReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.AutoNumberConfigRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.AutoNumberRuleVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.build.controller.admin.number.vo.AutoNumberConfigWithRulesRespVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberConfigRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberStateRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberResetLogRepository;
import com.cmsr.onebase.module.metadata.core.enums.AutoNumberItemTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveConfigWithUnifiedRules(String fieldUuid, AutoNumberConfigReqVO reqVO) {
        List<AutoNumberRuleVO> rules = reqVO.getRuleItems();
        
        // 1. 校验规则项列表
        validateUnifiedRules(rules);
        
        // 2. 查找或创建配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        boolean isNew = (config == null);
        if (isNew) {
            config = new MetadataAutoNumberConfigDO();
            config.setConfigUuid(UUID.randomUUID().toString());
            config.setFieldUuid(fieldUuid);
        }
        
        // 3. 设置Config级别属性
        config.setIsEnabled(reqVO.getIsEnabled());
        
        // 4. 从规则列表中提取SEQUENCE配置
        AutoNumberRuleVO sequenceRule = rules.stream()
                .filter(r -> AutoNumberItemTypeEnum.SEQUENCE.getCode().equals(r.getItemType()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                        "规则项列表中必须包含一个SEQUENCE类型"));
        
        // 5. 将SEQUENCE配置存入Config表
        config.setNumberMode(sequenceRule.getNumberMode());
        config.setDigitWidth(sequenceRule.getDigitWidth());
        config.setOverflowContinue(sequenceRule.getOverflowContinue());
        config.setInitialValue(sequenceRule.getInitialValue());
        config.setResetCycle(sequenceRule.getResetCycle());
        config.setResetOnInitialChange(sequenceRule.getResetOnInitialChange());
        config.setSequenceOrder(sequenceRule.getItemOrder());
        
        // 6. 保存Config
        if (isNew) {
            configRepository.save(config);
        } else {
            configRepository.updateById(config);
        }
        
        // 7. 删除旧的规则项
        ruleItemRepository.deleteByConfigUuid(config.getConfigUuid());
        
        // 8. 保存其他类型的规则项（非SEQUENCE）
        for (AutoNumberRuleVO rule : rules) {
            if (AutoNumberItemTypeEnum.SEQUENCE.getCode().equals(rule.getItemType())) {
                continue; // SEQUENCE已存入Config，跳过
            }
            
            MetadataAutoNumberRuleItemDO ruleItem = new MetadataAutoNumberRuleItemDO();
            ruleItem.setRuleItemUuid(UUID.randomUUID().toString());
            ruleItem.setConfigUuid(config.getConfigUuid());
            ruleItem.setItemType(rule.getItemType());
            ruleItem.setItemOrder(rule.getItemOrder());
            ruleItem.setIsEnabled(rule.getIsEnabled() != null ? rule.getIsEnabled() : 1);
            
            // 根据类型设置专属字段
            if (AutoNumberItemTypeEnum.TEXT.getCode().equals(rule.getItemType())) {
                ruleItem.setTextValue(rule.getTextValue());
            } else if (AutoNumberItemTypeEnum.DATE.getCode().equals(rule.getItemType())) {
                ruleItem.setFormat(rule.getFormat());
            } else if (AutoNumberItemTypeEnum.FIELD_REF.getCode().equals(rule.getItemType())) {
                ruleItem.setRefFieldUuid(rule.getRefFieldUuid());
            }
            
            ruleItemRepository.save(ruleItem);
        }
        
        log.info("Saved auto number config with unified rules, fieldUuid: {}, configId: {}, rulesCount: {}", 
                fieldUuid, config.getId(), rules.size());
        
        return config.getId();
    }

    @Override
    public AutoNumberConfigRespVO getConfigWithUnifiedRules(String fieldUuid) {
        // 1. 查询Config
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        if (config == null) {
            return null;
        }
        
        // 2. 查询RuleItem列表
        List<MetadataAutoNumberRuleItemDO> ruleItems = ruleItemRepository.listByConfigUuid(config.getConfigUuid());
        
        // 3. 组装统一规则列表
        List<AutoNumberRuleVO> unifiedRules = new ArrayList<>();
        
        // 3.1 将SEQUENCE配置组装为规则项（id返回configId）
        AutoNumberRuleVO sequenceRule = new AutoNumberRuleVO();
        sequenceRule.setId(config.getId());
        sequenceRule.setUuid(config.getConfigUuid());
        sequenceRule.setItemType(AutoNumberItemTypeEnum.SEQUENCE.getCode());
        sequenceRule.setItemOrder(config.getSequenceOrder() != null ? config.getSequenceOrder() : 999);
        sequenceRule.setIsEnabled(config.getIsEnabled());
        sequenceRule.setNumberMode(config.getNumberMode());
        sequenceRule.setDigitWidth(config.getDigitWidth());
        sequenceRule.setOverflowContinue(config.getOverflowContinue());
        sequenceRule.setInitialValue(config.getInitialValue());
        sequenceRule.setResetCycle(config.getResetCycle());
        sequenceRule.setResetOnInitialChange(config.getResetOnInitialChange());
        sequenceRule.setCreateTime(config.getCreateTime());
        sequenceRule.setUpdateTime(config.getUpdateTime());
        unifiedRules.add(sequenceRule);
        
        // 3.2 将RuleItem转换为统一规则VO（id返回ruleItemId）
        for (MetadataAutoNumberRuleItemDO item : ruleItems) {
            AutoNumberRuleVO ruleVO = new AutoNumberRuleVO();
            ruleVO.setId(item.getId());
            ruleVO.setUuid(item.getRuleItemUuid());
            ruleVO.setItemType(item.getItemType());
            ruleVO.setItemOrder(item.getItemOrder());
            ruleVO.setIsEnabled(item.getIsEnabled());
            ruleVO.setTextValue(item.getTextValue());
            ruleVO.setFormat(item.getFormat());
            ruleVO.setRefFieldUuid(item.getRefFieldUuid());
            ruleVO.setCreateTime(item.getCreateTime());
            ruleVO.setUpdateTime(item.getUpdateTime());
            unifiedRules.add(ruleVO);
        }
        
        // 4. 按itemOrder排序
        unifiedRules.sort(Comparator.comparing(AutoNumberRuleVO::getItemOrder));
        
        // 5. 组装响应VO
        AutoNumberConfigRespVO respVO = new AutoNumberConfigRespVO();
        respVO.setId(config.getId());
        respVO.setConfigUuid(config.getConfigUuid());
        respVO.setFieldUuid(config.getFieldUuid());
        respVO.setIsEnabled(config.getIsEnabled());
        respVO.setCreateTime(config.getCreateTime());
        respVO.setUpdateTime(config.getUpdateTime());
        respVO.setRuleItems(unifiedRules);
        
        return respVO;
    }

    /**
     * 校验统一规则列表
     * <ul>
     *   <li>必须且只能有一个SEQUENCE类型</li>
     *   <li>itemOrder不能重复</li>
     * </ul>
     *
     * @param rules 规则列表
     */
    private void validateUnifiedRules(List<AutoNumberRuleVO> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "规则项列表不能为空");
        }
        
        // 校验SEQUENCE数量
        long sequenceCount = rules.stream()
                .filter(r -> AutoNumberItemTypeEnum.SEQUENCE.getCode().equals(r.getItemType()))
                .count();
        if (sequenceCount == 0) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "规则项列表中必须包含一个SEQUENCE类型");
        }
        if (sequenceCount > 1) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "规则项列表中只能有一个SEQUENCE类型");
        }
        
        // 校验itemOrder不能重复
        long distinctOrderCount = rules.stream()
                .map(AutoNumberRuleVO::getItemOrder)
                .distinct()
                .count();
        if (distinctOrderCount != rules.size()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "规则项的排序序号不能重复");
        }
    }
}