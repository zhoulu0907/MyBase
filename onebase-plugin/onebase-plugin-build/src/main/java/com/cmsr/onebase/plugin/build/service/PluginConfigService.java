package com.cmsr.onebase.plugin.build.service;

import com.cmsr.onebase.plugin.build.vo.req.PluginConfigSaveReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigDetailRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigTemplateRespVO;
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
     * 获取配置项（配置模板信息，从plugin_info表的plugin_meta_info字段获取）
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 配置模板信息
     */
    PluginConfigTemplateRespVO getConfigTemplate(String pluginId, String pluginVersion);

    /**
     * 获取配置项详情（Map结构，key为configKey，value为配置值对象）
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 配置详情
     */
    PluginConfigDetailRespVO getConfigDetail(String pluginId, String pluginVersion);

    /**
     * 创建/更新配置（先删除旧配置再插入新配置）
     *
     * @param saveReqVO 配置保存请求
     */
    void saveConfigs(@Valid PluginConfigSaveReqVO saveReqVO);

    /**
     * 获取包信息列表
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 包信息列表
     */
    List<PluginPackageRespVO> getPackageList(String pluginId, String pluginVersion);

}
