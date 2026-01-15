package com.cmsr.onebase.plugin.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginPackageInfoDO;
import com.cmsr.onebase.plugin.core.dal.mapper.PluginPackageInfoMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 插件包信息仓储类
 *
 * @author GitHub Copilot
 * @date 2026-01-05
 */
@Repository
@Slf4j
public class PluginPackageInfoRepository extends BaseDataRepository<PluginPackageInfoMapper, PluginPackageInfoDO> {

    /**
     * 根据ID获取插件包信息
     *
     * @param id 主键ID
     * @return 插件包信息
     */
    public PluginPackageInfoDO getPluginPackageInfoById(Long id) {
        return getById(id);
    }

    /**
     * 根据插件ID获取插件包信息列表
     *
     * @param pluginId 插件ID
     * @return 插件包信息列表
     */
    public List<PluginPackageInfoDO> getListByPluginId(String pluginId) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginPackageInfoDO::getPluginId, pluginId);
        return list(queryWrapper);
    }

    /**
     * 根据插件ID和版本获取包信息列表
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @return 包信息列表
     */
    public List<PluginPackageInfoDO> getListByPluginIdAndVersion(String pluginId, String pluginVersion) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginPackageInfoDO::getPluginId, pluginId)
                .eq(PluginPackageInfoDO::getPluginVersion, pluginVersion);
        return list(queryWrapper);
    }

    /**
     * 根据插件ID和版本删除包信息
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     */
    public void deleteByPluginIdAndVersion(String pluginId, String pluginVersion) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginPackageInfoDO::getPluginId, pluginId)
                .eq(PluginPackageInfoDO::getPluginVersion, pluginVersion);
        remove(queryWrapper);
    }
}
