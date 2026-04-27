package com.cmsr.onebase.framework.common.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * IO 工具类，用于 {@link com.cmsr.onebase.framework.common.tools.core.io.IoUtil} 缺失的方法
 */
public class IoUtils {

    /**
     * 从流中读取 UTF8 编码的内容
     */
    public static String readUtf8(InputStream in, boolean isClose) throws IOException {
        try {
            return org.apache.commons.io.IOUtils.toString(in, StandardCharsets.UTF_8);
        } finally {
            if (isClose) {
                org.apache.commons.io.IOUtils.closeQuietly(in);
            }
        }
    }

}
