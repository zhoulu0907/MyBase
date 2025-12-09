package com.cmsr.onebase.plugin.context;

/**
 * 插件上下文持有器
 * <p>
 * 基于 ThreadLocal 实现的上下文持有器，用于在插件执行过程中传递上下文信息。
 * 宿主应用会在调用插件前设置上下文，插件执行完成后清理上下文。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public final class PluginContextHolder {

    private static final ThreadLocal<PluginContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private PluginContextHolder() {
        // 工具类，禁止实例化
    }

    /**
     * 获取当前上下文
     *
     * @return 当前线程的插件上下文，如果未设置则返回null
     */
    public static PluginContext get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 获取当前上下文（非空）
     *
     * @return 当前线程的插件上下文
     * @throws IllegalStateException 如果上下文未设置
     */
    public static PluginContext require() {
        PluginContext ctx = CONTEXT_HOLDER.get();
        if (ctx == null) {
            throw new IllegalStateException("PluginContext not initialized. " +
                    "This method must be called within a plugin execution context.");
        }
        return ctx;
    }

    /**
     * 设置当前上下文
     * <p>
     * 通常由宿主应用调用，插件开发者不需要直接调用此方法。
     * </p>
     *
     * @param context 插件上下文
     */
    public static void set(PluginContext context) {
        if (context == null) {
            CONTEXT_HOLDER.remove();
        } else {
            CONTEXT_HOLDER.set(context);
        }
    }

    /**
     * 清除当前上下文
     * <p>
     * 通常由宿主应用调用，插件开发者不需要直接调用此方法。
     * </p>
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 检查上下文是否已设置
     *
     * @return true表示已设置
     */
    public static boolean isPresent() {
        return CONTEXT_HOLDER.get() != null;
    }
}
