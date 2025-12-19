package com.cmsr.onebase.module.metadata.core.semantic.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * BPM系统字段枚举
 *
 * @author liyang
 * @date 2025-12-12
 */
@Getter
@AllArgsConstructor
public enum BpmSystemFieldEnum {
    /**
     * 流程标题
     */
    BPM_TITLE(100L, "bpm_title", "流程标题", "TEXT"),

    /**
     * 发起人
     */
    BPM_INITIATOR_ID(101L, "bpm_initiator_id", "发起人ID", "USER"),

    /**
     * 发起时间
     */
    BPM_SUBMIT_TIME(102L, "bpm_submit_time", "发起时间", "DATETIME"),

    /**
     * 流程状态
     */
    BPM_STATUS(103L, "bpm_status", "流程状态", "SELECT"),

    /**
     * 当前节点
     */
    BPM_CURRENT_NODE(104L, "bpm_current_node", "当前节点", "SELECT"),

    /**
     * 当前流程实例ID
     */
    BPM_INSTANCE_ID(105L, "bpm_instance_id", "流程实例ID", "ID"),
    ;

    /**
     * 字段ID
     */
    private final Long id;

    /**
     * 字段名
     */
    private final String fieldName;

    /**
     * 显示名称
     */
    private final String displayName;

    /**
     * 字段类型编码
     */
    private final String fieldTypeCode;

    /**
     * 根据字段名获取枚举
     *
     * @param fieldName 字段名
     * @return 枚举
     */
    public static BpmSystemFieldEnum getByFieldName(String fieldName) {
        return Arrays.stream(values())
                .filter(e -> e.getFieldName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }
}
