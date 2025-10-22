package com.cmsr.onebase.module.metadata.core.service.number;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.enums.AutoNumberItemTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.NumberModeEnum;
import com.cmsr.onebase.module.metadata.core.enums.ResetCycleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 自动编号规则引擎
 * 负责解析和执行各种规则项
 *
 * @author bty418
 * @date 2025-09-17
 */
@Slf4j
@Component
public class AutoNumberRuleEngine {

    /**
     * 执行规则项
     *
     * @param ruleItem    规则项
     * @param contextData 上下文数据
     * @param sequence    序号值（仅SEQUENCE类型使用）
     * @param digitWidth  位数（仅SEQUENCE类型使用）
     * @param numberMode  编号模式（仅SEQUENCE类型使用）
     * @return 规则项执行结果
     */
    public String executeRuleItem(MetadataAutoNumberRuleItemDO ruleItem, Map<String, Object> contextData, 
                                 Long sequence, Short digitWidth, String numberMode) {
        AutoNumberItemTypeEnum itemType = AutoNumberItemTypeEnum.fromCode(ruleItem.getItemType());
        
        switch (itemType) {
            case TEXT:
                return executeTextRule(ruleItem);
            case DATE:
                return executeDateRule(ruleItem);
            case SEQUENCE:
                return executeSequenceRule(sequence, digitWidth, numberMode);
            case FIELD_REF:
                return executeFieldRefRule(ruleItem, contextData);
            default:
                throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                        "不支持的规则项类型: " + ruleItem.getItemType());
        }
    }

    /**
     * 生成周期键（用于重置周期判断）
     *
     * @param resetCycle 重置周期
     * @param now        当前时间
     * @return 周期键
     */
    public String generatePeriodKey(String resetCycle, LocalDateTime now) {
        ResetCycleEnum cycle = ResetCycleEnum.fromCode(resetCycle);
        
        switch (cycle) {
            case NEVER:
                return "NEVER";
            case NONE:
                return "NONE";
            case DAILY:
                return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            case MONTHLY:
                return now.format(DateTimeFormatter.ofPattern("yyyyMM"));
            case YEARLY:
                return now.format(DateTimeFormatter.ofPattern("yyyy"));
            default:
                throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                        "不支持的重置周期: " + resetCycle);
        }
    }

    /**
     * 执行固定文本规则
     *
     * @param ruleItem 规则项
     * @return 固定文本值
     */
    private String executeTextRule(MetadataAutoNumberRuleItemDO ruleItem) {
        if (StrUtil.isBlank(ruleItem.getTextValue())) {
            log.warn("Text rule item has empty text value: {}", ruleItem.getId());
            return "";
        }
        return ruleItem.getTextValue();
    }

    /**
     * 执行日期时间规则
     *
     * @param ruleItem 规则项
     * @return 格式化后的日期时间字符串
     */
    private String executeDateRule(MetadataAutoNumberRuleItemDO ruleItem) {
        LocalDateTime now = LocalDateTime.now();
        String format = ruleItem.getFormat();
        
        if (StrUtil.isBlank(format)) {
            // 默认格式
            format = "yyyyMMdd";
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return now.format(formatter);
        } catch (Exception e) {
            log.error("Failed to format date with pattern: {}", format, e);
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                    "日期格式化失败: " + format);
        }
    }

    /**
     * 格式化序号（公共方法）
     *
     * @param sequence   序号值
     * @param digitWidth 位数
     * @param numberMode 编号模式
     * @return 格式化后的序号字符串
     */
    public String formatSequence(Long sequence, Short digitWidth, String numberMode) {
        if (sequence == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "序号值不能为空");
        }
        
        NumberModeEnum mode = NumberModeEnum.fromCode(numberMode);
        
        switch (mode) {
            case NATURAL:
                // 自然数编号，直接返回数字
                return String.valueOf(sequence);
            case FIXED_DIGIT:
            case FIXED_DIGITS:
                // 指定位数编号，用零填充
                if (digitWidth == null || digitWidth <= 0) {
                    throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "指定位数编号必须设置有效位数");
                }
                String format = "%0" + digitWidth + "d";
                return String.format(format, sequence);
            default:
                throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), 
                        "不支持的编号模式: " + numberMode);
        }
    }

    /**
     * 执行序号规则
     *
     * @param sequence   序号值
     * @param digitWidth 位数
     * @param numberMode 编号模式
     * @return 格式化后的序号字符串
     */
    private String executeSequenceRule(Long sequence, Short digitWidth, String numberMode) {
        return formatSequence(sequence, digitWidth, numberMode);
    }

    /**
     * 执行字段引用规则
     *
     * @param ruleItem    规则项
     * @param contextData 上下文数据
     * @return 字段值的字符串表示
     */
    private String executeFieldRefRule(MetadataAutoNumberRuleItemDO ruleItem, Map<String, Object> contextData) {
        if (ruleItem.getRefFieldId() == null) {
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "字段引用规则必须指定引用字段ID");
        }
        
        if (contextData == null || contextData.isEmpty()) {
            log.warn("Context data is empty for field reference rule: {}", ruleItem.getId());
            return "";
        }
        
        // 从上下文数据中获取字段值
        // 这里需要根据实际的字段ID到字段名的映射逻辑来实现
        // 暂时使用字段ID作为key
        String fieldKey = "field_" + ruleItem.getRefFieldId();
        Object fieldValue = contextData.get(fieldKey);
        
        if (fieldValue == null) {
            log.warn("Field value not found in context data for field: {}", ruleItem.getRefFieldId());
            return "";
        }
        
        return String.valueOf(fieldValue);
    }
}
