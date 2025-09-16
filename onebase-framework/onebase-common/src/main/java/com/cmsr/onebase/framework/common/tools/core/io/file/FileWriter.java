package com.cmsr.onebase.framework.common.tools.core.io.file;

import com.cmsr.onebase.framework.common.tools.core.io.FileUtil;
import com.cmsr.onebase.framework.common.tools.core.io.IORuntimeException;
import com.cmsr.onebase.framework.common.tools.core.io.IoUtil;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.charset.Charset;

/**
 * 文件写入器
 *
 * @author Looly
 */
public class FileWriter extends FileWrapper {
    private static final long serialVersionUID = 1L;

    /**
     * 创建 FileWriter
     *
     * @param file    文件
     * @return FileWriter
     */
    public static FileWriter create(File file, Charset charset) {
        return new FileWriter(file, charset);
    }

    /**
     * 创建 FileWriter, 编码：{@link FileWrapper#DEFAULT_CHARSET}
     *
     * @param file 文件
     * @return FileWriter
     */
    public static FileWriter create(File file) {
        return new FileWriter(file);
    }

    /**
     * 构造<br>
     * 编码使用 {@link FileWrapper#DEFAULT_CHARSET}
     *
     * @param file 文件
     */
    public FileWriter(File file) {
        this(file, DEFAULT_CHARSET);
    }

    // ------------------------------------------------------- Constructor start

    /**
     * 构造
     *
     * @param file    文件
     */
    public FileWriter(File file, Charset charset) {
        super(file, charset);
        checkFile();
    }

    /**
     * 检查文件
     *
     * @throws IORuntimeException IO异常
     */
    private void checkFile() throws IORuntimeException {
        Assert.notNull(file, "File to write content is null !");
        if (this.file.exists() && false == file.isFile()) {
            throw new IORuntimeException("File [{}] is not a file !", this.file.getAbsoluteFile());
        }
    }

    /**
     * 写入数据到文件
     *
     * @param data     数据
     * @param off      数据开始位置
     * @param len      数据长度
     * @param isAppend 是否追加模式
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(byte[] data, int off, int len, boolean isAppend) throws IORuntimeException {
        try (FileOutputStream out = new FileOutputStream(FileUtil.touch(file), isAppend)) {
            out.write(data, off, len);
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return file;
    }

    /**
     * 将String写入文件，覆盖模式
     *
     * @param content 写入的内容
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(String content) throws IORuntimeException {
        return write(content, false);
    }

    /**
     * 将String写入文件
     *
     * @param content  写入的内容
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(String content, boolean isAppend) throws IORuntimeException {
        BufferedWriter writer = null;
        try {
            writer = getWriter(isAppend);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtil.close(writer);
        }
        return file;
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param isAppend 是否追加
     * @return BufferedReader对象
     * @throws IORuntimeException IO异常
     */
    public BufferedWriter getWriter(boolean isAppend) throws IORuntimeException {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.touch(file), isAppend), charset));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

}