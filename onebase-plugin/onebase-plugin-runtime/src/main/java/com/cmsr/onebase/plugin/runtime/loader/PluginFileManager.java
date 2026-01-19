package com.cmsr.onebase.plugin.runtime.loader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginState;
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

    @Resource
    private OneBasePluginManager oneBasePluginManager;

    /**
     * 下载并解压插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param packageFileId 包文件ID
     */
    public void downloadAndExtractPlugin(String pluginId, String pluginVersion, Long packageFileId) {
        log.info("开始下载插件: pluginId={}, version={}, fileId={}", pluginId, pluginVersion, packageFileId);

        // 1. 构建目标目录路径（直接放在backend目录下，不再创建子目录）
        Path targetDir = Paths.get(backendDir);
        Path tempPath = Paths.get(tempDir, pluginId + "_" + pluginVersion + "_" + System.currentTimeMillis());

        try {
            // 2. 如果目标目录不存在，创建目录
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
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

            // 6. 解压后端zip到目标目录
            Path extractedZipPath = extractBackendZip(tempZipFile, targetDir);

            // 7. 加载并启动插件
            if (extractedZipPath != null) {
                log.info("插件下载解压成功，开始加载: path={}", extractedZipPath);
                oneBasePluginManager.loadAndStartPlugin(extractedZipPath);
            } else {
                throw new RuntimeException("未在插件包中找到后端ZIP文件");
            }

            log.info("插件加载启动流程完成: pluginId={}, version={}", pluginId, pluginVersion);

        } catch (Exception e) {
            log.error("下载加载插件失败: pluginId={}, version={}", pluginId, pluginVersion, e);
            throw new RuntimeException("下载加载插件失败", e);
        } finally {
            // 清理临时目录
            FileUtil.del(tempPath.toFile());
        }
    }

    /**
     * 加载并启动插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 插件状态
     */
    public PluginState loadPlugin(String pluginId, String pluginVersion) {
        log.info("加载插件: pluginId={}, version={}", pluginId, pluginVersion);

        Path pluginDir = getPluginDir(pluginId, pluginVersion);
        if (!Files.exists(pluginDir)) {
            throw new RuntimeException("插件目录不存在: " + pluginDir);
        }

        // 查找插件ZIP文件
        Path zipPath = findPluginZip(pluginDir);
        if (zipPath == null) {
            throw new RuntimeException("插件目录下未找到ZIP文件: " + pluginDir);
        }

        // 调用OneBasePluginManager加载并启动插件
        PluginState state = oneBasePluginManager.loadAndStartPlugin(zipPath);
        log.info("插件加载完成: pluginId={}, version={}, state={}", pluginId, pluginVersion, state);
        return state;
    }

    /**
     * 卸载插件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否成功
     */
    public boolean unloadPlugin(String pluginId, String pluginVersion) {
        log.info("卸载插件: pluginId={}, version={}", pluginId, pluginVersion);

        // 调用OneBasePluginManager卸载插件
        boolean result = oneBasePluginManager.stopAndUnloadPlugin(pluginId);
        log.info("插件卸载结果: pluginId={}, version={}, success={}", pluginId, pluginVersion, result);
        return result;
    }

    /**
     * 清理插件文件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void cleanupPluginFiles(String pluginId, String pluginVersion) {
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
    public Path getPluginDir(String pluginId, String pluginVersion) {
        return Paths.get(backendDir, pluginId, pluginVersion);
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
            log.info("从MinIO下载文件: fileId={}", fileId);
            CommonResult<byte[]> result = fileApi.getFileContentBytes(fileId);
            if (result.isSuccess()) {
                return result.getData();
            } else {
                log.error("FileApi调用失败: code={}, msg={}", result.getCode(), result.getMsg());
                throw new RuntimeException("FileApi调用失败: " + result.getMsg());
            }

        } catch (Exception e) {
            log.error("从MinIO下载文件失败: fileId={}", fileId, e);
            throw new RuntimeException("下载文件失败", e);
        }
    }

    /**
     * 从主ZIP中提取后端ZIP包
     * <p>
     * 插件包结构：主zip包含前端zip、后端zip、两个json文件
     * 后端zip文件名通常包含"backend"或以"-backend.zip"结尾
     * </p>
     *
     * @param zipFile 主ZIP文件路径
     * @param targetDir 目标目录
     * @return 提取出的后端ZIP文件路径，如果未找到返回null
     */
    private Path extractBackendZip(Path zipFile, Path targetDir) throws IOException {
        Path extractedPath = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 提取后端zip文件（文件名包含"backend"或以".zip"结尾且不包含"frontend"）
                if (entryName.endsWith(".zip") && !entry.isDirectory()) {
                    String lowerName = entryName.toLowerCase();
                    // 优先匹配包含backend的zip，否则跳过包含frontend的zip
                    if (lowerName.contains("backend") || !lowerName.contains("frontend")) {
                        Path targetFile = targetDir.resolve(Paths.get(entryName).getFileName());
                        log.info("解压后端插件包: {} -> {}", entryName, targetFile);

                        Files.createDirectories(targetFile.getParent());
                        try (OutputStream os = new FileOutputStream(targetFile.toFile())) {
                            IoUtil.copy(zis, os);
                        }
                        extractedPath = targetFile;
                        // 只提取一个后端zip
                        if (lowerName.contains("backend")) {
                            break;
                        }
                    }
                }

                zis.closeEntry();
            }
        }
        return extractedPath;
    }

    /**
     * 检查插件是否已加载
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否已加载
     */
    public boolean isPluginLoaded(String pluginId, String pluginVersion) {
        return oneBasePluginManager.getPlugin(pluginId).isPresent();
    }

    /**
     * 检查插件目录是否存在
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否存在
     */
    public boolean isPluginDirExists(String pluginId, String pluginVersion) {
        return Files.exists(getPluginDir(pluginId, pluginVersion));
    }

    /**
     * 删除插件
     * 先停止并卸载插件，然后清理本地文件
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 是否成功
     */
    public boolean deletePlugin(String pluginId, String pluginVersion) {
        log.info("删除插件: pluginId={}, version={}", pluginId, pluginVersion);

        // 1. 调用OneBasePluginManager删除插件（会先停止、卸载，然后删除文件）
        boolean result = oneBasePluginManager.deletePlugin(pluginId);

        // 2. 无论删除结果如何，都清理本地插件目录（确保文件被清理）
        cleanupPluginFiles(pluginId, pluginVersion);

        log.info("插件删除完成: pluginId={}, version={}, success={}", pluginId, pluginVersion, result);
        return result;
    }

    /**
     * 查找插件目录下的ZIP文件
     *
     * @param pluginDir 插件目录
     * @return ZIP文件路径，未找到返回null
     */
    private Path findPluginZip(Path pluginDir) {
        try {
            return Files.list(pluginDir)
                    .filter(p -> p.toString().endsWith(".zip"))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            log.error("查找插件ZIP文件失败: {}", pluginDir, e);
            return null;
        }
    }

}
