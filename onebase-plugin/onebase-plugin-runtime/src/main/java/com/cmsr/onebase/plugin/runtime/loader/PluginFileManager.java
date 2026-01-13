package com.cmsr.onebase.plugin.runtime.loader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.plugin.core.message.PluginCommandMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
     * 下载并解压插件（支持前后端分离存储）
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param packageFileId 包文件ID
     * @param packages 插件包信息列表
     */
    public void downloadAndExtractPlugin(String pluginId, String pluginVersion, Long packageFileId, 
                                         List<PluginCommandMessage.PackageInfo> packages) {
        log.info("开始下载插件: pluginId={}, version={}, fileId={}", pluginId, pluginVersion, packageFileId);

        Path tempPath = Paths.get(tempDir, pluginId + "_" + pluginVersion + "_" + System.currentTimeMillis());

        try {
            // 1. 删除该插件的旧版本文件（前端和后端目录下该pluginId的所有文件）
            cleanupOldVersions(pluginId);

            // 2. 创建临时目录
            Files.createDirectories(tempPath);

            // 3. 从MinIO下载文件
            byte[] content = downloadFileFromMinIO(packageFileId);
            if (content == null || content.length == 0) {
                throw new RuntimeException("下载插件文件失败，文件内容为空");
            }

            // 4. 保存到临时文件
            String zipFileName = pluginId + "_" + pluginVersion + ".zip";
            Path tempZipFile = tempPath.resolve(zipFileName);
            Files.write(tempZipFile, content);

            // 5. 解压并分离前后端包
            extractAndSeparatePackages(tempZipFile, pluginId, pluginVersion, packages);

            log.info("插件下载解压成功: pluginId={}, version={}", pluginId, pluginVersion);

        } catch (Exception e) {
            log.error("下载解压插件失败: pluginId={}, version={}", pluginId, pluginVersion, e);
            throw new RuntimeException("下载解压插件失败", e);
        } finally {
            // 清理临时目录
            FileUtil.del(tempPath.toFile());
        }
    }

    /**
     * 下载并解压插件（旧版兼容方法）
     */
    public void downloadAndExtractPlugin(String pluginId, String pluginVersion, Long packageFileId) {
        downloadAndExtractPlugin(pluginId, pluginVersion, packageFileId, null);
    }

    /**
     * 清理该插件的所有旧版本文件
     *
     * @param pluginId 插件ID
     */
    private void cleanupOldVersions(String pluginId) {
        // 清理前端目录下该插件的文件
        Path frontendPluginDir = Paths.get(frontendDir, pluginId);
        if (Files.exists(frontendPluginDir)) {
            log.info("清理前端插件旧版本: {}", frontendPluginDir);
            FileUtil.del(frontendPluginDir.toFile());
        }

        // 清理后端目录下该插件的文件
        Path backendPluginDir = Paths.get(backendDir, pluginId);
        if (Files.exists(backendPluginDir)) {
            log.info("清理后端插件旧版本: {}", backendPluginDir);
            FileUtil.del(backendPluginDir.toFile());
        }
    }

    /**
     * 解压并分离前后端包
     *
     * @param mainZipFile 主ZIP文件
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param packages 包信息列表
     */
    private void extractAndSeparatePackages(Path mainZipFile, String pluginId, String pluginVersion,
                                           List<PluginCommandMessage.PackageInfo> packages) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(mainZipFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 检查是否是内嵌的ZIP文件
                if (entryName.toLowerCase().endsWith(".zip") && !entry.isDirectory()) {
                    // 读取内嵌ZIP内容
                    byte[] nestedZipContent = readEntryContent(zis);
                    String nestedZipName = getFileName(entryName);

                    // 确定包类型
                    Integer packageType = getPackageType(nestedZipName, packages);
                    if (packageType == null) {
                        // 如果没有包信息，通过内容检测
                        packageType = detectPackageType(nestedZipContent);
                    }

                    if (packageType != null) {
                        // 根据包类型决定存放目录
                        String targetDirPath = (packageType == PluginCommandMessage.PACKAGE_TYPE_FRONTEND) 
                                ? frontendDir : backendDir;
                        Path targetDir = Paths.get(targetDirPath, pluginId);
                        Files.createDirectories(targetDir);

                        // ZIP包命名为 pluginId_version.zip
                        String newZipName = pluginId + "_" + pluginVersion + ".zip";
                        Path targetZipFile = targetDir.resolve(newZipName);
                        Files.write(targetZipFile, nestedZipContent);
                        log.info("保存{}包: {}", 
                                packageType == PluginCommandMessage.PACKAGE_TYPE_FRONTEND ? "前端" : "后端",
                                targetZipFile);

                        // 解压ZIP包
                        extractZipToDirectory(nestedZipContent, targetDir);
                        log.info("解压{}包完成: {}",
                                packageType == PluginCommandMessage.PACKAGE_TYPE_FRONTEND ? "前端" : "后端",
                                targetDir);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 根据包名称获取包类型
     */
    private Integer getPackageType(String zipName, List<PluginCommandMessage.PackageInfo> packages) {
        if (packages == null || packages.isEmpty()) {
            return null;
        }
        for (PluginCommandMessage.PackageInfo pkg : packages) {
            if (zipName.equals(pkg.getPackageName())) {
                return pkg.getPackageType();
            }
        }
        return null;
    }

    /**
     * 通过内容检测包类型
     */
    private Integer detectPackageType(byte[] zipContent) {
        try (ZipInputStream nestedZis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
            ZipEntry nestedEntry;
            while ((nestedEntry = nestedZis.getNextEntry()) != null) {
                String nestedEntryName = nestedEntry.getName();

                // 检查前端包标识：frontend.manifest.json
                if ("frontend.manifest.json".equals(nestedEntryName)
                        || nestedEntryName.endsWith("/frontend.manifest.json")) {
                    return PluginCommandMessage.PACKAGE_TYPE_FRONTEND;
                }

                // 检查后端包标识：.properties文件
                if (nestedEntryName.toLowerCase().endsWith(".properties")) {
                    return PluginCommandMessage.PACKAGE_TYPE_BACKEND;
                }

                nestedZis.closeEntry();
            }
        } catch (IOException e) {
            log.warn("检测包类型失败", e);
        }
        return null;
    }

    /**
     * 解压ZIP到目标目录
     */
    private void extractZipToDirectory(byte[] zipContent, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                Path targetPath = targetDir.resolve(entryName);

                // 防止路径穿越攻击
                if (!targetPath.normalize().startsWith(targetDir.normalize())) {
                    throw new IOException("非法的ZIP条目路径: " + entryName);
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    try (OutputStream os = new FileOutputStream(targetPath.toFile())) {
                        IoUtil.copy(zis, os);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 读取ZipInputStream当前条目的内容
     */
    private byte[] readEntryContent(ZipInputStream zis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = zis.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * 从路径中提取文件名
     */
    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0) {
            return path.substring(lastSlash + 1);
        }
        return path;
    }

    /**
     * 加载插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void loadPlugin(String pluginId, String pluginVersion) {
        log.info("加载插件: pluginId={}, version={}", pluginId, pluginVersion);

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
        cleanupOldVersions(pluginId);
    }

    /**
     * 获取插件目录路径（旧版兼容）
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 插件目录路径
     */
    public Path getPluginDir(String pluginId, String pluginVersion) {
        return Paths.get(pluginsDir, pluginId, pluginVersion);
    }

    /**
     * 获取前端插件目录
     */
    public Path getFrontendPluginDir(String pluginId) {
        return Paths.get(frontendDir, pluginId);
    }

    /**
     * 获取后端插件目录
     */
    public Path getBackendPluginDir(String pluginId) {
        return Paths.get(backendDir, pluginId);
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
            return fileApi.getFileContentBytes(fileId).getCheckedData();
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
     * 检查插件目录是否存在
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否存在
     */
    public boolean isPluginDirExists(String pluginId, String pluginVersion) {
        return Files.exists(getFrontendPluginDir(pluginId)) || 
               Files.exists(getBackendPluginDir(pluginId));
    }

}
