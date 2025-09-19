package com.cmsr.onebase.framework.common.tools.core.util;


import com.cmsr.onebase.framework.common.tools.core.codec.Base16Codec;

import java.nio.charset.Charset;

/**
 * 十六进制（简写为hex或下标16）在数学中是一种逢16进1的进位制，一般用数字0到9和字母A到F表示（其中:A~F即10~15）。<br>
 * 例如十进制数57，在二进制写作111001，在16进制写作39。<br>
 * 像java,c这样的语言为了区分十六进制和十进制数值,会在十六进制数的前面加上 0x,比如0x20是十进制的32,而不是十进制的20<br>
 * <p>
 * 参考：https://my.oschina.net/xinxingegeya/blog/287476
 *
 * @author Looly
 */
public class HexUtil {

    /**
     * 将byte值转为16进制并添加到{@link StringBuilder}中
     *
     * @param builder     {@link StringBuilder}
     * @param b           byte
     * @param toLowerCase 是否使用小写
     * @since 4.4.1
     */
    public static void appendHex(StringBuilder builder, byte b, boolean toLowerCase) {
        (toLowerCase ? Base16Codec.CODEC_LOWER : Base16Codec.CODEC_UPPER).appendHex(builder, b);
    }

    /**
     * 将指定char值转换为Unicode字符串形式，常用于特殊字符（例如汉字）转Unicode形式<br>
     * 转换的字符串如果u后不足4位，则前面用0填充，例如：
     *
     * <pre>
     * '你' =》'\u4f60'
     * </pre>
     *
     * @param ch char值
     * @return Unicode表现形式
     * @since 4.0.1
     */
    public static String toUnicodeHex(char ch) {
        return Base16Codec.CODEC_LOWER.toUnicodeHex(ch);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data        byte[]
     * @param toLowerCase {@code true} 传换成小写格式 ， {@code false} 传换成大写格式
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }

    /**
     * 将字符串转换为十六进制字符串，结果为小写
     *
     * @param data    需要被编码的字符串
     * @param charset 编码
     * @return 十六进制String
     */
    public static String encodeHexStr(String data, Charset charset) {
        return encodeHexStr(StrUtil.bytes(data, charset), true);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data        byte[]
     * @param toLowerCase {@code true} 传换成小写格式 ， {@code false} 传换成大写格式
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return (toLowerCase ? Base16Codec.CODEC_LOWER : Base16Codec.CODEC_UPPER).encode(data);
    }

    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param hexData 十六进制char[]
     * @return byte[]
     * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
     */
    public static byte[] decodeHex(char[] hexData) {
        return decodeHex(String.valueOf(hexData));
    }

    /**
     * 将十六进制字符串解码为byte[]
     *
     * @param hexStr 十六进制String
     * @return byte[]
     */
    public static byte[] decodeHex(String hexStr) {
        return decodeHex((CharSequence) hexStr);
    }

    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param hexData 十六进制字符串
     * @return byte[]
     * @since 5.6.6
     */
    public static byte[] decodeHex(CharSequence hexData) {
        return Base16Codec.CODEC_LOWER.decode(hexData);
    }

    /**
     * 将十六进制字符数组转换为字符串
     *
     * @param hexStr  十六进制String
     * @param charset 编码
     * @return 字符串
     */
    public static String decodeHexStr(String hexStr, Charset charset) {
        if (StrUtil.isEmpty(hexStr)) {
            return hexStr;
        }
        return StrUtil.str(decodeHex(hexStr), charset);
    }
}