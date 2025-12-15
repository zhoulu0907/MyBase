package com.cmsr.onebase.plugin.core;

/**
 * 插件运行模式枚举
 * <p>
 * 定义插件系统的三种运行模式，控制扩展点的加载策略。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-15
 */
public enum PluginMode {

    /**
     * 开发模式
     * <p>
     * 只加载classpath下的扩展点，不加载plugin目录的ZIP包。
     * 适用场景：IDE中直接启动和调试，支持断点调试，提升开发效率。
     * </p>
     */
    DEV("dev"),

    /**
     * 预发布模式（全生命周期验证模式）
     * <p>
     * 只加载plugin目录ZIP包中的扩展点，不加载classpath。
     * 适用场景：验证插件完整生命周期（加载、启动、热插拔、卸载），
     * 测试ClassLoader隔离，模拟真实生产环境。
     * </p>
     */
    STAGING("staging"),

    /**
     * 生产模式
     * <p>
     * 使用PF4J默认策略，同时扫描classpath和plugin目录。
     * 适用场景：生产环境，提供最大灵活性。
     * </p>
     */
    PROD("prod");

    private final String value;

    PluginMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举
     *
     * @param value 模式值（不区分大小写）
     * @return 对应的枚举
     * @throws IllegalArgumentException 如果值不在枚举范围内
     */
    public static PluginMode fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PROD; // 默认值
        }

        for (PluginMode mode : values()) {
            if (mode.value.equalsIgnoreCase(value.trim())) {
                return mode;
            }
        }

        throw new IllegalArgumentException(
                String.format("无效的插件运行模式: '%s'。有效值: dev, staging, prod", value)
        );
    }

    /**
     * 是否为开发模式
     *
     * @return true表示开发模式
     */
    public boolean isDev() {
        return this == DEV;
    }

    /**
     * 是否为预发布模式
     *
     * @return true表示预发布模式
     */
    public boolean isStaging() {
        return this == STAGING;
    }

    /**
     * 是否为生产模式
     *
     * @return true表示生产模式
     */
    public boolean isProd() {
        return this == PROD;
    }

    @Override
    public String toString() {
        return value;
    }
}
