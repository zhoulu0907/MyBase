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

    private static final ThreadLocal<Boolean> applicationIdHolder = new ThreadLocal<>();

    private static final ThreadLocal<Long> versionTags = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> versionTagHolder = new ThreadLocal<>();

    public static void setApplicationId(Long applicationId) {
        applicationIds.set(applicationId);
    }

    public static Long getApplicationId() {
        if (isIgnoreApplicationCondition()) {
            return null;
        }
        return applicationIds.get();
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

    public static void ignoreApplicationCondition() {
        applicationIdHolder.set(Boolean.TRUE);
    }

    public static boolean isIgnoreApplicationCondition() {
        return Boolean.TRUE.equals(applicationIdHolder.get());
    }

    public static void restoreApplicationCondition() {
        applicationIdHolder.remove();
    }

    public static void setVersionTag(Long versionTag) {
        versionTags.set(versionTag);
    }

    public static Long getVersionTag() {
        if (isIgnoreVersionTagCondition()) {
            return null;
        }
        return versionTags.get();
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

    public static void ignoreVersionTagCondition() {
        versionTagHolder.set(Boolean.TRUE);
    }

    public static boolean isIgnoreVersionTagCondition() {
        return Boolean.TRUE.equals(versionTagHolder.get());
    }

    public static void restoreVersionTagCondition() {
        versionTagHolder.remove();
    }
}
