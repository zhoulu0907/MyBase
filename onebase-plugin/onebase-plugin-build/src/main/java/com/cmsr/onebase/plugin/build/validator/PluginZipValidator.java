package com.cmsr.onebase.plugin.build.validator;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.plugin.build.constant.PluginErrorCodeConstants.*;

/**
 * 插件ZIP包校验器
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Component
@Slf4j
public class PluginZipValidator {

    /**
     * 最大文件大小：100MB
     */
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    /**
     * 允许的文件扩展名
     */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("zip", "jar");

    /**
     * 不允许的文件类型
     */
    private static final Set<String> FORBIDDEN_EXTENSIONS = Set.of("exe", "sh", "bat", "cmd", "ps1");

    /**
     * 校验上传的文件
     *
     * @param file 上传的文件
     * @return 文件内容字节数组
     */
    public byte[] validate(MultipartFile file) {
        // 1. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw exception(PLUGIN_FILE_EMPTY);
        }

        // 2. 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw exception(PLUGIN_FILE_TOO_LARGE);
        }

        // 3. 校验文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw exception(PLUGIN_FILE_NAME_INVALID);
        }
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw exception(PLUGIN_FILE_TYPE_INVALID);
        }

        // 4. 读取文件内容
        byte[] content;
        try {
            content = IoUtil.readBytes(file.getInputStream());
        } catch (IOException e) {
            log.error("读取插件文件失败", e);
            throw exception(PLUGIN_FILE_READ_ERROR);
        }

        // 5. 如果是ZIP文件，校验ZIP结构
        if ("zip".equalsIgnoreCase(extension)) {
            validateZipStructure(content);
        }

        return content;
    }

    /**
     * 校验ZIP文件结构
     *
     * @param content ZIP文件内容
     */
    private void validateZipStructure(byte[] content) {
        boolean hasPluginJson = false;
        Set<String> entryNames = new HashSet<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 检查路径遍历攻击
                if (entryName.contains("..")) {
                    throw exception(PLUGIN_ZIP_PATH_TRAVERSAL);
                }

                // 检查是否包含禁止的文件类型
                String ext = getFileExtension(entryName);
                if (FORBIDDEN_EXTENSIONS.contains(ext.toLowerCase())) {
                    throw exception(PLUGIN_ZIP_FORBIDDEN_FILE);
                }

                // 检查是否包含plugin.json
                if ("plugin.json".equals(entryName) || entryName.endsWith("/plugin.json")) {
                    hasPluginJson = true;
                }

                entryNames.add(entryName);
                zis.closeEntry();
            }
        } catch (IOException e) {
            log.error("解析插件ZIP文件失败", e);
            throw exception(PLUGIN_ZIP_INVALID);
        }

        // 必须包含plugin.json
        if (!hasPluginJson) {
            throw exception(PLUGIN_JSON_NOT_FOUND);
        }
    }

    /**
     * 从ZIP中提取plugin.json内容
     *
     * @param content ZIP文件内容
     * @return plugin.json内容
     */
    public String extractPluginJson(byte[] content) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("plugin.json".equals(entryName) || entryName.endsWith("/plugin.json")) {
                    return IoUtil.readUtf8(zis);
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            log.error("提取plugin.json失败", e);
            throw exception(PLUGIN_JSON_READ_ERROR);
        }
        throw exception(PLUGIN_JSON_NOT_FOUND);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

}
