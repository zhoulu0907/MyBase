package com.cmsr.onebase.framework.common.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;

/**
 * ZIP工具类
 *
 * @author zhoumingji
 * @date 2025-01-12
 */
public class ZipUtils {

    private ZipUtils() {
    }

    /**
     * 将对象序列化为JSON并写入ZIP文件
     *
     * @param zos       ZIP输出流
     * @param entryName ZIP条目名称
     * @param data      要写入的数据
     * @throws IOException IO异常
     */
    public static void writeJsonToZip(ZipOutputStream zos, String entryName, Object data) throws IOException {
        if (data == null) {
            return;
        }
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        String jsonContent = JsonUtils.toJsonPrettyString(data);
        zos.write(jsonContent.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * 读取ZIP压缩包所有条目，返回名称与字节内容映射
     *
     * @param inputStream ZIP输入流
     * @return 条目名称与内容映射
     * @throws IOException 读取异常
     */
    public static Map<String, byte[]> readZipEntries(InputStream inputStream) throws IOException {
        Map<String, byte[]> entryMap = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;
                while ((len = zis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                entryMap.put(entry.getName(), bos.toByteArray());
            }
        }
        return entryMap;
    }

    /**
     * 将字节数组转换为UTF-8字符串（空值或空内容返回null）
     *
     * @param bytes 字节内容
     * @return 字符串或null
     */
    public static String toUtf8String(byte[] bytes) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return null;
        }
        String value = new String(bytes, StandardCharsets.UTF_8);
        return value.isBlank() ? null : value;
    }
}
