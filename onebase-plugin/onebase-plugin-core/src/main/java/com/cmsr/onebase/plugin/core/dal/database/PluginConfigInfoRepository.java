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
}
