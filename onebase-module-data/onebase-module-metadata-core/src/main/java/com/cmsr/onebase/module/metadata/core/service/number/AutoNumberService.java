package com.cmsr.onebase.module.metadata.core.service.number;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberConfigRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 自动编号核心服务
 * 负责协调整个自动编号流程
 *
 * @author bty418
 * @date 2025-09-17
 */
@Slf4j
@Service
public class AutoNumberService {

    @Resource
    private MetadataAutoNumberConfigRepository configRepository;

    @Resource
    private AutoNumberGenerator generator;

    @Resource
    private AutoNumberStateManager stateManager;

    @Resource
    private AutoNumberRuleEngine ruleEngine;

    /**
     * 为指定字段生成自动编号
     *
     * @param fieldId     字段ID
     * @param contextData 上下文数据（用于规则计算）
     * @return 生成的编号
     */
    public String generateNumber(Long fieldId, Map<String, Object> contextData) {
        // 1. 查询字段的自动编号配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldId(fieldId);
        if (config == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段未配置自动编号: " + fieldId);
        }

        // 2. 生成编号
        return generator.generate(config, contextData);
    }

    /**
     * 批量生成自动编号
     *
     * @param fieldIds        字段ID列表
     * @param contextDataList 上下文数据列表
     * @return 生成的编号列表
     */
    public List<String> batchGenerateNumbers(List<Long> fieldIds, List<Map<String, Object>> contextDataList) {
        if (fieldIds.size() != contextDataList.size()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段ID数量与上下文数据数量不匹配");
        }

        return fieldIds.stream()
                .mapToInt(i -> fieldIds.indexOf(i))
                .mapToObj(i -> generateNumber(fieldIds.get(i), contextDataList.get(i)))
                .toList();
    }

    /**
     * 预览编号格式（不消费序号）
     *
     * @param fieldId     字段ID
     * @param contextData 上下文数据
     * @return 预览结果
     */
    public String previewNumber(Long fieldId, Map<String, Object> contextData) {
        // 1. 查询字段的自动编号配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldId(fieldId);
        if (config == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段未配置自动编号: " + fieldId);
        }

        // 2. 预览编号
        return generator.preview(config, contextData);
    }

    public Map<String, String> generateDataNumbers(List<Long> fieldIds, Map<String, Object> contextData) {
        Map<String, String> result = new HashMap<>();
        if (fieldIds == null || fieldIds.isEmpty()) {
            return result;
        }
        List<MetadataAutoNumberConfigDO> configs = configRepository.listEnabledByFieldIds(fieldIds);
        for (MetadataAutoNumberConfigDO config : configs) {
            String number = generator.generate(config, contextData);
            result.put(String.valueOf(config.getFieldId()), number);
        }
        return result;
    }

    /**
     * 手动重置编号序号
     *
     * @param fieldId     字段ID
     * @param nextValue   下一个编号值
     * @param resetReason 重置原因
     * @param operator    操作者ID
     */
    public void resetSequence(Long fieldId, Long nextValue, String resetReason, Long operator) {
        // 1. 查询字段的自动编号配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldId(fieldId);
        if (config == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段未配置自动编号: " + fieldId);
        }

        // 2. 验证下一个编号值
        if (nextValue == null || nextValue < config.getInitialValue()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "下一个编号值不能小于初始值: " + config.getInitialValue());
        }

        // 3. 生成当前周期键
        String periodKey = ruleEngine.generatePeriodKey(config.getResetCycle(), 
                java.time.LocalDateTime.now());

        // 4. 重置序号
        stateManager.resetSequence(config.getId(), periodKey, resetReason, operator);
    }

    /**
     * 检查字段是否配置了自动编号
     *
     * @param fieldId 字段ID
     * @return 是否配置了自动编号
     */
    public boolean hasAutoNumber(Long fieldId) {
        MetadataAutoNumberConfigDO config = configRepository.findByFieldId(fieldId);
        return config != null && config.getIsEnabled() != null && config.getIsEnabled() == 1;
    }

    /**
     * 获取字段的自动编号配置
     *
     * @param fieldId 字段ID
     * @return 自动编号配置
     */
    public MetadataAutoNumberConfigDO getAutoNumberConfig(Long fieldId) {
        return configRepository.findByFieldId(fieldId);
    }
}
