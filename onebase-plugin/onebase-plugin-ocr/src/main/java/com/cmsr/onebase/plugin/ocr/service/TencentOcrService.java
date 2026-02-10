package com.cmsr.onebase.plugin.ocr.service;

import com.cmsr.onebase.plugin.ocr.config.OcrPluginConfig;
import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;
import com.cmsr.onebase.plugin.ocr.util.OcrSignUtil;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 腾讯云 OCR 服务实现 (TC3-HMAC-SHA256)
 */
public class TencentOcrService {

    private static final Logger log = LoggerFactory.getLogger(TencentOcrService.class);

    private final OcrPluginConfig config;
    private static final String DEFAULT_HOST = "ocr.tencentcloudapi.com";
    private static final String SERVICE = "ocr";
    private static final String VERSION = "2018-11-19";

    public TencentOcrService(OcrPluginConfig config) {
        this.config = config;
    }

    public String recognizeIdCard(String imageB64, IdCardSideEnum idCardSide) {
        JSONObject payload = new JSONObject();
        payload.put("ImageBase64", imageB64);
        payload.put("CardSide", idCardSide == IdCardSideEnum.FRONT ? "FRONT" : "BACK");
        return callTencent("IDCardOCR", payload.toString());
    }

    public String recognizeExitentrypermit(String imageB64, ExitentrypermitType type) {
        JSONObject payload = new JSONObject();
        payload.put("ImageBase64", imageB64);
        return callTencent("PermitOCR", payload.toString());
    }

    public String recognizePassport(String imageB64) {
        JSONObject payload = new JSONObject();
        payload.put("ImageBase64", imageB64);
        return callTencent("PassportOCR", payload.toString());
    }

    private String callTencent(String action, String jsonPayload) {
        try {
            String secretId = config.getClientId();
            String secretKey = config.getClientSecret();
            String host = config.getEndpoint();
            
            if (!StringUtils.hasText(host)) {
                host = DEFAULT_HOST;
            }
            // 移除 http/https 前缀
            host = host.replace("https://", "").replace("http://", "");
            
            if (secretId == null || secretKey == null) {
                throw new RuntimeException("Tencent OCR 配置缺失");
            }
            
            long timestamp = System.currentTimeMillis() / 1000;

            String authorization = OcrSignUtil.signTencentV3(
                    secretId, secretKey, SERVICE, host, action, VERSION, jsonPayload, timestamp
            );

            HttpResponse<String> response = Unirest.post("https://" + host)
                    .header("Content-Type", "application/json")
                    .header("Host", host)
                    .header("X-TC-Action", action)
                    .header("X-TC-Version", VERSION)
                    .header("X-TC-Timestamp", String.valueOf(timestamp))
                    .header("Authorization", authorization)
                    .body(jsonPayload)
                    .asString();

            if (!response.isSuccess()) {
                throw new RuntimeException("Tencent OCR Error: " + response.getBody());
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("Call Tencent OCR failed", e);
            throw new RuntimeException(e);
        }
    }
}
