package com.cmsr.onebase.framework.security.build.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * 缓存 Multipart 请求的包装类
 *
 * <p>用于解决 multipart/form-data 请求体只能读取一次的问题。</p>
 *
 * <p>在文件上传验证场景中，需要先读取请求体验证文件类型，
 * 验证通过后再转发请求。使用此包装类可以缓存请求体，允许多次读取。</p>
 *
 * <p>此包装类会恢复真实的 Content-Type（绕过 DisableMultipartFilter 的伪装），
 * 并支持 getParts() 方法用于文件验证。</p>
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
     * 真实的 Content-Type（从原始请求获取，绕过 DisableMultipartFilter 的伪装）
     */
    private final String realContentType;

    /**
     * multipart 解析器
     */
    private final MultipartResolver multipartResolver = new StandardServletMultipartResolver();

    /**
     * 已解析的 multipart 请求（用于支持 getParts）
     */
    private HttpServletRequest multipartRequest;

    /**
     * 构造函数，读取并缓存请求体
     *
     * @param request 原始请求（可能是被 DisableMultipartFilter 包装过的）
     * @throws IOException 读取请求体时发生IO异常
     */
    public CachedMultipartRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 缓存整个请求体
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());

        // 从原始请求获取真实的 Content-Type（需要层层解开包装）
        this.realContentType = extractRealContentType(request);
    }

    /**
     * 从可能被包装的请求中提取真实的 Content-Type
     *
     * <p>解开 HttpServletRequestWrapper 包装链，找到原始请求的 Content-Type</p>
     */
    private String extractRealContentType(HttpServletRequest request) {
        HttpServletRequest current = request;

        // 层层解开包装，直到找到原始请求
        while (current instanceof HttpServletRequestWrapper) {
            // 检查当前包装类是否伪装了 Content-Type（DisableMultipartFilter 的特征）
            // 如果 getContentType() 返回 null 但原始请求有值，说明被伪装了
            HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) current;
            HttpServletRequest inner = (HttpServletRequest) wrapper.getRequest();

            // 如果 inner 请求的 Content-Type 有值，说明这是真实的 Content-Type
            if (inner != null && inner.getContentType() != null) {
                return inner.getContentType();
            }

            current = inner;
        }

        // 最终返回最外层请求的 Content-Type
        return request.getContentType();
    }

    /**
     * 返回真实的 Content-Type（恢复被伪装的 multipart 类型）
     *
     * <p>DisableMultipartFilter 会把 multipart 的 Content-Type 伪装成 null，
     * 此方法恢复真实值，用于文件验证判断。</p>
     */
    @Override
    public String getContentType() {
        return realContentType;
    }

    /**
     * 返回真实的 Content-Type header
     */
    @Override
    public String getHeader(String name) {
        if ("content-type".equalsIgnoreCase(name) || "Content-Type".equalsIgnoreCase(name)) {
            return realContentType;
        }
        return super.getHeader(name);
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
     * 获取 multipart 请求的所有 Part
     *
     * <p>用于文件验证时获取上传的文件。</p>
     *
     * @return Part 集合
     * @throws IOException 解析 multipart 时发生异常
     */
    @Override
    public Collection<Part> getParts() throws IOException {
        // 如果不是 multipart 请求，返回空集合
        if (realContentType == null || !realContentType.toLowerCase().startsWith("multipart/")) {
            return Collections.emptyList();
        }

        // 使用 Spring 的 multipart 解析器解析缓存的请求
        if (multipartRequest == null) {
            // 创建一个临时请求用于解析 multipart
            multipartRequest = multipartResolver.resolveMultipart(this);
        }
        return multipartRequest.getParts();
    }

    /**
     * 获取指定名称的 Part
     */
    @Override
    public Part getPart(String name) throws IOException {
        Collection<Part> parts = getParts();
        for (Part part : parts) {
            if (part.getName().equals(name)) {
                return part;
            }
        }
        return null;
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