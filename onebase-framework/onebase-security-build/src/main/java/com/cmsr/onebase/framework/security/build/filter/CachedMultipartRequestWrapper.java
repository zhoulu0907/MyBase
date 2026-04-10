package com.cmsr.onebase.framework.security.build.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 缓存 Multipart 请求的包装类
 *
 * <p>用于解决 multipart/form-data 请求体只能读取一次的问题。</p>
 *
 * <p>在文件上传验证场景中，需要先读取请求体验证文件类型，
 * 验证通过后再转发请求。使用此包装类可以缓存请求体，允许多次读取。</p>
 *
 * <p>使用方式：</p>
 * <pre>
 * CachedMultipartRequestWrapper wrappedRequest = new CachedMultipartRequestWrapper(request);
 * // 验证文件类型
 * if (!FileValidateUtil.validateMultipartFiles(wrappedRequest)) {
 *     // 拒绝请求
 * }
 * // 使用 wrappedRequest 进行后续处理
 * </pre>
 */
public class CachedMultipartRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存的请求体内容
     */
    private final byte[] cachedBody;

    /**
     * 构造函数，读取并缓存请求体
     *
     * @param request 原始请求
     * @throws IOException 读取请求体时发生IO异常
     */
    public CachedMultipartRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 缓存整个请求体
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    /**
     * 返回缓存的请求体输入流
     *
     * <p>每次调用都会创建新的输入流，允许多次读取</p>
     *
     * @return ServletInputStream
     */
    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(cachedBody);
    }

    /**
     * 获取缓存的请求体字节数组
     *
     * @return 请求体字节数组
     */
    public byte[] getCachedBody() {
        return cachedBody;
    }

    /**
     * 从缓存字节数组创建的 ServletInputStream
     */
    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public CachedServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return inputStream.read(b, off, len);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
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