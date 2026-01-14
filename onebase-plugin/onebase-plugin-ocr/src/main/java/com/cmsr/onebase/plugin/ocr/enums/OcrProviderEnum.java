package com.cmsr.onebase.plugin.ocr.enums;

import lombok.Getter;

/**
 * OCR 服务商枚举
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Getter
public enum OcrProviderEnum {

    /**
     * 百度 OCR
     */
    BAIDU("baidu", "百度OCR"),

    /**
     * 阿里云 OCR
     */
    ALIYUN("aliyun", "阿里云OCR"),

    /**
     * 腾讯云 OCR
     */
    TENCENT("tencent", "腾讯云OCR");

    private final String code;
    private final String name;

    OcrProviderEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取服务商枚举
     *
     * @param code 服务商代码
     * @return 服务商枚举,未找到则返回 BAIDU
     */
    public static OcrProviderEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return BAIDU; // 默认使用百度
        }
        for (OcrProviderEnum provider : values()) {
            if (provider.getCode().equalsIgnoreCase(code.trim())) {
                return provider;
            }
        }
        return BAIDU; // 未匹配到则默认使用百度
    }
}
