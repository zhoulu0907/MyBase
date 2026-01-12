package com.cmsr.onebase.plugin.simulator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 配置文件监听器
 * <p>
 * 监听 plugin-context.yml 文件的变化，触发配置重载。
 * 支持防抖处理，合并短时间内的多次变更。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-01-08
 */
public class ContextFileWatcher {

    private static final Logger log = LoggerFactory.getLogger(ContextFileWatcher.class);

    private final Path configFilePath;
    private final Runnable reloadCallback;
    private WatchService watchService;
    private Thread watchThread;
    private volatile boolean running = false;

    /**
     * 防抖延迟时间（毫秒）
     * 编辑器保存时可能会短时间内多次修改文件，使用防抖避免重复重载
     */
    private static final long DEBOUNCE_DELAY_MS = 500;

    /**
     * 待处理的文件变更队列（用于防抖）
     */
    private final Map<Path, Long> pendingChanges = new ConcurrentHashMap<>();
    private final ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "ContextFileWatcher-Debounce"));

    /**
     * 构造函数
     *
     * @param configFilePath 配置文件路径
     * @param reloadCallback 重载回调函数
     */
    public ContextFileWatcher(Path configFilePath, Runnable reloadCallback) {
        this.configFilePath = configFilePath;
        this.reloadCallback = reloadCallback;
    }

    /**
     * 启动文件监听
     */
    public void start() {
        if (running) {
            log.warn("ContextFileWatcher 已经在运行中");
            return;
        }

        if (configFilePath == null || !Files.exists(configFilePath)) {
            log.warn("配置文件不存在，无法启动监听: {}", configFilePath);
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();

            // 监听配置文件所在目录
            Path watchDir = configFilePath.getParent();
            watchDir.register(watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE);

            running = true;

            // 启动监听线程
            watchThread = new Thread(this::watchLoop, "ContextFileWatcher");
            watchThread.setDaemon(true);
            watchThread.start();

            // 启动防抖处理线程
            debounceExecutor.scheduleWithFixedDelay(
                    this::processDebounced,
                    DEBOUNCE_DELAY_MS,
                    DEBOUNCE_DELAY_MS,
                    TimeUnit.MILLISECONDS);

            log.info("ContextFileWatcher 已启动，监听文件: {}", configFilePath);
        } catch (IOException e) {
            log.error("启动 ContextFileWatcher 失败", e);
        }
    }

    /**
     * 监听循环
     */
    private void watchLoop() {
        log.debug("ContextFileWatcher 监听线程已启动");

        while (running) {
            try {
                WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                if (key == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        log.warn("文件监听事件溢出，可能遗漏部分变更");
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    Path dir = (Path) key.watchable();
                    Path fullPath = dir.resolve(fileName);

                    // 只处理目标配置文件
                    if (fullPath.equals(configFilePath)) {
                        handleConfigFileChange(fullPath, kind);
                    }
                }

                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("ContextFileWatcher 监听线程被中断", e);
                break;
            } catch (ClosedWatchServiceException e) {
                // WatchService 已关闭，这是正常的关闭流程
                log.debug("WatchService 已关闭，监听线程退出");
                break;
            } catch (Exception e) {
                log.error("处理文件变更事件时出错", e);
            }
        }

        log.debug("ContextFileWatcher 监听线程已退出");
    }

    /**
     * 处理配置文件变更
     */
    private void handleConfigFileChange(Path configFile, WatchEvent.Kind<?> kind) {
        log.debug("检测到配置文件变化: {} ({})", configFile.getFileName(), kind.name());

        // 添加到防抖队列
        pendingChanges.put(configFile, System.currentTimeMillis());
    }

    /**
     * 处理防抖后的文件变更
     */
    private void processDebounced() {
        long now = System.currentTimeMillis();

        // 检查是否有需要处理的变更
        boolean hasChanges = pendingChanges.entrySet().removeIf(entry -> now - entry.getValue() >= DEBOUNCE_DELAY_MS);

        // 触发重载
        if (hasChanges) {
            try {
                log.info("触发配置重载: {}", configFilePath.getFileName());
                reloadCallback.run();
            } catch (Exception e) {
                log.error("配置重载失败: {}", configFilePath, e);
            }
        }
    }

    /**
     * 停止文件监听
     */
    public void stop() {
        if (!running) {
            return;
        }

        running = false;

        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            log.error("关闭 WatchService 失败", e);
        }

        if (watchThread != null) {
            watchThread.interrupt();
        }

        debounceExecutor.shutdown();

        log.info("ContextFileWatcher 已停止");
    }
}
