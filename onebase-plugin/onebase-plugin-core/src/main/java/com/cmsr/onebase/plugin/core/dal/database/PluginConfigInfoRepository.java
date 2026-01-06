package com.cmsr.onebase.plugin.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginConfigInfoDO;
import com.cmsr.onebase.plugin.core.dal.mapper.PluginConfigInfoMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 插件配置信息仓储类
 *
 * @author GitHub Copilot
 * @date 2026-01-05
 */
@Repository
@Slf4j
public class PluginConfigInfoRepository extends BaseDataRepository<PluginConfigInfoMapper, PluginConfigInfoDO> {

    /**
     * 根据ID获取插件配置信息
     *
     * @param id 主键ID
     * @return 插件配置信息
     */
    public PluginConfigInfoDO getPluginConfigInfoById(Long id) {
        return getById(id);
    }

    /**
     * 根据插件ID获取插件配置信息列表
     *
     * @param pluginId 插件ID
     * @return 插件配置信息列表
     */
    public List<PluginConfigInfoDO> getListByPluginId(Long pluginId) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginConfigInfoDO::getPluginId, pluginId);
        return list(queryWrapper);
    }

    /**
     * 根据插件ID和版本获取配置列表
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 配置列表
     */
    public List<PluginConfigInfoDO> getListByPluginIdAndVersion(Long pluginId, String pluginVersion) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginConfigInfoDO::getPluginId, pluginId)
                .eq(PluginConfigInfoDO::getPluginVersion, pluginVersion);
        return list(queryWrapper);
    }

    /**
     * 根据插件ID和版本删除配置
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void deleteByPluginIdAndVersion(Long pluginId, String pluginVersion) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginConfigInfoDO::getPluginId, pluginId)
                .eq(PluginConfigInfoDO::getPluginVersion, pluginVersion);
        remove(queryWrapper);
    }
}
