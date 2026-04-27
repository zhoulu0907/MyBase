package com.cmsr.onebase.module.infra.framework.file.core.utils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class LightweightPdfXssDetector {
    
    // PDF中常见的JavaScript相关关键词（二进制形式）
    private static final byte[][] MALICIOUS_PATTERNS = {
        "/JavaScript".getBytes(StandardCharsets.UTF_8),
        "/JS".getBytes(StandardCharsets.UTF_8),
        "/OpenAction".getBytes(StandardCharsets.UTF_8),
        "/AA".getBytes(StandardCharsets.UTF_8),
        "javascript:".getBytes(StandardCharsets.ISO_8859_1),
        "onload".getBytes(StandardCharsets.ISO_8859_1),
        "onerror".getBytes(StandardCharsets.ISO_8859_1),
        "eval(".getBytes(StandardCharsets.ISO_8859_1),
        "<script".getBytes(StandardCharsets.ISO_8859_1),
        "</script>".getBytes(StandardCharsets.ISO_8859_1)
    };
    
    /**
     * 轻量级二进制扫描
     */
    public static boolean hasPdfXssContent(byte[] content) {
        
        // 扫描二进制内容中的恶意模式
        for (byte[] pattern : MALICIOUS_PATTERNS) {
            if (containsBytes(content, pattern)) {
                return true;
            }
        }
        
        // 转换为ASCII字符串进行二次检查
        String asciiContent = bytesToAscii(content);
        Pattern xssPattern = Pattern.compile(
            "(?i)(<script|javascript:|onload\\s*=|onerror\\s*=|eval\\s*\\(|document\\.|window\\.|alert\\s*\\()",
            Pattern.CASE_INSENSITIVE
        );
        
        return xssPattern.matcher(asciiContent).find();
    }
    
    /**
     * 在字节数组中搜索子数组
     */
    private static boolean containsBytes(byte[] source, byte[] target) {
        if (target.length == 0) return false;
        
        outer:
        for (int i = 0; i <= source.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * 将字节转换为ASCII字符串（非UTF-8）
     */
    private static String bytesToAscii(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 只保留可打印的ASCII字符
            if (b >= 32 && b <= 126) {
                sb.append((char) b);
            } else {
                sb.append(' '); // 非打印字符替换为空格
            }
        }
        return sb.toString();
    }
}