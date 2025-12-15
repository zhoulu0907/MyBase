package com.cmsr.onebase.plugin.runtime.scanner;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 插件目录扫描器
 * <p>
 * 扫描插件目录，实现插件的一次性扫描用于加载/重载/卸载操作。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-15
 */
public class PluginDirectoryScanner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PluginDirectoryScanner.class);

    /**
     * 插件管理器
     */
    private final PluginManager pluginManager;

    /**
     * OneBase插件管理器（用于热加载）
     */
    private final OneBasePluginManager oneBasePluginManager;

    /**
     * 插件目录
     */
    private final Path pluginDirectory;

    /**
     * 已知插件文件及其最后修改时间
     */
    private final Map<Path, Long> knownPluginFiles = new ConcurrentHashMap<>();

    /**
     * 插件目录扫描器现在为一次性扫描
     */

    public PluginDirectoryScanner(PluginManager pluginManager,
                                   OneBasePluginManager oneBasePluginManager,
                                   Path pluginDirectory) {
        this.pluginManager = pluginManager;
        this.oneBasePluginManager = oneBasePluginManager;
        this.pluginDirectory = pluginDirectory;

        log.info("=".repeat(60));
        log.info("初始化插件目录扫描器（一次性扫描）");
        log.info("扫描目录: {}", pluginDirectory);
        log.info("=".repeat(60));

        // 初始化已知插件文件（使用规范化路径，避免不同Path实例导致的不匹配）
        initializeKnownPlugins();
    }

    /**
     * 初始化已知插件文件
     */
    private void initializeKnownPlugins() {
        if (!Files.exists(pluginDirectory)) {
            log.warn("插件目录不存在: {}", pluginDirectory);
            return;
        }

        File dir = pluginDirectory.toFile();
        File[] files = dir.listFiles((f) -> f.isFile() && isPluginFile(f.toPath()));
        if (files == null) {
            log.warn("无法列出插件目录文件: {}", pluginDirectory);
        } else {
            for (File f : files) {
                try {
                    Path real = f.toPath().toAbsolutePath().normalize();
                    long lastModified = Files.getLastModifiedTime(real).toMillis();
                    knownPluginFiles.put(real, lastModified);
                    log.debug("已记录插件文件: {} (最后修改时间: {})", real.getFileName(), lastModified);
                } catch (IOException e) {
                    log.warn("读取插件文件信息失败: {}", f, e);
                }
            }
        }

        log.info("已初始化 {} 个已知插件文件", knownPluginFiles.size());
    }

    /**
     * 判断是否为插件文件
     */
    private boolean isPluginFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".zip") || fileName.endsWith(".jar");
    }

    @Override
    public void run() {
        log.info("插件目录扫描器启动（一次性执行扫描）");
        try {
            scanPluginDirectory();
        } catch (Exception e) {
            log.error("扫描插件目录时发生异常", e);
        }
        log.info("插件目录扫描完成");
    }

    /**
     * 扫描插件目录
     */
    private void scanPluginDirectory() {
        if (!Files.exists(pluginDirectory)) {
            log.warn("插件目录不存在，跳过本次扫描: {}", pluginDirectory);
            return;
        }

        // （已移除）调试计数块，避免在某些 JDK/编译配置下产生受检异常处理问题。

        Set<Path> currentPluginFiles = new HashSet<>();

        File dir = pluginDirectory.toFile();
        File[] files = dir.listFiles((f) -> f.isFile() && isPluginFile(f.toPath()));
        if (files == null) {
            log.error("扫描插件目录失败（无法列出目录）: {}", pluginDirectory);
            return;
        }

        for (File f : files) {
            try {
                Path real = f.toPath().toAbsolutePath().normalize();
                currentPluginFiles.add(real);
                handlePluginFile(real);
            } catch (Exception e) {
                log.warn("读取插件文件信息失败: {}", f, e);
            }
        }

        // 检测已删除的插件
        Set<Path> deletedPlugins = knownPluginFiles.keySet().stream()
                .filter(path -> !currentPluginFiles.contains(path))
                .collect(Collectors.toSet());

        for (Path deletedPlugin : deletedPlugins) {
            handleDeletedPlugin(deletedPlugin);
        }
    }

    /**
     * 处理插件文件
     */
    private void handlePluginFile(Path pluginFile) {
        try {
            long currentModified = Files.getLastModifiedTime(pluginFile).toMillis();
            Long previousModified = knownPluginFiles.get(pluginFile);

            if (previousModified == null) {
                // 新增插件
                log.info("检测到新插件: {}", pluginFile.getFileName());
                loadNewPlugin(pluginFile);
                knownPluginFiles.put(pluginFile, currentModified);
            } else if (currentModified > previousModified) {
                // 插件已更新
                log.info("检测到插件更新: {}", pluginFile.getFileName());
                reloadPlugin(pluginFile);
                knownPluginFiles.put(pluginFile, currentModified);
            }
        } catch (IOException e) {
            log.error("处理插件文件失败: {}", pluginFile, e);
        }
    }

    /**
     * 加载新插件
     */
    private void loadNewPlugin(Path pluginFile) {
        try {
            String pluginId = oneBasePluginManager.loadPlugin(pluginFile);
            if (pluginId != null) {
                PluginState state = oneBasePluginManager.startPlugin(pluginId);
                log.info("新插件加载并启动成功: {} (状态: {})", pluginId, state);
            } else {
                log.error("新插件加载失败: {}", pluginFile.getFileName());
            }
        } catch (Exception e) {
            log.error("加载新插件失败: {}", pluginFile.getFileName(), e);
        }
    }

    /**
     * 重新加载插件
     */
    private void reloadPlugin(Path pluginFile) {
        try {
            // 查找并卸载旧版本
            String pluginId = findPluginIdByPath(pluginFile);
            if (pluginId != null) {
                log.info("卸载旧版本插件: {}", pluginId);
                oneBasePluginManager.stopPlugin(pluginId);
                oneBasePluginManager.unloadPlugin(pluginId);
            }

            // 加载新版本
            String newPluginId = oneBasePluginManager.loadPlugin(pluginFile);
            if (newPluginId != null) {
                PluginState state = oneBasePluginManager.startPlugin(newPluginId);
                log.info("插件重新加载并启动成功: {} (状态: {})", newPluginId, state);
            } else {
                log.error("插件重新加载失败: {}", pluginFile.getFileName());
            }
        } catch (Exception e) {
            log.error("重新加载插件失败: {}", pluginFile.getFileName(), e);
        }
    }

    /**
     * 处理已删除的插件
     */
    private void handleDeletedPlugin(Path deletedPlugin) {
        log.info("检测到插件已删除: {}", deletedPlugin.getFileName());
        
        try {
            String pluginId = findPluginIdByPath(deletedPlugin);
            if (pluginId != null) {
                log.info("卸载已删除的插件: {}", pluginId);
                oneBasePluginManager.stopPlugin(pluginId);
                oneBasePluginManager.unloadPlugin(pluginId);
            }
            knownPluginFiles.remove(deletedPlugin);
        } catch (Exception e) {
            log.error("处理已删除插件失败: {}", deletedPlugin.getFileName(), e);
        }
    }

    /**
     * 根据插件路径查找插件ID
     */
    private String findPluginIdByPath(Path pluginPath) {
        String target = pluginPath.toAbsolutePath().normalize().toString();
        return pluginManager.getPlugins().stream()
                .filter(wrapper -> {
                    Path wrapperPath = wrapper.getPluginPath();
                    if (wrapperPath == null) {
                        return false;
                    }
                    String w = wrapperPath.toAbsolutePath().normalize().toString();
                    return target.equals(w);
                })
                .map(wrapper -> wrapper.getPluginId())
                .findFirst()
                .orElse(null);
    }

    /**
     * 停止扫描器
     */
    public void stop() {
        // 不再支持周期扫描，stop 为空实现
        log.debug("PluginDirectoryScanner.stop() 被调用（已忽略，当前为一次性扫描）");
    }

    /**
     * 是否正在运行
     */
    public boolean isRunning() {
        // 对于一次性扫描，总是返回 false
        return false;
    }
}
