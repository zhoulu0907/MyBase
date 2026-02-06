package com.cmsr.onebase.plugin.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 插件包处理工具类
 * <p>
 * 提供插件包的解压、提取等通用功能。
 * 替代原有的分散在各模块中的解压逻辑，并不依赖Hutool。
 * </p>
 *
 * @author onebase
 * @date 2026-02-05
 */
@Slf4j
public class PluginPackageUtil {

    private static final int BUFFER_SIZE = 8192;

    /**
     * 从主ZIP中提取后端ZIP包
     *
     * @param zipFile 主ZIP文件路径
     * @param targetDir 目标目录
     * @return 提取出的后端ZIP文件路径，如果未找到返回null
     */
    public static Path extractBackendZip(Path zipFile, Path targetDir) throws IOException {
        Path extractedPath = null;
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
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

                        createDirectories(targetFile.getParent());
                        try (OutputStream os = Files.newOutputStream(targetFile)) {
                            copy(zis, os);
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
     * 从主ZIP中提取前端ZIP包并解压
     *
     * @param zipFile 主ZIP文件路径
     * @param targetDir 前端目标目录 (e.g. plugins/frontend/{pluginId}/{version})
     */
    public static void extractFrontendZip(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 提取前端zip文件（文件名包含"frontend"且以".zip"结尾）
                if (entryName.endsWith(".zip") && !entry.isDirectory() && entryName.toLowerCase().contains("frontend")) {
                    log.info("发现前端插件包，开始解压: {}", entryName);
                    
                    // 1. 提取 frontend.zip 到临时文件
                    Path tempFrontendZip = Files.createTempFile("frontend-", ".zip");
                    try {
                        try (OutputStream os = Files.newOutputStream(tempFrontendZip)) {
                            copy(zis, os);
                        }

                        // 2. 解压 frontend.zip 到 targetDir
                        unzip(tempFrontendZip, targetDir);

                    } finally {
                        deleteFile(tempFrontendZip);
                    }

                    log.info("前端插件包解压完成: {} -> {}", entryName, targetDir);
                    // 只处理一个前端zip
                    break;
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 解压ZIP文件到指定目录
     *
     * @param zipFile ZIP文件路径
     * @param targetDir 目标目录
     */
    public static void unzip(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                Path targetFile = targetDir.resolve(entry.getName());
                // 防止Zip Slip
                if (!targetFile.normalize().startsWith(targetDir.normalize())) {
                    throw new IOException("Zip entry is outside of the target dir: " + entry.getName());
                }
                createDirectories(targetFile.getParent());
                try (OutputStream os = Files.newOutputStream(targetFile)) {
                    copy(zis, os);
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 复制流
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    /**
     * 创建目录（如果不存在）
     */
    public static void createDirectories(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    /**
     * 删除文件或目录
     */
    public static void deleteFile(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("删除文件失败: {}", path, e);
        }
    }
}
