package com.cmsr.onebase.framework.security.build.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 缓存 Multipart 请求的包装类
 *
 * <p>用于解决 multipart/form-data 请求体被 Tomcat 解析后无法再次读取的问题。</p>
 *
 * <p>Tomcat 在请求到达 filter chain 之前会自动解析 multipart 并消费原始流，
 * 导致 request.getInputStream() 返回空流。</p>
 *
 * <p><b>解决方案：</b></p>
 * <ul>
 *     <li>使用 Servlet Part API (request.getParts()) 获取解析后的文件</li>
 *     <li>重新构建完整的 multipart 请求体用于转发</li>
 * </ul>
 *
 * <p><b>缓存策略：</b></p>
 * <ul>
 *     <li>小文件（≤2MB）：内存缓存</li>
 *     <li>大文件（>2MB）：磁盘缓存</li>
 * </ul>
 */
@Slf4j
public class CachedMultipartRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 内存缓存阈值（2MB）
     */
    private static final int MEMORY_THRESHOLD = 2 * 1024 * 1024;

    /**
     * 缓存的请求体内容（重新构建的 multipart 格式）
     */
    private byte[] cachedBody;

    /**
     * 临时文件路径（磁盘缓存模式）
     */
    private Path tempFile;

    /**
     * 是否使用磁盘缓存
     */
    private final boolean useDiskCache;

    /**
     * 解析出的文件信息列表
     */
    private final List<FileInfo> files = new ArrayList<>();

    /**
     * 构造函数，通过 Servlet Part API 获取文件并重建 multipart 请求体
     *
     * @param request 原始请求
     * @throws IOException 读取文件时发生IO异常
     */
    public CachedMultipartRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        // 尝试从 Servlet Part API 获取解析后的文件
        Collection<Part> parts = null;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            // 如果 getParts() 失败（例如请求不是 multipart），使用原始流
            log.warn("[CachedMultipartRequestWrapper] getParts() 失败，尝试读取原始流: {}", e.getMessage());
        }

        if (parts != null && !parts.isEmpty()) {
            // 使用 Part API 获取文件并重建 multipart body
            log.info("[CachedMultipartRequestWrapper] 从 Part API 获取到 {} 个 part", parts.size());

            String boundary = extractBoundary(request.getContentType());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            for (Part part : parts) {
                String filename = part.getSubmittedFileName();
                String name = part.getName();

                if (filename != null && !filename.isEmpty()) {
                    // 这是一个文件 part
                    log.info("[CachedMultipartRequestWrapper] 文件 part: name={}, filename={}, size={}KB",
                            name, filename, part.getSize() / 1024);

                    // 读取文件内容
                    byte[] content = StreamUtils.copyToByteArray(part.getInputStream());
                    files.add(new FileInfo(filename, content));

                    // 写入 multipart 格式
                    writeMultipartPart(baos, boundary, name, filename, part.getContentType(), content);
                } else {
                    // 这是一个普通表单字段
                    log.debug("[CachedMultipartRequestWrapper] 表单字段: name={}", name);
                    byte[] content = StreamUtils.copyToByteArray(part.getInputStream());
                    writeMultipartField(baos, boundary, name, content);
                }
            }

            // 写入结束 boundary
            baos.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            byte[] reconstructedBody = baos.toByteArray();
            log.info("[CachedMultipartRequestWrapper] 重建的 multipart body 大小: {}KB", reconstructedBody.length / 1024);

            // 判断缓存策略
            useDiskCache = reconstructedBody.length > MEMORY_THRESHOLD;
            if (useDiskCache) {
                tempFile = Files.createTempFile("multipart-cache-", ".tmp");
                Files.write(tempFile, reconstructedBody);
                tempFile.toFile().deleteOnExit();
                cachedBody = null;
                log.info("[CachedMultipartRequestWrapper] 使用磁盘缓存: size={}MB", reconstructedBody.length / 1024 / 1024);
            } else {
                cachedBody = reconstructedBody;
                tempFile = null;
            }
        } else {
            // 尝试读取原始流（可能已经是空的）
            byte[] originalBody = StreamUtils.copyToByteArray(request.getInputStream());
            log.info("[CachedMultipartRequestWrapper] 原始流读取到的 body 大小: {}KB", originalBody.length / 1024);

            useDiskCache = originalBody.length > MEMORY_THRESHOLD;
            if (useDiskCache && originalBody.length > 0) {
                tempFile = Files.createTempFile("multipart-cache-", ".tmp");
                Files.write(tempFile, originalBody);
                tempFile.toFile().deleteOnExit();
                cachedBody = null;
            } else {
                cachedBody = originalBody;
                tempFile = null;
            }
        }
    }

    /**
     * 从 Content-Type 提取 boundary
     */
    private String extractBoundary(String contentType) {
        if (contentType == null) {
            return "----WebKitFormBoundary7MA4YWxkTrZu0gW"; // 默认 boundary
        }
        int boundaryIndex = contentType.indexOf("boundary=");
        if (boundaryIndex == -1) {
            return "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        }
        String boundary = contentType.substring(boundaryIndex + 9);
        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
            boundary = boundary.substring(1, boundary.length() - 1);
        }
        return boundary.trim();
    }

    /**
     * 写入 multipart 文件 part
     */
    private void writeMultipartPart(ByteArrayOutputStream baos, String boundary, String name,
                                     String filename, String contentType, byte[] content) throws IOException {
        baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n")
                .getBytes(StandardCharsets.UTF_8));
        if (contentType != null && !contentType.isEmpty()) {
            baos.write(("Content-Type: " + contentType + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write(content);
        baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 写入 multipart 表单字段
     */
    private void writeMultipartField(ByteArrayOutputStream baos, String boundary, String name, byte[] content) throws IOException {
        baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write(content);
        baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 返回缓存的请求体输入流（每次调用创建新的流）
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (useDiskCache) {
            return new CachedServletInputStream(Files.newInputStream(tempFile));
        } else {
            return new CachedServletInputStream(cachedBody);
        }
    }

    /**
     * 获取缓存的请求体内容
     */
    public byte[] getCachedBody() throws IOException {
        if (useDiskCache) {
            return Files.readAllBytes(tempFile);
        }
        return cachedBody != null ? cachedBody : new byte[0];
    }

    /**
     * 获取缓存大小
     */
    public long getCachedSize() throws IOException {
        if (useDiskCache) {
            return Files.size(tempFile);
        }
        return cachedBody != null ? cachedBody.length : 0;
    }

    /**
     * 获取解析出的文件列表
     */
    public List<FileInfo> getFiles() {
        return files;
    }

    /**
     * 清理缓存资源
     */
    public void cleanup() {
        if (useDiskCache && tempFile != null) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                // deleteOnExit 兜底
            }
        }
    }

    /**
     * 文件信息
     */
    public static class FileInfo {
        private final String filename;
        private final byte[] content;

        public FileInfo(String filename, byte[] content) {
            this.filename = filename;
            this.content = content;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getContent() {
            return content;
        }
    }

    /**
     * 从缓存创建的 ServletInputStream
     */
    private static class CachedServletInputStream extends ServletInputStream {

        private final InputStream inputStream;

        public CachedServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        public CachedServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public boolean isFinished() {
            try {
                return inputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("非异步请求不支持 ReadListener");
        }
    }
}