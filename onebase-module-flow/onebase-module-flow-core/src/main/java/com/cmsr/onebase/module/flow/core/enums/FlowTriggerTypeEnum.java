package com.cmsr.onebase.module.flow.core.enums;

/**
 * 流程触发类型枚举
 */
public enum FlowTriggerTypeEnum {

    TIME("time", "时间触发"),
    FORM("form", "表单触发"),
    DATE_FIELD("date_field", "日期字段触发"),
    ENTITY("entity", "实体触发"),
    API("api", "API触发"),
    BPM("bpm", "BPM触发");

    private final String type;
    private final String name;

    FlowTriggerTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }




    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String getName(String type) {
        for (FlowTriggerTypeEnum value : FlowTriggerTypeEnum.values()) {
            if (value.type.equals(type)) {
                return value.name;
            }
        }
        return null;
    }

    public static FlowTriggerTypeEnum getByType(String type) {
        for (FlowTriggerTypeEnum value : FlowTriggerTypeEnum.values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isTime(String type) {
        return TIME.type.equals(type);
    }

    public static boolean isDateField(String triggerType) {
        return DATE_FIELD.type.equals(triggerType);
    }
}
