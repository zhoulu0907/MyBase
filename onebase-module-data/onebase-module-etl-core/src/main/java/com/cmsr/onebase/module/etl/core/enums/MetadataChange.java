package com.cmsr.onebase.module.etl.core.enums;

import lombok.Getter;

import java.util.Collection;

public enum MetadataChange {
    NONE(0, "无变化"),
    ADD(1, "新增"),
    NO_AFFECT(2, "无影响"),
    DATA_AFFECT(3, "影响数据IO"),
    FUNCTIONAL_AFFECT(4, "影响功能"),
    DEL(5, "删除");

    @Getter
    private Integer affectLevel;
    @Getter
    private String description;

    MetadataChange(Integer affectLevel, String description) {
        this.affectLevel = affectLevel;
        this.description = description;
    }

    public static MetadataChange calculateTableAffect(Collection<MetadataChange> columnChanges) {
        MetadataChange mostAffectedColumn = NONE;
        for (MetadataChange columnChange : columnChanges) {
            mostAffectedColumn = mostAffected(mostAffectedColumn, columnChange);
        }
        return switch (mostAffectedColumn) {
            // 新增字段不影响既有流程、表单
            case ADD, NO_AFFECT -> NO_AFFECT;
            // 数据IO层面影响
            case DATA_AFFECT -> DATA_AFFECT;
            // 功能层面影响
            case FUNCTIONAL_AFFECT, DEL -> FUNCTIONAL_AFFECT;
            // 无影响
            default -> NONE;
        };
    }

    public static MetadataChange mostAffected(MetadataChange c1, MetadataChange c2) {
        if (c1 == null && c2 == null) return NONE;
        if (c1 == null) return c2;
        if (c2 == null) return c1;

        return c1.affectLevel > c2.affectLevel ? c1 : c2;
    }
}
