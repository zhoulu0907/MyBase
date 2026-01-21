package com.cmsr.onebase.plugin.sandbox.executor;

import com.cmsr.onebase.plugin.sandbox.manager.PluginSecurityManager;
import com.cmsr.onebase.plugin.sandbox.model.PluginPermissions;
import com.cmsr.onebase.plugin.sandbox.model.SandboxResult;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 插件沙箱执行器
 * <p>
 * 在受控的沙箱环境中执行插件代码，提供：
 * 1. 权限控制（通过 SecurityManager）
 * 2. 超时控制（通过 ExecutorService）
 * 3. 线程数限制（通过线程池）
 * </p>
 *
 * @author OneBase Plugin Team
 * @since 1.0.0
 */
@Slf4j
public class PluginSandboxExecutor {

    /**
     * 安全管理器
     */
    private final PluginSecurityManager securityManager;

    /**
     * 线程池（用于执行插件代码）
     */
    private final ExecutorService executorService;

    /**
     * 插件线程计数器（用于限制线程数）
     */
    private final ConcurrentHashMap<String, AtomicInteger> threadCounters = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param securityManager 安全管理器
     * @param maxThreads     最大线程数
     */
    public PluginSandboxExecutor(PluginSecurityManager securityManager, int maxThreads) {
        this.securityManager = securityManager;
        this.executorService = Executors.newFixedThreadPool(maxThreads,
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("plugin-sandbox-" + thread.getId());
                    thread.setDaemon(true);
                    return thread;
                });
    }

    /**
     * 在沙箱中执行插件代码
     *
     * @param pluginId 插件ID
     * @param callable 要执行的代码
     * @param <T>      结果类型
     * @return 执行结果
     */
    public <T> SandboxResult<T> execute(String pluginId, Callable<T> callable) {
        return execute(pluginId, callable, PluginPermissions.defaultPermissions());
    }

    /**
     * 在沙箱中执行插件代码（使用指定权限）
     *
     * @param pluginId    插件ID
     * @param callable    要执行的代码
     * @param permissions 权限配置
     * @param <T>         结果类型
     * @return 执行结果
     */
    public <T> SandboxResult<T> execute(String pluginId,
                                        Callable<T> callable,
                                        PluginPermissions permissions) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 配置权限
            securityManager.configurePlugin(pluginId, permissions);

            // 2. 检查线程数限制
            AtomicInteger threadCounter = threadCounters.computeIfAbsent(
                    pluginId, k -> new AtomicInteger(0)
            );
            int currentThreads = threadCounter.incrementAndGet();
            if (currentThreads > permissions.getMaxThreads()) {
                threadCounter.decrementAndGet();
                return SandboxResult.error(
                        String.format("插件 %s 超过最大线程数限制: %d",
                                pluginId, permissions.getMaxThreads())
                );
            }

            // 3. 执行代码（带超时控制），在执行线程中进入和退出沙箱
            Future<T> future = executorService.submit(() -> {
                securityManager.enterSandbox(pluginId);
                try {
                    return callable.call();
                } finally {
                    threadCounter.decrementAndGet();
                    securityManager.exitSandbox();
                }
            });

            try {
                T result = future.get(permissions.getMaxExecutionTime(), TimeUnit.MILLISECONDS);
                long duration = System.currentTimeMillis() - startTime;

                log.info("插件 {} 执行成功，耗时: {}ms", pluginId, duration);
                return SandboxResult.success(result, duration);

            } catch (TimeoutException e) {
                log.warn("插件 {} 执行超时（{}ms）", pluginId, permissions.getMaxExecutionTime());
                future.cancel(true);
                return SandboxResult.timeout("执行超时");

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                // 检查是否为安全异常（包括被包装的情况）
                SecurityException securityException = extractSecurityException(cause);
                if (securityException != null) {
                    log.warn("插件 {} 安全违规: {}", pluginId, securityException.getMessage());
                    return SandboxResult.securityViolation(securityException.getMessage());
                } else {
                    log.error("插件 {} 执行异常", pluginId, cause);
                    return SandboxResult.error(cause != null ? cause.getMessage() : "Unknown error");
                }
            } catch (InterruptedException e) {
                log.warn("插件 {} 执行被中断", pluginId);
                Thread.currentThread().interrupt();
                return SandboxResult.error("执行被中断");
            }

        } catch (Exception e) {
            log.error("插件 {} 执行失败", pluginId, e);
            String message = e.getMessage();
            if (message == null && e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            return SandboxResult.error(message);
        }
    }

    /**
     * 在沙箱中执行插件代码（使用 Runnable）
     *
     * @param pluginId 插件ID
     * @param runnable 要执行的代码
     * @return 执行结果
     */
    public SandboxResult<Void> execute(String pluginId, Runnable runnable) {
        return execute(pluginId, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 在沙箱中执行插件代码（使用 Runnable 和指定权限）
     *
     * @param pluginId    插件ID
     * @param runnable    要执行的代码
     * @param permissions 权限配置
     * @return 执行结果
     */
    public SandboxResult<Void> execute(String pluginId,
                                        Runnable runnable,
                                        PluginPermissions permissions) {
        return execute(pluginId, () -> {
            runnable.run();
            return null;
        }, permissions);
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        log.info("关闭插件沙箱执行器");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 从异常中提取 SecurityException（包括被包装的情况）
     *
     * @param throwable 异常
     * @return SecurityException 如果存在，否则返回 null
     */
    private SecurityException extractSecurityException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        // 如果本身就是 SecurityException
        if (throwable instanceof SecurityException) {
            return (SecurityException) throwable;
        }

        // ExceptionInInitializerError 可能包装了 SecurityException
        if (throwable instanceof ExceptionInInitializerError) {
            Throwable cause = throwable.getCause();
            if (cause instanceof SecurityException) {
                return (SecurityException) cause;
            }
        }

        // 递归检查 cause
        return extractSecurityException(throwable.getCause());
    }

    /**
     * 获取当前线程计数
     *
     * @param pluginId 插件ID
     * @return 当前线程数
     */
    public int getThreadCount(String pluginId) {
        AtomicInteger counter = threadCounters.get(pluginId);
        return counter != null ? counter.get() : 0;
    }
}
