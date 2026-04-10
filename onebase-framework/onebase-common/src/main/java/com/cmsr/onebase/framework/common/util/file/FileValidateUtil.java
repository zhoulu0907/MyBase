package com.cmsr.onebase.framework.common.util.file;

import cn.hutool.core.io.IoUtil;
import com.cmsr.onebase.framework.common.config.FileUploadSecurityProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件安全验证工具类
 *
 * <p>提供文件上传安全验证功能，防止任意文件上传漏洞</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *     <li>使用 Apache Tika 检测真实 MIME 类型，防止伪造文件头绕过</li>
 *     <li>严格的白名单验证，只允许指定的文件类型上传</li>
 *     <li>检测危险脚本类型，防止上传恶意脚本文件</li>
 *     <li>防止路径遍历攻击</li>
 * </ul>
 *
 * <h3>配置说明</h3>
 *
 * <p>在 application.yaml 中配置文件上传安全设置：</p>
 *
 * <pre>
 * onebase:
 *   file-upload:
 *     security:
 *       enabled: true  # 是否启用文件上传安全验证（默认 true）
 *
 *       # 允许的文件类型白名单（扩展名 -> MIME类型列表）
 *       # 如未配置，使用内置默认白名单（见下方"默认白名单"）
 *       allowed-extensions:
 *         .pdf:
 *           - application/pdf
 *         .png:
 *           - image/png
 *         .jpg:
 *           - image/jpeg
 *         .docx:
 *           - application/vnd.openxmlformats-officedocument.wordprocessingml.document
 *         .xlsx:
 *           - application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 *         .mp4:
 *           - video/mp4
 *         .zip:
 *           - application/zip
 *         .md:
 *           - text/markdown
 *           - text/plain
 *
 *       # 危险的 MIME 类型列表（黑名单，检测到直接拒绝）
 *       # 如未配置，使用内置默认危险类型列表（见下方"默认危险类型"）
 *       dangerous-mime-types:
 *         - application/x-php
 *         - application/x-jsp
 *         - application/x-sh
 *         - application/x-executable
 * </pre>
 *
 * <h4>默认白名单（内置，无需配置）</h4>
 * <ul>
 *   <li>文档类：.pdf, .doc, .docx, .xls, .xlsx, .ppt, .pptx, .txt, .md, .json, .xml</li>
 *   <li>图片类：.png, .jpg, .jpeg, .gif, .bmp, .webp, .svg</li>
 *   <li>视频类：.mp4, .avi, .mov, .wmv, .flv, .mkv, .webm</li>
 *   <li>音频类：.mp3, .wav, .ogg, .flac, .aac</li>
 *   <li>压缩包：.zip, .rar, .7z, .tar, .gz</li>
 * </ul>
 *
 * <h4>默认危险类型（内置，无需配置）</h4>
 * <ul>
 *   <li>PHP相关：application/x-php, text/x-php</li>
 *   <li>JSP相关：application/x-jsp, text/x-jsp</li>
 *   <li>脚本类：application/x-sh, application/x-shellscript, text/x-shellscript</li>
 *   <li>可执行文件：application/x-executable, application/x-dosexec</li>
 *   <li>其他危险类型：application/x-msdownload, application/x-msi</li>
 * </ul>
 *
 * <h4>配置优先级</h4>
 * <ol>
 *   <li>配置文件中的 allowed-extensions 和 dangerous-mime-types 优先</li>
 *   <li>如果配置文件未配置，使用内置默认值</li>
 *   <li>扩展名白名单和 MIME 类型白名单必须同时满足</li>
 * </ol>
 *
 * <h4>验证流程</h4>
 * <ol>
 *   <li>检查文件扩展名是否在白名单中</li>
 *   <li>使用 Tika 检测真实 MIME 类型（防止伪造文件头）</li>
 *   <li>检查 MIME 类型是否在危险类型黑名单中</li>
 *   <li>验证 MIME 类型与扩展名是否匹配</li>
 * </ol>
 */
@Slf4j
public class FileValidateUtil {

    private static final Tika TIKA = new Tika();

    /**
     * 配置持有者（由 Spring 注入）
     */
    private static volatile FileUploadSecurityProperties properties;

    /**
     * 设置配置（由 Spring 自动配置调用）
     */
    public static void setProperties(FileUploadSecurityProperties props) {
        properties = props;
        log.info("[FileValidateUtil] 文件上传安全配置已更新");
    }

    /**
     * 获取有效的允许文件类型白名单
     */
    private static Map<String, List<String>> getEffectiveAllowedExtensions() {
        if (properties != null) {
            return properties.getEffectiveAllowedExtensions();
        }
        return FileUploadSecurityProperties.getDefaultAllowedExtensions();
    }

    /**
     * 获取有效的危险 MIME 类型列表
     */
    private static List<String> getEffectiveDangerousMimeTypes() {
        if (properties != null) {
            return properties.getEffectiveDangerousMimeTypes();
        }
        return FileUploadSecurityProperties.getDefaultDangerousMimeTypes();
    }

    /**
     * 验证文件类型（针对 MultipartFile）
     *
     * @param file 上传的文件
     * @return 是否通过验证
     */
    public static boolean validateFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("[validateFileType] 文件为空");
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            log.warn("[validateFileType] 文件名为空");
            return false;
        }

        // 防止路径遍历攻击
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            log.warn("[validateFileType] 文件名包含非法字符: {}", originalFilename);
            return false;
        }

        String extension = getFileExtension(originalFilename);
        String detectedMimeType;

        try (InputStream inputStream = file.getInputStream()) {
            // 使用 Tika 检测真实 MIME 类型
            detectedMimeType = TIKA.detect(inputStream, originalFilename);
            log.debug("[validateFileType] 文件: {}, 检测到的 MIME 类型: {}", originalFilename, detectedMimeType);
        } catch (IOException e) {
            log.error("[validateFileType] 检测文件类型失败: {}", e.getMessage());
            return false;
        }

        return validateFileCommon(extension, detectedMimeType, originalFilename);
    }

    /**
     * 验证文件类型（针对字节数组）
     *
     * @param content 文件内容
     * @param name    文件名
     * @return 是否通过验证
     */
    public static boolean validateFileType(byte[] content, String name) {
        if (content == null || content.length == 0) {
            log.warn("[validateFileType] 文件内容为空");
            return false;
        }

        if (name == null || name.trim().isEmpty()) {
            log.warn("[validateFileType] 文件名为空");
            return false;
        }

        // 防止路径遍历攻击
        if (name.contains("..") || name.contains("/") || name.contains("\\")) {
            log.warn("[validateFileType] 文件名包含非法字符: {}", name);
            return false;
        }

        String extension = getFileExtension(name);
        String detectedMimeType;

        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            // 使用 Tika 检测真实 MIME 类型
            detectedMimeType = TIKA.detect(inputStream, name);
            log.debug("[validateFileType] 文件: {}, 检测到的 MIME 类型: {}", name, detectedMimeType);
        } catch (IOException e) {
            log.error("[validateFileType] 检测文件类型失败: {}", e.getMessage());
            return false;
        }

        return validateFileCommon(extension, detectedMimeType, name);
    }

    /**
     * 验证 multipart 请求中的所有文件类型
     *
     * @param request HttpServletRequest（需要支持 getParts() 方法）
     * @return 验证结果（true=所有文件通过验证，false=存在不合规文件）
     * @throws IOException        读取 multipart 内容时发生异常
     * @throws ServletException   解析 multipart 请求时发生异常
     */
    public static boolean validateMultipartFiles(HttpServletRequest request) throws IOException, ServletException {
        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            String submittedFileName = part.getSubmittedFileName();

            // 只验证文件类型的 Part（submittedFileName 不为空）
            if (submittedFileName != null && !submittedFileName.isEmpty()) {
                // 读取文件内容
                byte[] content = IoUtil.readBytes(part.getInputStream());

                // 验证文件类型
                if (!validateFileType(content, submittedFileName)) {
                    log.warn("[validateMultipartFiles] 文件验证失败: {}", submittedFileName);
                    return false;
                }

                log.debug("[validateMultipartFiles] 文件验证通过: {}", submittedFileName);
            }
        }

        return true;
    }

    /**
     * 公共验证逻辑
     *
     * @param extension        文件扩展名
     * @param detectedMimeType 检测到的 MIME 类型
     * @param filename         文件名（用于日志）
     * @return 是否通过验证
     */
    private static boolean validateFileCommon(String extension, String detectedMimeType, String filename) {
        Map<String, List<String>> allowedExtensions = getEffectiveAllowedExtensions();
        List<String> dangerousMimeTypes = getEffectiveDangerousMimeTypes();

        // 1. 检查扩展名是否在白名单中
        if (!allowedExtensions.containsKey(extension)) {
            log.warn("[validateFileType] 不允许的文件扩展名: {}, 文件名: {}", extension, filename);
            return false;
        }

        // 2. 检查是否为危险的 MIME 类型
        if (isDangerousMimeType(detectedMimeType, dangerousMimeTypes)) {
            log.warn("[validateFileType] 检测到危险的 MIME 类型: {}, 文件名: {}", detectedMimeType, filename);
            return false;
        }

        // 3. 验证 MIME 类型与扩展名是否匹配
        List<String> allowedMimeTypes = allowedExtensions.get(extension);
        if (!allowedMimeTypes.contains(detectedMimeType)) {
            log.warn("[validateFileType] MIME 类型与扩展名不匹配, 文件名: {}, 扩展名: {}, 期望类型: {}, 实际类型: {}",
                    filename, extension, allowedMimeTypes, detectedMimeType);
            return false;
        }

        return true;
    }

    /**
     * 检查是否为危险的 MIME 类型
     *
     * @param mimeType           MIME 类型
     * @param dangerousMimeTypes 危险类型列表
     * @return 是否危险
     */
    private static boolean isDangerousMimeType(String mimeType, List<String> dangerousMimeTypes) {
        if (mimeType == null) {
            return false;
        }

        String lowerMimeType = mimeType.toLowerCase();

        // 检查是否在危险类型列表中
        for (String dangerous : dangerousMimeTypes) {
            if (lowerMimeType.contains(dangerous.toLowerCase())) {
                return true;
            }
        }

        // 额外检查：包含 script 关键字
        if (lowerMimeType.contains("script")) {
            return true;
        }

        // 额外检查：可执行文件类型
        if (lowerMimeType.contains("executable") || lowerMimeType.contains("x-dosexec")) {
            return true;
        }

        return false;
    }

    /**
     * 获取文件扩展名（包含点号）
     *
     * @param filename 文件名
     * @return 扩展名（包含点号，如 ".pdf"），如果没有扩展名则返回空字符串
     */
    private static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 验证文件大小
     *
     * @param file         上传的文件
     * @param maxSizeBytes 最大文件大小（字节）
     * @return 是否在允许范围内
     */
    public static boolean validateFileSize(MultipartFile file, long maxSizeBytes) {
        if (file == null) {
            return false;
        }

        long fileSize = file.getSize();
        if (fileSize > maxSizeBytes) {
            log.warn("[validateFileSize] 文件大小超出限制: 文件名={}, 大小={}字节, 限制={}字节",
                    file.getOriginalFilename(), fileSize, maxSizeBytes);
            return false;
        }

        return true;
    }

    /**
     * 验证文件大小（字节数组）
     *
     * @param content      文件内容
     * @param maxSizeBytes 最大文件大小（字节）
     * @param filename     文件名（用于日志）
     * @return 是否在允许范围内
     */
    public static boolean validateFileSize(byte[] content, long maxSizeBytes, String filename) {
        if (content == null) {
            return false;
        }

        long fileSize = content.length;
        if (fileSize > maxSizeBytes) {
            log.warn("[validateFileSize] 文件大小超出限制: 文件名={}, 大小={}字节, 限制={}字节",
                    filename, fileSize, maxSizeBytes);
            return false;
        }

        return true;
    }

    /**
     * 检查请求是否为 multipart/form-data 类型
     *
     * @param request HTTP 请求
     * @return 是否为 multipart 请求
     */
    public static boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    /**
     * 获取允许上传的文件扩展名列表
     *
     * @return 允许的扩展名列表
     */
    public static List<String> getAllowedExtensions() {
        return List.copyOf(getEffectiveAllowedExtensions().keySet());
    }

    /**
     * 获取指定扩展名允许的 MIME 类型列表
     *
     * @param extension 文件扩展名（包含点号）
     * @return 允许的 MIME 类型列表，如果扩展名不在白名单中则返回 null
     */
    public static List<String> getAllowedMimeTypes(String extension) {
        return getEffectiveAllowedExtensions().get(extension);
    }
}