package com.cmsr.onebase.plugin.ocr.controller;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.ocr.config.OcrPluginConfig;
import com.cmsr.onebase.plugin.ocr.enums.ExitentrypermitType;
import com.cmsr.onebase.plugin.ocr.enums.IdCardSideEnum;
import com.cmsr.onebase.plugin.ocr.enums.OcrProviderEnum;
import com.cmsr.onebase.plugin.ocr.service.AliyunOcrService;
import com.cmsr.onebase.plugin.ocr.service.BaiduOcrService;
import com.cmsr.onebase.plugin.ocr.service.TencentOcrService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * OCR 插件控制器
 * <p>
 * 提供统一的 OCR 识别接口，根据配置自动切换底层的服务商（百度/阿里/腾讯）。
 * </p>
 */
@RestController
@RequestMapping("/plugin/onebase-plugin-ocr")
public class OcrController implements HttpHandler {

    @Resource
    private OcrPluginConfig ocrConfig;

    // 各厂商服务实例（懒加载）
    private BaiduOcrService baiduOcrService;
    private AliyunOcrService aliyunOcrService;
    private TencentOcrService tencentOcrService;

    /**
     * 身份证识别
     */
    @Operation(summary = "身份证识别", description = "支持中国大陆居民身份证正反面识别")
    @PostMapping("/id-card")
    public Object recognizeIdCard(@RequestParam("frontFile") MultipartFile file,
                                @RequestParam(value = "side", defaultValue = "FRONT") IdCardSideEnum side) throws Exception {
        String imageB64 = Base64.getEncoder().encodeToString(file.getBytes());
        String providerCode = ocrConfig.getProvider();
        OcrProviderEnum provider = OcrProviderEnum.fromCode(providerCode);

        switch (provider) {
            case ALIYUN:
                return getAliyunService().recognizeIdCard(imageB64, side);
            case TENCENT:
                return getTencentService().recognizeIdCard(imageB64, side);
            case BAIDU:
            default:
                return getBaiduService().recognizeIdCard(imageB64, side);
        }
    }

    /**
     * 港澳台通行证识别
     */
    @Operation(summary = "港澳台通行证识别", description = "支持港澳台来往内地通行证识别")
    @PostMapping("/exitentrypermit")
    public Object recognizeExitentrypermit(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "type", defaultValue = "HK_MACAU") ExitentrypermitType type) throws Exception {
        String imageB64 = Base64.getEncoder().encodeToString(file.getBytes());
        String providerCode = ocrConfig.getProvider();
        OcrProviderEnum provider = OcrProviderEnum.fromCode(providerCode);

        switch (provider) {
            case ALIYUN:
                return getAliyunService().recognizeExitentrypermit(imageB64, type);
            case TENCENT:
                return getTencentService().recognizeExitentrypermit(imageB64, type);
            case BAIDU:
            default:
                return getBaiduService().recognizeExitentrypermit(imageB64, type);
        }
    }

    /**
     * 护照识别
     */
    @Operation(summary = "护照识别", description = "支持中国护照识别")
    @PostMapping("/passport")
    public Object recognizePassport(@RequestParam("file") MultipartFile file) throws Exception {
        String imageB64 = Base64.getEncoder().encodeToString(file.getBytes());
        String providerCode = ocrConfig.getProvider();
        OcrProviderEnum provider = OcrProviderEnum.fromCode(providerCode);

        switch (provider) {
            case ALIYUN:
                return getAliyunService().recognizePassport(imageB64);
            case TENCENT:
                return getTencentService().recognizePassport(imageB64);
            case BAIDU:
            default:
                return getBaiduService().recognizePassport(imageB64);
        }
    }

    // --- 懒加载服务实例 ---

    private BaiduOcrService getBaiduService() {
        if (baiduOcrService == null) {
            baiduOcrService = new BaiduOcrService(ocrConfig);
        }
        return baiduOcrService;
    }

    private AliyunOcrService getAliyunService() {
        if (aliyunOcrService == null) {
            aliyunOcrService = new AliyunOcrService(ocrConfig);
        }
        return aliyunOcrService;
    }

    private TencentOcrService getTencentService() {
        if (tencentOcrService == null) {
            tencentOcrService = new TencentOcrService(ocrConfig);
        }
        return tencentOcrService;
    }
}
