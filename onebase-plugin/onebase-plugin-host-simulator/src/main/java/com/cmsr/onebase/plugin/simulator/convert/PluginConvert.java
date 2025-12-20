package com.cmsr.onebase.plugin.simulator.convert;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.simulator.controller.plugin.vo.PluginRespVO;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件信息转换器
 *
 * @author matianyu
 * @date 2025-12-17
 */
@Component
public class PluginConvert {

    /**
     * PluginWrapper 转换为 PluginRespVO
     */
    public PluginRespVO convert(PluginWrapper wrapper) {
        if (wrapper == null) {
            return null;
        }
        PluginRespVO vo = new PluginRespVO();
        vo.setPluginId(wrapper.getPluginId());
        vo.setDescription(wrapper.getDescriptor().getPluginDescription());
        vo.setVersion(wrapper.getDescriptor().getVersion());
        vo.setProvider(wrapper.getDescriptor().getProvider());
        vo.setState(wrapper.getPluginState().toString());
        vo.setPluginClass(wrapper.getDescriptor().getPluginClass());
        vo.setPluginPath(wrapper.getPluginPath() != null ? wrapper.getPluginPath().toString() : null);
        return vo;
    }

    /**
     * PluginInfo 转换为 PluginRespVO
     */
    public PluginRespVO convert(OneBasePluginManager.PluginInfo info) {
        if (info == null) {
            return null;
        }
        PluginRespVO vo = new PluginRespVO();
        vo.setPluginId(info.getPluginId());
        vo.setDescription(info.getDescription());
        vo.setVersion(info.getVersion());
        vo.setState(info.getState());
        return vo;
    }

    /**
     * PluginInfo 列表转换为 PluginRespVO 列表
     */
    public List<PluginRespVO> convertList(List<OneBasePluginManager.PluginInfo> list) {
        if (list == null) {
            return null;
        }
        return list.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
