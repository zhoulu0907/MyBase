package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 系统字段枚举
 *
 * @author GitHub Copilot
 * @date 2025-12-08
 */
@Getter
@AllArgsConstructor
public enum SystemFieldEnum {

    ID(1L, "id", "主键ID", "ID"),
    OWNER_ID(2L, "owner_id", "拥有者ID", "USER"),
    OWNER_DEPT(3L, "owner_dept", "拥有部门ID", "DEPARTMENT"),
    CREATOR(4L, "creator", "创建人ID", "USER"),
    UPDATER(5L, "updater", "更新人ID", "USER"),
    CREATED_TIME(6L, "created_time", "创建时间", "DATETIME"),
    UPDATED_TIME(7L, "updated_time", "更新时间", "DATETIME"),
    LOCK_VERSION(8L, "lock_version", "乐观锁", "NUMBER"),
    DELETED(9L, "deleted", "删除标识", "NUMBER"),
    PARENT_ID(10L, "parent_id", "关联主表ID", "ID"),
    DRAFT_STATUS(11L, "draft_status", "草稿状态", "NUMBER");

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
    public static SystemFieldEnum getByFieldName(String fieldName) {
        return Arrays.stream(values())
                .filter(e -> e.getFieldName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }
}
