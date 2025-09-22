package com.cmsr.onebase.framework.common.tools.core.util;


import com.cmsr.onebase.framework.common.tools.core.text.escape.XmlEscape;

/**
 * 转义和反转义工具类Escape / Unescape<br>
 * escape采用ISO Latin字符集对指定的字符串进行编码。<br>
 * 所有的空格符、标点符号、特殊字符以及其他非ASCII字符都将被转化成%xx格式的字符编码(xx等于该字符在字符集表里面的编码的16进制数字)。
 * TODO 6.x迁移到core.text.escape包下
 *
 * @author xiaoleilu
 */
public class EscapeUtil {

    /**
     * 转义XML中的特殊字符<br>
     * <pre>
     * 	 &amp; (ampersand) 替换为 &amp;amp;
     * 	 &lt; (less than) 替换为 &amp;lt;
     * 	 &gt; (greater than) 替换为 &amp;gt;
     * 	 &quot; (double quote) 替换为 &amp;quot;
     * 	 ' (single quote / apostrophe) 替换为 &amp;apos;
     * </pre>
     *
     * @param xml XML文本
     * @return 转义后的文本
     * @since 5.7.2
     */
    public static String escapeXml(CharSequence xml) {
        XmlEscape escape = new XmlEscape();
        return escape.replace(xml).toString();
    }
}