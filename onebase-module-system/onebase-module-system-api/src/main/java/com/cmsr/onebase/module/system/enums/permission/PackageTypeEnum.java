package com.cmsr.onebase.module.system.enums.permission;

/**
 * 套餐类型枚举
 *
 * @author GitHub Copilot
 * @date 2025-08-11
 */
public enum PackageTypeEnum {
    /**
     * 普通套餐
     */
    NORMAL(111, "普通套餐"),
    /**
     * 租户简餐
     */
    SIMPLE(112, "租户简餐");

    /**
     * 套餐ID
     */
    private final Integer packageId;
    /**
     * 套餐名称
     */
    private final String name;

    PackageTypeEnum(Integer packageId, String name) {
        this.packageId = packageId;
        this.name = name;
    }

    /**
     * 获取套餐ID
     *
     * @return 套餐ID
     */
    public Integer getPackageId() {
        return packageId;
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

