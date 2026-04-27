package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;

/**
 * 字段值存储策略接口
 *
 * @author matianyu
 * @date 2025-11-15
 */
public interface FieldValueStorageStrategy {

    /**
     * 判断当前策略是否支持指定字段类型
     *
     * @param fieldType 字段类型
     * @return true 表示支持
     */
    boolean supports(String fieldType);

    /**
     * 按照转换模式处理字段值
     *
     * @param rawValue 原始值
     * @param mode     转换模式（写入/读取）
     * @return 转换后的值
     */
    Object transform(Object rawValue, FieldValueTransformMode mode);

    /**
     * 按照转换模式处理字段值（带完整上下文）
     * 当策略需要访问 ProcessContext 时（如需要创建关联表记录），使用此方法
     *
     * @param rawValue 原始值
     * @param mode     转换模式（写入/读取）
     * @param context  处理上下文
     * @param field    字段定义
     * @return 转换后的值
     */
    default Object transform(Object rawValue, FieldValueTransformMode mode, ProcessContext context, MetadataEntityFieldDO field) {
        // 默认实现调用无上下文的方法，保持向后兼容
        return transform(rawValue, mode);
    }

    /**
     * 策略匹配顺序，值越小优先级越高
     *
     * @return 优先级
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}

