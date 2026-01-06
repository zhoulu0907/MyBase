package com.cmsr.onebase.plugin.build.service;

import com.cmsr.onebase.plugin.build.vo.req.PluginVersionUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginVersionUploadReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginVersionRespVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 插件版本服务接口
 *
 * @author matianyu
 * @date 2026-01-06
 */
public interface PluginVersionService {

    /**
     * 上传新版本
     *
     * @param uploadReqVO 上传请求
     * @return 版本记录ID
     */
    Long uploadVersion(@Valid PluginVersionUploadReqVO uploadReqVO);

    /**
     * 获取版本列表
     *
     * @param pluginId 插件ID
     * @return 版本列表
     */
    List<PluginVersionRespVO> getVersionList(Long pluginId);

    /**
     * 更新版本信息（仅停用状态可更新）
     *
     * @param updateReqVO 更新请求
     */
    void updateVersion(@Valid PluginVersionUpdateReqVO updateReqVO);

    /**
     * 删除版本（不可删除启用版本和唯一版本）
     *
     * @param id 版本记录ID
     */
    void deleteVersion(Long id);

}
