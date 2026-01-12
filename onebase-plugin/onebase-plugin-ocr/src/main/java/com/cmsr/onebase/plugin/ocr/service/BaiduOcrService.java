package com.cmsr.onebase.plugin.ocr.service;

import com.cmsr.onebase.plugin.ocr.config.OcrPluginConfig;
import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 百度 OCR 服务实现
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Slf4j
@Service
public class BaiduOcrService implements IOcrProvider {

    private static final String TOKEN_URI = "/oauth/2.0/token";
    private static final String IDCARD_URI = "/rest/2.0/ocr/v1/idcard";
    private static final String EXITENTRYPERMIT_URI = "/rest/2.0/ocr/v1/hk_macau_taiwan_exitentrypermit";
    private static final String PASSPORT_URI = "/rest/2.0/ocr/v1/passport";

    private static final Long CACHE_KEY = 0L;

    @Resource
    private OcrPluginConfig ocrConfig;

    /**
     * Access Token 缓存 (缩短为 1 天，配合被动失效机制)
     */
    private final LoadingCache<Long, String> tokenCache = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(86400L, TimeUnit.SECONDS) // 1天 (安全期，避免服务端提前改策略)
            .build(new CacheLoader<>() {
                @Override
                public String load(Long key) {
                    return fetchAccessToken();
                }
            });

    /**
     * 获取 Access Token
     *
     * @return Access Token
     */
    public String getAccessToken() {
        try {
            return tokenCache.get(CACHE_KEY);
        } catch (ExecutionException e) {
            log.error("从缓存获取百度 OCR Access Token 失败", e);
            throw new RuntimeException("获取百度 OCR Access Token 失败", e);
        }
    }

    /**
     * 从百度 API 获取 Access Token
     */
    private String fetchAccessToken() {
        String clientId = ocrConfig.getClientId();
        String clientSecret = ocrConfig.getClientSecret();
        String endpoint = ocrConfig.getEndpoint();

        if (clientId == null || clientSecret == null) {
            throw new IllegalArgumentException("百度 OCR 配置缺失: Client ID 或 Client Secret 未配置");
        }

        log.info("正在获取百度 OCR Access Token...");
        HttpResponse<JsonNode> response = Unirest.post(endpoint + TOKEN_URI)
                .contentType(ContentType.APPLICATION_FORM_URLENCODED.toString())
                .field("client_id", clientId)
                .field("client_secret", clientSecret)
                .field("grant_type", "client_credentials")
                .asJson();

        if (!response.isSuccess()) {
            throw new RuntimeException("获取百度 OCR Token 失败: " + response.getStatus() + " - " + response.getBody());
        }

        return response.getBody().getObject().getString("access_token");
    }

    /**
     * 身份证识别
     *
     * @param imageB64   图片Base64
     * @param idCardSide 身份证正反面
     * @return JSON 响应字符串
     */
    @Override
    public String recognizeIdCard(String imageB64, IdCardSideEnum idCardSide) {
        return executeWithRetry(token -> {
            String endpoint = ocrConfig.getEndpoint();
            return Unirest.post(endpoint + IDCARD_URI)
                    .contentType(ContentType.APPLICATION_FORM_URLENCODED.toString())
                    .field("access_token", token)
                    .field("image", imageB64)
                    .field("id_card_side", idCardSide.getValue())
                    .asJson();
        });
    }

    /**
     * 港澳台通行证识别
     *
     * @param imageB64            图片Base64
     * @param exitentrypermitType 通行证类型
     * @return JSON 响应字符串
     */
    @Override
    public String recognizeExitentrypermit(String imageB64, ExitentrypermitType exitentrypermitType) {
        return executeWithRetry(token -> {
            String endpoint = ocrConfig.getEndpoint();
            return Unirest.post(endpoint + EXITENTRYPERMIT_URI)
                    .contentType(ContentType.APPLICATION_FORM_URLENCODED.toString())
                    .field("access_token", token)
                    .field("image", imageB64)
                    .field("exitentrypermit_type", exitentrypermitType.getValue())
                    .asJson();
        });
    }

    /**
     * 护照识别
     *
     * @param imageB64 图片Base64
     * @return JSON 响应字符串
     */
    @Override
    public String recognizePassport(String imageB64) {
        return executeWithRetry(token -> {
            String endpoint = ocrConfig.getEndpoint();
            return Unirest.post(endpoint + PASSPORT_URI)
                    .contentType(ContentType.APPLICATION_FORM_URLENCODED.toString())
                    .field("access_token", token)
                    .field("image", imageB64)
                    .asJson();
        });
    }

    /**
     * 执行请求并处理 Token 失效重试
     *
     * @param requestFunc 请求构建函数 (参数为 access_token)
     * @return 响应体字符串
     */
    private String executeWithRetry(java.util.function.Function<String, HttpResponse<JsonNode>> requestFunc) {
        // 1. 获取当前 Token
        String token = getAccessToken();
        
        // 2. 执行首次请求
        HttpResponse<JsonNode> response = requestFunc.apply(token);
        JsonNode body = response.getBody();

        // 3. 检查是否 Token 失效
        // 110: Access Token invalid or no longer valid
        // 111: Access Token expired
        if (body != null && body.getObject().has("error_code")) {
            int errorCode = body.getObject().getInt("error_code");
            if (errorCode == 110 || errorCode == 111) {
                log.warn("[OCR 插件] 百度 Token 失效 (code={}), 正在刷新重试...", errorCode);
                
                // 4. 失效缓存并重新获取
                tokenCache.invalidate(CACHE_KEY);
                token = getAccessToken(); // 重新加载
                
                // 5. 使用新 Token 重试
                response = requestFunc.apply(token);
            }
        }

        return response.getBody().toPrettyString();
    }
}
