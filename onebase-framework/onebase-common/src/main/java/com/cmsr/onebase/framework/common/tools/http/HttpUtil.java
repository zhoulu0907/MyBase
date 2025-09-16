package com.cmsr.onebase.framework.common.tools.http;

import com.cmsr.onebase.framework.common.tools.core.codec.Base64;
import com.cmsr.onebase.framework.common.tools.core.io.FileUtil;
import com.cmsr.onebase.framework.common.tools.core.util.CharsetUtil;
import com.cmsr.onebase.framework.common.tools.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.tools.core.util.ReUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Http请求工具类
 *
 */
public class HttpUtil {

    /**
     * 正则：Content-Type中的编码信息
     */
    public static final Pattern CHARSET_PATTERN = Pattern.compile("charset\\s*=\\s*([a-z0-9-]*)", Pattern.CASE_INSENSITIVE);
    /**
     * 正则：匹配meta标签的编码信息
     */
    public static final Pattern META_CHARSET_PATTERN = Pattern.compile("<meta[^>]*?charset\\s*=\\s*['\"]?([a-z0-9-]*)", Pattern.CASE_INSENSITIVE);

    /**
     * 根据文件扩展名获得MimeType
     *
     * @param filePath     文件路径或文件名
     * @param defaultValue 当获取MimeType为null时的默认值
     * @return MimeType
     * @since 4.6.5
     */
    public static String getMimeType(String filePath, String defaultValue) {
        return ObjUtil.defaultIfNull(getMimeType(filePath), defaultValue);
    }

    /**
     * 根据文件扩展名获得MimeType
     *
     * @param filePath 文件路径或文件名
     * @return MimeType
     * @see FileUtil#getMimeType(String)
     */
    public static String getMimeType(String filePath) {
        return FileUtil.getMimeType(filePath);
    }

    /**
     * 构建简单的账号秘密验证信息，构建后类似于：
     * <pre>
     *     Basic YWxhZGRpbjpvcGVuc2VzYW1l
     * </pre>
     *
     * @param username 账号
     * @param password 密码
     * @param charset  编码（如果账号或密码中有非ASCII字符适用）
     * @return 密码验证信息
     * @since 5.4.6
     */
    public static String buildBasicAuth(String username, String password, Charset charset) {
        final String data = username.concat(":").concat(password);
        return "Basic " + Base64.encode(data, charset);
    }

    /**
     * 从请求参数的body中判断请求的Content-Type类型，支持的类型有：
     *
     * <pre>
     * 1. application/json
     * 1. application/xml
     * </pre>
     *
     * @param body 请求参数体
     * @return Content-Type类型，如果无法判断返回null
     * @see ContentType#get(String)
     * @since 3.2.0
     */
    public static String getContentTypeByRequestBody(String body) {
        final ContentType contentType = ContentType.get(body);
        return (null == contentType) ? null : contentType.toString();
    }

    /**
     * 从流中读取内容<br>
     * 首先尝试使用charset编码读取内容（如果为空默认UTF-8），如果isGetCharsetFromContent为true，则通过正则在正文中获取编码信息，转换为指定编码；
     *
     * @param contentBytes            内容byte数组
     * @param charset                 字符集
     * @param isGetCharsetFromContent 是否从返回内容中获得编码信息
     * @return 内容
     */
    public static String getString(byte[] contentBytes, Charset charset, boolean isGetCharsetFromContent) {
        if (null == contentBytes) {
            return null;
        }

        if (null == charset) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        String content = new String(contentBytes, charset);
        if (isGetCharsetFromContent) {
            final String charsetInContentStr = ReUtil.get(META_CHARSET_PATTERN, content, 1);
            if (StrUtil.isNotBlank(charsetInContentStr)) {
                Charset charsetInContent = null;
                try {
                    charsetInContent = Charset.forName(charsetInContentStr);
                } catch (Exception e) {
                    if (StrUtil.containsIgnoreCase(charsetInContentStr, "utf-8") || StrUtil.containsIgnoreCase(charsetInContentStr, "utf8")) {
                        charsetInContent = CharsetUtil.CHARSET_UTF_8;
                    } else if (StrUtil.containsIgnoreCase(charsetInContentStr, "gbk")) {
                        charsetInContent = CharsetUtil.CHARSET_GBK;
                    }
                    // ignore
                }
                if (null != charsetInContent && false == charset.equals(charsetInContent)) {
                    content = new String(contentBytes, charsetInContent);
                }
            }
        }
        return content;
    }
    
    /**
     * 从Http连接的头信息中获得字符集<br>
     * 从ContentType中获取
     *
     * @param conn HTTP连接对象
     * @return 字符集
     */
    public static String getCharset(HttpURLConnection conn) {
        if (conn == null) {
            return null;
        }
        return getCharset(conn.getContentType());
    }

    /**
     * 从Http连接的头信息中获得字符集<br>
     * 从ContentType中获取
     *
     * @param contentType Content-Type
     * @return 字符集
     * @since 5.2.6
     */
    public static String getCharset(String contentType) {
        if (StrUtil.isBlank(contentType)) {
            return null;
        }
        return ReUtil.get(CHARSET_PATTERN, contentType, 1);
    }

    /**
     * 发送post请求<br>
     * 请求体body参数支持两种类型：
     *
     * <pre>
     * 1. 标准参数，例如 a=1&amp;b=2 这种格式
     * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
     * </pre>
     *
     * @param urlString 网址
     * @param body      post表单数据
     * @return 返回数据
     */
    public static String post(String urlString, String body) {
        return post(urlString, body, HttpGlobalConfig.getTimeout());
    }

    /**
     * 发送post请求<br>
     * 请求体body参数支持两种类型：
     *
     * <pre>
     * 1. 标准参数，例如 a=1&amp;b=2 这种格式
     * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
     * </pre>
     *
     * @param urlString 网址
     * @param body      post表单数据
     * @param timeout   超时时长，-1表示默认超时，单位毫秒
     * @return 返回数据
     * @since 3.2.0
     */
    public static String post(String urlString, String body, int timeout) {
        return HttpRequest.post(urlString).timeout(timeout).body(body).execute().body();
    }

    /**
     * 检测是否http
     *
     * @param url URL
     * @return 是否http
     * @since 5.3.8
     */
    public static boolean isHttp(String url) {
        return StrUtil.startWithIgnoreCase(url, "http:");
    }

    /**
     * 检测是否https
     *
     * @param url URL
     * @return 是否https
     */
    public static boolean isHttps(String url) {
        return StrUtil.startWithIgnoreCase(url, "https:");
    }


    /**
     * 解析URL参数字符串为Map
     *
     * @param requestBody 参数字符串
     * @param charset 字符集
     * @return 参数Map
     */
    public static Map<String, String> decodeParamMap(String requestBody, Charset charset) {
        Map<String, String> params = new java.util.HashMap<>();
        if (requestBody == null || requestBody.isEmpty()) {
            return params;
        }

        String charsetName = charset.name();
        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), charsetName);
                    String value = URLDecoder.decode(pair.substring(idx + 1), charsetName);
                    params.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    // 忽略解码失败的参数
                }
            }
        }
        return params;
    }
}
