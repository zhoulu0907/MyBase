package com.cmsr.onebase.plugin.runtime.loader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 插件文件管理器
 * 负责插件文件的下载、解压、加载和卸载
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Component
@Slf4j
public class PluginFileManager {

    /**
     * 插件存放目录
     */
    @Value("${onebase.plugin.plugins-dir:plugins}")
    private String pluginsDir;

    /**
     * 临时目录
     */
    @Value("${onebase.plugin.temp-dir:plugins/.temp}")
    private String tempDir;

    @Resource
    private FileApi fileApi;

    // TODO: 注入OneBasePluginManager（如果需要与现有插件管理器集成）
    // @Resource
    // private OneBasePluginManager pluginManager;

    /**
     * 下载并解压插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param packageFileId 包文件ID
     */
    public void downloadAndExtractPlugin(Long pluginId, String pluginVersion, Long packageFileId) {
        log.info("开始下载插件: pluginId={}, version={}, fileId={}", pluginId, pluginVersion, packageFileId);

        // 1. 构建目标目录路径
        Path targetDir = getPluginDir(pluginId, pluginVersion);
        Path tempPath = Paths.get(tempDir, pluginId + "_" + pluginVersion + "_" + System.currentTimeMillis());

        try {
            // 2. 如果目标目录已存在，先删除
            if (Files.exists(targetDir)) {
                log.info("删除已存在的插件目录: {}", targetDir);
                FileUtil.del(targetDir.toFile());
            }

            // 3. 创建临时目录
            Files.createDirectories(tempPath);

            // 4. 从MinIO下载文件
            byte[] content = downloadFileFromMinIO(packageFileId);
            if (content == null || content.length == 0) {
                throw new RuntimeException("下载插件文件失败，文件内容为空");
            }

            // 5. 保存到临时文件
            Path tempZipFile = tempPath.resolve("plugin.zip");
            Files.write(tempZipFile, content);

            // 6. 解压到目标目录
            Files.createDirectories(targetDir);
            extractBackendJar(tempZipFile, targetDir);

            log.info("插件下载解压成功: pluginId={}, version={}, targetDir={}",
                    pluginId, pluginVersion, targetDir);

        } catch (Exception e) {
            log.error("下载解压插件失败: pluginId={}, version={}", pluginId, pluginVersion, e);
            // 清理可能创建的目录
            FileUtil.del(targetDir.toFile());
            throw new RuntimeException("下载解压插件失败", e);
        } finally {
            // 清理临时目录
            FileUtil.del(tempPath.toFile());
        }
    }

    /**
     * 加载插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void loadPlugin(Long pluginId, String pluginVersion) {
        log.info("加载插件: pluginId={}, version={}", pluginId, pluginVersion);

        Path pluginDir = getPluginDir(pluginId, pluginVersion);
        if (!Files.exists(pluginDir)) {
            throw new RuntimeException("插件目录不存在: " + pluginDir);
        }

        // TODO: 调用OneBasePluginManager加载插件
        // 这里需要根据实际的PluginManager实现来调用
        // pluginManager.loadPlugin(pluginDir);

        log.info("插件加载成功: pluginId={}, version={}", pluginId, pluginVersion);
    }

    /**
     * 卸载插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void unloadPlugin(Long pluginId, String pluginVersion) {
        log.info("卸载插件: pluginId={}, version={}", pluginId, pluginVersion);

        // TODO: 调用OneBasePluginManager卸载插件
        // pluginManager.unloadPlugin(pluginId, pluginVersion);

        log.info("插件卸载成功: pluginId={}, version={}", pluginId, pluginVersion);
    }

    /**
     * 清理插件文件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void cleanupPluginFiles(Long pluginId, String pluginVersion) {
        Path pluginDir = getPluginDir(pluginId, pluginVersion);
        if (Files.exists(pluginDir)) {
            log.info("清理插件文件: {}", pluginDir);
            FileUtil.del(pluginDir.toFile());
        }
    }

    /**
     * 获取插件目录路径
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 插件目录路径
     */
    public Path getPluginDir(Long pluginId, String pluginVersion) {
        return Paths.get(pluginsDir, String.valueOf(pluginId), pluginVersion);
    }

    /**
     * 从MinIO下载文件
     *
     * @param fileId 文件ID
     * @return 文件内容
     */
    private byte[] downloadFileFromMinIO(Long fileId) {
        try {
            // 通过FileApi获取文件内容
            // 注意：这里需要根据实际的FileApi实现来调整
            log.info("从MinIO下载文件: fileId={}", fileId);

            // TODO: 根据实际FileApi实现调整
            // 目前FileApi没有直接返回byte[]的方法，需要通过Response获取
            // 临时方案：可以通过内部调用FileService来获取

            // 这里返回null，实际实现时需要补充
            log.warn("FileApi暂不支持直接获取文件内容，需要补充实现");
            return null;

        } catch (Exception e) {
            log.error("从MinIO下载文件失败: fileId={}", fileId, e);
            throw new RuntimeException("下载文件失败", e);
        }
    }

    /**
     * 从ZIP中提取后端JAR包
     *
     * @param zipFile ZIP文件路径
     * @param targetDir 目标目录
     */
    private void extractBackendJar(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 只提取后端相关的文件（jar包）
                if (entryName.endsWith(".jar") && !entry.isDirectory()) {
                    Path targetFile = targetDir.resolve(Paths.get(entryName).getFileName());
                    log.info("解压文件: {} -> {}", entryName, targetFile);

                    Files.createDirectories(targetFile.getParent());
                    try (OutputStream os = new FileOutputStream(targetFile.toFile())) {
                        IoUtil.copy(zis, os);
                    }
                }

                zis.closeEntry();
            }
        }
    }

    /**
     * 检查插件是否已加载
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否已加载
     */
    public boolean isPluginLoaded(Long pluginId, String pluginVersion) {
        // TODO: 根据实际PluginManager实现来检查
        return false;
    }

    /**
     * 检查插件目录是否存在
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否存在
     */
    public boolean isPluginDirExists(Long pluginId, String pluginVersion) {
        return Files.exists(getPluginDir(pluginId, pluginVersion));
    }

}
