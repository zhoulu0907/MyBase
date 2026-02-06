package com.cmsr.onebase.plugin.build.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        if (!StringUtils.hasText(originalFilename)) {
            throw exception(PLUGIN_FILE_NAME_INVALID);
        }
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw exception(PLUGIN_FILE_TYPE_INVALID);
        }

        // 4. 读取文件内容
        byte[] content;
        try {
            content = file.getInputStream().readAllBytes();
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

                // 检查是否包含plugin.manifest.json
                if ("plugin.manifest.json".equals(entryName) || entryName.endsWith("/plugin.manifest.json")) {
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
     * 从 ZIP 中提取plugin.json内容
     *
     * @param content ZIP文件内容
     * @return plugin.json内容
     */
    public String extractPluginJson(byte[] content) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("plugin.manifest.json".equals(entryName) || entryName.endsWith("/plugin.manifest.json")) {
                    return new String(readEntryContent(zis), StandardCharsets.UTF_8);
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
     * 从ZIP中提取plugin.schema.json内容（插件配置模板）
     *
     * @param content ZIP文件内容
     * @return plugin.schema.json内容，如果不存在则返回空JSON对象
     */
    public String extractPluginSchemaJson(byte[] content) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("plugin.schema.json".equals(entryName) || entryName.endsWith("/plugin.schema.json")) {
                    return new String(readEntryContent(zis), StandardCharsets.UTF_8);
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            log.error("提取plugin.schema.json失败", e);
            // plugin.schema.json不是必须的，提取失败时返回空JSON对象
        }
        // 如果文件不存在，返回空JSON对象
        return "{}";
    }

    /**
     * 插件包信息
     */
    public static class PackageInfo {
        private final String packageName;
        private final Integer packageType;

        public PackageInfo(String packageName, Integer packageType) {
            this.packageName = packageName;
            this.packageType = packageType;
        }

        public String getPackageName() {
            return packageName;
        }

        public Integer getPackageType() {
            return packageType;
        }
    }

    /**
     * 包类型常量
     */
    public static final int PACKAGE_TYPE_FRONTEND = 0;
    public static final int PACKAGE_TYPE_BACKEND = 1;

    /**
     * 检测ZIP包中的前端/后端包
     * <p>
     * 前端zip包内会包含 frontend.manifest.json
     * 后端zip包内会包含 .properties 文件（基于pf4j）
     *
     * @param content ZIP文件内容
     * @return 检测到的包信息列表
     */
    public List<PackageInfo> detectPackages(byte[] content) {
        List<PackageInfo> packages = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 检查是否是zip文件（内嵌的前后端包）
                if (entryName.toLowerCase().endsWith(".zip") && !entry.isDirectory()) {
                    // 读取内嵌zip文件内容
                    byte[] nestedZipContent = readEntryContent(zis);

                    // 检测该zip是前端包还是后端包
                    Integer packageType = detectNestedPackageType(nestedZipContent);
                    if (packageType != null) {
                        // 提取文件名（去除路径）
                        String packageName = getFileName(entryName);
                        packages.add(new PackageInfo(packageName, packageType));
                        log.info("检测到{}包: {}",
                                packageType == PACKAGE_TYPE_FRONTEND ? "前端" : "后端",
                                packageName);
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            log.error("检测插件包失败", e);
            throw exception(PLUGIN_ZIP_INVALID);
        }

        // 校验至少存在一个包
        if (packages.isEmpty()) {
            log.error("插件包中未检测到前端或后端包");
            throw exception(PLUGIN_PACKAGE_NOT_FOUND);
        }

        return packages;
    }

    /**
     * 检测内嵌zip包的类型
     *
     * @param zipContent 内嵌zip文件内容
     * @return 包类型（0=前端，1=后端），如果无法识别则返回null
     */
    private Integer detectNestedPackageType(byte[] zipContent) {
        try (ZipInputStream nestedZis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
            ZipEntry nestedEntry;
            while ((nestedEntry = nestedZis.getNextEntry()) != null) {
                String nestedEntryName = nestedEntry.getName();

                // 检查前端包标识：frontend.manifest.json
                if ("frontend.manifest.json".equals(nestedEntryName)
                        || nestedEntryName.endsWith("/frontend.manifest.json")) {
                    return PACKAGE_TYPE_FRONTEND;
                }

                // 检查后端包标识：.properties文件（pf4j插件描述文件）
                if (nestedEntryName.toLowerCase().endsWith(".properties")) {
                    return PACKAGE_TYPE_BACKEND;
                }

                nestedZis.closeEntry();
            }
        } catch (IOException e) {
            log.warn("解析内嵌zip包失败", e);
        }
        return null;
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
