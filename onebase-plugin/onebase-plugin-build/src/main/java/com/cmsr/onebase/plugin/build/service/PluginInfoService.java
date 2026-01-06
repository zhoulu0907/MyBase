package com.cmsr.onebase.plugin.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.plugin.build.vo.req.PluginInfoPageReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginInfoUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginUploadReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginInfoDetailRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginInfoRespVO;
import jakarta.validation.Valid;

/**
 * 插件信息服务接口
 *
 * @author matianyu
 * @date 2026-01-06
 */
public interface PluginInfoService {

    /**
     * 上传插件（首次上传）
     *
     * @param uploadReqVO 上传请求
     * @return 插件ID
     */
    Long uploadPlugin(@Valid PluginUploadReqVO uploadReqVO);

    /**
     * 获取插件详情（含版本列表）
     *
     * @param id 版本记录ID
     * @return 插件详情
     */
    PluginInfoDetailRespVO getPluginDetail(Long id);

    /**
     * 分页查询插件列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<PluginInfoRespVO> getPluginPage(PluginInfoPageReqVO pageReqVO);

    /**
     * 更新插件基础信息（对所有版本生效）
     *
     * @param updateReqVO 更新请求
     */
    void updatePluginInfo(@Valid PluginInfoUpdateReqVO updateReqVO);

    /**
     * 删除插件及其所有版本
     *
     * @param pluginId 插件ID
     */
    void deletePlugin(Long pluginId);

    /**
     * 启用插件版本
     *
     * @param id 版本记录ID
     */
    void enablePlugin(Long id);

    /**
     * 禁用插件版本
     *
     * @param id 版本记录ID
     */
    void disablePlugin(Long id);

}
