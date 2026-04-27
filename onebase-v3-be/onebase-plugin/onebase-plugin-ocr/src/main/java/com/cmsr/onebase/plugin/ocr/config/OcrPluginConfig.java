package com.cmsr.onebase.plugin.ocr.config;

import com.cmsr.onebase.plugin.ocr.constant.OcrConfigKeys;
import com.cmsr.onebase.plugin.service.PluginContextService;
import com.cmsr.onebase.plugin.util.PluginPropertiesUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * OCR 插件配置管理
 * <p>
 * 适配器模式：通过 PluginContextService 从宿主系统动态获取配置。
 * 支持 Dev 模式（本地模拟配置）和 Prod 模式（宿主真实配置）。
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Slf4j
@Component
public class OcrPluginConfig {

    private static final String DEFAULT_BAIDU_ENDPOINT = "https://aip.baidubce.com";

    @Resource
    private PluginContextService pluginContextService;

    /**
     * 获取 OCR 服务商 Client ID
     * 适用于百度 API Key / 阿里 Access Key ID / 腾讯 Secret ID 等
     *
     * @return Client ID
     */
    public String getClientId() {
        return getConfig(OcrConfigKeys.CONFIG_CLIENT_ID);
    }

    /**
     * 获取 OCR 服务商 Client Secret
     * 适用于百度 Secret Key / 阿里 Access Key Secret / 腾讯 Secret Key 等
     *
     * @return Client Secret
     */
    public String getClientSecret() {
        return getConfig(OcrConfigKeys.CONFIG_CLIENT_SECRET);
    }

    /**
     * 获取 OCR 服务商 API 接入点
     *
     * @return Endpoint
     */
    public String getEndpoint() {
        String endpoint = getConfig(OcrConfigKeys.CONFIG_ENDPOINT);
        return StringUtils.isBlank(endpoint) ? DEFAULT_BAIDU_ENDPOINT : endpoint;
    }

    /**
     * 获取 OCR 服务商 Region ID
     *
     * @return Region ID
     */
    public String getRegionId() {
        return getConfig(OcrConfigKeys.CONFIG_REGION_ID);
    }

    /**
     * 获取 OCR 服务商选择
     *
     * @return 服务商代码 (baidu / aliyun / tencent),默认 baidu
     */
    public String getProvider() {
        String provider = getConfig(OcrConfigKeys.CONFIG_PROVIDER);
        return StringUtils.isBlank(provider) ? "baidu" : provider;
    }

    /**
     * 从插件上下文中读取配置
     *
     * @param key 配置键
     * @return 配置值
     */
    private String getConfig(String key) {
        try {
            String pluginId = PluginPropertiesUtil.getPluginId(this.getClass());
            String pluginVersion = PluginPropertiesUtil.getPluginVersion(this.getClass());
            
            Object value = pluginContextService.getConfigValue(
                    pluginId, 
                    pluginVersion, 
                    key
            );
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("[OCR 插件] 获取配置失败，配置键: {}", key, e);
            return null;
        }
    }
}
