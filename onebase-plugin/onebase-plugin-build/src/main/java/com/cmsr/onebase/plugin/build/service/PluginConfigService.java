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
     * <p>
     * 1. 如果valueType为secret，则配置值用******代替
     * 2. 如果用户上传的新版本没有更新配置，用最新的pluginId是查不到配置的，这时候会返回上一个版本的配置
     * 3. 如果上一个版本也没有配置，则返回空
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
