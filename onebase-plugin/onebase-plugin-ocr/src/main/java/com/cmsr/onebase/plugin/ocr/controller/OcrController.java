package com.cmsr.onebase.plugin.ocr.controller;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.ocr.config.OcrPluginConfig;
import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;
import com.cmsr.onebase.plugin.ocr.service.BaiduOcrService;
import com.cmsr.onebase.plugin.ocr.service.OcrProviderFactory;
import com.cmsr.onebase.plugin.ocr.service.OcrService;
import com.cmsr.onebase.plugin.service.PluginContextService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * OCR 插件 HTTP 接口处理器
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Slf4j
@RestController
@RequestMapping("/plugin/onebase-plugin-ocr")
public class OcrController implements HttpHandler {

    private OcrService ocrService; // 手动管理

    @Resource
    private PluginContextService pluginContextService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 获取或初始化 OcrService (懒加载)
     */
    private synchronized OcrService getOcrService() {
        if (this.ocrService != null) {
            return this.ocrService;
        }

        log.info("[OCR 插件] 正在初始化 OcrService...");

        if (this.pluginContextService == null) {
            log.error("[OCR 插件] 致命错误: PluginContextService 未注入!");
            throw new RuntimeException("PluginContextService Dependency Injection Failed");
        }
        if (this.objectMapper == null) {
            log.error("[OCR 插件] 致命错误: ObjectMapper 未注入!");
            // 尝试降级，手动创建 (虽然可能缺少全局配置)
            this.objectMapper = new ObjectMapper();
        }

        try {
            OcrPluginConfig config = new OcrPluginConfig(pluginContextService);
            BaiduOcrService baiduService = new BaiduOcrService(config);
            OcrProviderFactory factory = new OcrProviderFactory(config, baiduService);
            this.ocrService = new OcrService(factory, objectMapper);
            log.info("[OCR 插件] OcrService 初始化成功");
        } catch (Exception e) {
            log.error("[OCR 插件] OcrService 初始化失败", e);
            throw e;
        }

        return this.ocrService;
    }

    /**
     * 身份证识别接口
     * POST /plugin/onebase-plugin-ocr/id-card
     *
     * @param frontFile 身份证正面文件 (可选)
     * @param backFile  身份证反面文件 (可选)
     * @return 识别结果
     */
    @PostMapping("/id-card")
    public Map<String, Object> ocrIdCardRecognition(
            @RequestPart(name = "frontFile", required = false) MultipartFile frontFile,
            @RequestPart(name = "backFile", required = false) MultipartFile backFile
    ) {
        if ((frontFile == null || frontFile.isEmpty()) && (backFile == null || backFile.isEmpty())) {
            return createErrorResponse(400, "未传入文件");
        }

        OcrService service = getOcrService();

        Map<String, Object> frontResp = null;
        if (frontFile != null && !frontFile.isEmpty()) {
            frontResp = service.recognizeIdCard(frontFile, IdCardSideEnum.FRONT);
        }

        Map<String, Object> backResp = null;
        if (backFile != null && !backFile.isEmpty()) {
            backResp = service.recognizeIdCard(backFile, IdCardSideEnum.BACK);
        }

        Map<String, Object> resultMap = new HashMap<>();
        if (frontResp != null) {
            resultMap.put("front", frontResp);
        }
        if (backResp != null) {
            resultMap.put("back", backResp);
        }

        if (MapUtils.isEmpty(resultMap)) {
            return createErrorResponse(500, "数据解析失败");
        }

        return createSuccessResponse(resultMap);
    }

    /**
     * 港澳台通行证识别接口
     * POST /plugin/onebase-plugin-ocr/exitentrypermit
     *
     * @param frontFile           正面文件 (可选)
     * @param backFile            反面文件 (可选)
     * @param exitentrypermitType 通行证类型 (必填)
     * @return 识别结果
     */
    @PostMapping("/exitentrypermit")
    public Map<String, Object> ocrExitentrypermitRecognition(
            @RequestPart(name = "frontFile", required = false) MultipartFile frontFile,
            @RequestPart(name = "backFile", required = false) MultipartFile backFile,
            @RequestPart("exitentrypermitType") String exitentrypermitType
    ) {
        if ((frontFile == null || frontFile.isEmpty()) && (backFile == null || backFile.isEmpty())) {
            return createErrorResponse(400, "未传入文件");
        }

        ExitentrypermitType frontType;
        ExitentrypermitType backType;

        try {
            switch (exitentrypermitType) {
                case "hk_mc_passport":
                    frontType = ExitentrypermitType.HK_MC_PASSPORT_FRONT;
                    backType = ExitentrypermitType.HK_MC_PASSPORT_BACK;
                    break;
                case "tw_passport":
                    frontType = ExitentrypermitType.TW_PASSPORT_FRONT;
                    backType = ExitentrypermitType.TW_PASSPORT_BACK;
                    break;
                case "tw_return_passport":
                    frontType = ExitentrypermitType.TW_RETURN_PASSPORT_FRONT;
                    backType = ExitentrypermitType.TW_RETURN_PASSPORT_BACK;
                    break;
                case "hk_mc_return_passport":
                    frontType = ExitentrypermitType.HK_MC_RETURN_PASSPORT_FRONT;
                    backType = ExitentrypermitType.HK_MC_RETURN_PASSPORT_BACK;
                    break;
                default:
                    return createErrorResponse(400, "不支持的通行证类型: " + exitentrypermitType);
            }
        } catch (Exception e) {
             return createErrorResponse(400, "通行证类型解析错误: " + e.getMessage());
        }

        OcrService service = getOcrService();

        Map<String, Object> frontResp = null;
        if (frontFile != null && !frontFile.isEmpty()) {
            frontResp = service.recognizeExitentrypermit(frontFile, frontType);
        }

        Map<String, Object> backResp = null;
        if (backFile != null && !backFile.isEmpty()) {
            backResp = service.recognizeExitentrypermit(backFile, backType);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("front", frontResp);
        result.put("back", backResp);

        return createSuccessResponse(result);
    }

    /**
     * 护照识别接口
     * POST /plugin/onebase-plugin-ocr/passport
     *
     * @param file 护照文件 (必填)
     * @return 识别结果
     */
    @PostMapping("/passport")
    public Map<String, Object> ocrPassportRecognition(
            @RequestPart("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return createErrorResponse(400, "未传入文件");
        }

        Map<String, Object> result = getOcrService().recognizePassport(file);
        if (result == null) {
            return createErrorResponse(500, "护照识别失败");
        }
        
        return createSuccessResponse(result);
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("msg", "success");
        response.put("data", data);
        return response;
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("msg", message);
        response.put("data", null);
        return response;
    }
}
