package com.cmsr.onebase.plugin.ocr.service;

import com.cmsr.onebase.plugin.ocr.config.OcrPluginConfig;
import com.cmsr.onebase.plugin.ocr.enums.OcrProviderEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OCR 服务商工厂
 * 根据配置动态选择服务商实现
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Slf4j
@Component
public class OcrProviderFactory {

    @Resource
    private OcrPluginConfig ocrConfig;

    @Resource
    private BaiduOcrService baiduOcrService;

    // 未来扩展: 注入阿里云、腾讯云服务
    // @Resource
    // private AliyunOcrService aliyunOcrService;
    // @Resource
    // private TencentOcrService tencent OcrService;

    /**
     * 获取当前配置的 OCR 服务提供商
     *
     * @return OCR 服务提供商实例
     */
    public IOcrProvider getProvider() {
        String providerCode = ocrConfig.getProvider();
        OcrProviderEnum provider = OcrProviderEnum.fromCode(providerCode);
        
        log.info("[OCR 插件] 使用服务商: {} ({})", provider.getName(), provider.getCode());
        
        switch (provider) {
            case BAIDU:
                return baiduOcrService;
            case ALIYUN:
                // 未来实现: return aliyunOcrService;
                log.warn("[OCR 插件] 阿里云 OCR 服务尚未实现,降级使用百度 OCR");
                return baiduOcrService;
            case TENCENT:
                // 未来实现: return tencentOcrService;
                log.warn("[OCR 插件] 腾讯云 OCR 服务尚未实现,降级使用百度 OCR");
                return baiduOcrService;
            default:
                log.warn("[OCR 插件] 未知服务商: {},使用默认百度 OCR", providerCode);
                return baiduOcrService;
        }
    }
}
