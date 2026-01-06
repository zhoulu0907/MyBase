package com.cmsr.onebase.plugin.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginInfoDO;
import com.cmsr.onebase.plugin.core.dal.mapper.PluginInfoMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 插件信息仓储类
 *
 * @author GitHub Copilot
 * @date 2026-01-05
 */
@Repository
@Slf4j
public class PluginInfoRepository extends BaseDataRepository<PluginInfoMapper, PluginInfoDO> {

    /**
     * 根据ID获取插件信息
     *
     * @param id 主键ID
     * @return 插件信息
     */
    public PluginInfoDO getPluginInfoById(Long id) {
        return getById(id);
    }

    /**
     * 根据插件ID获取插件信息列表
     *
     * @param pluginId 插件ID
     * @return 插件信息列表
     */
    public List<PluginInfoDO> getListByPluginId(Long pluginId) {
        QueryWrapper queryWrapper = this.query()
                .eq(PluginInfoDO::getPluginId, pluginId);
        return list(queryWrapper);
    }
}
