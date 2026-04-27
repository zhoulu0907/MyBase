package com.cmsr.onebase.module.system.enums.permission;

/**
 * 套餐类型枚举
 *
 * @author GitHub Copilot
 * @date 2025-08-11
 */
public enum PackageTypeEnum {

    /**
     * 全功能套餐
     */
    ALL("super_package", "全功能套餐");

    /**
     * 套餐编码
     */
    private final String code;
    /**
     * 套餐名称
     */
    private final String name;

    PackageTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取套餐ID
     *
     * @return 套餐ID
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取套餐名称
     *
     * @return 套餐名称
     */
    public String getName() {
        return name;
    }
}

