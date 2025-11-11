package com.cmsr.onebase.framework.common.enums;

/**
 * @Author：gaoguoqing
 * @Date：2025/11/0 15:39
 */
public enum OwnerTagEnum {

    /**
     * 自己
     */
    MY(1, "自己"),

    /**
     * 全部
     */
    ALL(0, "全部"),
    ;

    private final Integer value;

    private final String name;


    OwnerTagEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
