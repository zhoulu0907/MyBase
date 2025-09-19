package com.cmsr.onebase.framework.common.tools.core.util;

import com.cmsr.onebase.framework.common.tools.core.exceptions.UtilException;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

/**
 * XML工具类<br>
 * 此工具使用w3c dom工具，不需要依赖第三方包。<br>
 * 工具类封装了XML文档的创建、读取、写出和部分XML操作
 *
 * @author xiaoleilu
 */
public class XmlUtil {

    /**
     * 字符串常量：XML 不间断空格转义 {@code "&nbsp;" -> " "}
     */
    public static final String NBSP = "&nbsp;";

    /**
     * 字符串常量：XML And 符转义 {@code "&amp;" -> "&"}
     */
    public static final String AMP = "&amp;";

    /**
     * 字符串常量：XML 双引号转义 {@code "&quot;" -> "\""}
     */
    public static final String QUOTE = "&quot;";

    /**
     * 字符串常量：XML 单引号转义 {@code "&apos" -> "'"}
     */
    public static final String APOS = "&apos;";

    /**
     * 字符串常量：XML 小于号转义 {@code "&lt;" -> "<"}
     */
    public static final String LT = "&lt;";

    /**
     * 字符串常量：XML 大于号转义 {@code "&gt;" -> ">"}
     */
    public static final String GT = "&gt;";

    /**
     * 在XML中无效的字符 正则
     */
    public static final String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";
    /**
     * 在XML中注释的内容 正则
     */
    public static final String COMMENT_REGEX = "(?s)<!--.+?-->";
    /**
     * XML格式化输出默认缩进量
     */
    public static final int INDENT_DEFAULT = 2;

    /**
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8<br>
     *
     * @param doc XML文档
     * @return XML字符串
     * @since 5.4.5
     */
    public static String toStr(Node doc) {
        return toStr(doc, false);
    }

    /**
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
     *
     * @param doc      XML文档
     * @param isPretty 是否格式化输出
     * @return XML字符串
     * @since 5.4.5
     */
    public static String toStr(Node doc, boolean isPretty) {
        return toStr(doc, CharsetUtil.UTF_8, isPretty);
    }

    /**
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
     *
     * @param doc      XML文档
     * @param charset  编码
     * @param isPretty 是否格式化输出
     * @return XML字符串
     * @since 5.4.5
     */
    public static String toStr(Node doc, String charset, boolean isPretty) {
        return toStr(doc, charset, isPretty, false);
    }

    /**
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
     *
     * @param doc                XML文档
     * @param charset            编码
     * @param isPretty           是否格式化输出
     * @param omitXmlDeclaration 是否忽略 xml Declaration
     * @return XML字符串
     * @since 5.1.2
     */
    public static String toStr(Node doc, String charset, boolean isPretty, boolean omitXmlDeclaration) {
        final StringWriter writer = StrUtil.getWriter();
        try {
            write(doc, writer, charset, isPretty ? INDENT_DEFAULT : 0, omitXmlDeclaration);
        } catch (Exception e) {
            throw new UtilException(e, "Trans xml document to string error!");
        }
        return writer.toString();
    }

    /**
     * 将XML文档写出
     *
     * @param node               {@link Node} XML文档节点或文档本身
     * @param writer             写出的Writer，Writer决定了输出XML的编码
     * @param charset            编码
     * @param indent             格式化输出中缩进量，小于1表示不格式化输出
     * @param omitXmlDeclaration 是否忽略 xml Declaration
     * @since 5.1.2
     */
    public static void write(Node node, Writer writer, String charset, int indent, boolean omitXmlDeclaration) {
        transform(new DOMSource(node), new StreamResult(writer), charset, indent, omitXmlDeclaration);
    }

    /**
     * 将XML文档写出<br>
     * 格式化输出逻辑参考：https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
     *
     * @param source             源
     * @param result             目标
     * @param charset            编码
     * @param indent             格式化输出中缩进量，小于1表示不格式化输出
     * @param omitXmlDeclaration 是否忽略 xml Declaration
     * @since 5.1.2
     */
    public static void transform(Source source, Result result, String charset, int indent, boolean omitXmlDeclaration) {
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            final Transformer xformer = factory.newTransformer();
            if (indent > 0) {
                xformer.setOutputProperty(OutputKeys.INDENT, "yes");
                //fix issue#1232@Github
                xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
                xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            }
            if (StrUtil.isNotBlank(charset)) {
                xformer.setOutputProperty(OutputKeys.ENCODING, charset);
            }
            if (omitXmlDeclaration) {
                xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            xformer.transform(source, result);
        } catch (Exception e) {
            throw new UtilException(e, "Trans xml document to string error!");
        }
    }

}