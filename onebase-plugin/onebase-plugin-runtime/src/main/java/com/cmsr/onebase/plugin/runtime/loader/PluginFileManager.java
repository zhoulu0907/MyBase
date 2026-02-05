package com.cmsr.onebase.plugin.runtime.loader;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.plugin.common.util.PluginPackageUtil;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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

    @Resource
    private PluginProperties pluginProperties;

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

        // 1. 构建目标目录路径
        Path backendTargetDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getBackendDir());
        Path frontendTargetDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir(), "frontend-" + pluginId + "-" + pluginVersion);

        Path tempPath = Paths.get(tempDir, pluginId + "_" + pluginVersion + "_" + System.currentTimeMillis());

        try {
            // 2. 如果目标目录不存在，创建目录
            if (!Files.exists(backendTargetDir)) {
                Files.createDirectories(backendTargetDir);
            }
            if (!Files.exists(frontendTargetDir)) {
                Files.createDirectories(frontendTargetDir);
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
            Path extractedZipPath = PluginPackageUtil.extractBackendZip(tempZipFile, backendTargetDir);

            // 7. 解压前端zip到前端目标目录
            PluginPackageUtil.extractFrontendZip(tempZipFile, frontendTargetDir);

            // 8. 加载并启动插件
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
            PluginPackageUtil.deleteFile(tempPath);
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
        // 清理后端文件
        Path pluginDir = getPluginDir(pluginId, pluginVersion);
        if (Files.exists(pluginDir)) {
            log.info("清理插件后端文件: {}", pluginDir);
            PluginPackageUtil.deleteFile(pluginDir);
        }

        // 清理前端文件
        Path frontendDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir(), "frontend-" + pluginId + "-" + pluginVersion);
        if (Files.exists(frontendDir)) {
            log.info("清理插件前端文件: {}", frontendDir);
            PluginPackageUtil.deleteFile(frontendDir);
        }
    }

    /**
     * 清理指定pluginId下所有版本的文件
     * 包括zip包和解压后的目录，用于版本切换时清理旧版本
     *
     * @param pluginId 插件ID
     */
    public void cleanupPluginAllVersionFiles(String pluginId) {
        log.info("开始清理插件所有版本文件: pluginId={}", pluginId);

        cleanupDirectory(Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getBackendDir()), pluginId);
        cleanupDirectory(Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir()), pluginId);
        
        log.info("插件所有版本文件清理完成: pluginId={}", pluginId);
    }

    private void cleanupDirectory(Path rootDir, String pluginId) {
        if (!Files.exists(rootDir)) {
            return;
        }
        try (Stream<Path> list = Files.list(rootDir)) {
            list.forEach(path -> {
                String fileName = path.getFileName().toString();
                // 匹配以pluginId开头的文件或目录
                if (fileName.startsWith(pluginId) || fileName.contains(pluginId)) {
                    log.info("删除插件文件/目录: {}", path);
                    PluginPackageUtil.deleteFile(path);
                }
            });
        } catch (IOException e) {
            log.error("清理插件文件失败: root={}, pluginId={}", rootDir, pluginId, e);
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
        return Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getBackendDir(), pluginId, pluginVersion);
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
        try (Stream<Path> stream = Files.list(pluginDir)) {
            return stream
                    .filter(p -> p.toString().endsWith(".zip"))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            log.error("查找插件ZIP文件失败: {}", pluginDir, e);
            return null;
        }
    }

}
