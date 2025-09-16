package com.cmsr.onebase.framework.common.tools.core.io.file;

import com.cmsr.onebase.framework.common.tools.core.io.IORuntimeException;
import com.cmsr.onebase.framework.common.tools.core.io.IoUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 文件读取器
 *
 * @author Looly
 *
 */
public class FileReader extends FileWrapper {
    private static final long serialVersionUID = 1L;

    /**
     * 创建 FileReader
     * @param file 文件
     * @return FileReader
     */
    public static FileReader create(File file, Charset charset){
        return new FileReader(file, charset);
    }

    /**
     * 构造<br>
     * 编码使用 {@link FileWrapper#DEFAULT_CHARSET}
     * @param file 文件
     */
    public FileReader(File file) {
        this(file, DEFAULT_CHARSET);
    }

    /**
     * 创建 FileReader, 编码：{@link FileWrapper#DEFAULT_CHARSET}
     * @param file 文件
     * @return FileReader
     */
    public static FileReader create(File file){
        return new FileReader(file);
    }

    // ------------------------------------------------------- Constructor start
    /**
     * 构造
     * @param file 文件
     */
    public FileReader(File file, Charset charset) {
        super(file, charset);
        checkFile();
    }

    /**
     * 读取文件内容
     *
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public String readString() throws IORuntimeException {
        return new String(readBytes(), this.charset);
    }

    /**
     * 读取文件所有数据<br>
     * 文件的长度不能超过 {@link Integer#MAX_VALUE}
     *
     * @return 字节码
     * @throws IORuntimeException IO异常
     */
    public byte[] readBytes() throws IORuntimeException {
        long len = file.length();
        if (len >= Integer.MAX_VALUE) {
            throw new IORuntimeException("File is larger then max array size");
        }

        byte[] bytes = new byte[(int) len];
        FileInputStream in = null;
        int readLength;
        try {
            in = new FileInputStream(file);
            readLength = in.read(bytes);
            if(readLength < len){
                throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", len, readLength));
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtil.close(in);
        }

        return bytes;
    }

    /**
     * 检查文件
     *
     */
    private void checkFile() throws IORuntimeException {
        if (false == file.exists()) {
            throw new IORuntimeException("File not exist: " + file);
        }
        if (false == file.isFile()) {
            throw new IORuntimeException("Not a file:" + file);
        }
    }
}