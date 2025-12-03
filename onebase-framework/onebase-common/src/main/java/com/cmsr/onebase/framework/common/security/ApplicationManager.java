package com.cmsr.onebase.framework.common.security;

import java.util.function.Supplier;

/**
 * @Author：huangjie
 * @Date：2025/11/27 20:55
 */
public class ApplicationManager {

    private ApplicationManager() {
    }

    private static final ThreadLocal<Long> applicationIdHolder = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> applicationIdBooleanHolder = new ThreadLocal<>();

    private static final ThreadLocal<Long> versionTagHolder = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> versionTagBooleanHolder = new ThreadLocal<>();

    public static void setApplicationId(Long applicationId) {
        applicationIdHolder.set(applicationId);
    }

    public static Long getApplicationId() {
        if (isIgnoreApplicationCondition()) {
            return null;
        }
        return applicationIdHolder.get();
    }

    public static <T> T withoutApplicationCondition(Supplier<T> supplier) {
        try {
            ignoreApplicationCondition();
            return supplier.get();
        } finally {
            restoreApplicationCondition();
        }
    }

    public static void withoutApplicationCondition(Runnable runnable) {
        try {
            ignoreApplicationCondition();
            runnable.run();
        } finally {
            restoreApplicationCondition();
        }
    }

    private static void ignoreApplicationCondition() {
        applicationIdBooleanHolder.set(Boolean.TRUE);
    }

    private static boolean isIgnoreApplicationCondition() {
        return Boolean.TRUE.equals(applicationIdBooleanHolder.get());
    }

    private static void restoreApplicationCondition() {
        applicationIdBooleanHolder.remove();
    }

    public static void setVersionTag(Long versionTag) {
        versionTagHolder.set(versionTag);
    }

    public static Long getVersionTag() {
        if (isIgnoreVersionTagCondition()) {
            return null;
        }
        return versionTagHolder.get();
    }

    public static <T> T withoutVersionTagCondition(Supplier<T> supplier) {
        try {
            ignoreApplicationCondition();
            return supplier.get();
        } finally {
            restoreApplicationCondition();
        }
    }

    public static void withoutVersionTagCondition(Runnable runnable) {
        try {
            ignoreApplicationCondition();
            runnable.run();
        } finally {
            restoreApplicationCondition();
        }
    }

    private static void ignoreVersionTagCondition() {
        versionTagBooleanHolder.set(Boolean.TRUE);
    }

    private static boolean isIgnoreVersionTagCondition() {
        return Boolean.TRUE.equals(versionTagBooleanHolder.get());
    }

    private static void restoreVersionTagCondition() {
        versionTagBooleanHolder.remove();
    }
}
