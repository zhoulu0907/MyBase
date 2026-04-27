package com.cmsr.onebase.plugin.ocr.constant;

/**
 * OCR 插件配置键常量
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
public class OcrConfigKeys {

    /**
     * OCR 服务商选择配置键
     * 可选值: baidu / aliyun / tencent
     */
    public static final String CONFIG_PROVIDER = "provider";

    /**
     * OCR 服务商 Client ID 配置键
     * 适用于百度/阿里/腾讯等服务商的 API Key / Access Key ID
     */
    public static final String CONFIG_CLIENT_ID = "client-id";

    /**
     * OCR 服务商 Client Secret 配置键
     * 适用于百度/阿里/腾讯等服务商的 Secret Key / Access Key Secret
     */
    public static final String CONFIG_CLIENT_SECRET = "client-secret";

    /**
     * OCR 服务商 API 接入点配置键
     * 不同服务商的 API 端点地址
     */
    public static final String CONFIG_ENDPOINT = "endpoint";

    /**
     * OCR 服务商 Region ID 配置键
     * 适用于阿里云等需要指定区域的服务商
     */
    public static final String CONFIG_REGION_ID = "region-id";
}
