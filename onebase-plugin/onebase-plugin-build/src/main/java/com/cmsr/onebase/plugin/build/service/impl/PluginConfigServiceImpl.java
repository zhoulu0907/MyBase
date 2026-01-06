package com.cmsr.onebase.plugin.build.service.impl;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.plugin.build.service.PluginConfigService;
import com.cmsr.onebase.plugin.build.vo.req.PluginConfigUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginPackageRespVO;
import com.cmsr.onebase.plugin.core.dal.database.PluginConfigInfoRepository;
import com.cmsr.onebase.plugin.core.dal.database.PluginPackageInfoRepository;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginConfigInfoDO;
import com.cmsr.onebase.plugin.core.dal.dataobject.PluginPackageInfoDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.plugin.build.constant.PluginErrorCodeConstants.PLUGIN_NOT_FOUND;

/**
 * 插件配置服务实现类
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Service
@Slf4j
public class PluginConfigServiceImpl implements PluginConfigService {

    @Resource
    private PluginConfigInfoRepository pluginConfigInfoRepository;

    @Resource
    private PluginPackageInfoRepository pluginPackageInfoRepository;

    @Override
    public List<PluginConfigRespVO> getConfigList(Long pluginId, String pluginVersion) {
        List<PluginConfigInfoDO> configs = pluginConfigInfoRepository.getListByPluginIdAndVersion(
                pluginId, pluginVersion);
        return configs.stream()
                .map(config -> BeanUtils.toBean(config, PluginConfigRespVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfigs(List<PluginConfigUpdateReqVO> updateReqVOList) {
        for (PluginConfigUpdateReqVO updateReqVO : updateReqVOList) {
            PluginConfigInfoDO config = pluginConfigInfoRepository.getById(updateReqVO.getId());
            if (config == null) {
                throw exception(PLUGIN_NOT_FOUND);
            }
            config.setConfigValue(updateReqVO.getConfigValue());
            pluginConfigInfoRepository.update(config);
        }
        log.info("插件配置更新成功，更新数量：{}", updateReqVOList.size());
    }

    @Override
    public List<PluginPackageRespVO> getPackageList(Long pluginId, String pluginVersion) {
        List<PluginPackageInfoDO> packages = pluginPackageInfoRepository.getListByPluginIdAndVersion(
                pluginId, pluginVersion);
        return packages.stream()
                .map(pkg -> BeanUtils.toBean(pkg, PluginPackageRespVO.class))
                .collect(Collectors.toList());
    }

}
