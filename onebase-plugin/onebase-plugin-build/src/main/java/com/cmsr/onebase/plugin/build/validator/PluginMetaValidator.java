package com.cmsr.onebase.plugin.build.validator;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.plugin.core.model.PluginMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.plugin.build.constant.PluginErrorCodeConstants.*;

/**
 * 插件元数据校验器
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Component
@Slf4j
public class PluginMetaValidator {

    /**
     * 版本号格式正则：x.y.z 或 x.y.z-suffix
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9]+)?$");

    /**
     * 校验并解析plugin.json内容
     *
     * @param jsonContent plugin.json内容
     * @return 解析后的元数据信息
     */
    public PluginMetaInfo validate(String jsonContent) {
        if (StrUtil.isBlank(jsonContent)) {
            throw exception(PLUGIN_JSON_EMPTY);
        }

        // 1. 解析JSON
        PluginMetaInfo metaInfo;
        try {
            metaInfo = JsonUtils.parseObject(jsonContent, PluginMetaInfo.class);
        } catch (Exception e) {
            log.error("解析plugin.json失败: {}", e.getMessage());
            throw exception(PLUGIN_JSON_PARSE_ERROR);
        }

        if (metaInfo == null) {
            throw exception(PLUGIN_JSON_PARSE_ERROR);
        }

        // 2. 校验必填字段
        if (metaInfo.getPluginId() == null) {
            throw exception(PLUGIN_META_PLUGIN_ID_REQUIRED);
        }

        if (StrUtil.isBlank(metaInfo.getPluginName())) {
            throw exception(PLUGIN_META_PLUGIN_NAME_REQUIRED);
        }

        if (StrUtil.isBlank(metaInfo.getPluginVersion())) {
            throw exception(PLUGIN_META_VERSION_REQUIRED);
        }

        // 3. 校验版本号格式
        if (!VERSION_PATTERN.matcher(metaInfo.getPluginVersion()).matches()) {
            throw exception(PLUGIN_META_VERSION_FORMAT_INVALID);
        }

        // 4. 校验插件名称长度
        if (metaInfo.getPluginName().length() > 200) {
            throw exception(PLUGIN_META_PLUGIN_NAME_TOO_LONG);
        }

        return metaInfo;
    }

}
