package com.cmsr.extensions.datafilling.plugin;

//import com.cmsr.exception.DEException;
import com.cmsr.extensions.datafilling.factory.ExtDDLProviderFactory;
import com.cmsr.extensions.datafilling.provider.ExtDDLProvider;
import com.cmsr.extensions.datafilling.vo.XpackPluginsDfVO;
//import com.cmsr.license.utils.JsonUtil;
//import com.cmsr.plugins.template.DataEasePlugin;
//import com.cmsr.plugins.vo.DataEasePluginVO;
//public abstract class DataFillingPlugin extends ExtDDLProvider implements DataEasePlugin {

public abstract class DataFillingPlugin extends ExtDDLProvider{


    public void loadPlugin() {
        XpackPluginsDfVO viewConfig = getConfig();
        ExtDDLProviderFactory.loadPlugin(viewConfig.getType(), this);
    }


    public XpackPluginsDfVO getConfig() {
        XpackPluginsDfVO vo = new XpackPluginsDfVO();

        //DataEasePluginVO pluginInfo = null;
        try {
            //pluginInfo = getPluginInfo();
        } catch (Exception e) {
            //DEException.throwException(e);
        }
        //String config = pluginInfo.getConfig();
        //XpackPluginsDfVO vo = JsonUtil.parseObject(config, XpackPluginsDfVO.class);
        //vo.setIcon(pluginInfo.getIcon());
        return vo;
    }


    public void unloadPlugin() {

    }
}
