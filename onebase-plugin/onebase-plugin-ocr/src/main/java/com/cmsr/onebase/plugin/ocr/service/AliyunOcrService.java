package com.cmsr.onebase.plugin.ocr.service;

import com.cmsr.onebase.plugin.ocr.config.OcrPluginConfig;
import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;
import com.cmsr.onebase.plugin.ocr.util.OcrSignUtil;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 阿里云 OCR 服务实现 (RPC 风格)
 */
public class AliyunOcrService {

    private static final Logger log = LoggerFactory.getLogger(AliyunOcrService.class);

    private final OcrPluginConfig config;
    private static final String DEFAULT_HOST = "ocr.cn-shanghai.aliyuncs.com";
    private static final String DEFAULT_REGION = "cn-shanghai";
    private static final String VERSION = "2019-12-30";

    public AliyunOcrService(OcrPluginConfig config) {
        this.config = config;
    }

    public String recognizeIdCard(String imageB64, IdCardSideEnum idCardSide) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "RecognizeIdentityCard");
        params.put("Side", idCardSide == IdCardSideEnum.FRONT ? "face" : "back");
        params.put("ImageContent", imageB64);
        
        return callAliyun(params);
    }

    public String recognizeExitentrypermit(String imageB64, ExitentrypermitType type) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "RecognizeExitEntryPermitToMainland");
        params.put("ImageContent", imageB64);
        return callAliyun(params);
    }

    public String recognizePassport(String imageB64) {
        Map<String, String> params = new HashMap<>();
        params.put("Action", "RecognizePassport");
        params.put("ImageContent", imageB64);
        return callAliyun(params);
    }

    private String callAliyun(Map<String, String> bizParams) {
        try {
            String accessKeyId = config.getClientId();
            String accessKeySecret = config.getClientSecret();
            String endpoint = config.getEndpoint();
            String regionId = config.getRegionId();

            if (!StringUtils.hasText(endpoint)) {
                endpoint = DEFAULT_HOST;
            }
            // 移除 http/https 前缀，如果存在
            endpoint = endpoint.replace("https://", "").replace("http://", "");

            if (!StringUtils.hasText(regionId)) {
                regionId = DEFAULT_REGION;
            }
            
            if (accessKeyId == null || accessKeySecret == null) {
                throw new RuntimeException("Aliyun OCR 配置缺失");
            }

            Map<String, String> params = new HashMap<>(bizParams);
            params.put("AccessKeyId", accessKeyId);
            params.put("Format", "JSON");
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("SignatureVersion", "1.0");
            params.put("Timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
            params.put("Version", VERSION);
            params.put("RegionId", regionId);

            String signature = OcrSignUtil.signAliyunRpc(params, accessKeySecret);
            params.put("Signature", signature);

            // Unirest fields expects Map<String, Object>
            Map<String, Object> fieldParams = new HashMap<>(params);

            HttpResponse<String> response = Unirest.post("https://" + endpoint)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .fields(fieldParams)
                    .asString();

            if (!response.isSuccess()) {
                throw new RuntimeException("Aliyun OCR Error: " + response.getBody());
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("Call Aliyun OCR failed", e);
            throw new RuntimeException(e);
        }
    }
}
