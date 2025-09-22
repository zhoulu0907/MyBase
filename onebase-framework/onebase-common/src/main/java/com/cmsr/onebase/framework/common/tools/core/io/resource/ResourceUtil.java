package com.cmsr.onebase.framework.common.tools.core.io.resource;



import com.cmsr.onebase.framework.common.tools.core.io.FileUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;
import com.cmsr.onebase.framework.common.tools.core.util.URLUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Resource资源工具类
 *
 */
public class ResourceUtil {

    /**
     * 读取Classpath下的资源为byte[]
     *
     * @param resource 可以是绝对路径，也可以是相对路径（相对ClassPath）
     * @return 资源内容
     * @since 4.5.19
     */
    public static byte[] readBytes(String resource) {
        return getResourceObj(resource).readBytes();
    }

    /**
     * 从类路径读取资源字节数组
     *
     * @param resourcePath 资源路径
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] readBytesFromClasspath(String resourcePath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return inputStream.readAllBytes();
        }
    }

    /**
     * 获取资源URL
     *
     * @param path 资源路径
     * @param baseClass 基础类
     * @return 资源URL
     */
    public static URL getResource(String path, Class<?> baseClass) {
        // 如果指定了基础类，优先使用该类的类加载器
        if (baseClass != null) {
            return baseClass.getResource(path);
        }

        // 使用当前线程上下文类加载器
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            URL url = contextClassLoader.getResource(path);
            if (url != null) {
                return url;
            }
        }

        // 使用默认类加载器
        return ResourceUtil.class.getClassLoader().getResource(path);
    }

    /**
     * 获取资源URL
     *
     * @param path 资源路径
     * @return 资源URL
     */
    public static URL getResource(String path) {
        return getResource(path, null);
    }

    /**
     * 获取{@link FileResource} 资源对象
     *
     * @param file {@link File}
     * @return {@link Resource} 资源对象
     * @since 6.0.0
     */
    public static Resource getResource(final File file) {
        return new FileResource(file);
    }

    /**
     * 获取{@link Resource} 资源对象<br>
     * 如果提供路径为绝对路径或路径以file:开头，返回{@link FileResource}，否则返回{@link ClassPathResource}
     *
     * @param path 路径，可以是绝对路径，也可以是相对路径（相对ClassPath）
     * @return {@link Resource} 资源对象
     * @since 3.2.1
     */
    public static Resource getResourceObj(String path) {
        if (StrUtil.isNotBlank(path)) {
            if (path.startsWith(URLUtil.FILE_URL_PREFIX) || FileUtil.isAbsolutePath(path)) {
                return new FileResource(path);
            }
        }
        return new ClassPathResource(path);
    }


}
