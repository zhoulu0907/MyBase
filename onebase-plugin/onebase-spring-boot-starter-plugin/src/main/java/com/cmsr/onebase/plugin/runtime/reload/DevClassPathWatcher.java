package com.cmsr.onebase.plugin.runtime.reload;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 开发模式下的类路径监听器
 * <p>
 * 监听 dev-class-paths 目录中的 .class 文件变化，触发热重载。
 * 支持防抖处理，合并短时间内的多次变更。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-23
 */
@Slf4j
public class DevClassPathWatcher {

    private final List<Path> watchPaths;
    private final HotReloadManager hotReloadManager;
    private WatchService watchService;
    private Thread watchThread;
    private volatile boolean running = false;

    /**
     * 防抖延迟时间（毫秒）
     * IDE 编译时可能会短时间内多次修改同一个文件，使用防抖避免重复重载
     */
    private static final long DEBOUNCE_DELAY_MS = 500;

    /**
     * 待处理的文件变更队列（用于防抖）
     */
    private final Map<Path, Long> pendingChanges = new ConcurrentHashMap<>();
    private final ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "DevClassPathWatcher-Debounce"));

    public DevClassPathWatcher(List<String> devClassPaths, HotReloadManager hotReloadManager) {
        this.watchPaths = new ArrayList<>();
        for (String pathStr : devClassPaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path) && Files.isDirectory(path)) {
                this.watchPaths.add(path);
            } else {
                log.warn("开发类路径不存在或不是目录，跳过监听: {}", pathStr);
            }
        }
        this.hotReloadManager = hotReloadManager;
    }

    /**
     * 启动文件监听
     */
    public void start() {
        if (running) {
            log.warn("DevClassPathWatcher 已经在运行中");
            return;
        }

        if (watchPaths.isEmpty()) {
            log.warn("没有可监听的开发类路径，热重载功能不可用");
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();

            // 注册所有监听路径
            for (Path path : watchPaths) {
                registerRecursive(path);
                log.info("已注册热重载监听路径: {}", path);
            }

            running = true;

            // 启动监听线程
            watchThread = new Thread(this::watchLoop, "DevClassPathWatcher");
            watchThread.setDaemon(true);
            watchThread.start();

            // 启动防抖处理线程
            debounceExecutor.scheduleWithFixedDelay(
                    this::processDebounced,
                    DEBOUNCE_DELAY_MS,
                    DEBOUNCE_DELAY_MS,
                    TimeUnit.MILLISECONDS);

            log.info("DevClassPathWatcher 已启动，监听 {} 个路径", watchPaths.size());
        } catch (IOException e) {
            log.error("启动 DevClassPathWatcher 失败", e);
        }
    }

    /**
     * 递归注册目录及其子目录
     */
    private void registerRecursive(Path path) throws IOException {
        // 注册当前目录
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

        // 递归注册子目录
        Files.walk(path)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try {
                        if (!dir.equals(path)) {
                            dir.register(watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_MODIFY,
                                    StandardWatchEventKinds.ENTRY_DELETE);
                        }
                    } catch (IOException e) {
                        log.error("注册子目录监听失败: {}", dir, e);
                    }
                });
    }

    /**
     * 监听循环
     */
    private void watchLoop() {
        log.debug("DevClassPathWatcher 监听线程已启动");

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

                    // 只处理 .class 文件
                    if (fileName.toString().endsWith(".class")) {
                        handleClassFileChange(fullPath, kind);
                    }

                    // 如果是新创建的目录，注册监听
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(fullPath)) {
                        try {
                            registerRecursive(fullPath);
                            log.debug("新目录已注册监听: {}", fullPath);
                        } catch (IOException e) {
                            log.error("注册新目录监听失败: {}", fullPath, e);
                        }
                    }
                }

                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("DevClassPathWatcher 监听线程被中断");
                break;
            } catch (ClosedWatchServiceException e) {
                // WatchService 已关闭，这是正常的关闭流程
                log.debug("WatchService 已关闭，监听线程退出");
                break;
            } catch (Exception e) {
                log.error("处理文件变更事件时出错", e);
            }
        }

        log.debug("DevClassPathWatcher 监听线程已退出");
    }

    /**
     * 处理 .class 文件变更
     */
    private void handleClassFileChange(Path classFile, WatchEvent.Kind<?> kind) {
        log.debug("检测到 .class 文件变化: {} ({})", classFile.getFileName(), kind.name());

        // 添加到防抖队列
        pendingChanges.put(classFile, System.currentTimeMillis());
    }

    /**
     * 处理防抖后的文件变更
     */
    private void processDebounced() {
        long now = System.currentTimeMillis();
        List<Path> toProcess = new ArrayList<>();

        // 找出已经超过防抖延迟的文件
        pendingChanges.entrySet().removeIf(entry -> {
            if (now - entry.getValue() >= DEBOUNCE_DELAY_MS) {
                toProcess.add(entry.getKey());
                return true;
            }
            return false;
        });

        // 处理这些文件
        for (Path classFile : toProcess) {
            try {
                String className = extractClassName(classFile);
                if (className != null) {
                    log.info("触发热重载: {}", className);
                    hotReloadManager.reloadExtension(className, classFile);
                }
            } catch (Exception e) {
                log.error("热重载失败: {}", classFile, e);
            }
        }
    }

    /**
     * 从 .class 文件路径提取完整类名
     */
    private String extractClassName(Path classFile) {
        // 找到包含此文件的监听根路径
        for (Path watchPath : watchPaths) {
            if (classFile.startsWith(watchPath)) {
                Path relativePath = watchPath.relativize(classFile);
                String className = relativePath.toString()
                        .replace(classFile.getFileSystem().getSeparator(), ".")
                        .replace(".class", "");
                return className;
            }
        }
        return null;
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

        log.info("DevClassPathWatcher 已停止");
    }
}
