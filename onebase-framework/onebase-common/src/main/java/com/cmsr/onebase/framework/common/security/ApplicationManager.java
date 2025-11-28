package com.cmsr.onebase.framework.common.security;

import java.util.function.Supplier;

/**
 * @Author：huangjie
 * @Date：2025/11/27 20:55
 */
public class ApplicationManager {

    private ApplicationManager() {
    }

    private static final ThreadLocal<Long> applicationIds = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> ignoreFlags = new ThreadLocal<>();

    public static void setApplicationId(Long applicationId) {
        applicationIds.set(applicationId);
    }

    public static Long getApplicationId() {
        if (isIgnoreApplicationCondition()) {
            return null;
        }
        return applicationIds != null ? applicationIds.get() : null;
    }

    /**
     * 忽略 tenant 条件
     */
    public static <T> T withoutApplicationCondition(Supplier<T> supplier) {
        try {
            ignoreApplicationCondition();
            return supplier.get();
        } finally {
            restoreApplicationCondition();
        }
    }

    /**
     * 忽略 tenant 条件
     */
    public static void withoutApplicationCondition(Runnable runnable) {
        try {
            ignoreApplicationCondition();
            runnable.run();
        } finally {
            restoreApplicationCondition();
        }
    }


    /**
     * 忽略 tenant 条件
     */
    public static void ignoreApplicationCondition() {
        ignoreFlags.set(Boolean.TRUE);
    }

    /**
     * 是否忽略 tenant 条件
     */
    public static boolean isIgnoreApplicationCondition() {
        return Boolean.TRUE.equals(ignoreFlags.get());
    }

    /**
     * 恢复 tenant 条件
     */
    public static void restoreApplicationCondition() {
        ignoreFlags.remove();
    }


}
