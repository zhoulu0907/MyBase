package com.cmsr.onebase.framework.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传安全配置
 *
 * <p>用于配置允许上传的文件类型白名单和危险类型列表。</p>
 *
 * <p>配置示例：</p>
 * <pre>
 * onebase:
 *   file-upload:
 *     security:
 *       # 是否启用安全验证
 *       enabled: true
 *       # 允许的文件类型（扩展名 -> MIME类型列表）
 *       allowed-extensions:
 *         .pdf:
 *           - application/pdf
 *         .png:
 *           - image/png
 *       # 危险的 MIME 类型
 *       dangerous-mime-types:
 *         - application/x-php
 *         - application/x-jsp
 * </pre>
 */
@ConfigurationProperties(prefix = "onebase.file-upload.security")
@Validated
@Data
public class FileUploadSecurityProperties {

    /**
     * 是否启用文件上传安全验证
     */
    private boolean enabled = true;

    /**
     * 允许的文件类型白名单（扩展名 -> 允许的 MIME 类型列表）
     *
     * <p>如果不配置，使用默认白名单</p>
     */
    private Map<String, List<String>> allowedExtensions = new HashMap<>();

    /**
     * 危险的 MIME 类型列表
     *
     * <p>如果不配置，使用默认列表</p>
     */
    private List<String> dangerousMimeTypes = List.of();

    /**
     * 获取默认的允许文件类型白名单
     */
    public static Map<String, List<String>> getDefaultAllowedExtensions() {
        Map<String, List<String>> defaults = new HashMap<>();

        // 文档格式
        defaults.put(".docx", List.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        defaults.put(".doc", List.of("application/msword", "application/x-tika-msoffice"));
        defaults.put(".xls", List.of("application/vnd.ms-excel", "application/x-tika-msoffice"));
        defaults.put(".xlsx", List.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        defaults.put(".ppt", List.of("application/vnd.ms-powerpoint", "application/x-tika-msoffice"));
        defaults.put(".pptx", List.of("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        defaults.put(".pdf", List.of("application/pdf"));
        defaults.put(".csv", List.of("text/csv", "text/plain", "application/csv", "text/x-csv"));

        // 图片格式
        defaults.put(".jpg", List.of("image/jpeg"));
        defaults.put(".jpeg", List.of("image/jpeg"));
        defaults.put(".png", List.of("image/png"));
        defaults.put(".gif", List.of("image/gif"));
        defaults.put(".bmp", List.of("image/bmp", "image/x-ms-bmp"));
        defaults.put(".webp", List.of("image/webp"));
        defaults.put(".svg", List.of("image/svg+xml"));

        // 视频格式
        defaults.put(".mp4", List.of("video/mp4"));
        defaults.put(".avi", List.of("video/x-msvideo"));
        defaults.put(".mov", List.of("video/quicktime"));
        defaults.put(".wmv", List.of("video/x-ms-wmv"));
        defaults.put(".flv", List.of("video/x-flv"));
        defaults.put(".mkv", List.of("video/x-matroska"));
        defaults.put(".webm", List.of("video/webm"));
        defaults.put(".m4v", List.of("video/x-m4v"));
        defaults.put(".3gp", List.of("video/3gpp"));
        defaults.put(".mpeg", List.of("video/mpeg"));
        defaults.put(".mpg", List.of("video/mpeg"));

        // 压缩包格式
        defaults.put(".zip", List.of("application/zip", "application/x-zip-compressed"));
        defaults.put(".rar", List.of("application/x-rar-compressed", "application/vnd.rar"));
        defaults.put(".7z", List.of("application/x-7z-compressed"));
        defaults.put(".tar", List.of("application/x-tar"));
        defaults.put(".gz", List.of("application/gzip", "application/x-gzip"));
        defaults.put(".bz2", List.of("application/x-bzip2"));
        defaults.put(".tgz", List.of("application/gzip"));

        // 文本格式
        defaults.put(".txt", List.of("text/plain"));
        defaults.put(".md", List.of("text/markdown", "text/plain", "text/x-web-markdown"));
        defaults.put(".json", List.of("application/json", "text/plain"));
        defaults.put(".xml", List.of("application/xml", "text/xml", "text/plain"));

        return defaults;
    }

    /**
     * 获取默认的危险 MIME 类型列表
     */
    public static List<String> getDefaultDangerousMimeTypes() {
        return List.of(
                "application/x-php",
                "application/x-httpd-php",
                "text/x-php",
                "application/x-jsp",
                "text/x-jsp",
                "application/x-asp",
                "application/x-aspx",
                "text/html",
                "application/javascript",
                "text/javascript",
                "application/x-sh",
                "application/x-bat",
                "application/x-dosexec",
                "application/x-msdos-program",
                "application/x-executable",
                "application/x-sharedlib"
        );
    }

    /**
     * 获取有效的允许文件类型白名单
     *
     * <p>如果配置了自定义白名单则使用自定义的，否则使用默认白名单</p>
     */
    public Map<String, List<String>> getEffectiveAllowedExtensions() {
        if (allowedExtensions != null && !allowedExtensions.isEmpty()) {
            return allowedExtensions;
        }
        return getDefaultAllowedExtensions();
    }

    /**
     * 获取有效的危险 MIME 类型列表
     *
     * <p>如果配置了自定义列表则使用自定义的，否则使用默认列表</p>
     */
    public List<String> getEffectiveDangerousMimeTypes() {
        if (dangerousMimeTypes != null && !dangerousMimeTypes.isEmpty()) {
            return dangerousMimeTypes;
        }
        return getDefaultDangerousMimeTypes();
    }
}