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

        // 2. 解析plugin_meta_info中的配置模板
        PluginConfigTemplateRespVO respVO = new PluginConfigTemplateRespVO();
        List<PluginConfigTemplateRespVO.ConfigTemplateItem> templateItems = new ArrayList<>();

        if (StrUtil.isNotBlank(pluginInfo.getPluginMetaInfo())) {
            PluginMetaInfo metaInfo = JSONUtil.toBean(pluginInfo.getPluginMetaInfo(), PluginMetaInfo.class);
            if (CollUtil.isNotEmpty(metaInfo.getConfigTemplates())) {
                for (PluginMetaInfo.PluginConfigTemplate template : metaInfo.getConfigTemplates()) {
                    PluginConfigTemplateRespVO.ConfigTemplateItem item = new PluginConfigTemplateRespVO.ConfigTemplateItem();
                    item.setConfigKey(template.getConfigKey());
                    item.setDefaultValue(template.getDefaultValue());
                    item.setValueType(template.getValueType());
                    item.setDescription(template.getDescription());
                    templateItems.add(item);
                }
            }
        }

        respVO.setConfigTemplates(templateItems);
        return respVO;
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
