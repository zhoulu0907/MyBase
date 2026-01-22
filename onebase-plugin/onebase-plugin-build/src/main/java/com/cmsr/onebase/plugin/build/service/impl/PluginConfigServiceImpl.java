package com.cmsr.onebase.plugin.build.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.plugin.build.service.PluginConfigService;
import com.cmsr.onebase.plugin.build.vo.req.PluginConfigSaveReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigDetailRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigTemplateRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginPackageRespVO;
import com.cmsr.onebase.plugin.core.dal.database.PluginConfigInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginPackageInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginConfigInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginPackageInfoDO;
import com.cmsr.onebase.plugin.core.model.PluginMetaInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.plugin.build.constant.PluginErrorCodeConstants.PLUGIN_VERSION_NOT_FOUND;

/**
 * 插件配置服务实现类
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Service
@Slf4j
public class PluginConfigServiceImpl implements PluginConfigService {

    @Resource
    private PluginConfigInfoRepository pluginConfigInfoRepository;

    @Resource
    private PluginPackageInfoRepository pluginPackageInfoRepository;

    @Resource
    private PluginInfoRepository pluginInfoRepository;

    @Override
    public PluginConfigTemplateRespVO getConfigTemplate(String pluginId, String pluginVersion) {
        // 1. 获取插件信息
        PluginInfoDO pluginInfo = pluginInfoRepository.getByPluginIdAndVersion(pluginId, pluginVersion);
        if (pluginInfo == null) {
            throw exception(PLUGIN_VERSION_NOT_FOUND);
        }

        // 2. 构建响应对象
        PluginConfigTemplateRespVO respVO = new PluginConfigTemplateRespVO();
        List<PluginConfigTemplateRespVO.ConfigTemplateItem> templateItems = new ArrayList<>();

        // 3. 优先从plugin_config_info（plugin.schema.json）读取配置模板
        if (StrUtil.isNotBlank(pluginInfo.getPluginConfigInfo()) && !"{}".equals(pluginInfo.getPluginConfigInfo())) {
            templateItems = parseConfigTemplatesFromSchemaJson(pluginInfo.getPluginConfigInfo());
        }

        // 4. 如果plugin_config_info为空，则从plugin_meta_info（plugin.json）读取
        if (CollUtil.isEmpty(templateItems) && StrUtil.isNotBlank(pluginInfo.getPluginMetaInfo())) {
            PluginMetaInfo metaInfo = JSONUtil.toBean(pluginInfo.getPluginMetaInfo(), PluginMetaInfo.class);
            if (CollUtil.isNotEmpty(metaInfo.getConfigTemplates())) {
                for (PluginMetaInfo.PluginConfigTemplate template : metaInfo.getConfigTemplates()) {
                    PluginConfigTemplateRespVO.ConfigTemplateItem item = new PluginConfigTemplateRespVO.ConfigTemplateItem();
                    item.setConfigKey(template.getConfigKey());
                    item.setConfigName(template.getConfigName());
                    item.setDefaultValue(template.getDefaultValue());
                    item.setValueType(template.getValueType());
                    item.setDescription(template.getDescription());
                    item.setRequired(template.getRequired());
                    templateItems.add(item);
                }
            }
        }

        respVO.setConfigTemplates(templateItems);
        respVO.setPluginConfigInfo(pluginInfo.getPluginConfigInfo());
        return respVO;
    }

    /**
     * 从plugin.schema.json解析配置模板
     * 支持多种格式：
     * 1. 直接的配置数组: [{"configKey": "xxx", ...}]
     * 2. 包含configTemplates字段: {"configTemplates": [...]}
     * 3. 标准JSON Schema格式（通过properties定义）
     *
     * @param schemaJson plugin.schema.json内容
     * @return 配置模板列表
     */
    private List<PluginConfigTemplateRespVO.ConfigTemplateItem> parseConfigTemplatesFromSchemaJson(String schemaJson) {
        List<PluginConfigTemplateRespVO.ConfigTemplateItem> templateItems = new ArrayList<>();
        try {
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(schemaJson);
            cn.hutool.json.JSONArray configArray = null;

            // 1. 尝试获取configTemplates字段 or configs (Custom format)
            if (jsonObject.containsKey("configTemplates")) {
                configArray = jsonObject.getJSONArray("configTemplates");
            } else if (jsonObject.containsKey("configs")) {
                configArray = jsonObject.getJSONArray("configs");
            }

            // Custom format handling
            if (configArray != null && !configArray.isEmpty()) {
                for (int i = 0; i < configArray.size(); i++) {
                    cn.hutool.json.JSONObject configObj = configArray.getJSONObject(i);
                    PluginConfigTemplateRespVO.ConfigTemplateItem item = new PluginConfigTemplateRespVO.ConfigTemplateItem();
                    item.setConfigKey(configObj.getStr("configKey"));
                    item.setConfigName(configObj.getStr("configName"));
                    item.setDefaultValue(configObj.getStr("defaultValue"));
                    item.setValueType(configObj.getStr("valueType"));
                    item.setDescription(configObj.getStr("description"));
                    item.setRequired(configObj.getBool("required"));
                    templateItems.add(item);
                }
            }
            // 2. Standard JSON Schema handling
            else if (jsonObject.containsKey("properties")) {
                parseJsonSchema(jsonObject, "", templateItems);
            }
        } catch (Exception e) {
            log.warn("解析plugin.schema.json失败: {}", e.getMessage());
        }
        return templateItems;
    }

    private void parseJsonSchema(cn.hutool.json.JSONObject schemaObj, String prefix,
                                 List<PluginConfigTemplateRespVO.ConfigTemplateItem> templateItems) {
        if (!schemaObj.containsKey("properties")) {
            return;
        }
        cn.hutool.json.JSONObject properties = schemaObj.getJSONObject("properties");
        cn.hutool.json.JSONArray requiredArray = schemaObj.getJSONArray("required");
        List<String> requiredFields = requiredArray != null ? requiredArray.toList(String.class) : new ArrayList<>();

        for (String key : properties.keySet()) {
            cn.hutool.json.JSONObject propObj = properties.getJSONObject(key);
            String fullKey = StrUtil.isEmpty(prefix) ? key : prefix + "." + key;
            String type = propObj.getStr("type");

            if ("object".equalsIgnoreCase(type) && propObj.containsKey("properties")) {
                parseJsonSchema(propObj, fullKey, templateItems);
            } else {
                PluginConfigTemplateRespVO.ConfigTemplateItem item = new PluginConfigTemplateRespVO.ConfigTemplateItem();
                item.setConfigKey(fullKey);
                // Use title as configName, fallback to key
                String title = propObj.getStr("title");
                item.setConfigName(StrUtil.isNotBlank(title) ? title : key);
                item.setDescription(propObj.getStr("description"));

                // Handle defaultValue
                Object defaultVal = propObj.get("default");
                item.setDefaultValue(defaultVal != null ? String.valueOf(defaultVal) : null);

                // Handle valueType
                String uiType = propObj.getStr("$ui:type");
                if (StrUtil.isNotEmpty(uiType)) {
                    item.setValueType(uiType);
                } else {
                    item.setValueType("normal");
                }

                item.setRequired(requiredFields.contains(key));
                templateItems.add(item);
            }
        }
    }

    @Override
    public PluginConfigDetailRespVO getConfigDetail(String pluginId, String pluginVersion) {
        // 1. 获取配置列表
        List<PluginConfigInfoDO> configs = pluginConfigInfoRepository.getListByPluginIdAndVersion(
                pluginId, pluginVersion);

        // 2. 转换为Map结构
        PluginConfigDetailRespVO respVO = new PluginConfigDetailRespVO();
        Map<String, PluginConfigDetailRespVO.ConfigValueItem> configMap = new HashMap<>();

        for (PluginConfigInfoDO config : configs) {
            PluginConfigDetailRespVO.ConfigValueItem item = new PluginConfigDetailRespVO.ConfigValueItem();
            item.setConfigValue(config.getConfigValue());
            item.setValueType(config.getValueType());
            configMap.put(config.getConfigKey(), item);
        }

        respVO.setConfigs(configMap);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfigs(PluginConfigSaveReqVO saveReqVO) {
        String pluginId = saveReqVO.getPluginId();
        String pluginVersion = saveReqVO.getPluginVersion();

        // 1. 校验插件版本存在
        PluginInfoDO pluginInfo = pluginInfoRepository.getByPluginIdAndVersion(pluginId, pluginVersion);
        if (pluginInfo == null) {
            throw exception(PLUGIN_VERSION_NOT_FOUND);
        }

        // 2. 删除旧配置
        pluginConfigInfoRepository.deleteByPluginIdAndVersion(pluginId, pluginVersion);

        // 3. 插入新配置
        Map<String, PluginConfigSaveReqVO.ConfigValueVO> configs = saveReqVO.getConfigs();
        if (CollUtil.isNotEmpty(configs)) {
            for (Map.Entry<String, PluginConfigSaveReqVO.ConfigValueVO> entry : configs.entrySet()) {
                PluginConfigInfoDO configDO = PluginConfigInfoDO.builder()
                        .pluginId(pluginId)
                        .pluginVersion(pluginVersion)
                        .configKey(entry.getKey())
                        .configValue(entry.getValue().getConfigValue())
                        .valueType(entry.getValue().getValueType())
                        .build();
                pluginConfigInfoRepository.insert(configDO);
            }
        }

        log.info("插件配置保存成功: pluginId={}, version={}, configCount={}",
                pluginId, pluginVersion, configs != null ? configs.size() : 0);
    }

    @Override
    public List<PluginPackageRespVO> getPackageList(String pluginId, String pluginVersion) {
        List<PluginPackageInfoDO> packages = pluginPackageInfoRepository.getListByPluginIdAndVersion(
                pluginId, pluginVersion);
        return packages.stream()
                .map(pkg -> BeanUtils.toBean(pkg, PluginPackageRespVO.class))
                .toList();
    }

}
