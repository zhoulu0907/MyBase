package com.cmsr.onebase.plugin.runtime.service;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.plugin.core.dal.database.PluginConfigInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginConfigInfoDO;
import com.cmsr.onebase.plugin.service.PluginContextService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件配置查询服务实现类
 *
 * @author chengyuansen
 * @date 2026-01-07
 */
@Slf4j
@Service
public class PluginContextServiceImpl implements PluginContextService {

    /** 插件配置信息仓储 */
    @Resource
    private PluginConfigInfoRepository pluginConfigInfoRepository;

    @Override
    public Long getTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        log.debug("租户ID: {}", tenantId);
        return tenantId;
    }

    /**
     * 获取指定插件的全部配置
     *
     * @param pluginId 插件ID（字符串格式，会转换为 Long）
     * @param version  插件版本
     * @return 配置键值对，如果插件无配置则返回空 Map（不返回 null）
     */
    @Override
    public Map<String, Object> getConfig(String pluginId, String version) {
        // 获取当前租户 ID
        Long tenantId = TenantContextHolder.getTenantId();

        log.info("从数据库加载插件配置，tenantId: {}, pluginId: {}, version: {}", tenantId, pluginId, version);

        // 从数据库查询配置列表
        List<PluginConfigInfoDO> configList = pluginConfigInfoRepository.getListByPluginIdAndVersion(pluginId, version);

        if (configList == null || configList.isEmpty()) {
            log.info("插件无配置数据，tenantId: {}, pluginId: {}, version: {}", tenantId, pluginId, version);
            return Collections.emptyMap();
        }

        // 转换为 Map 格式
        Map<String, Object> configMap = new HashMap<>(configList.size());
        for (PluginConfigInfoDO config : configList) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }

        log.info("插件配置加载完成，tenantId: {}, pluginId: {}, version: {}, 配置项数量: {}", tenantId, pluginId, version,
                configMap.size());

        return configMap;
    }
}
