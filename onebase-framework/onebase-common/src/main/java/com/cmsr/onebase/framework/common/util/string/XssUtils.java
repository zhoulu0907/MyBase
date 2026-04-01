package com.cmsr.onebase.framework.common.util.string;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.util.HtmlUtils;

/**
 * XSS（跨站脚本攻击）防护工具类
 * <p>
 * 提供统一的 XSS 转义方法，防止恶意脚本注入
 * </p>
 *
 * @author onebase
 * @date 2026-04-01
 */
public class XssUtils {

    /**
     * 对字符串进行 HTML 转义，防止 XSS 攻击
     * <p>
     * 转义规则：
     * - &lt; 替换 <
     * - &gt; 替换 >
     * - &amp; 替换 &
     * - &quot; 替换 "
     * - &#39; 替换 '
     * </p>
     *
     * @param input 原始字符串
     * @return 转义后的安全字符串，如果输入为空则返回空字符串
     */
    public static String escape(String input) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * 对字符串进行 HTML 转义（指定编码）
     *
     * @param input       原始字符串
     * @param encoding    字符编码（如 "UTF-8"）
     * @return 转义后的安全字符串
     */
    public static String escape(String input, String encoding) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        return HtmlUtils.htmlEscape(input, encoding);
    }

    /**
     * 对字符串进行 HTML 反转义
     *
     * @param input 转义后的字符串
     * @return 原始字符串
     */
    public static String unescape(String input) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        return HtmlUtils.htmlUnescape(input);
    }

    /**
     * 清理字符串中的潜在 XSS 危险字符
     * <p>
     * 适用于需要移除而非转义的场景
     * </p>
     *
     * @param input 原始字符串
     * @return 清理后的字符串
     */
    public static String sanitize(String input) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        // 移除常见的 XSS 攻击模式
        String result = input;
        // 移除 script 标签
        result = result.replaceAll("<script[^>]*>.*?</script>", "");
        result = result.replaceAll("<script[^>]*>", "");
        result = result.replaceAll("</script>", "");
        // 移除 javascript: 协议
        result = result.replaceAll("javascript:", "");
        // 移除 onxxx 事件属性
        result = result.replaceAll("on\\w+\\s*=", "");
        // 移除 data: 协议（防止 data URI XSS）
        result = result.replaceAll("data:", "");
        // 移除 vbscript: 协议
        result = result.replaceAll("vbscript:", "");
        return result.trim();
    }

    /**
     * 检查字符串是否包含潜在的 XSS 攻击内容
     *
     * @param input 待检查字符串
     * @return true 表示包含潜在危险内容
     */
    public static boolean containsXss(String input) {
        if (StrUtil.isEmpty(input)) {
            return false;
        }
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("<script") ||
               lowerInput.contains("javascript:") ||
               lowerInput.contains("onload=") ||
               lowerInput.contains("onerror=") ||
               lowerInput.contains("onclick=") ||
               lowerInput.contains("onmouseover=") ||
               lowerInput.contains("<iframe") ||
               lowerInput.contains("<object") ||
               lowerInput.contains("<embed") ||
               lowerInput.contains("expression(") ||
               lowerInput.contains("vbscript:") ||
               lowerInput.contains("data:");
    }
}