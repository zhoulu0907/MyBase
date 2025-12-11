package com.cmsr.extensions.view.plugin;

//import com.cmsr.exception.DEException;
import com.cmsr.extensions.view.factory.PluginsChartFactory;
import com.cmsr.extensions.view.vo.XpackPluginsViewVO;
//import com.cmsr.license.utils.JsonUtil;
//import com.cmsr.plugins.template.DataEasePlugin;
//import com.cmsr.plugins.vo.DataEasePluginVO;
//public abstract class DataEaseChartPlugin extends AbstractChartPlugin implements DataEasePlugin {

public abstract class DataEaseChartPlugin extends AbstractChartPlugin {


    public void loadPlugin() {
        XpackPluginsViewVO viewConfig = getConfig();
        PluginsChartFactory.loadPlugin(viewConfig.getRender(), viewConfig.getChartValue(), this);
    }

    public XpackPluginsViewVO getConfig() {
        XpackPluginsViewVO vo = new XpackPluginsViewVO();
/*         DataEasePluginVO pluginInfo = null;
        try {
            pluginInfo = getPluginInfo();
        } catch (Exception e) {
            DEException.throwException(e);
        }
        String config = pluginInfo.getConfig();
        XpackPluginsViewVO vo = JsonUtil.parseObject(config, XpackPluginsViewVO.class);
        vo.setIcon(pluginInfo.getIcon()); */
        return vo;
    }
}
