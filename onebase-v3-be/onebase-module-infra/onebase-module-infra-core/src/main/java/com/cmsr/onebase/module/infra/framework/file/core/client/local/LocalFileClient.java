package com.cmsr.onebase.module.infra.framework.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import com.cmsr.onebase.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件客户端
 *
 */
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        File file = getSecureFile(path);
        // 确保目录存在
        FileUtil.mkParentDirs(file);
        // 写入文件
        FileUtil.writeBytes(content, file);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        File file = getSecureFile(path);
        FileUtil.del(file);
    }

    @Override
    public byte[] getContent(String path) {
        File file = getSecureFile(path);
        return FileUtil.readBytes(file);
    }

    /**
     * 获取安全的文件路径，防止路径遍历攻击
     *
     * @param path 相对路径
     * @return 安全的文件对象
     */
    private File getSecureFile(String path) {
        // 获取项目根目录
        String projectBaseDir = System.getProperty("user.dir");
        String basePath = config.getBasePath();

        // 构建基础目录路径
        Path baseDirPath = Paths.get(projectBaseDir, basePath).normalize().toAbsolutePath();

        // 清洗路径：移除路径遍历字符
        String sanitizedPath = sanitizePath(path);

        // 构建完整文件路径
        Path filePath = Paths.get(projectBaseDir, basePath, sanitizedPath).normalize().toAbsolutePath();

        // 验证最终路径在基础目录范围内
        if (!filePath.startsWith(baseDirPath)) {
            throw new SecurityException("检测到路径遍历攻击，拒绝访问: " + path);
        }

        return filePath.toFile();
    }

    /**
     * 清洗路径，移除路径遍历字符
     *
     * @param path 原始路径
     * @return 清洗后的安全路径
     */
    private String sanitizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        // 移除路径遍历字符
        String sanitized = path.replaceAll("\\.{2,}", "."); // 合并连续点号
        sanitized = sanitized.replaceAll("[/\\\\]+", "/");  // 统一路径分隔符
        // 移除开头的斜杠
        if (sanitized.startsWith("/")) {
            sanitized = sanitized.substring(1);
        }
        return sanitized;
    }

}
