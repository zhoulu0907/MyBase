package com.cmsr.onebase.plugin.build.service;

import com.cmsr.onebase.plugin.build.vo.req.PluginConfigUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginPackageRespVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 插件配置服务接口
 *
 * @author matianyu
 * @date 2026-01-06
 */
public interface PluginConfigService {

    /**
     * 获取配置列表
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 配置列表
     */
    List<PluginConfigRespVO> getConfigList(Long pluginId, String pluginVersion);

    /**
     * 批量更新配置
     *
     * @param updateReqVOList 更新请求列表
     */
    void updateConfigs(@Valid List<PluginConfigUpdateReqVO> updateReqVOList);

    /**
     * 获取包信息列表
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 包信息列表
     */
    List<PluginPackageRespVO> getPackageList(Long pluginId, String pluginVersion);

}
