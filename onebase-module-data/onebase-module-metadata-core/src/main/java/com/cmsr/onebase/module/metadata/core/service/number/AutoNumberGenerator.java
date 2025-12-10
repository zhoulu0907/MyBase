package com.cmsr.onebase.module.metadata.core.service.number;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;
import com.cmsr.onebase.module.metadata.core.enums.AutoNumberItemTypeEnum;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 自动编号生成器
 * 负责根据规则项生成完整编号
 * <p>
 * 支持统一规则排序：将Config中的SEQUENCE配置与RuleItem合并，按sequenceOrder和itemOrder统一排序后生成
 *
 * @author bty418
 * @date 2025-09-17
 */
@Slf4j
@Component
public class AutoNumberGenerator {

    @Resource
    private MetadataAutoNumberRuleItemRepository ruleItemRepository;

    @Resource
    private AutoNumberRuleEngine ruleEngine;

    @Resource
    private AutoNumberStateManager stateManager;

    /**
     * 根据规则配置生成编号
     * <p>
     * 将Config中的SEQUENCE配置与RuleItem合并为统一规则列表，按顺序生成编号
     *
     * @param config      自动编号配置
     * @param contextData 上下文数据
     * @return 生成的编号
     */
    public String generate(MetadataAutoNumberConfigDO config, Map<String, Object> contextData) {
        // 1. 检查配置是否启用
        if (config.getIsEnabled() == null || config.getIsEnabled() != 1) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "自动编号配置未启用");
        }

        // 2. 获取规则项列表
        List<MetadataAutoNumberRuleItemDO> ruleItems = ruleItemRepository
                .listByConfigUuid(config.getConfigUuid());
        
        // 3. 生成周期键
        String periodKey = ruleEngine.generatePeriodKey(config.getResetCycle(), LocalDateTime.now());

        // 4. 构建统一规则列表（合并SEQUENCE和RuleItem）
        List<UnifiedRule> unifiedRules = buildUnifiedRules(config, ruleItems);
        
        // 5. 如果没有规则，返回错误
        if (unifiedRules.isEmpty()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "自动编号配置没有有效的规则项");
        }

        // 6. 获取序号（只需获取一次）
        Long sequence = stateManager.getNextSequence(config, periodKey);

        // 7. 按统一顺序生成编号
        StringBuilder result = new StringBuilder();
        for (UnifiedRule rule : unifiedRules) {
            String itemResult;
            if (rule.isSequence()) {
                // SEQUENCE类型：格式化序号
                itemResult = ruleEngine.formatSequence(sequence, config.getDigitWidth(), config.getNumberMode());
            } else {
                // 其他类型：执行规则项
                itemResult = ruleEngine.executeRuleItem(rule.getRuleItem(), contextData, sequence, 
                        config.getDigitWidth(), config.getNumberMode());
            }
            result.append(itemResult);
        }

        String generatedNumber = result.toString();
        log.debug("Generated auto number: {} for config: {}", generatedNumber, config.getId());
        return generatedNumber;
    }

    /**
     * 预览编号格式（不消费序号）
     *
     * @param config      自动编号配置
     * @param contextData 上下文数据
     * @return 预览结果
     */
    public String preview(MetadataAutoNumberConfigDO config, Map<String, Object> contextData) {
        // 1. 获取规则项列表
        List<MetadataAutoNumberRuleItemDO> ruleItems = ruleItemRepository
                .listByConfigUuid(config.getConfigUuid());
        
        // 2. 构建统一规则列表
        List<UnifiedRule> unifiedRules = buildUnifiedRules(config, ruleItems);
        
        if (unifiedRules.isEmpty()) {
            return "无有效规则项";
        }

        // 3. 使用示例序号进行预览
        Long previewSequence = config.getInitialValue() != null ? config.getInitialValue() : 1L;

        // 4. 按统一顺序生成预览编号
        StringBuilder result = new StringBuilder();
        for (UnifiedRule rule : unifiedRules) {
            String itemResult;
            if (rule.isSequence()) {
                itemResult = ruleEngine.formatSequence(previewSequence, config.getDigitWidth(), config.getNumberMode());
            } else {
                itemResult = ruleEngine.executeRuleItem(rule.getRuleItem(), contextData, previewSequence, 
                        config.getDigitWidth(), config.getNumberMode());
            }
            result.append(itemResult);
        }

        return result.toString();
    }

    /**
     * 构建统一规则列表
     * <p>
     * 将Config中的SEQUENCE配置与RuleItem合并，按sequenceOrder和itemOrder统一排序
     *
     * @param config    配置
     * @param ruleItems 规则项列表
     * @return 统一规则列表（已排序）
     */
    private List<UnifiedRule> buildUnifiedRules(MetadataAutoNumberConfigDO config, 
                                                  List<MetadataAutoNumberRuleItemDO> ruleItems) {
        List<UnifiedRule> rules = new ArrayList<>();
        
        // 添加SEQUENCE规则（从Config中获取）
        // 只有配置了numberMode时才添加SEQUENCE
        if (config.getNumberMode() != null && !config.getNumberMode().isEmpty()) {
            int sequenceOrder = config.getSequenceOrder() != null ? config.getSequenceOrder() : 999;
            rules.add(new UnifiedRule(sequenceOrder, true, null));
        }
        
        // 添加其他规则项（排除SEQUENCE类型，因为SEQUENCE已从Config获取）
        for (MetadataAutoNumberRuleItemDO item : ruleItems) {
            // 跳过SEQUENCE类型（兼容旧数据中可能存在的SEQUENCE规则项）
            if (AutoNumberItemTypeEnum.SEQUENCE.getCode().equals(item.getItemType())) {
                continue;
            }
            rules.add(new UnifiedRule(item.getItemOrder(), false, item));
        }
        
        // 按order排序
        rules.sort(Comparator.comparing(UnifiedRule::getOrder));
        
        return rules;
    }

    /**
     * 统一规则内部类
     * 用于将SEQUENCE配置和RuleItem统一处理
     */
    @Data
    @AllArgsConstructor
    private static class UnifiedRule {
        /**
         * 排序序号
         */
        private int order;
        
        /**
         * 是否为SEQUENCE类型
         */
        private boolean sequence;
        
        /**
         * 规则项（非SEQUENCE类型时有值）
         */
        private MetadataAutoNumberRuleItemDO ruleItem;
    }
}
