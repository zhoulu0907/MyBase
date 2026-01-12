package com.cmsr.onebase.plugin.ocr.service;

import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * OCR 核心业务服务
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Slf4j
@Service
public class OcrService {

    @Resource
    private OcrProviderFactory providerFactory;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 身份证识别
     *
     * @param file       身份证图片文件
     * @param idCardSide 身份证正反面
     * @return 识别结果 Map
     */
    public Map<String, Object> ocrIdCard(MultipartFile file, IdCardSideEnum idCardSide) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String imgB64 = fileToBase64(file);
            IOcrProvider provider = providerFactory.getProvider();
            String jsonRes = provider.recognizeIdCard(imgB64, idCardSide);
            return parseResult(jsonRes);
        } catch (Exception e) {
            log.error("身份证识别失败", e);
            return null;
        }
    }

    /**
     * 港澳台通行证识别
     *
     * @param file                通行证图片文件
     * @param exitentrypermitType 通行证类型
     * @return 识别结果 Map
     */
    public Map<String, Object> ocrExitentrypermit(MultipartFile file, ExitentrypermitType exitentrypermitType) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String imgB64 = fileToBase64(file);
            IOcrProvider provider = providerFactory.getProvider();
            String jsonRes = provider.recognizeExitentrypermit(imgB64, exitentrypermitType);
            return parseResult(jsonRes);
        } catch (Exception e) {
            log.error("港澳台通行证识别失败", e);
            return null;
        }
    }

    /**
     * 护照识别
     *
     * @param file 护照图片文件
     * @return 识别结果 Map
     */
    public Map<String, Object> ocrPassport(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String imgB64 = fileToBase64(file);
            IOcrProvider provider = providerFactory.getProvider();
            String jsonRes = provider.recognizePassport(imgB64);
            return parseResult(jsonRes);
        } catch (Exception e) {
            log.error("护照识别失败", e);
            return null;
        }
    }

    /**
     * 将文件转为 Base64 字符串
     *
     * @param file 文件
     * @return Base64 字符串
     * @throws IOException IO异常
     */
    private String fileToBase64(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    /**
     * 解析 JSON 结果
     *
     * @param jsonStr JSON 字符串
     * @return Map 对象
     * @throws IOException 解析异常
     */
    private Map<String, Object> parseResult(String jsonStr) throws IOException {
        if (jsonStr == null) {
            return null;
        }
        return objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
    }
}
