package com.cmsr.onebase.plugin.ocr.service;

import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;

/**
 * OCR 服务接口
 * 定义统一的 OCR 识别能力,支持多种服务商实现
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
public interface IOcrProvider {

    /**
     * 身份证识别
     *
     * @param imageB64   图片Base64编码
     * @param idCardSide 身份证正反面
     * @return JSON 响应字符串
     */
    String recognizeIdCard(String imageB64, IdCardSideEnum idCardSide);

    /**
     * 港澳台通行证识别
     *
     * @param imageB64            图片Base64编码
     * @param exitentrypermitType 通行证类型
     * @return JSON 响应字符串
     */
    String recognizeExitentrypermit(String imageB64, ExitentrypermitType exitentrypermitType);

    /**
     * 护照识别
     *
     * @param imageB64 图片Base64编码
     * @return JSON 响应字符串
     */
    String recognizePassport(String imageB64);
}
