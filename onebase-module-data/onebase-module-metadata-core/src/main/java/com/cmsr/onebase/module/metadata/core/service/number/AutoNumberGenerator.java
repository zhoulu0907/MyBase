package com.cmsr.onebase.module.metadata.core.service.number;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;
import com.cmsr.onebase.module.metadata.core.enums.AutoNumberItemTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 自动编号生成器
 * 负责根据规则项生成完整编号
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

        // 4. 如果没有规则项，检查是否配置了 number_mode
        if (ruleItems.isEmpty()) {
            // 如果配置了 NATURAL 或 FIXED_DIGIT/FIXED_DIGITS 模式，直接生成序号
            if ("NATURAL".equals(config.getNumberMode()) 
                    || "FIXED_DIGIT".equals(config.getNumberMode())
                    || "FIXED_DIGITS".equals(config.getNumberMode())) {
                Long sequence = stateManager.getNextSequence(config, periodKey);
                String generatedNumber = ruleEngine.formatSequence(sequence, config.getDigitWidth(), config.getNumberMode());
                log.debug("Generated auto number (no rules): {} for config: {}", generatedNumber, config.getId());
                return generatedNumber;
            }
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "自动编号配置没有有效的规则项");
        }

        // 5. 检查是否需要序号
        // 情况1：规则项中显式包含 SEQUENCE 类型
        // 情况2：配置了 number_mode 但规则项中没有 SEQUENCE（需要自动添加序号）
        boolean hasSequenceRule = ruleItems.stream()
                .anyMatch(item -> AutoNumberItemTypeEnum.SEQUENCE.getCode().equals(item.getItemType()));
        
        boolean autoAppendSequence = !hasSequenceRule && 
                ("NATURAL".equals(config.getNumberMode()) 
                || "FIXED_DIGIT".equals(config.getNumberMode())
                || "FIXED_DIGITS".equals(config.getNumberMode()));
        
        Long sequence = null;
        if (hasSequenceRule || autoAppendSequence) {
            sequence = stateManager.getNextSequence(config, periodKey);
        }

        // 6. 按规则项顺序执行生成
        StringBuilder result = new StringBuilder();
        for (MetadataAutoNumberRuleItemDO ruleItem : ruleItems) {
            String itemResult = ruleEngine.executeRuleItem(ruleItem, contextData, sequence, 
                    config.getDigitWidth(), config.getNumberMode());
            result.append(itemResult);
        }
        
        // 7. 如果配置了 number_mode 但规则项中没有 SEQUENCE，自动追加序号
        if (autoAppendSequence && sequence != null) {
            String sequenceStr = ruleEngine.formatSequence(sequence, config.getDigitWidth(), config.getNumberMode());
            result.append(sequenceStr);
            log.debug("Auto appended sequence: {} for config: {}", sequenceStr, config.getId());
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
        
        if (ruleItems.isEmpty()) {
            return "无有效规则项";
        }

        // 2. 使用示例序号进行预览
        Long previewSequence = config.getInitialValue() != null ? config.getInitialValue() : 1L;

        // 3. 按规则项顺序执行生成
        StringBuilder result = new StringBuilder();
        for (MetadataAutoNumberRuleItemDO ruleItem : ruleItems) {
            String itemResult = ruleEngine.executeRuleItem(ruleItem, contextData, previewSequence, 
                    config.getDigitWidth(), config.getNumberMode());
            result.append(itemResult);
        }

        return result.toString();
    }
}
