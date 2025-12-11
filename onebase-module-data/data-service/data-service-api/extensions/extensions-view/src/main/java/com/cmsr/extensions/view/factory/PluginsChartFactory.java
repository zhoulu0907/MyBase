package com.cmsr.extensions.view.factory;

import com.cmsr.exception.DEException;
import com.cmsr.extensions.view.plugin.AbstractChartPlugin;
import com.cmsr.extensions.view.plugin.DataEaseChartPlugin;
import com.cmsr.extensions.view.vo.XpackPluginsViewVO;
import com.cmsr.license.utils.LicenseUtil;
//import com.cmsr.license.utils.LogUtil;
//import com.cmsr.plugins.factory.DataEasePluginFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginsChartFactory {

    private static final Map<String, DataEaseChartPlugin> templateMap = new ConcurrentHashMap<>();


    public static AbstractChartPlugin getInstance(String render, String type) {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        String key = render + "_" + type;
        return templateMap.get(key);
    }

    public static void loadPlugin(String render, String type, DataEaseChartPlugin plugin) {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
/*         String key = render + "_" + type;
        if (templateMap.containsKey(key)) return;
        templateMap.put(key, plugin);
        try {
            String moduleName = plugin.getPluginInfo().getModuleName();
            DataEasePluginFactory.loadTemplate(moduleName, plugin);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), new Throwable(e));
            DEException.throwException(e);
        } */
    }

    public static List<XpackPluginsViewVO> getViewConfigList() {
        //if (!LicenseUtil.licenseValid()) DEException.throwException("插件功能只对企业版本可用！");
        return templateMap.values().stream().map(DataEaseChartPlugin::getConfig).toList();
    }
}
