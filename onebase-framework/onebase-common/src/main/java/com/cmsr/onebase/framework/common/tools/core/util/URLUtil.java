package com.cmsr.onebase.framework.common.tools.core.util;

import com.cmsr.onebase.framework.common.tools.core.exceptions.UtilException;
import com.cmsr.onebase.framework.common.tools.core.io.FileUtil;
import com.cmsr.onebase.framework.common.tools.core.io.IORuntimeException;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.net.RFC3986;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * URL工具类
 * 用于替代 hutool 的 URLUtil 功能
 */
public class URLUtil {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";

    /**
     * URL 协议表示文件: "file"
     */
    public static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL 协议表示JBoss文件: "vfsfile"
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL 协议表示JBoss VFS资源: "vfs"
     */
    public static final String URL_PROTOCOL_VFS = "vfs";

    /**
     * 编码字符为URL中查询语句<br>
     * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
     * 此方法用于POST请求中的请求体自动编码，转义大部分特殊字符
     *
     * @param url     被编码内容
     * @param charset 编码
     * @return 编码后的字符
     * @since 4.4.1
     */
    public static String encodeQuery(String url, Charset charset) {
        return RFC3986.QUERY.encode(url, charset);
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。<br>
     * 规则见：https://url.spec.whatwg.org/#urlencoded-parsing
     *
     * @param content 被解码内容
     * @param charset 编码，null表示不解码
     * @return 编码后的字符
     * @since 4.4.1
     */
    public static String decode(String content, Charset charset) {
        return URLDecoder.decode(content, charset);
    }

    /**
     * 解码URL<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param url URL
     * @return 解码后的URL
     * @throws UtilException UnsupportedEncodingException
     * @since 3.1.2
     */
    public static String decode(String url) throws UtilException {
        return decode(url, CharsetUtil.UTF_8);
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param content URL
     * @param charset 编码
     * @return 解码后的URL
     * @throws UtilException UnsupportedEncodingException
     */
    public static String decode(String content, String charset) throws UtilException {
        return decode(content, StrUtil.isEmpty(charset) ? null : CharsetUtil.charset(charset));
    }

    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr URL字符串
     * @return URL
     * @since 4.1.9
     */
    public static URL toUrlForHttp(String urlStr) {
        return toUrlForHttp(urlStr, null);
    }

    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr  URL字符串
     * @param handler {@link URLStreamHandler}
     * @return URL
     * @since 4.1.9
     */
    public static URL toUrlForHttp(String urlStr, URLStreamHandler handler) {
        Assert.notBlank(urlStr, "Url is blank !");
        // 编码空白符，防止空格引起的请求异常
        urlStr = encodeBlank(urlStr);
        try {
            return new URL(null, urlStr, handler);
        } catch (MalformedURLException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 单独编码URL中的空白符，空白符编码为%20
     *
     * @param urlStr URL字符串
     * @return 编码后的字符串
     * @since 4.5.14
     */
    public static String encodeBlank(CharSequence urlStr) {
        if (urlStr == null) {
            return null;
        }

        int len = urlStr.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = urlStr.charAt(i);
            if (CharUtil.isBlankChar(c)) {
                sb.append("%20");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 解码URL路径
     *
     * @param url URL对象
     * @return 解码后的路径
     */
    public static String getDecodedPath(URL url) {
        if (url == null) {
            return null;
        }
        
        String path = url.getPath();
        if (path == null || path.isEmpty()) {
            return path;
        }
        
        try {
            // 使用UTF-8解码URL路径
            return URLDecoder.decode(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 如果解码失败，返回原始路径
            return path;
        }
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     * @throws IORuntimeException URL格式错误
     */
    public static URL getURL(final File file) {
        Assert.notNull(file, "File is null !");
        try {
            return file.toURI().toURL();
        } catch (final MalformedURLException e) {
            throw new IORuntimeException(e, "Error occurred when get URL!");
        }
    }

    /**
     * 将{@link URI}转换为{@link URL}
     *
     * @param uri {@link URI}
     * @return URL对象
     * @throws RuntimeException {@link MalformedURLException}包装，URI格式有问题时抛出
     * @see URI#toURL()
     * @since 5.7.21
     */
    public static URL url(final URI uri) throws RuntimeException {
        if (null == uri) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url URL
     * @return URL对象
     */
    public static URL url(String url) {
        return url(url, null);
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url     URL
     * @param handler {@link URLStreamHandler}
     * @return URL对象
     * @since 4.1.1
     */
    public static URL url(String url, URLStreamHandler handler) {
        if(null == url){
            return null;
        }

        // 兼容Spring的ClassPath路径
        if (url.startsWith(CLASSPATH_URL_PREFIX)) {
            url = url.substring(CLASSPATH_URL_PREFIX.length());
            return ClassLoaderUtil.getClassLoader().getResource(url);
        }

        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            // issue#I8PY3Y
            if(e.getMessage().contains("Accessing an URL protocol that was not enabled")){
                // Graalvm打包需要手动指定参数开启协议：
                // --enable-url-protocols=http
                // --enable-url-protocols=https
                throw new UtilException(e);
            }

            // 尝试文件路径
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new UtilException(e);
            }
        }
    }

    /**
     * 转URL为URI
     *
     * @param url URL
     * @return URI
     * @throws RuntimeException 包装URISyntaxException
     */
    public static URI toURI(final URL url) throws RuntimeException {
        return toURI(url, false);
    }

    /**
     * 转URL为URI
     *
     * @param url      URL
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     * @throws RuntimeException 包装URISyntaxException
     * @since 4.6.9
     */
    public static URI toURI(final URL url, final boolean isEncode) throws RuntimeException {
        if (null == url) {
            return null;
        }

        return toURI(url.toString(), isEncode);
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     * @throws RuntimeException 包装URISyntaxException
     * @since 4.6.9
     */
    public static URI toURI(String location, final boolean isEncode) throws RuntimeException {
        if (isEncode) {
            location = encode(location);
        }
        try {
            return new URI(StrUtil.trim(location));
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 编码URL，默认使用UTF-8编码<br>
     * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
     * 此方法用于URL自动编码，类似于浏览器中键入地址自动编码，对于像类似于“/”的字符不再编码
     *
     * @param url URL
     * @return 编码后的URL
     * @throws RuntimeException UnsupportedEncodingException
     * @since 3.1.2
     */
    public static String encode(String url) throws RuntimeException {
        return encode(url, StandardCharsets.UTF_8);
    }

    /**
     * 编码字符为 application/x-www-form-urlencoded<br>
     * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
     * 此方法用于URL自动编码，类似于浏览器中键入地址自动编码，对于像类似于“/”的字符不再编码
     *
     * @param url     被编码内容
     * @param charset 编码
     * @return 编码后的字符
     * @since 4.4.1
     */
    public static String encode(String url, Charset charset) {
        return RFC3986.PATH.encode(url, charset);
    }

    /**
     * 获取URL对应数据长度
     * <ul>
     *     <li>如果URL为文件，转换为文件获取文件长度。</li>
     *     <li>其它情况获取{@link URLConnection#getContentLengthLong()}</li>
     * </ul>
     *
     * @param url URL
     * @return 长度
     * @since 6.0.0
     */
    public static long size(final URL url) {
        if (URLUtil.isFileURL(url)) {
            // 如果资源以独立文件形式存在，尝试获取文件长度
            final File file = FileUtil.file(url);
            final long length = file.length();
            if (length == 0L && !file.exists()) {
                throw new IORuntimeException("File not exist or size is zero!");
            }
            return length;
        } else {
            // 如果资源打在jar包中或来自网络，使用网络请求长度
            // issue#3226, 来自Spring的AbstractFileResolvingResource
            try {
                final URLConnection con = url.openConnection();
                useCachesIfNecessary(con);
                if (con instanceof HttpURLConnection) {
                    final HttpURLConnection httpCon = (HttpURLConnection) con;
                    httpCon.setRequestMethod("HEAD");
                }
                return con.getContentLengthLong();
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    /**
     * 提供的URL是否为文件<br>
     * 文件协议包括"file", "vfsfile" 或 "vfs".
     *
     * @param url {@link URL}
     * @return 是否为文件
     * @since 3.0.9
     */
    public static boolean isFileURL(URL url) {
        Assert.notNull(url, "URL must be not null");
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || //
                URL_PROTOCOL_VFSFILE.equals(protocol) || //
                URL_PROTOCOL_VFS.equals(protocol));
    }

    /**
     * 如果连接为JNLP方式，则打开缓存
     *
     * @param con {@link URLConnection}
     * @since 6.0.0
     */
    public static void useCachesIfNecessary(final URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }

    /**
     * 从URL中获取流
     *
     * @param url {@link URL}
     * @return InputStream流
     * @since 3.2.1
     */
    public static InputStream getStream(final URL url) {
        Assert.notNull(url, "URL must be not null");
        try {
            return url.openStream();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
