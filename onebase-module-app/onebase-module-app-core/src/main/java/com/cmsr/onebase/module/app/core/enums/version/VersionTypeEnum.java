package com.cmsr.onebase.module.app.core.enums.version;


/**
 * @Author：huangjie
 * @Date：2025/8/12 11:20
 */
public enum VersionTypeEnum {
    BUILD(0, "编辑版本"),
    RUNTIME(1, "运行版本"),
    HISTORY(2, "历史版本"),
    ;

    private final int value;
    private final String label;

    VersionTypeEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer versionType) {
        for (VersionTypeEnum value : VersionTypeEnum.values()) {
            if (value.value == versionType) {
                return value.label;
            }
        }
        return "未知";
    }

    public int getValue() {
        return value;
    }
}
