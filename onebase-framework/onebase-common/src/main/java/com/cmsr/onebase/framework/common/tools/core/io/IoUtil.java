package com.cmsr.onebase.framework.common.tools.core.io;


import com.cmsr.onebase.framework.common.tools.core.convert.Convert;
import com.cmsr.onebase.framework.common.tools.core.exceptions.UtilException;
import com.cmsr.onebase.framework.common.tools.core.io.copy.StreamCopier;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.util.CharsetUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * IO工具类<br>
 * IO工具类只是辅助流的读写，并不负责关闭流。原因是流可能被多次读写，读写关闭后容易造成问题。
 *
 * @author xiaoleilu
 */
public class IoUtil {

    /**
     * 默认缓存大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;

    /**
     * 获得一个文件读取器，默认使用UTF-8编码
     *
     * @param in 输入流
     * @return BufferedReader对象
     * @since 5.1.6
     */
    public static BufferedReader getUtf8Reader(InputStream in) {
        return getReader(in, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 从流中读取对象，即对象的反序列化
     *
     * <p>
     * 注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
     * </p>
     *
     * @param <T> 读取对象的类型
     * @param in  输入流
     * @return 输出流
     * @throws IORuntimeException IO异常
     * @throws UtilException      ClassNotFoundException包装
     */
    public static <T> T readObj(InputStream in) throws IORuntimeException, UtilException {
        return readObj(in, null);
    }

    /**
     * 从流中读取对象，即对象的反序列化，读取后不关闭流
     *
     * <p>
     * 注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
     * </p>
     *
     * @param <T>   读取对象的类型
     * @param in    输入流
     * @param clazz 读取对象类型
     * @return 输出流
     * @throws IORuntimeException IO异常
     * @throws UtilException      ClassNotFoundException包装
     */
    public static <T> T readObj(InputStream in, Class<T> clazz) throws IORuntimeException, UtilException {
        try {
            return readObj((in instanceof ValidateObjectInputStream) ?
                            (ValidateObjectInputStream) in : new ValidateObjectInputStream(in),
                    clazz);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容
     * @throws IORuntimeException IO异常
     */
    public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents) throws IORuntimeException {
        ObjectOutputStream osw = null;
        try {
            osw = out instanceof ObjectOutputStream ? (ObjectOutputStream) out : new ObjectOutputStream(out);
            for (Object content : contents) {
                if (content != null) {
                    osw.writeObject(content);
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                close(osw);
            }
        }
    }

    /**
     * 从流中读取内容，读取完毕后关闭流
     *
     * @param in      输入流，读取完毕后关闭流
     * @param charset 字符集
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String read(InputStream in, Charset charset) throws IORuntimeException {
        return StrUtil.str(readBytes(in), charset);
    }

    /**
     * 从Reader中读取String，读取完毕后关闭Reader
     *
     * @param reader Reader
     * @return String
     * @throws RuntimeException IO异常
     */
    public static String read(Reader reader) throws RuntimeException {
        return read(reader, true);
    }

    /**
     * 从{@link Reader}中读取String
     *
     * @param reader  {@link Reader}
     * @param isClose 是否关闭{@link Reader}
     * @return String
     */
    public static String read(Reader reader, boolean isClose) throws RuntimeException {
        final StringBuilder builder = StrUtil.builder();
        final CharBuffer buffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
        try {
            while (-1 != reader.read(buffer)) {
                builder.append(buffer.flip());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (isClose) {
                IoUtil.close(reader);
            }
        }
        return builder.toString();
    }

    /**
     * 从流中读取bytes，读取完毕后关闭流
     *
     * @param in {@link InputStream}
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in) throws RuntimeException {
        return readBytes(in, true);
    }

    /**
     * 从流中读取bytes
     *
     * @param in      {@link InputStream}
     * @param isClose 是否关闭输入流
     * @return bytes
     * @throws IORuntimeException IO异常
     * @since 5.0.4
     */
    public static byte[] readBytes(InputStream in, boolean isClose) throws RuntimeException {
        return read(in, isClose).toByteArray();
    }

    /**
     * 从流中读取内容，读到输出流中，读取完毕后可选是否关闭流
     *
     * @param in      输入流
     * @param isClose 读取完毕后是否关闭流
     * @return 输出流
     * @throws IORuntimeException IO异常
     * @since 5.5.3
     */
    public static FastByteArrayOutputStream read(InputStream in, boolean isClose) throws RuntimeException {
        final FastByteArrayOutputStream out;
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            try {
                out = new FastByteArrayOutputStream(in.available());
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } else {
            out = new FastByteArrayOutputStream();
        }
        try {
            copy(in, out);
        } finally {
            if (isClose) {
                close(in);
            }
        }
        return out;
    }

    /**
     * 拷贝流，使用默认Buffer大小，拷贝后不关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws IORuntimeException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IORuntimeException {
        return copy(in, out, bufferSize, null);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
        return copy(in, out, bufferSize, -1, streamProgress);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param count          总拷贝长度
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     * @since 5.7.8
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
        return new StreamCopier(bufferSize, count, streamProgress).copy(in, out);
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    /**
     * 将byte[]写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content    写入的内容
     * @throws IORuntimeException IO异常
     */
    public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IORuntimeException {
        try {
            out.write(content);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                close(out);
            }
        }
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out        输出流
     * @param charset    写出的内容的字符集
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IORuntimeException IO异常
     * @since 3.0.9
     */
    public static void write(OutputStream out, Charset charset, boolean isCloseOut, Object... contents) throws IORuntimeException {
        OutputStreamWriter osw = null;
        try {
            osw = getWriter(out, charset);
            for (Object content : contents) {
                if (content != null) {
                    osw.write(Convert.toStr(content, StrUtil.EMPTY));
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                close(osw);
            }
        }
    }

    /**
     * 获得一个Writer
     *
     * @param out     输入流
     * @param charset 字符集
     * @return OutputStreamWriter对象
     */
    public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
        if (null == out) {
            return null;
        }

        if (null == charset) {
            return new OutputStreamWriter(out);
        } else {
            return new OutputStreamWriter(out, charset);
        }
    }

    /**
     * 按照给定顺序连续关闭一系列对象<br>
     * 这些对象必须按照顺序关闭，否则会出错。
     *
     * @param closeables 需要关闭的对象
     */
    public static void closeQuietly(final AutoCloseable... closeables) {
        for (final AutoCloseable closeable : closeables) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (final Exception e) {
                    // 静默关闭
                }
            }
        }
    }



    /**
     * 获得一个Reader
     *
     * @param in      输入流
     * @param charset 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader toReader(final InputStream in, final Charset charset) {
        if (null == in) {
            return null;
        }

        final InputStreamReader reader;
        if (null == charset) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }

        return new BufferedReader(reader);
    }

    /**
     * 获得一个Reader
     *
     * @param in      输入流
     * @param charset 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader getReader(InputStream in, Charset charset) {
        if (null == in) {
            return null;
        }

        InputStreamReader reader;
        if (null == charset) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }

        return new BufferedReader(reader);
    }

    /**
     * 获得{@link BufferedReader}<br>
     * 如果是{@link BufferedReader}强转返回，否则新建。如果提供的Reader为null返回null
     *
     * @param reader 普通Reader，如果为null返回null
     * @return {@link BufferedReader} or null
     * @since 3.0.9
     */
    public static BufferedReader getReader(Reader reader) {
        if (null == reader) {
            return null;
        }

        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * 转换为{@link BufferedInputStream}
     *
     * @param in {@link InputStream}
     * @return {@link BufferedInputStream}
     * @since 4.0.10
     */
    public static BufferedInputStream toBuffered(final InputStream in) {
        Assert.notNull(in, "InputStream must be not null!");
        return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
    }

    /**
     * 转换为{@link BufferedOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link BufferedOutputStream}
     * @since 4.0.10
     */
    public static BufferedOutputStream toBuffered(OutputStream out) {
        Assert.notNull(out, "OutputStream must be not null!");
        return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out);
    }

    /**
     * 文件转为{@link InputStream}
     *
     * @param file 文件，非空
     * @return {@link InputStream}
     */
    public static InputStream toStream(final File file) {
        Assert.notNull(file);
        return toStream(file.toPath());
    }

    /**
     * byte[] 转为{@link ByteArrayInputStream}
     *
     * @param content 内容bytes
     * @return 字节流
     * @since 4.1.8
     */
    public static ByteArrayInputStream toStream(byte[] content) {
        if (content == null) {
            return null;
        }
        return new ByteArrayInputStream(content);
    }

    /**
     * 文件转为{@link InputStream}
     *
     * @param path {@link Path}，非空
     * @return {@link InputStream}
     */
    public static InputStream toStream(final Path path) {
        Assert.notNull(path);
        try {
            return Files.newInputStream(path);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * {@link ByteArrayOutputStream} 转换为String
     * @param out {@link ByteArrayOutputStream}
     * @param charset 编码
     * @return 字符串
     * @since 5.7.17
     */
    public static String toStr(ByteArrayOutputStream out, Charset charset){
        try {
            return out.toString(charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new IORuntimeException(e);
        }
    }

}
