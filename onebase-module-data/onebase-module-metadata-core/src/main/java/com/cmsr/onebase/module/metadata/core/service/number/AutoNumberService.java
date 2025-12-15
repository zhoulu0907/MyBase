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
     * @param fieldUuid   字段UUID
     * @param contextData 上下文数据（用于规则计算）
     * @return 生成的编号
     */
    public String generateNumber(String fieldUuid, Map<String, Object> contextData) {
        // 1. 查询字段的自动编号配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        if (config == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段未配置自动编号: " + fieldUuid);
        }

        // 2. 生成编号
        return generator.generate(config, contextData);
    }

    /**
     * 批量生成自动编号
     *
     * @param fieldUuids      字段UUID列表
     * @param contextDataList 上下文数据列表
     * @return 生成的编号列表
     */
    public List<String> batchGenerateNumbers(List<String> fieldUuids, List<Map<String, Object>> contextDataList) {
        if (fieldUuids.size() != contextDataList.size()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段UUID数量与上下文数据数量不匹配");
        }

        return fieldUuids.stream()
                .mapToInt(i -> fieldUuids.indexOf(i))
                .mapToObj(i -> generateNumber(fieldUuids.get(i), contextDataList.get(i)))
                .toList();
    }

    /**
     * 预览编号格式（不消费序号）
     *
     * @param fieldUuid   字段UUID
     * @param contextData 上下文数据
     * @return 预览结果
     */
    public String previewNumber(String fieldUuid, Map<String, Object> contextData) {
        // 1. 查询字段的自动编号配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        if (config == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段未配置自动编号: " + fieldUuid);
        }

        // 2. 预览编号
        return generator.preview(config, contextData);
    }

    public Map<String, String> generateDataNumbers(List<String> fieldIds, Map<String, Object> contextData) {
        Map<String, String> result = new HashMap<>();
        if (fieldIds == null || fieldIds.isEmpty()) {
            return result;
        }
        List<MetadataAutoNumberConfigDO> configs = configRepository.listEnabledByFieldIds(fieldIds);
        for (MetadataAutoNumberConfigDO config : configs) {
            String number = generator.generate(config, contextData);
            result.put(config.getFieldUuid(), number);
        }
        return result;
    }

    /**
     * 手动重置编号序号
     *
     * @param fieldUuid   字段UUID
     * @param nextValue   下一个编号值
     * @param resetReason 重置原因
     * @param operator    操作者ID
     */
    public void resetSequence(String fieldUuid, Long nextValue, String resetReason, Long operator) {
        // 1. 查询字段的自动编号配置
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        if (config == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "字段未配置自动编号: " + fieldUuid);
        }

        // 2. 验证下一个编号值
        if (nextValue == null || nextValue < config.getInitialValue()) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "下一个编号值不能小于初始值: " + config.getInitialValue());
        }

        // 3. 生成当前周期键
        String periodKey = ruleEngine.generatePeriodKey(config.getResetCycle(), 
                java.time.LocalDateTime.now());

        // 4. 重置序号（使用configUuid）
        stateManager.resetSequence(config.getConfigUuid(), periodKey, resetReason, operator);
    }

    /**
     * 检查字段是否配置了自动编号
     *
     * @param fieldUuid 字段UUID
     * @return 是否配置了自动编号
     */
    public boolean hasAutoNumber(String fieldUuid) {
        MetadataAutoNumberConfigDO config = configRepository.findByFieldUuid(fieldUuid);
        return config != null && config.getIsEnabled() != null && config.getIsEnabled() == 1;
    }

    /**
     * 获取字段的自动编号配置
     *
     * @param fieldUuid 字段UUID
     * @return 自动编号配置
     */
    public MetadataAutoNumberConfigDO getAutoNumberConfig(String fieldUuid) {
        return configRepository.findByFieldUuid(fieldUuid);
    }
}
