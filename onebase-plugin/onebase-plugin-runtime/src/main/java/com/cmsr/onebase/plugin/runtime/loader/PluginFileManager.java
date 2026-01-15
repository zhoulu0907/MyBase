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
     * 插件存放根目录
     */
    @Value("${onebase.plugin.plugins-dir:plugins}")
    private String pluginsDir;

    /**
     * 前端插件存放目录
     */
    @Value("${onebase.plugin.frontend-dir:plugins/frontend}")
    private String frontendDir;

    /**
     * 后端插件存放目录
     */
    @Value("${onebase.plugin.backend-dir:plugins/backend}")
    private String backendDir;

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
     * 流程：
     * 1. 从MinIO下载插件包
     * 2. 从插件包中提取后端zip
     * 3. 删除指定目录下该pluginId的所有旧zip包
     * 4. 将后端zip命名为{pluginId}-{version}.zip放到指定目录
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param packageFileId 包文件ID
     */
    public void downloadAndExtractPlugin(String pluginId, String pluginVersion, Long packageFileId) {
        log.info("开始下载插件: pluginId={}, version={}, fileId={}", pluginId, pluginVersion, packageFileId);

        Path tempPath = Paths.get(tempDir, pluginId + "_" + pluginVersion + "_" + System.currentTimeMillis());

        try {
            // 1. 创建临时目录
            Files.createDirectories(tempPath);

            // 2. 从MinIO下载文件
            byte[] content = downloadFileFromMinIO(packageFileId);
            if (content == null || content.length == 0) {
                throw new RuntimeException("下载插件文件失败，文件内容为空");
            }

            // 3. 保存到临时文件
            Path tempZipFile = tempPath.resolve("plugin.zip");
            Files.write(tempZipFile, content);

            // 4. 从插件包中提取后端zip包
            byte[] backendZipContent = extractBackendZipFromPackage(tempZipFile);
            if (backendZipContent == null || backendZipContent.length == 0) {
                throw new RuntimeException("插件包中未找到后端zip包");
            }

            // 5. 确保后端插件目录存在
            Path backendDirPath = Paths.get(backendDir);
            Files.createDirectories(backendDirPath);

            // 6. 删除该pluginId的所有旧zip包
            deleteOldPluginZips(pluginId);

            // 7. 将后端zip命名为{pluginId}-{version}.zip并保存到后端目录
            String targetZipName = pluginId + "-" + pluginVersion + ".zip";
            Path targetZipPath = backendDirPath.resolve(targetZipName);
            Files.write(targetZipPath, backendZipContent);

            log.info("插件下载成功: pluginId={}, version={}, targetPath={}",
                    pluginId, pluginVersion, targetZipPath);

        } catch (Exception e) {
            log.error("下载解压插件失败: pluginId={}, version={}", pluginId, pluginVersion, e);
            throw new RuntimeException("下载解压插件失败", e);
        } finally {
            // 清理临时目录
            FileUtil.del(tempPath.toFile());
        }
    }

    /**
     * 从插件包中提取后端zip包
     * 后端包命名格式：backend-*.zip
     *
     * @param packageZipFile 插件包路径
     * @return 后端zip包内容
     */
    private byte[] extractBackendZipFromPackage(Path packageZipFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(packageZipFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                // 查找后端zip包（以backend-开头，以.zip结尾）
                if (entryName.startsWith("backend-") && entryName.endsWith(".zip") && !entry.isDirectory()) {
                    log.info("找到后端包: {}", entryName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IoUtil.copy(zis, baos);
                    return baos.toByteArray();
                }
                zis.closeEntry();
            }
        }
        log.warn("插件包中未找到后端包(backend-*.zip)");
        return null;
    }

    /**
     * 删除后端目录下该pluginId的所有旧zip包
     *
     * @param pluginId 插件ID
     */
    private void deleteOldPluginZips(String pluginId) {
        Path backendDirPath = Paths.get(backendDir);
        if (!Files.exists(backendDirPath)) {
            return;
        }

        File[] files = backendDirPath.toFile().listFiles((dir, name) ->
                name.startsWith(pluginId + "-") && name.endsWith(".zip"));

        if (files != null && files.length > 0) {
            for (File file : files) {
                log.info("删除旧的插件包: {}", file.getName());
                FileUtil.del(file);
            }
        }
    }

    /**
     * 加载插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void loadPlugin(String pluginId, String pluginVersion) {
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
    public void unloadPlugin(String pluginId, String pluginVersion) {
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
    public void cleanupPluginFiles(String pluginId, String pluginVersion) {
        // 删除指定版本的后端zip包
        String targetZipName = pluginId + "-" + pluginVersion + ".zip";
        Path targetZipPath = Paths.get(backendDir, targetZipName);
        if (Files.exists(targetZipPath)) {
            log.info("清理插件文件: {}", targetZipPath);
            FileUtil.del(targetZipPath.toFile());
        }
    }

    /**
     * 获取后端插件zip包路径
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 后端插件zip包路径
     */
    public Path getPluginZipPath(String pluginId, String pluginVersion) {
        return Paths.get(backendDir, pluginId + "-" + pluginVersion + ".zip");
    }

    /**
     * 获取插件目录路径
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 插件目录路径
     * @deprecated 已不再使用目录结构，请使用 {@link #getPluginZipPath(String, String)}
     */
    @Deprecated
    public Path getPluginDir(String pluginId, String pluginVersion) {
        return Paths.get(pluginsDir, pluginId, pluginVersion);
    }

    /**
     * 从MinIO下载文件
     *
     * @param fileId 文件ID
     * @return 文件内容
     */
    private byte[] downloadFileFromMinIO(Long fileId) {
        try {
            log.info("从MinIO下载文件: fileId={}", fileId);
            // 通过FileApi获取文件内容
            byte[] content = fileApi.getFileContentBytes(fileId).getCheckedData();
            log.info("文件下载成功: fileId={}, size={}bytes", fileId, content != null ? content.length : 0);
            return content;
        } catch (Exception e) {
            log.error("从MinIO下载文件失败: fileId={}", fileId, e);
            throw new RuntimeException("下载文件失败", e);
        }
    }



    /**
     * 检查插件是否已加载
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否已加载
     */
    public boolean isPluginLoaded(String pluginId, String pluginVersion) {
        // TODO: 根据实际PluginManager实现来检查
        return false;
    }

    /**
     * 检查插件zip包是否存在
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否存在
     */
    public boolean isPluginZipExists(String pluginId, String pluginVersion) {
        return Files.exists(getPluginZipPath(pluginId, pluginVersion));
    }

    /**
     * 检查插件目录是否存在
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否存在
     * @deprecated 已不再使用目录结构，请使用 {@link #isPluginZipExists(String, String)}
     */
    @Deprecated
    public boolean isPluginDirExists(String pluginId, String pluginVersion) {
        return Files.exists(getPluginDir(pluginId, pluginVersion));
    }

}
